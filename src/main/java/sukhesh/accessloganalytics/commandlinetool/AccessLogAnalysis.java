package sukhesh.accessloganalytics.commandlinetool;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import sukhesh.accessloganalytics.api.AccessApis;
import sukhesh.accessloganalytics.config.GlobalConfig;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * the command line tool
 * Created by sukhesh on 11/09/16.
 */
public class AccessLogAnalysis {
    static boolean firstTime = true;
    public static void run() throws ValidationException {
        if(firstTime) {
            firstTime = false;
            System.out.println("\n\n.........................Apache Access Log Analysis Tool........................");
            System.out.println("\nYou can capture metrics over some dimensions using this tool.\n\nAllowed operations are sum, avg and count.");
            System.out.println("Allowed metrics are Size and ResponseCode.");
            System.out.println("Allowed dimensions are Resource, Verb, Host and UserAgent.");
            System.out.println("\nMetrics can be sprcified as a comma separated string of <operation>:<metric>.");
            System.out.println("Dimensions can be specified as a comma separated string.");
            System.out.println("\nIn addition you can specify a start and end time in the form yyyy/MM/DD/HH/mm/ss.");
            System.out.println("Entries can be sorted by one of the specified metrics in descending order.");
            System.out.println("And a limit on number of records can be specified.");
        }
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n\nEnter the metrics to capture in the form of comma separated list of operation:metric (Example: sum:Size,count,avg:ResponseCode)");
        String metrics = scanner.nextLine();

        System.out.println("\nEnter the list of comma separated dimensions (Example: Resource,Verb,UserAgent,Host)");
        String dimensions = scanner.nextLine();

        System.out.println("\nEnter a start date (optional) (yyyy/MM/DD/HH/MM/ss)");
        String start = scanner.nextLine();

        System.out.println("\nEnter an end date (optional) (yyyy/MM/DD/HH/MM/ss)");
        String end = scanner.nextLine();

        // System.out.println("Do you want the output to be sorted in ascending order (true/false)");
        String _sort = "n";
        boolean sortAscending = false;
        if(_sort.equals("y")) {
            sortAscending = true;
        }

        System.out.println("\nEnter the metric to be used for sorting (optional) (Example: sum:Size)");
        String sortBy = scanner.nextLine();

        int limit = -1;
        String _limit = null;
        if(StringUtils.isNotEmpty(sortBy)) {
            System.out.println("\nEnter a limit on number of records (optional)");
            _limit = scanner.nextLine();
            if (StringUtils.isNotEmpty(_limit)) {
                try {
                    limit = Integer.parseInt(_limit);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Ignoring");
                }
            }
        }


        String url = "http://localhost:" + GlobalConfig.INSTANCE.getServicePort() + "/v1/accessloganalytics/query?";
        if(StringUtils.isNotEmpty(start)) {
            url += "startTime=" + start + "&";
            if(StringUtils.isNotEmpty(end)) {
                url+= "endTime=" + end + "&";
            }
        }

        url+="metrics=" + metrics + "&";
        url+="dimensions=" + dimensions + "&";

        if(sortAscending) {
            url+="sortAscending=true&";
        } else {
            url+="sortAscending=false&";
        }

        if(StringUtils.isNotEmpty(sortBy)) {
            url+="sortby=" + sortBy + "&";
        }

        if(limit != -1) {
            url+="limit=" + limit + "&";
        }

        System.out.println("\nMaking get call on api " + url + "...\n");

        Map<List<Object>, List<Double>> res = new AccessApis().query(start, end, metrics, dimensions, _sort, sortBy, _limit);

        if(res == null || res.size() == 0) {
            System.out.println("No records found for your criteria");
            System.out.println("Possibly the system is still reading the entries are there are no such records yet. Please try in a while\n\n");
        }

        System.out.println("......................................RESULTS...................................\n");

        String[] dimeSplit = dimensions.split(",");
        String[] metricSplit = metrics.split(",");

        Set<List<Object>> keySet = res.keySet();

        for(List<Object> key:keySet) {
            List<Double> vals = res.get(key);
            int k=0;
            int v=0;
            for(Object obj:key) {
                System.out.print(dimeSplit[k++] + ": " + obj + " ");
            }
            System.out.println();
            System.out.print("\t\t");
            for(Double d:vals) {
                System.out.print(metricSplit[v++] + ": " + d.intValue()  + " ");
            }
            System.out.println();
        }

//        String output = makeGetCall(url);
//
//        Type type = new TypeToken<Map<List<Object>, List<Double>>>(){}.getType();
//        // Type type = new TypeToken<List<>>(){}.getType();
//
//
//        Object result = parseJson(output, type);
//
//        System.out.println(output);


    }

    private static Object parseJson(String input, Type type) {
        Gson gson = new Gson();
        Object obj = null;
        try {
            obj = gson.fromJson(input, type);
        } catch (Throwable t) {
            System.out.println("Exception while parsing response from metadata service" + t.getMessage());
        }
        return obj;
    }

    private static String makeGetCall(String url) {

        HttpClient client = new HttpClient();
        GetMethod getMethod = new GetMethod(url);
        getMethod.setRequestHeader("Accept", "application/json");

        int response = 0;
        try {
            response = client.executeMethod(getMethod);
        } catch (IOException e) {
            System.out.println("Exception while making a get call to metadata server" + e.getMessage());
            return null;
        }

        if(response != 200){
            System.out.println("Error from metadata service: " + response);
            return null;
        }

        String jsonResp = null;
        try {
            jsonResp = getMethod.getResponseBodyAsString();
        } catch (IOException e) {
            System.out.println("Exception while getting response from metadata service" + e.getMessage());
            return null;
        }
        return jsonResp;
    }
}
