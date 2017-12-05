package net.radialdata.collector.newscrapper.service;


import com.google.cloud.language.v1beta2.Document;
import com.google.cloud.language.v1beta2.LanguageServiceClient;
import com.google.cloud.language.v1beta2.Sentiment;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentOptions;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import net.radialdata.collector.newscrapper.data.article.Article;
import net.radialdata.collector.newscrapper.data.article.ArticleRepository;
import net.radialdata.collector.newscrapper.data.company.Company;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class BrowserService {

    private static final Logger log = LoggerFactory.getLogger(BrowserService.class);

    @Value("${watson.auth.username}")
    private String watsonUsername;
    @Value("${watson.auth.password}")
    private String watsonPassword;

    @Autowired
    @Qualifier("poolTargetSourceWebDriver")
    CommonsPool2TargetSource poolTargetSourceWebDriver;

    @Autowired
    ArticleRepository articleRepository;

    @Async("linkCollectorTaskExecutor")
    public Future<ArrayList<String>> collectLinksFromPage(String page) throws Exception {
        WebDriver driver = (WebDriver) poolTargetSourceWebDriver.getTarget();
        driver.get(page);

        // echo for demo
        ArrayList<String> links = new ArrayList<String>();
        links.add("Result echo" + page.split(" ")[0]);

        poolTargetSourceWebDriver.releaseTarget(driver);
        return new AsyncResult<ArrayList<String>>(links);
    }

    @Async("linkCollectorTaskExecutor")
    public Future<ArrayList<Article>> collectArticlesWithNoContentFromGoogleNews(Company company) throws Exception {
        String baseUrl = "https://news.google.com/news/search/section/q/";
        String url = baseUrl + company.getMarket() + "%3A" + company.getSymbol();

        WebDriver driver = (WebDriver) poolTargetSourceWebDriver.getTarget();
        driver.get(url);

        List<WebElement> links = driver.findElements(By.tagName("a"));

        ArrayList<Article> articles = new ArrayList<Article>();
        for (WebElement link : links) {
            try {
                WebElement parent = link.findElement(By.xpath("./.."));
                WebElement divChild = parent.findElements(By.tagName("div")).get(0);
                WebElement dateElement = divChild.findElements(By.tagName("span")).get(2);

                if (dateElement.getText() != null && !dateElement.getText().isEmpty()) {
                    String rawDate = dateElement.getText();
                    Date formatedDate = null;
                    if ("just now".equals(rawDate)) {
                        formatedDate = Calendar.getInstance().getTime();
                    } else if (rawDate.contains("h ago")) {
                        String hours = rawDate.split("h")[0];
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.HOUR, -(Integer.parseInt(hours)));
                        formatedDate = cal.getTime();
                    } else if (rawDate.contains("m ago")) {
                        String hours = rawDate.split("m")[0];
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.MINUTE, -(Integer.parseInt(hours)));
                        formatedDate = cal.getTime();
                    } else {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                        formatedDate = simpleDateFormat.parse(StringUtils.substring(rawDate, 0, StringUtils.indexOf(rawDate, "2017") + 4));
                    }

                    SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
                    log.info(outputFormat.format(formatedDate));
                    log.info(dateElement.getText() + " | " + link.getAttribute("href"));


                    articles.add(new Article(company.getSymbol(), link.getAttribute("href"), new Timestamp(formatedDate.getTime())));

                    // got date - now extract content -
//                        String content = ArticleExtractor.INSTANCE.getText(new URL(link.getAttribute("href")));

                }
            } catch (Exception ex) {
                // NoSuchElementException | IndexOutOfBoundsException  |
                log.error("ERRed", ex);
                System.out.println("ERRed");
            }
        }

        poolTargetSourceWebDriver.releaseTarget(driver);
        return new AsyncResult<ArrayList<Article>>(articles);
    }


    @Async("linkCollectorTaskExecutor")
    public Future<Article> collectContentFromArticle(Article article) throws Exception {

        WebDriver driver = (WebDriver) poolTargetSourceWebDriver.getTarget();
        try {
            log.info("Gathering content from article: " + article.getSymbol() + " | URL: " + article.getUrl());
            driver.get(article.getUrl());
            String pageSource = driver.getPageSource();
            String content = ArticleExtractor.INSTANCE.getText(pageSource);
            article.setContent(content);
        } catch (Exception ex) {
            log.error("ERROR in getting content", ex);
        } finally {
            poolTargetSourceWebDriver.releaseTarget(driver);
        }

        return new AsyncResult<Article>(article);
    }

    @Transactional
    @Async("linkCollectorTaskExecutor")
    public Future<Article> analyzeSentimentWithWatson(Article article){

        NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
                NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
                watsonUsername,
                watsonPassword);

        String url = article.getUrl();

        List<String> targets = new ArrayList<>();
        targets.add("stocks");

        SentimentOptions sentiment = new SentimentOptions.Builder().targets(targets)
                .build();

        Features features = new Features.Builder()
                .sentiment(sentiment)
                .build();

        AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                .url(url)
                .features(features)
                .build();

        AnalysisResults response = service
                .analyze(parameters)
                .execute();

        article.setScore(response.getSentiment().getDocument().getScore().floatValue());

        System.out.println(response);

        articleRepository.save(article);

        return new AsyncResult<Article>(article);

    }

    public Article analyzeSentimentWithGoogle(Article article) throws Exception {
        // analyze teh sentiment
        try (LanguageServiceClient language = LanguageServiceClient.create()) {

            if (article.getContent() != null && StringUtils.isNotEmpty(article.getContent())) {
                log.info("Sentimenet " + article.getSymbol() + " | Processing content | URL: " + article.getUrl());

                try {
                    // The text to analyze
                    Document doc = Document
                            .newBuilder()
                            .setContent(article.getContent())
                            .setType(Document.Type.PLAIN_TEXT)
                            .build();

                    // Detects the sentiment of the text
                    Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
                    article.setMagnitude(sentiment.getMagnitude());
                    article.setScore(sentiment.getScore());
                    log.info("Sentiment -  Symbol : " + article.getSymbol() + " | " + " Score : " + sentiment.getScore() + " | " + "Mag : " + sentiment.getMagnitude() + " | URL : " + article.getUrl());
                } catch (Exception e2) {
                    log.error("ERROR in getting analysis", e2);
                }

            }else{
                log.error("Sentiment : " + article.getSymbol() + " : There is no content to process in this article " + "| URL : " +  article.getUrl());
            }

            return article;
        }


    }
}