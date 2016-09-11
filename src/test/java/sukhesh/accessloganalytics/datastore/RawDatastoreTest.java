package sukhesh.accessloganalytics.datastore;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sukhesh.accessloganalytics.LogEntryParser;
import sukhesh.accessloganalytics.model.LogEntry;
import sukhesh.accessloganalytics.queryengine.QueryEngine;
import sukhesh.accessloganalytics.queryengine.QueryEngineImpl;
import sukhesh.accessloganalytics.querymodel.Function;
import sukhesh.accessloganalytics.storage.AggregatedDataStore;
import sukhesh.accessloganalytics.storage.InMemoryRawDataStore;
import sukhesh.accessloganalytics.storage.RawDataStore;
import sukhesh.accessloganalytics.storage.TimeBasedAggregatedDataStore;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sukhesh on 09/09/16.
 */
public class RawDatastoreTest {
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
        e4 = new LogEntryParser().parse("83.167.113.100 - - [12/Dec/2015:18:31:27 +0100] \"GET /administrator/ HTTP/1.1\" 200 4263 \"-\" \"Mozilla/5.0 (Windows NT 6.0; rv:34.0) Gecko/20100101 Firefox/34.0\" \"-\"");

        rawDataStore.addAggregatedDataStore(timeBased);

        rawDataStore.write(e1);
        rawDataStore.write(e2);
        rawDataStore.write(e3);
        rawDataStore.write(e4);
    }

    @Test
    public void testCountRaw() {
        Assert.assertEquals(rawDataStore.currentSize(), 4);
    }

    @Test
    public void testCountTimebased() {
        Assert.assertEquals(3, timeBased.currentSize());
    }

    @Test
    public void testEntriesTimeBased() {
        Collection<List<LogEntry>> groupedEntries = timeBased.getGroupedEntries(new String[]{"Date"});
        Assert.assertEquals(3, groupedEntries.size());
        List<LogEntry>[] ar = groupedEntries.toArray(new List[3]);
        Assert.assertEquals(3, ar.length);

        List<LogEntry> l1 = ar[0];
        List<LogEntry> l2 = ar[1];
        List<LogEntry> l3 = ar[1];

        Assert.assertEquals(2, l1.size());
        Assert.assertEquals(1, l2.size());
        Assert.assertEquals(1, l3.size());

        LogEntry le1 = l1.get(0);
        LogEntry le2 = l1.get(1);

        Assert.assertEquals(e1, le1);
        Assert.assertEquals(e2, le2);

    }


    @Test
    public void testAggregatedSets() {
        Function sum = new Function("sum", "Size");
        Function avg = new Function("avg", "Size");

        String[] dimensions = {"Date"};
        Function[] functions = {sum, avg};

        Map<Object, List<Double>> res = timeBased.getGroupedAggregatedEntries(dimensions, functions);
        Collection<List<Double>> c = res.values();
        List<List<Double>> l = new LinkedList<>();
        for(List<Double> ls:c) {
            l.add(ls);
        }
        List<Double> l1 = l.get(0);
        List<Double> l2 = l.get(1);
        List<Double> l3 = l.get(2);

        Assert.assertEquals(2, l1.size());
        Assert.assertEquals(2, l2.size());
        Assert.assertEquals(2, l3.size());

        Assert.assertEquals(8526.0, (double)l1.get(0), 0);
        Assert.assertEquals(4263.0, (double)l1.get(1), 0);

        System.out.println();
    }

    @Test
    public void testQueryEngineDateGrouping() {
        String[] dimensions = {"Date", "Resource", "Verb"};
        Function f1 = new Function("sum", "Size");
        Function f2 = new Function("avg", "Size");
        Function f3 = new Function("sum", "ResponseCode");
        Function f4 = new Function("avg", "ResponseCode");
        Function[] metrics = {f1,f2,f3,f4};
        Collection<List<LogEntry>> collection = timeBased.getGroupedEntries(dimensions);

        Map<List<Object>, List<Double>> res = new QueryEngineImpl().getAggregatedData(dimensions, metrics, timeBased);
        System.out.print("");
    }

    @Test
    public void testStartTimeQuery() {
        DateTime start = new DateTime(2015,12,12,18,31,26, ISOChronology.getInstanceUTC());
        Collection<List<LogEntry>> entries = ((TimeBasedAggregatedDataStore)timeBased).getEntriesAfterTime(start);
        Assert.assertEquals(2, entries.size());
    }

    @Test
    public void testStartAndEndTimeQuery() {
        DateTime start = new DateTime(2015,12,12,18,31,25, ISOChronology.getInstanceUTC());
        DateTime end = new DateTime(2015,12,12,18,31,26, ISOChronology.getInstanceUTC());
        Collection<List<LogEntry>> entries = ((TimeBasedAggregatedDataStore)timeBased).getEntriesBetweenTime(start, end);
        Assert.assertEquals(2, entries.size());
    }
}
