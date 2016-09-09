package sukhesh.accessloganalytics.storage;

import sukhesh.accessloganalytics.model.LogEntry;

/**
 * Created by sukhesh on 08/09/16.
 */
public interface AggregatedDataStore {
    void write(LogEntry entry);
    void remove(LogEntry entry);
    public int currentSize();
}
