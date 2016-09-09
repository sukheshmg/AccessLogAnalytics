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

import java.util.*;

/**
 * Created by sukhesh on 08/09/16.
 */
@Component
public class TimeBasedAggregatedDataStore implements AggregatedDataStore {

    private static final Logger logger = LoggerFactory.getLogger(TimeBasedAggregatedDataStore.class);

    TreeMap < DateTime, List < LogEntry >> entries = new TreeMap<>();

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

    @Override
    public Collection<List<LogEntry>> getGroupedEntries(String[] dimensions) {
        if(dimensions == null || dimensions.length != 1 || !dimensions[0].equals("time")) {
            logger.error("Time based aggregation accepts only time as dimension");
            return null;
        }

        return entries.values();
    }

    @Override
    public Map<Object, List<Double>> getGroupedAggregatedEntries(String[] dimensions, Function[] metrics) {
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
    public int currentSize() {
        return entries.size();
    }
}
