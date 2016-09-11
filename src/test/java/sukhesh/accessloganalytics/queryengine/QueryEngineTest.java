package sukhesh.accessloganalytics.queryengine;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import sukhesh.accessloganalytics.LogEntryParser;
import sukhesh.accessloganalytics.model.LogEntry;
import sukhesh.accessloganalytics.querymodel.Function;
import sukhesh.accessloganalytics.storage.AggregatedDataStore;
import sukhesh.accessloganalytics.storage.InMemoryRawDataStore;
import sukhesh.accessloganalytics.storage.RawDataStore;
import sukhesh.accessloganalytics.storage.TimeBasedAggregatedDataStore;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sukhesh on 09/09/16.
 */
public class QueryEngineTest {
    static LogEntry e1;
    static LogEntry e2;
    static LogEntry e3;
    static LogEntry e4;
    static RawDataStore rawDataStore = new InMemoryRawDataStore();
    static AggregatedDataStore timeBased = new TimeBasedAggregatedDataStore();

    @BeforeClass
    public static void setup(){
        e1 = new LogEntryParser().parse("83.167.113.100 - - [12/Dec/2015:18:31:25 +0100] \"GET /administrator/ HTTP/1.1\" 200 4263 \"-\" \"Mozilla/5.0 (Windows NT 6.0; rv:34.0) Gecko/20100101 Firefox/34.0\" \"-\"");
        e2 = new LogEntryParser().parse("83.167.113.100 - - [12/Dec/2015:18:31:25 +0100] \"GET /administrator/ HTTP/1.1\" 200 4263 \"-\" \"Mozilla/5.0 (Windows NT 6.0; rv:34.0) Gecko/20100101 Firefox/34.0\" \"-\"");
        e3 = new LogEntryParser().parse("83.167.113.100 - - [12/Dec/2015:18:31:26 +0100] \"GET /administrator/ HTTP/1.1\" 200 4263 \"-\" \"Mozilla/5.0 (Windows NT 6.0; rv:34.0) Gecko/20100101 Firefox/34.0\" \"-\"");
        e4 = new LogEntryParser().parse("83.167.113.100 - - [12/Dec/2015:18:31:27 +0100] \"POST /administrator/ HTTP/1.1\" 200 4263 \"-\" \"Mozilla/5.0 (Windows NT 6.0; rv:34.0) Gecko/20100101 Firefox/34.0\" \"-\"");

        rawDataStore.addAggregatedDataStore(timeBased);

        rawDataStore.write(e1);
        rawDataStore.write(e2);
        rawDataStore.write(e3);
        rawDataStore.write(e4);
    }

    @Test
    public void testQueryEngineDateGrouping() {
        String[] dimensions = {"Date", "Resource", "Verb"};
        Function f1 = new Function("sum", "Size");
        Function f2 = new Function("avg", "Size");
        Function f3 = new Function("sum", "ResponseCode");
        Function f4 = new Function("avg", "ResponseCode");
        Function f5 = new Function("count", null);
        Function[] metrics = {f1,f2,f3,f4, f5};

        Map<List<Object>, List<Double>> res = new QueryEngineImpl().getAggregatedData(dimensions, metrics, timeBased);
        Assert.assertEquals(3, res.size());
        Set<List<Object>> keys = res.keySet();
        Assert.assertEquals(3, keys.size());

        for(List<Object> key:keys) {
            Assert.assertEquals(3, key.size());
        }
    }

    @Test
    public void testQueryEngineGroupByResource() {
        String[] dimensions = {"Resource"};
        Function f1 = new Function("sum", "Size");
        Function f2 = new Function("avg", "Size");
        Function f3 = new Function("sum", "ResponseCode");
        Function f4 = new Function("avg", "ResponseCode");
        Function f5 = new Function("count", null);
        Function[] metrics = {f1,f2,f3,f4, f5};

        Map<List<Object>, List<Double>> res = new QueryEngineImpl().getAggregatedData(dimensions, metrics, rawDataStore);

        Assert.assertEquals(1, res.size());

        Set<List<Object>> keys = res.keySet();
        List<Object> key = null;
        for(List<Object> k:keys) {
            key = k;
        }
        Assert.assertNotNull(key);
        String resource = (String) key.get(0);

        Assert.assertEquals("/administrator/", resource);

        List<Double> vals = res.get(key);
        Assert.assertEquals(17052.0, (double) vals.get(0), 0);

    }

    @Test
    public void testQueryEngineGroupByResourceAndVerb() {
        String[] dimensions = {"Resource", "Verb"};
        Function f1 = new Function("sum", "Size");
        Function f2 = new Function("avg", "Size");
        Function f3 = new Function("sum", "ResponseCode");
        Function f4 = new Function("avg", "ResponseCode");
        Function f5 = new Function("count", null);
        Function[] metrics = {f1,f2,f3,f4, f5};

        Map<List<Object>, List<Double>> res = new QueryEngineImpl().getAggregatedData(dimensions, metrics, rawDataStore);

        Assert.assertEquals(2, res.size());

        Set<List<Object>> keys = res.keySet();
        List<Object> key = null;
        for(List<Object> k:keys) {
            key = k;
        }
        Assert.assertNotNull(key);
        String resource = (String) key.get(0);
        String verb = (String) key.get(1);

        Assert.assertEquals("/administrator/", resource);
        Assert.assertEquals("GET", verb);

        List<Double> vals = res.get(key);
        Assert.assertEquals(12789.0, (double) vals.get(0), 0);

    }

    @Test
    public void testQueryEngineGroupByResourceAndVerbSortAscendingByCount() {
        String[] dimensions = {"Resource", "Verb"};
        Function f1 = new Function("sum", "Size");
        Function f2 = new Function("avg", "Size");
        Function f3 = new Function("sum", "ResponseCode");
        Function f4 = new Function("avg", "ResponseCode");
        Function f5 = new Function("count", null);
        Function[] metrics = {f1,f2,f3,f4, f5};

        Map<List<Object>, List<Double>> res = new QueryEngineImpl().getAggregatedData(dimensions, metrics, true, 4, rawDataStore);

        Assert.assertEquals(2, res.size());

        Set<List<Object>> keys = res.keySet();
        List<Object> key = null;
        for(List<Object> k:keys) {
            key = k;
        }
        Assert.assertNotNull(key);
        String resource = (String) key.get(0);
        String verb = (String) key.get(1);

        Assert.assertEquals("/administrator/", resource);
        Assert.assertEquals("GET", verb);

        List<Double> vals = res.get(key);
        Assert.assertEquals(12789.0, (double) vals.get(0), 0);

    }

    @Test
    public void testQueryEngineGroupByResourceAndVerbSortDescendingByCount() {
        String[] dimensions = {"Resource", "Verb"};
        Function f1 = new Function("sum", "Size");
        Function f2 = new Function("avg", "Size");
        Function f3 = new Function("sum", "ResponseCode");
        Function f4 = new Function("avg", "ResponseCode");
        Function f5 = new Function("count", null);
        Function[] metrics = {f1,f2,f3,f4, f5};

        Map<List<Object>, List<Double>> res = new QueryEngineImpl().getAggregatedData(dimensions, metrics, false, 4, rawDataStore);

        Assert.assertEquals(2, res.size());

        Set<List<Object>> keys = res.keySet();
        List<Object> key = null;
        for(List<Object> k:keys) {
            key = k;
        }
        Assert.assertNotNull(key);
        String resource = (String) key.get(0);
        String verb = (String) key.get(1);

        Assert.assertEquals("/administrator/", resource);
        Assert.assertEquals("POST", verb);

        List<Double> vals = res.get(key);
        Assert.assertEquals(4263.0, (double) vals.get(0), 0);

    }

    @Test
    public void testQueryEngineGroupByResourceAndVerbSortDescendingByCountAndLimit() {
        String[] dimensions = {"Resource", "Verb"};
        Function f1 = new Function("sum", "Size");
        Function f2 = new Function("avg", "Size");
        Function f3 = new Function("sum", "ResponseCode");
        Function f4 = new Function("avg", "ResponseCode");
        Function f5 = new Function("count", null);
        Function[] metrics = {f1,f2,f3,f4, f5};

        Map<List<Object>, List<Double>> res = new QueryEngineImpl().getAggregatedData(dimensions, metrics, false, 4, 1, rawDataStore);

        Assert.assertEquals(1, res.size());

        Set<List<Object>> keys = res.keySet();
        List<Object> key = null;
        for(List<Object> k:keys) {
            key = k;
        }
        Assert.assertNotNull(key);
        String resource = (String) key.get(0);
        String verb = (String) key.get(1);

        Assert.assertEquals("/administrator/", resource);
        Assert.assertEquals("GET", verb);

        List<Double> vals = res.get(key);
        Assert.assertEquals(12789.0, (double) vals.get(0), 0);

    }

    @Test
    public void testQueryEngineGroupByResourceAndVerbSortAscendingByCountAndLimit() {
        String[] dimensions = {"Resource", "Verb"};
        Function f1 = new Function("sum", "Size");
        Function f2 = new Function("avg", "Size");
        Function f3 = new Function("sum", "ResponseCode");
        Function f4 = new Function("avg", "ResponseCode");
        Function f5 = new Function("count", null);
        Function[] metrics = {f1,f2,f3,f4, f5};

        Map<List<Object>, List<Double>> res = new QueryEngineImpl().getAggregatedData(dimensions, metrics, true, 4, 1, rawDataStore);

        Assert.assertEquals(1, res.size());

        Set<List<Object>> keys = res.keySet();
        List<Object> key = null;
        for(List<Object> k:keys) {
            key = k;
        }
        Assert.assertNotNull(key);
        String resource = (String) key.get(0);
        String verb = (String) key.get(1);

        Assert.assertEquals("/administrator/", resource);
        Assert.assertEquals("POST", verb);

        List<Double> vals = res.get(key);
        Assert.assertEquals(4263.0, (double) vals.get(0), 0);

    }
}
