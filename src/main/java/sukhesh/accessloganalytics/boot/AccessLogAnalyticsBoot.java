package sukhesh.accessloganalytics.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import sukhesh.accessloganalytics.commandlinetool.AccessLogAnalysis;
import sukhesh.accessloganalytics.config.ConfigLoader;
import sukhesh.accessloganalytics.config.GlobalConfig;
import sukhesh.accessloganalytics.taskmanager.TaskManager;
import sukhesh.accessloganalytics.util.BeanLookupHelper;

import javax.xml.bind.ValidationException;

/**
 * Created by sukhesh on 08/09/16.
 */

/**
 * main entry point for the service
 * starts the service on tomcat app server
 */
@ComponentScan(basePackages = "sukhesh.accessloganalytics")
@EnableAutoConfiguration
@SpringBootApplication
public class AccessLogAnalyticsBoot {


    private static final Logger logger = LoggerFactory.getLogger(AccessLogAnalyticsBoot.class);

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory(GlobalConfig.INSTANCE.getServicePort());
        return factory;
    }

    public static void main(String[] args) {

        ConfigLoader.INSTANCE.loadConfig();

        ConfigurableApplicationContext applicationContext =
                SpringApplication.run(AccessLogAnalyticsBoot.class);

        /**
         * store all the components for further fetching
         */
        BeanLookupHelper.INSTANCE.init(applicationContext);

        TaskManager.INSTANCE.startReading();

        /**
         * go in a loop asking for input and giving output
         */
        while (true) {
            try {
                AccessLogAnalysis.run();
            } catch (ValidationException e) {
                System.out.println("Validation exception. " + e.getMessage());
            } catch (Throwable t) {
                System.out.println("Exception while processing request. " + t.getMessage());
            }
        }
    }
}
