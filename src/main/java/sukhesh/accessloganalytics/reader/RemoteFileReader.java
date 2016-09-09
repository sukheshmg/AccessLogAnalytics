package sukhesh.accessloganalytics.reader;

import sukhesh.accessloganalytics.model.LogEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sukhesh on 08/09/16.
 */
public class RemoteFileReader implements Reader {
    InputStream inputStream;

    public RemoteFileReader(InputStream stream) {
        this.inputStream = stream;
    }

    @Override
    public List<LogEntry> getAll() throws IOException {
        List<LogEntry> result = new LinkedList<>();
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(reader);

        String line = null;
        while ((line = br.readLine()) != null) {
            LogEntry entry = new LogEntryParser().parse(line);
            if(entry != null) {
                result.add(entry);
            }
        }
        return result;
    }

    @Override
    public List<LogEntry> getBatch() throws IOException {
        List<LogEntry> result = new LinkedList<>();
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(reader);

        int counter = 0;
        String line = null;
        while ((line = br.readLine()) != null) {
            LogEntry entry = new LogEntryParser().parse(line);
            if(entry != null) {
                result.add(entry);
                counter++;
            }

            if(counter == 100) {
                break;
            }
        }
        return result;
    }
}
