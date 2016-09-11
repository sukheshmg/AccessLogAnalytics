package sukhesh.accessloganalytics.storage;

import sukhesh.accessloganalytics.model.LogEntry;

import java.util.Collection;
import java.util.List;

/**
 * Created by sukhesh on 08/09/16.
 */
public interface RawDataStore extends DataStore{
    public void write(LogEntry entry);
    public int currentSize();

    public void addAggregatedDataStore(AggregatedDataStore store);

    Collection<List<LogEntry>> getAllEntries();
}
