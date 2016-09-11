package sukhesh.accessloganalytics.queryengine;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sukhesh.accessloganalytics.model.LogEntry;
import sukhesh.accessloganalytics.operations.AggregateOperation;
import sukhesh.accessloganalytics.querymodel.Function;
import sukhesh.accessloganalytics.storage.AggregatedDataStore;
import sukhesh.accessloganalytics.storage.InMemoryRawDataStore;
import sukhesh.accessloganalytics.storage.RawDataStore;
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
    public Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, DateTime start, boolean sortAscending, int metricIndexToSort) {
        return null;
    }

    @Override
    public Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, DateTime start, boolean sortAscending, int metricIndexToSort, int limit) {
        return null;
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
            return null;
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException", e);
            return null;
        }
        return doGetAggregatedData(collection, metrics, dimensions);
    }

    @Override
    public Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, DateTime start, DateTime end, boolean sortAscending, int metricIndexToStart) {
        return null;
    }

    @Override
    public Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, DateTime start, DateTime end, boolean sortAscending, int metricIndexToStart, int limit) {
        return null;
    }

    @Override
    public Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics) {
        Collection<List<LogEntry>> collection = BeanLookupHelper.INSTANCE.getRawDataStore().getAllEntries();

        try {
            collection = doGroupBys(collection, dimensions, 0);
        } catch (InvocationTargetException e) {
            logger.error("InvocationTargetException", e);
            return null;
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException", e);
            return null;
        }

        return doGetAggregatedData(collection, metrics, dimensions);
    }

    @Override
    public Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, RawDataStore inMemoryRawDataStore) {
        Collection<List<LogEntry>> collection = inMemoryRawDataStore.getAllEntries();

        try {
            collection = doGroupBys(collection, dimensions, 0);
        } catch (InvocationTargetException e) {
            logger.error("InvocationTargetException", e);
            return null;
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException", e);
            return null;
        }

        return doGetAggregatedData(collection, metrics, dimensions);
    }

    @Override
    public Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, final boolean sortAscending, final int metricIndexToSort) {
        Map<List<Object>, List<Double>> map = getAggregatedData(dimensions, metrics);
        return doSort(map, metricIndexToSort, sortAscending);
    }

    private Map<List<Object>, List<Double>> doSort(Map<List<Object>, List<Double>> map, int index, boolean sort) {
        Set<Map.Entry<List<Object>, List<Double>>> entrySet = map.entrySet();
        List<Map.Entry<List<Object>, List<Double>>> entryList = new LinkedList<>(entrySet);
        final int metricIndexToSort = index;
        final boolean sortAscending = sort;
        Collections.sort(entryList, new Comparator<Map.Entry<List<Object>, List<Double>>>() {
            @Override
            public int compare(Map.Entry<List<Object>, List<Double>> o1, Map.Entry<List<Object>, List<Double>> o2) {
                if(o1.getValue().get(metricIndexToSort) > o2.getValue().get(metricIndexToSort)) {
                    if(sortAscending) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if(o1.getValue().get(metricIndexToSort) < o2.getValue().get(metricIndexToSort)) {
                    if(sortAscending) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                return 0;
            }
        });

        Map<List<Object>, List <Double>> ret = new LinkedHashMap<>();
        for(Map.Entry<List<Object>, List<Double>> e : entryList) {
            ret.put(e.getKey(), e.getValue());
        }

        return ret;
    }

    @Override
    public Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, boolean sortAscending, int metricIndexToSort,  RawDataStore inMemoryRawDataStore) {
        Map<List<Object>, List<Double>> map = getAggregatedData(dimensions, metrics, inMemoryRawDataStore);
        return doSort(map, metricIndexToSort, sortAscending);
    }

    @Override
    public Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, boolean sortAscending, int metricIndexToSort, int limit) {
        Map<List<Object>, List<Double>> map = getAggregatedData(dimensions, metrics, sortAscending, metricIndexToSort);
        return doLimitToCount(map, limit);
    }

    private Map<List<Object>, List<Double>> doLimitToCount(Map<List<Object>, List<Double>> map, int limit) {
        Map<List<Object>, List<Double>> ret = new LinkedHashMap<>();

        Set<List<Object>> keySet = map.keySet();
        if(keySet.size() < limit) {
            limit = keySet.size();
        }

        int count=0;
        for(List<Object> key : keySet) {
            if(count < limit) {
                ret.put(key, map.get(key));
            } else {
                break;
            }
            count++;
        }
        return ret;
    }

    @Override
    public Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, boolean sortAscending, int metricIndexToSort, int limit, RawDataStore inMemoryRawDataStore) {
        Map<List<Object>, List<Double>> map = getAggregatedData(dimensions, metrics, sortAscending, metricIndexToSort, inMemoryRawDataStore);
        return doLimitToCount(map, limit);
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
