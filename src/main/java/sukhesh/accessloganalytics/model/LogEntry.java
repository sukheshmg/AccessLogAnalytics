package sukhesh.accessloganalytics.model;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * models a log entry
 *
 * Created by sukhesh on 08/09/16.
 */
public class LogEntry {

    private static final Logger logger = LoggerFactory.getLogger(LogEntry.class);

    private String host;
    private String userId;
    private String user;
    private DateTime date;
    private String verb;
    private String resource;
    private String httpVersion;
    private int responseCode;
    private int size;
    private String referer;
    private String userAgent;
    private String lastField;

    public LogEntry(String host, String userId, String user, DateTime date, String verb, String resource,
                    String httpVersion, int responseCode, int size, String referer, String userAgent, String lastField) {
        this.host = host;
        this.userId = userId;
        this.user = user;
        this.date = date;
        this.verb = verb;
        this.resource = resource;
        this.httpVersion = httpVersion;
        this.responseCode = responseCode;
        this.size = size;
        this.referer = referer;
        this.userAgent = userAgent;
        this.lastField = lastField;
    }

    private void parseAndFile(String line) {
        String[] split = line.split(" ");
        if(split != null && split.length < 12) {
            logger.info("Malformed");
        }
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getLastField() {
        return lastField;
    }

    public void setLastField(String lastField) {
        this.lastField = lastField;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
