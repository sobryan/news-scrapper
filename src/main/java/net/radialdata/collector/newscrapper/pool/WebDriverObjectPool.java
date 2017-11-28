package net.radialdata.collector.newscrapper.pool;

import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.utils.SelfRegisteringRemote;
import org.openqa.grid.internal.utils.configuration.GridHubConfiguration;
import org.openqa.grid.internal.utils.configuration.GridNodeConfiguration;
import org.openqa.grid.shared.GridNodeServer;
import org.openqa.grid.web.Hub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.server.SeleniumServer;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WebDriverObjectPool extends CommonsPool2TargetSource {


    @Value("${webdriver.chrome.driver}")
    private String chromeDriverPath;

    @Value("${webdriver.gecko.driver}")
    private String geckoDriverPath;

    public void initializeObjects() throws Exception {

        List<WebDriver> pool = new ArrayList<WebDriver>();
        initializeGridAndNode();

        for (int i = 0; i < this.getMinIdle(); i++) {
            pool.add((WebDriver) this.getTarget());
        }
        for (WebDriver instance : pool) {
            this.releaseTarget(instance);
        }
        pool.clear();
    }

    private void initializeGridAndNode() throws Exception {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        System.setProperty("webdriver.gecko.driver", geckoDriverPath);

        //  HUB Configuration - org.openqa.grid.internal.utils.configuration.GridHubConfiguration
        GridHubConfiguration gridHubConfig = new GridHubConfiguration();
        File hubJson = ResourceUtils.getFile("classpath:gridHub.json");
        gridHubConfig = gridHubConfig.loadFromJSON( hubJson.toString() );

        Hub hub = new Hub(gridHubConfig);
        hub.start();

        // initialize node
        // NODE Configuration - org.openqa.selenium.remote.server.SeleniumServer
        GridNodeConfiguration gridNodeConfiguration = new GridNodeConfiguration();
        File nodeJson = ResourceUtils.getFile("classpath:registerNode.json");
        gridNodeConfiguration = gridNodeConfiguration.loadFromJSON( nodeJson.toString() );
        RegistrationRequest request = new RegistrationRequest( gridNodeConfiguration );
        GridNodeServer node = new SeleniumServer( request.getConfiguration() );

        SelfRegisteringRemote remote = new SelfRegisteringRemote( request );
        remote.setRemoteServer( node );
        remote.startRemoteServer();
        remote.startRegistrationProcess();
        System.out.println("initialization grid completed");

    }

}
