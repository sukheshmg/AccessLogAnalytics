package sukhesh.accessloganalytics.queryengine;

import org.joda.time.DateTime;
import sukhesh.accessloganalytics.querymodel.Function;
import sukhesh.accessloganalytics.storage.AggregatedDataStore;

import java.util.List;
import java.util.Map;

/**
 * Created by sukhesh on 09/09/16.
 */
public interface QueryEngine {
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, AggregatedDataStore aggregatedDataStore);
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, DateTime start);
    Map<List<Object>, List<Double>> getAggregatedData(String[] dimensions, Function[] metrics, DateTime start, DateTime end);
}
