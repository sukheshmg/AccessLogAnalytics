package sukhesh.accessloganalytics.reader;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sukhesh.accessloganalytics.model.LogEntry;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sukhesh on 08/09/16.
 */
public class LogEntryParser {
    private static final Logger logger = LoggerFactory.getLogger(LogEntryParser.class);

    private String pattern = "(.*)\\s(.*)\\s(.*)\\s(\\[.*\\s.*\\])\\s(\".*\\s.*\\s.*\")\\s(.*)\\s(.*)\\s(.*)\\s(\".*\")\\s(.*)";

    public LogEntry parse(String entry) {
        logger.info(entry);
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(entry);

        try {

            boolean found = matcher.find();

            if (!found) {
                logger.error("Entry " + entry + " is malformed");
                return null;
            }

            String ip = matcher.group(1);

            String userId = matcher.group(2);
            if(userId != null && userId.length() > 2 && userId.startsWith("\"") && userId.endsWith("\"") ) {
                userId = userId.substring(1, userId.length()-1);
            }

            String user = matcher.group(3);
            if(user != null && user.length() > 2 && user.startsWith("\"") && user.endsWith("\"")) {
                user = userId.substring(1, user.length() - 1);
            }

            String date = matcher.group(4);
            DateTime actual = null;
            if(date != null && date.length() > 2 && date.startsWith("[") && date.endsWith("]")) {
                date = date.substring(1, date.length()-1);
                String[] splits = date.split(" ");
                DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MMM/yyyy:HH:mm:ss").withChronology(ISOChronology.getInstanceUTC());
                DateTime dt = formatter.parseDateTime(splits[0]);

                char sign = splits[1].charAt(0);
                int hour = Integer.parseInt(splits[1].substring(1, 3));
                int min = Integer.parseInt(splits[1].substring(3, 5));

                int toAdd = hour*60 + min;
                if(sign == '-') {
                    toAdd = -1*toAdd;
                }

                actual = dt.plus(toAdd);
            }

            String req = matcher.group(5);
            String method = null;
            String resource = null;
            String httpVersion = null;
            if(req != null && req.length() > 2 && req.startsWith("\"") && req.endsWith("\"")) {
                req = req.substring(1, req.length()-1);
                String[] splits = req.split(" ");
                if(splits != null && splits.length == 3) {
                    method = splits[0].trim();
                    resource = splits[1].trim();
                    httpVersion = splits[2].trim();
                }
            }

            String _resp = matcher.group(6);
            int responseCode = 0;
            if(StringUtils.isNotEmpty(_resp)) {
                try {
                    responseCode = Integer.parseInt(_resp);
                } catch (NumberFormatException e) {
                    logger.info("Malformed response code " + _resp);
                }
            }

            String _bytes = matcher.group(7);
            int bytes = 0;
            if(StringUtils.isNotEmpty(_bytes)) {
                try {
                    bytes = Integer.parseInt(_bytes);
                } catch (NumberFormatException e) {
                    logger.info("Malformed bytes " + _bytes);
                }
            }

            String ref = matcher.group(8);
            if(ref != null && ref.length() > 2 && ref.startsWith("\"") && ref.endsWith("\"")) {
                ref = ref.substring(1, ref.length()-1);
            }

            String ua = matcher.group(9);
            if(ua != null && ua.length() > 2 && ua.startsWith("\"") && ua.endsWith("\"")) {
                ua = ua.substring(1, ua.length()-1);
            }

            String lastField = matcher.group(10);

            LogEntry res = new LogEntry(ip, userId, user, actual, method, resource, httpVersion, responseCode, bytes, ref, ua, lastField);
            return res;
        } catch (Throwable t) {
            logger.error("Exception while parsing " + entry, t);
            return null;
        }
    }

    public static void main(String[] args) {
        LogEntry e = new LogEntryParser().parse("46.72.177.4 - - [12/Dec/2015:18:31:08 +0100] \"GET /administrator/ HTTP/1.1\" 200 4263 \"-\" \"Mozilla/5.0 (Windows NT 6.0; rv:34.0) Gecko/20100101 Firefox/34.0\" \"-\"");
        System.out.println();
    }
}
