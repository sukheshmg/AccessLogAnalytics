package sukhesh.accessloganalytics.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import sukhesh.accessloganalytics.commandlinetool.AccessLogAnalysis;
import sukhesh.accessloganalytics.config.ConfigLoader;
import sukhesh.accessloganalytics.storage.InMemoryRawDataStore;
import sukhesh.accessloganalytics.storage.TimeBasedAggregatedDataStore;
import sukhesh.accessloganalytics.taskmanager.TaskManager;
import sukhesh.accessloganalytics.util.BeanLookupHelper;

import javax.xml.bind.ValidationException;

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

        BeanLookupHelper.INSTANCE.init(applicationContext);

        TaskManager.INSTANCE.startReading();

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
