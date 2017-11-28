package net.radialdata.collector.newscrapper.pool;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class BrowserPoolConfig {

    @Bean(name = "webDriver")
    @Scope("prototype")
    public WebDriver webDriver() throws MalformedURLException {
        URL remoteURL = new URL(String.format("http://%s:%d/wd/hub", "127.0.0.1", 4444) );
        System.out.println("Remote URL : "+remoteURL);
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        WebDriver driver = new RemoteWebDriver(remoteURL,capabilities);
        return driver;
    }

    @Bean(name = "poolTargetSourceWebDriver", initMethod = "initializeObjects")
    WebDriverObjectPool myObjectPool() {
        WebDriverObjectPool pool = new WebDriverObjectPool();
        pool.setMaxSize(15);
        pool.setTargetClass(WebDriver.class);
        pool.setMinIdle(15);
        pool.setTargetBeanName("webDriver");
        return pool;
    }

    @Bean
    ProxyFactoryBean proxyFactoryBean() {
        ProxyFactoryBean factoryBean = new ProxyFactoryBean();
        factoryBean.setTargetSource(myObjectPool());
        return factoryBean;
    }
}