package sukhesh.accessloganalytics.util;

import org.springframework.context.ConfigurableApplicationContext;
import sukhesh.accessloganalytics.readpipeline.ReadPipeline;
import sukhesh.accessloganalytics.storage.AggregatedDataStore;
import sukhesh.accessloganalytics.storage.RawDataStore;
import sukhesh.accessloganalytics.storage.TimeBasedAggregatedDataStore;

/**
 * Created by sukhesh on 08/09/16.
 */
public enum BeanLookupHelper {
    INSTANCE;

    ConfigurableApplicationContext context;

    public void init(ConfigurableApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    public RawDataStore getRawDataStore() {
        return (RawDataStore) context.getBean("inMemoryRawDataStore");
    }

    public TimeBasedAggregatedDataStore getTimeBasedAggregatedDataStore() {
        return (TimeBasedAggregatedDataStore) context.getBean("timeBasedAggregatedDataStore");
    }

    public ReadPipeline getReadPipeline() {
        return (ReadPipeline) context.getBean("readPipeline");
    }
}
