package sukhesh.accessloganalytics.operations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sukhesh.accessloganalytics.model.LogEntry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * utility to calculate the given metric over a list of log entries
 * uses reflection to get the values. so, the individual metric fields need to be verified to be valid beforehand
 * Created by sukhesh on 09/09/16.
 */
public class AggregateOperation {
    private static final Logger logger = LoggerFactory.getLogger(AggregateOperation.class);

    public double calculateOverGroupOfEntries(List<LogEntry> entries, String operation, String metric) {
        if(entries == null || entries.size() == 0) {
            return 0.0;
        }
        double res = 0.0;
        for(LogEntry entry : entries) {
            if(operation.equals("sum")) {
                res += getValueFromEntry(entry, metric);
            } else if(operation.equals("avg")) {
                res += getValueFromEntry(entry, metric);
            } else if(operation.equals("count")) {
                res += 1;
            }
        }
        if(operation.equals("avg")) {
            res = res/entries.size();
        }
        return res;
    }

    private int getValueFromEntry(LogEntry entry, String metric) {
        String name = "get" + metric;
        Method method = null;
        try {
            method = entry.getClass().getMethod(name);
        } catch (SecurityException e) {
            logger.error("Security exception", e);
            return 0;
        } catch (NoSuchMethodException e) {
            logger.error("Invalid metric name", e);
            return 0;
        }
        int res = 0;
        try {
            res = (int) method.invoke(entry);
        } catch (IllegalAccessException e) {
            logger.error("IllegalAcceeException", e);
        }catch (InvocationTargetException e) {
            logger.error("InvocationTargetException", e);
        }
        return res;
    }
}
