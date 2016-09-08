package sukhesh.accessloganalytics.util;

import org.springframework.context.ConfigurableApplicationContext;
import sukhesh.accessloganalytics.storage.RawDataStore;

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
}
