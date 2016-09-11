package sukhesh.accessloganalytics.queryengine;

import org.joda.time.DateTime;
import sukhesh.accessloganalytics.querymodel.Function;
import sukhesh.accessloganalytics.storage.AggregatedDataStore;
import sukhesh.accessloganalytics.storage.InMemoryRawDataStore;
import sukhesh.accessloganalytics.storage.RawDataStore;

import java.util.List;
import java.util.Map;

/**
 * Created by sukhesh on 09/09/16.
 */
public interface QueryEngine {
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, AggregatedDataStore aggregatedDataStore);
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, DateTime start);
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, DateTime start, boolean sortAscending, int metricIndexToSort);
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, DateTime start, boolean sortAscending, int metricIndexToSort, int limit);
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, DateTime start, DateTime end);
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, DateTime start, DateTime end, boolean sortAscending, int metricIndexToStart);
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, DateTime start, DateTime end, boolean sortAscending, int metricIndexToStart, int limit);
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics);
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, RawDataStore inMemoryRawDataStore);
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, boolean sortAscending, int metricIndexToSort);
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, boolean sortAscending, int metricIndexToSort, RawDataStore inMemoryRawDataStore);
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, boolean sortAscending, int metricIndexToSort, int limit);
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, boolean sortAscending, int metricIndexToSort, int limit,  RawDataStore inMemoryRawDataStore);
}
