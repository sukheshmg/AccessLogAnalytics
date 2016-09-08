package sukhesh.accessloganalytics.storage;

import sukhesh.accessloganalytics.model.LogEntry;

/**
 * Created by sukhesh on 08/09/16.
 */
public interface RawDataStore extends DataStore{
    public void write(LogEntry entry);

    public void addAggregatedDataStore(AggregatedDataStore store);
}
