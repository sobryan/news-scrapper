package net.radialdata.collector.newscrapper;

import net.radialdata.collector.newscrapper.data.article.Article;
import net.radialdata.collector.newscrapper.data.article.ArticleRepository;
import net.radialdata.collector.newscrapper.data.company.Company;
import net.radialdata.collector.newscrapper.service.BrowserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Autowired
    BrowserService browserService;

    @Autowired
    ArticleRepository articleRepository;

    @Override
    public void run(String... args) throws Exception {


        Company company = new Company();
        company.setSymbol("F");
        company.setMarket("NYSE");

        Future<ArrayList<Article>> articlesFuture = browserService.collectArticlesWithNoContentFromGoogleNews(company);
        ArrayList<Article> articles = articlesFuture.get();


        if(articlesFuture.isDone()){
            System.out.println("Compelted");
        }

        List<Article> tempArticles = articles.subList(0,9);

        for(Article article:tempArticles){
            browserService.analyzeSentimentWithWatson(article).get();

        }


        System.out.println("Completed");








        // nlp demo
//            Properties nlpProps = new Properties();
//            nlpProps.put("annotators","tokenize, ssplit, parse, sentiment");
//            StanfordCoreNLP pipeline = new StanfordCoreNLP(nlpProps);

//            String content = "This is the best content ever created!";
//            int mainSentiment = 0;
//            int longest = 0;
//            Annotation annotation = pipeline.process(content);
//            for (CoreMap sentence : annotation
//                    .get(CoreAnnotations.SentencesAnnotation.class)) {
//                Tree tree = sentence
//                        .get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
//                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
//                String partText = sentence.toString();
//                if (partText.length() > longest) {
//                    mainSentiment = sentiment;
//                    longest = partText.length();
//                }
//
//            }

    }

}
