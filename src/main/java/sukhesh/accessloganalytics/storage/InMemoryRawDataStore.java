package sukhesh.accessloganalytics.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sukhesh.accessloganalytics.config.GlobalConfig;
import sukhesh.accessloganalytics.model.LogEntry;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import sukhesh.accessloganalytics.util.BeanLookupHelper;
import sukhesh.accessloganalytics.util.Util;

/**
 * Created by sukhesh on 08/09/16.
 */
@Component
public class InMemoryRawDataStore implements RawDataStore {

    Queue<LogEntry> entries = new ConcurrentLinkedQueue<>();
    AtomicInteger counter = new AtomicInteger();

    List<AggregatedDataStore> aggregatedDataStores = new LinkedList<>();

    @Override
    public void write(LogEntry entry) {
        if(counter.addAndGet(1) > GlobalConfig.INSTANCE.getMaxRecordsInMemory()) {
            entries.add(entry);
            LogEntry removed = entries.remove();
            for(AggregatedDataStore store:aggregatedDataStores) {
                store.remove(removed);
            }
        } else {
            entries.add(entry);
        }

        for(AggregatedDataStore store:aggregatedDataStores) {
            store.write(entry);
        }
    }

    @Override
    public int currentSize() {
        return entries.size();
    }

    @Override
    public void addAggregatedDataStore(AggregatedDataStore store) {
        aggregatedDataStores.add(store);
    }

    @Override
    public Collection<List<LogEntry>> getAllEntries() {
        Collection<List<LogEntry>> ret = new LinkedList<>();
        List<LogEntry> lst = new LinkedList<>();
        lst.addAll(entries);
        ret.add(lst);
        return Util.deepCopy(ret);
    }

}
