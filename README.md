This tool helps in deriving interesting insights into apache access log.
This used maven, java 7 and Spring Boot 3.0 as the tech stack.

Usage

1. Get the repository
2. mvn clean install
3. java -Dlogging.config=file:./target/classes/logback.xml -jar target/accessloganalytics-1.0.0.jar

Those steps start the webapp on port 9080 with other defaults. By default, the service reads from "http://www.almhuette-raith.at/apache-log/access.log"

Configuration options
1. -Dservice.port : Change the port on which the service runs
2. -Dmax.records.inmemory : Limit the number of records kept in memory
3. -Dread.sleep.time : Sleep time between batch reads from the log file
4. -Dlog.uri : uri of a file that needs to be analysed

In addition to starting the web app, the service also starts a command line tool which can be used to do the slicing and dicing.
The command line tool asks some simple questions like what metrics are you interested in, what dimensions do you want to use, 
a time range, a sortby and a limit on the number of records you want to see. Once the details are entered, the result is fetched and
displayed.

For example, if you want to see what are the top 10 urls that where hit and the counts, you can follow this sequence of question and answers


Enter the metrics to capture in the form of comma separated list of operation:metric (Example: sum:Size,count,avg:ResponseCode)
count

Enter the list of comma separated dimensions (Example: Resource,Verb,UserAgent,Host)
Resource

Enter a start date (optional) (yyyy/MM/DD/HH/MM/ss)


Enter an end date (optional) (yyyy/MM/DD/HH/MM/ss)


Enter the metric to be used for sorting (optional) (Example: sum:Size)
count

Enter a limit on number of records (optional)
10
