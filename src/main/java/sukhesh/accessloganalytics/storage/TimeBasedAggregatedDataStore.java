package sukhesh.accessloganalytics.storage;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sukhesh.accessloganalytics.model.LogEntry;
import sukhesh.accessloganalytics.operations.AggregateOperation;
import sukhesh.accessloganalytics.querymodel.Function;
import sukhesh.accessloganalytics.util.BeanLookupHelper;
import sukhesh.accessloganalytics.util.Util;

import java.util.*;

/**
 * time based aggregator
 * provides time range query capabilities
 *
 * Created by sukhesh on 08/09/16.
 */
@Component
public class TimeBasedAggregatedDataStore implements AggregatedDataStore {

    private static final Logger logger = LoggerFactory.getLogger(TimeBasedAggregatedDataStore.class);

    TreeMap < DateTime, List < LogEntry >> entries = new TreeMap<>();

    @Override
    public synchronized void write(LogEntry entry) {
        if(entries.containsKey(entry.getDate())) {
            entries.get(entry.getDate()).add(entry);
        } else {
            List<LogEntry> lst = new LinkedList<>();
            lst.add(entry);
            entries.put(entry.getDate(), lst);
        }
    }

    @Override
    public synchronized void remove(LogEntry entry) {
        List<LogEntry> lst = entries.get(entry.getDate());
        if(lst != null) {
            lst.remove(entry);
        }
    }

    @Override
    public synchronized Collection<List<LogEntry>> getGroupedEntries(String[] dimensions) {
        if(dimensions == null || dimensions.length == 0 || !dimensions[0].equals("Date")) {
            logger.error("Time based aggregation accepts only time as dimension");
            return null;
        }

        return Util.deepCopy(entries.values());

    }



    @Override
    public synchronized Map<Object, List<Double>> getGroupedAggregatedEntries(String[] dimensions, Function[] metrics) {
        Collection<List<LogEntry>> groupedEntries = getGroupedEntries(dimensions);
        if(dimensions == null) {
            logger.error("Could not get grouped entries");
            return null;
        }
        Map<Object, List<Double>> ret = new HashMap<>();

        for(List<LogEntry> logEntries:groupedEntries) {
            LinkedList<Double> lst = new LinkedList<>();
            for(Function function:metrics) {
                String operation = function.getOperation();
                String metric = function.getMetric();
                Double res = new AggregateOperation().calculateOverGroupOfEntries(logEntries, operation, metric);
                lst.addLast(res);
            }
            ret.put(logEntries.get(0).getDate(), lst);
        }
        return ret;
    }

    @Override
    public List<String> getDimensions() {
        List<String> res = new LinkedList<>();
        res.add("Date");
        return res;
    }

    @Override
    public synchronized int currentSize() {
        return entries.size();
    }

    /**
     * entries after a time
     * returns a collection which contains a single list
     * @param start
     * @return
     */
    public synchronized Collection<List<LogEntry>> getEntriesAfterTime(DateTime start) {
        DateTime leastKey = entries.ceilingKey(start);
        if(leastKey == null) {
            return new LinkedList<>();
        }
        Map<DateTime, List<LogEntry>> tailMap = entries.tailMap(leastKey);
        Collection<List<LogEntry>> ret = new LinkedList<>();
        List<LogEntry> theOnlyList = new LinkedList<>();
        for(List<LogEntry> l : tailMap.values()) {
            for(LogEntry logEntry:l) {
                theOnlyList.add(logEntry);
            }
        }
        ret.add(theOnlyList);
        return Util.deepCopy(ret);
    }

    public synchronized Collection<List<LogEntry>> getEntriesBetweenTime(DateTime start, DateTime end) {
        DateTime leastKey = entries.ceilingKey(start);
        DateTime highestKey = entries.floorKey(end.plusSeconds(1));
        if(leastKey == null || highestKey == null) {
            return new LinkedList<>();
        }
        Map<DateTime, List<LogEntry>> subMap = entries.subMap(leastKey, true, highestKey, true);
        Collection<List<LogEntry>> ret = new LinkedList<>();
        List<LogEntry> theOnlyList = new LinkedList<>();
        for(List<LogEntry> l:subMap.values()) {
            for(LogEntry logEntry:l) {
                theOnlyList.add(logEntry);
            }
        }
        ret.add(theOnlyList);
        return Util.deepCopy(ret);
    }
}
