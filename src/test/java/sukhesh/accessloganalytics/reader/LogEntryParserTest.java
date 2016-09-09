package sukhesh.accessloganalytics.reader;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.junit.Assert;
import org.junit.Test;
import sukhesh.accessloganalytics.LogEntryParser;
import sukhesh.accessloganalytics.model.LogEntry;

import java.util.List;
import java.util.Map;

/**
 * Created by sukhesh on 09/09/16.
 */
public class LogEntryParserTest {
    LogEntryParser parser = new LogEntryParser();

    @Test
    public void testShortEntry() {
        LogEntry entry = parser.parse(" ");
        Assert.assertNull(entry);
    }

    @Test
    public void testEntryWithLessFields() {
        Assert.assertNull(parser.parse("83.167.113.100 - - [12/Dec/2015:18:31:25 +0100] \"GET /administrator/ HTTP/1.1\" 200 4263 \"-\" \"Mozilla/5.0 (Windows NT 6.0; rv:34.0) Gecko/20100101 Firefox/34.0\""));
    }

    @Test
    public void testValidEntry() {
        LogEntry entry = parser.parse("83.167.113.100 - - [12/Dec/2015:18:31:25 +0000] \"GET /administrator/ HTTP/1.1\" 200 4263 \"-\" \"Mozilla/5.0 (Windows NT 6.0; rv:34.0) Gecko/20100101 Firefox/34.0\" \"-\"");
        Assert.assertEquals("83.167.113.100", entry.getHost());

        DateTime dateTime = new DateTime(2015,12,12,18,31,25, ISOChronology.getInstanceUTC());
        Assert.assertEquals(dateTime, entry.getDate());

        Assert.assertEquals("GET", entry.getVerb());
        Assert.assertEquals("/administrator/", entry.getResource());
        Assert.assertEquals("HTTP/1.1", entry.getHttpVersion());

        Assert.assertEquals(200, entry.getResponseCode());
        Assert.assertEquals(4263, entry.getSize());

        Assert.assertEquals("-", entry.getReferer());

        Assert.assertEquals("Mozilla/5.0 (Windows NT 6.0; rv:34.0) Gecko/20100101 Firefox/34.0", entry.getUserAgent());
    }
}
