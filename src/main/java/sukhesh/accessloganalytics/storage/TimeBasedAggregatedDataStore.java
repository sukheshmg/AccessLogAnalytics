package sukhesh.accessloganalytics.storage;

import sukhesh.accessloganalytics.model.LogEntry;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by sukhesh on 08/09/16.
 */
public enum  TimeBasedAggregatedDataStore implements AggregatedDataStore {
    INSTANCE;

    TreeMap<Date, List<LogEntry>> entries = new TreeMap<>();

    @Override
    public void write(LogEntry entry) {
        if(entries.containsKey(entry.getDate())) {
            entries.get(entry.getDate()).add(entry);
        } else {
            List<LogEntry> lst = new LinkedList<>();
            lst.add(entry);
            entries.put(entry.getDate(), lst);
        }
    }

    @Override
    public void remove(LogEntry entry) {
        List<LogEntry> lst = entries.get(entry.getDate());
        if(lst != null) {
            lst.remove(entry);
        }
    }
}
