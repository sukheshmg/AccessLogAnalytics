package sukhesh.accessloganalytics.api;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sukhesh.accessloganalytics.exception.InvalidInputException;
import sukhesh.accessloganalytics.queryengine.QueryEngine;
import sukhesh.accessloganalytics.queryengine.QueryEngineImpl;
import sukhesh.accessloganalytics.querymodel.Function;
import sukhesh.accessloganalytics.storage.AggregatedDataStore;
import sukhesh.accessloganalytics.storage.RawDataStore;
import sukhesh.accessloganalytics.util.BeanLookupHelper;

import javax.xml.bind.ValidationException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sukhesh on 08/09/16.
 */

/**
 * the api module
 * provides query capabilities
 */
@RestController
@RequestMapping(value = "v1/accessloganalytics/")
@Component
public class AccessApis {
    @Autowired
    AggregatedDataStore aggregatedDataStore;

    @Autowired
    RawDataStore rawDataStore;

    @RequestMapping(value = "query")
    public Map<List<Object>, List<Double>> query(@RequestParam(value = "startTime", required = false) String startTime,
                                                   @RequestParam(value = "endTime", required = false) String endTime,
                                                    @RequestParam(value = "metrics", required = true) String metrics,
                                                    @RequestParam(value = "dimensions", required = false) String dimensions,
                                                    @RequestParam(value = "sortAscending", required = false) String sortAscending,
                                                    @RequestParam(value = "sortby", required = false) String sortBy,
                                                    @RequestParam(value = "limit", required = false) String limit) throws ValidationException {


        DateTime start = null;
        DateTime end = null;
        Function[] metric = null;
        String[] dimension = null;
        boolean sort = false;
        int metricIndexToSort = -1;
        int limitValue = -1;

        if(StringUtils.isNotEmpty(startTime)) {
            start = getTimeFromString(startTime);
        }

        if(StringUtils.isNotEmpty(endTime)) {
            end = getTimeFromString(endTime);
        }

        if(StringUtils.isNotEmpty(metrics)) {
            metric = parseMetrics(metrics);
        }

        if(metric == null) {
            throw new ValidationException("at least one metric must be specified");
        }

        if(StringUtils.isNotEmpty(dimensions)) {
            dimension = parseDimensions(dimensions);
        }

        if(dimension == null) {
            dimension = new String[0];
            // throw new ValidationException("at least one dimension must be specified");
        }

        if(StringUtils.isNotEmpty(sortAscending)) {
            if(sortAscending.equals("true")) {
                sort = true;
            }
        }

        if(StringUtils.isNotEmpty(sortBy)) {
            Function[] ar = parseMetrics(sortBy);
            if(ar == null || ar.length == 0) {
                metricIndexToSort = -1;
            } else {
                Function f = ar[0];
                for(int i=0;i<metric.length;i++) {
                    String member = metric[i].getMetric();
                    String op = metric[i].getOperation();
                    if(op.equals(f.getOperation())) {
                        if(member == null) {
                            if(f.getMetric() == null) {
                                metricIndexToSort = i;
                            }
                        } else {
                            if(member.equals(f.getMetric())) {
                                metricIndexToSort = i;
                            }
                        }
                    }
                }
            }
        }

        if(StringUtils.isNotEmpty(limit)) {
            try {
                limitValue = Integer.parseInt(limit);
            } catch (NumberFormatException e) {

            }
        }

        /**
         * if time is specified, use it
         */

        Map<List<Object>, List<Double>> res = null;
        QueryEngine queryEngine = new QueryEngineImpl();

        if(start != null) {
            if(end != null) {
                if(metricIndexToSort != -1) {
                    if(limitValue != -1) {
                        res = queryEngine.getAggregatedData(dimension, metric, start, end, sort, metricIndexToSort, limitValue);
                    } else {
                        res = queryEngine.getAggregatedData(dimension, metric, start, end, sort, metricIndexToSort);
                    }
                } else {
                    res = queryEngine.getAggregatedData(dimension, metric, start, end);
                }
            } else {
                res = queryEngine.getAggregatedData(dimension, metric, start);
            }
        } else {
            if(metricIndexToSort != -1) {
                if(limitValue != -1) {
                    res = queryEngine.getAggregatedData(dimension, metric, sort, metricIndexToSort, limitValue);
                } else {
                    res = queryEngine.getAggregatedData(dimension, metric, sort, metricIndexToSort);
                }
            } else {
                res = queryEngine.getAggregatedData(dimension, metric);
            }
        }

        return res;
    }

    private String[] parseDimensions(String dimensions) throws ValidationException {
        String[] splits = dimensions.split(",");
        if(splits == null || splits.length == 0) {
            return null;
        }
        String[] res = new String[splits.length];
        int i = 0;
        for(String split : splits) {
            if(split.equals("Resource") || split.equals("Verb") || split.equals("Host") || split.equals("UserAgent")) {
                res[i++] = split;
            } else {
                throw new ValidationException("dimension cna be Resource, Verb, Host or UserAgent");
            }
        }
        return res;
    }

    private Function[] parseMetrics(String metrics) throws ValidationException {
        String[] splits = metrics.split(",");
        if(splits == null || splits.length == 0) {
            return null;
        }

        int len = splits.length;
        Function[] res = new Function[len];

        int i=0;

        for(String split : splits) {
            String[] furtherSplits = split.split(":");
            if(furtherSplits == null || furtherSplits.length == 0 || furtherSplits.length >2) {
                throw new ValidationException("Invalid metrics");
            }

            if(furtherSplits.length == 1) {
                if(furtherSplits[0].equals("count")) {
                    Function f = new Function("count", null);
                    res[i] = f;
                } else {
                    throw new ValidationException("Invalid metric " + split);
                }
            } else {
                String op = furtherSplits[0];
                String m = furtherSplits[1];
                if(op.equals("sum") || op.equals("avg")) {
                    if(m.equals("Size") || m.equals("ResponseCode")) {
                        Function f = new Function(op, m);
                        res[i] = f;
                    } else {
                        throw new ValidationException("metric can be either Size or ResponseCode");
                    }
                } else {
                    throw new ValidationException("operation can be sum, avg or count");
                }
            }
            i++;
        }
        return res;
    }

    /**
     * String format yyyy/MM/DD/HH/mm/ss
     * @param startTime
     * @return
     */
    private DateTime getTimeFromString(String startTime) throws ValidationException {
        String[] splits = startTime.split("/");
        if(splits == null || splits.length != 6) {
            throw new ValidationException("Invalid time " + startTime);
        }
        DateTime res = null;
        try {
            int year = Integer.parseInt(splits[0]);
            int month = Integer.parseInt(splits[1]);
            int day = Integer.parseInt(splits[2]);
            int hour = Integer.parseInt(splits[3]);
            int minutes = Integer.parseInt(splits[4]);
            int second = Integer.parseInt(splits[5]);
            res = new DateTime(year, month, day, hour, minutes, second, ISOChronology.getInstanceUTC());
        }catch (NumberFormatException e) {
            throw new ValidationException("Invalid time " + startTime);
        }
        return res;
    }
}
