package sukhesh.accessloganalytics.queryengine;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sukhesh.accessloganalytics.model.LogEntry;
import sukhesh.accessloganalytics.operations.AggregateOperation;
import sukhesh.accessloganalytics.querymodel.Function;
import sukhesh.accessloganalytics.storage.AggregatedDataStore;
import sukhesh.accessloganalytics.storage.TimeBasedAggregatedDataStore;
import sukhesh.accessloganalytics.util.BeanLookupHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by sukhesh on 09/09/16.
 */
public class QueryEngineImpl implements QueryEngine {

    private static final Logger logger = LoggerFactory.getLogger(QueryEngineImpl.class);

    @Override
    public Map<List<Object>, List<Double>>  getAggregatedData(String[] dimensions, Function[] metrics, AggregatedDataStore aggregatedDataStore) {
        if(aggregatedDataStore == null) {
            aggregatedDataStore = AggregateDataStoreMapper.getAggregateDataStore(dimensions, metrics);
        }
        Collection<List<LogEntry>> collection = null;
        if(aggregatedDataStore != null) {
            String[] dim = {dimensions[0]};
            collection = aggregatedDataStore.getGroupedEntries(dim);
        }
        try {
            collection = doGroupBys(collection, dimensions, 1);
        } catch (InvocationTargetException e) {
            logger.error("InvocationTargetException", e);
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException", e);
        }
        return doGetAggregatedData(collection, metrics, dimensions);
    }

    @Override
    public Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, DateTime start) {
        TimeBasedAggregatedDataStore aggregatedDataStore = BeanLookupHelper.INSTANCE.getTimeBasedAggregatedDataStore();
        Collection<List<LogEntry>> collection = null;
        if(aggregatedDataStore != null) {
            collection = aggregatedDataStore.getEntriesAfterTime(start);
        }
        try {
            collection = doGroupBys(collection, dimensions, 0);
        } catch (InvocationTargetException e) {
            logger.error("InvocationTargetException", e);
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException", e);
        }
        return doGetAggregatedData(collection, metrics, dimensions);
    }

    @Override
    public Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, DateTime start, DateTime end) {
        TimeBasedAggregatedDataStore aggregatedDataStore = BeanLookupHelper.INSTANCE.getTimeBasedAggregatedDataStore();
        Collection<List<LogEntry>> collection = null;
        if(aggregatedDataStore != null) {
            collection = aggregatedDataStore.getEntriesBetweenTime(start, end);
        }
        try {
            collection = doGroupBys(collection, dimensions, 0);
        } catch (InvocationTargetException e) {
            logger.error("InvocationTargetException", e);
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException", e);
        }
        return doGetAggregatedData(collection, metrics, dimensions);
    }


    public Map<List<Object>, List<Double>>  testgetAggregatedData(Collection<List<LogEntry>> collection, String[] dimensions, Function[] metrics) {
        try {
            collection = doGroupBys(collection, dimensions, 0);
        } catch (InvocationTargetException e) {
            logger.error("InvocationTargetException", e);
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException", e);
        }
        return doGetAggregatedData(collection, metrics, dimensions);
    }

    private Collection<List<LogEntry>> doGroupBys(Collection<List<LogEntry>> collection, String[] dimensions, int index) throws InvocationTargetException, IllegalAccessException {
        if(index >= dimensions.length) {
            return collection;
        }

        String dimension = dimensions[index];
        String _method = "get" + dimension;
        Method method = null;

        try {
            method = LogEntry.class.getMethod(_method);
        } catch (NoSuchMethodException e) {
            logger.error("Wrong dimension", e );
            return collection;
        }

        Collection<List<LogEntry>> res = new LinkedList<>();

        for(List<LogEntry> list : collection) {
            while (list.size() != 0) {
                List<LogEntry> l = new LinkedList<>();
                LogEntry removed = list.remove(0);
                l.add(removed);

                int i=0;
                while (list.size() != 0 && i<list.size()) {
                    LogEntry e = list.get(i);
                    if(method.invoke(removed).equals(method.invoke(e))) {
                        list.remove(e);
                        l.add(e);
                    } else {
                        i++;
                    }
                }
                res.add(l);
            }
        }
        return doGroupBys(res, dimensions, index+1);
    }


    Map<List<Object>, List<Double>> doGetAggregatedData(Collection<List<LogEntry>> groupedEntries, Function[] metrics, String[] dimensions) {
        Map<List<Object>, List<Double>> ret = new HashMap<>();

        for(List<LogEntry> logEntries:groupedEntries) {
            LinkedList<Double> lst = new LinkedList<>();
            LinkedList<Object> key = new LinkedList<>();
            LogEntry entry = logEntries.get(0);
            for(String dimension : dimensions) {
                String _method = "get" + dimension;
                Method method = null;

                try {
                    method = LogEntry.class.getMethod(_method);
                } catch (NoSuchMethodException e) {
                    logger.error("Wrong dimension", e );
                    return null;
                }

                try {
                    key.addLast(method.invoke(entry));
                } catch (IllegalAccessException e) {
                    logger.error("IllegaAccessException", e);
                    return null;
                } catch (InvocationTargetException e) {
                    logger.error("InvocationTargetException", e);
                    return null;
                }
            }
            for(Function function:metrics) {
                String operation = function.getOperation();
                String metric = function.getMetric();
                Double res = new AggregateOperation().calculateOverGroupOfEntries(logEntries, operation, metric);
                lst.addLast(res);
            }
            ret.put(key, lst);
        }
        return ret;
    }
}
