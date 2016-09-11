package sukhesh.accessloganalytics.util;

import sukhesh.accessloganalytics.model.LogEntry;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sukhesh on 11/09/16.
 */
public class Util {

    /**
     * deep copy of list of log entries
     * needed as the group by operation alters the list structure
     * @param values
     * @return
     */
    public static Collection<List<LogEntry>> deepCopy(Collection<List<LogEntry>> values) {

        Collection<List<LogEntry>> ret = new LinkedList<>();
        for (List<LogEntry> lst : values) {
            List<LogEntry> newList = new LinkedList<>(lst);
            ret.add(newList);
        }
        return ret;
    }
}
