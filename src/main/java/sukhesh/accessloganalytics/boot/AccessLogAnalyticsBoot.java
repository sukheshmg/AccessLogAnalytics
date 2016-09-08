package sukhesh.accessloganalytics.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import sukhesh.accessloganalytics.config.ConfigLoader;

/**
 * Created by sukhesh on 08/09/16.
 */
@ComponentScan(basePackages = "sukhesh.accessloganalytics")
@EnableAutoConfiguration
@SpringBootApplication
public class AccessLogAnalyticsBoot {
    private static final Logger logger = LoggerFactory.getLogger(AccessLogAnalyticsBoot.class);

    public static void main(String[] args) {

        ConfigLoader.INSTANCE.loadConfig();

        ConfigurableApplicationContext applicationContext =
                SpringApplication.run(AccessLogAnalyticsBoot.class);
    }
}
