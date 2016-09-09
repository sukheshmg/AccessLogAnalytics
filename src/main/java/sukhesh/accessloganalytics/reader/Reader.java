package sukhesh.accessloganalytics.reader;

import sukhesh.accessloganalytics.model.LogEntry;

import java.io.IOException;
import java.util.List;

/**
 * Created by sukhesh on 08/09/16.
 */
public interface Reader {
    List<LogEntry> getAll() throws IOException;
    List<LogEntry> getBatch() throws IOException;
}
