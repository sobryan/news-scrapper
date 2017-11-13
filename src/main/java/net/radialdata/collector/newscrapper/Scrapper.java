package net.radialdata.collector.newscrapper;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.System;
import java.util.List;

/**
 * Created by morbo on 11/12/17.
 */
@SpringBootApplication
public class Scrapper implements CommandLineRunner{

    private static final Logger log = LoggerFactory.getLogger(Scrapper.class);

    @Autowired
    CompanyRepository companyRepository;

    public static void main(String[] args) {
        SpringApplication.run(Scrapper.class);
    }

    @Override
    public void run(String... args) throws Exception {

        ChromeDriver driver = null;
        try {
            System.out.println("Starting the browers");
            System.setProperty("webdriver.chrome.driver", "/home/morbo/Documents/webdrivers/current/chromedriver");
            driver = new ChromeDriver();

//            Iterable<Company> companies = companyRepository.findAll();

            Company ford = companyRepository.findBySymbol("F");

//            String baseUrl = "https://finance.google.com/finance/company_news?q=NYSE%3A";
//            String baseUrl = "https://news.google.com/news/search/section/q/";

            String baseUrl = "https://news.google.com/news/search/section/q/NYSE%3A";
            String url = baseUrl + ford.getSymbol();

            driver.get(url);
            List<WebElement> links = driver.findElements(By.tagName("a"));
            for(WebElement link : links){
                try {
                    WebElement dateElement = link.findElement(By.xpath("./..")).findElements(By.tagName("div")).get(0).findElements(By.tagName("span")).get(2);
                    if(dateElement.getText() != null && !dateElement.getText().isEmpty())
                        System.out.println(dateElement.getText() + " | " + link.getAttribute("href"));
                }catch(NoSuchElementException | IndexOutOfBoundsException ex){

                }
            }

            System.out.println("Started the browser");
        }catch(Exception ex){
            System.out.println("Error occured");
            ex.printStackTrace();
        }finally {
            if(driver != null) {
                System.out.println("Closing the browser");
                driver.quit();
            }
        }

    }
}
