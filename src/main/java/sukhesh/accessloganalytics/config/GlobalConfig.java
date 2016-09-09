package sukhesh.accessloganalytics.config;

/**
 * Created by sukhesh on 08/09/16.
 */
public enum GlobalConfig {
    INSTANCE;

    private int servicePort = 9080;
    private String logUri = "http://www.almhuette-raith.at/apache-log/access.log";
    private int maxRecordsInMemory = 10000000;


    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public String getLogUri() {
        return logUri;
    }

    public void setLogUri(String logUri) {
        this.logUri = logUri;
    }

    public int getMaxRecordsInMemory() {
        return maxRecordsInMemory;
    }

    public void setMaxRecordsInMemory(int maxRecordsInMemory) {
        this.maxRecordsInMemory = maxRecordsInMemory;
    }
}
