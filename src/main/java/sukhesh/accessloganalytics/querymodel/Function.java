package sukhesh.accessloganalytics.querymodel;

/**
 * Created by sukhesh on 09/09/16.
 */
public class Function {
    private String operation;
    private String metric;

    public Function(String operation, String metric) {
        this.operation = operation;
        this.metric = metric;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }
}
