package sukhesh.accessloganalytics.storage;

import sukhesh.accessloganalytics.model.LogEntry;
import sukhesh.accessloganalytics.querymodel.Function;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by sukhesh on 08/09/16.
 */
public interface AggregatedDataStore {
    void write(LogEntry entry);
    void remove(LogEntry entry);
    Collection<List<LogEntry>> getGroupedEntries(String[] dimensions);
    Map<Object, List<Double>> getGroupedAggregatedEntries(String[] dimensions, Function[] metrics);
    List<String> getDimensions();

    public int currentSize();
}
