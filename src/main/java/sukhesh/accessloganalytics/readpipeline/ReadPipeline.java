package sukhesh.accessloganalytics.readpipeline;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sukhesh.accessloganalytics.config.GlobalConfig;
import sukhesh.accessloganalytics.model.LogEntry;
import sukhesh.accessloganalytics.reader.RemoteFileReader;
import sukhesh.accessloganalytics.util.BeanLookupHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by sukhesh on 08/09/16.
 */
@Component
public class ReadPipeline implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(ReadPipeline.class);

    private String file;
    InputStream inputStream;

    private ReadPipeline() throws IOException {
        this.file = GlobalConfig.INSTANCE.getLogUri();
        URL url = new URL(file);
        URLConnection connection = url.openConnection();
        inputStream = connection.getInputStream();
    }

    @Override
    public void run() {
        while (true) {
            List<LogEntry> entries = null;
            try {
                entries = new RemoteFileReader(inputStream).getBatch();
            } catch (IOException e) {
                logger.error("IOException while reading from file", e);
            }
            for(LogEntry entry : entries) {
                BeanLookupHelper.INSTANCE.getRawDataStore().write(entry);
            }
        }
    }
}
