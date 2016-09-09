package sukhesh.accessloganalytics.taskmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sukhesh.accessloganalytics.readpipeline.ReadPipeline;
import sukhesh.accessloganalytics.util.BeanLookupHelper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by sukhesh on 08/09/16.
 */
public enum TaskManager {
    INSTANCE;

    Executor readTasks = Executors.newFixedThreadPool(1);
    public void startReading() {
        BeanLookupHelper.INSTANCE.getRawDataStore().addAggregatedDataStore(BeanLookupHelper.INSTANCE.getTimeBasedAggregatedDataStore());
        readTasks.execute(BeanLookupHelper.INSTANCE.getReadPipeline());
    }
}
