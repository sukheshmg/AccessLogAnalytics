package sukhesh.accessloganalytics.commandlinetool;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import sukhesh.accessloganalytics.api.AccessApis;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by sukhesh on 11/09/16.
 */
public class AccessLogAnalysis {
    public static void main(String[] args) throws ValidationException {
        System.out.println(".........................Apache Access Log Analysis Tool........................");
        System.out.println("You can capture metrics over some dimensions using this tool.\n\nAllowed operations are sum, avg and count");
        System.out.println("Allowed metrics are Size and ResponseCode");
        System.out.println("Allowed dimensions are Resource, Verb, Host and ResponseCode");
        System.out.println("\nMetrics can be sprcified as a comma separated string of <operation>:<metric>");
        System.out.println("Dimensions can be specified as a comma separated string");
        System.out.println("\nIn addition you can specify a start and end time in the form yyyy/MM/DD/HH/mm/ss");
        System.out.println("Entries can be sorted by one of the specified metrics in both ascending and descending order");
        System.out.println("And a limit on number of records can be specified");
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nEnter the metrics to capture in the form of comma separated list of operation:metric (sum:Size)");
        String metrics = scanner.nextLine();

        System.out.println("\nEnter the list comma separated dimensions");
        String dimensions = scanner.nextLine();

        System.out.println("Enter a start date (optional)");
        String start = scanner.nextLine();

        System.out.println("Enter an end date (optional)");
        String end = scanner.nextLine();

        System.out.println("Do you want the output to be sorted in ascending order (y/n)");
        String _sort = scanner.nextLine();
        boolean sortAscending = false;
        if(_sort.equals("y")) {
            sortAscending = true;
        }

        System.out.println("Enter the metric to be used for sorting");
        String sortBy = scanner.nextLine();

        System.out.println("Enter a limit on number of records (optional)");
        String _limit = scanner.next();
        int limit = -1;
        if(StringUtils.isNotEmpty(_limit)) {
            try {
                limit = Integer.parseInt(_limit);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Ignoring");
            }
        }


        String url = "http://localhost:8080/v1/accessloganalytics/query?";
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

        System.out.println("url: " + url);

//        Map<List<Object>, List<Double>> res = new AccessApis().query(start, end, metrics, dimensions, _sort, sortBy, _limit);
//
//        Set<List<Object>> keySet = res.keySet();
//
//        for(List<Object> key:keySet) {
//            List<Double> vals = res.get(key);
//            for(Object obj:key) {
//                System.out.print(obj + "~");
//            }
//            System.out.print(" ");
//            for(Double d:vals) {
//                System.out.print(d + "~");
//            }
//            System.out.println();
//        }

        String output = makeGetCall(url);

        System.out.println(output);


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
