package sukhesh.accessloganalytics.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sukhesh.accessloganalytics.storage.AggregatedDataStore;
import sukhesh.accessloganalytics.storage.RawDataStore;
import sukhesh.accessloganalytics.util.BeanLookupHelper;

import javax.xml.bind.ValidationException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sukhesh on 08/09/16.
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
    public List<Integer> query(@RequestParam(value = "startTime", required = true) String startTime,
                                                   @RequestParam(value = "endTime", required = false) String endTime,
                                                    @RequestParam(value = "metrics", required = true) String metrics,
                                                    @RequestParam(value = "dimensions", required = false) String dimensions) throws ValidationException {

        int size = rawDataStore.currentSize();

        List<Integer> lst = new LinkedList<>();
        lst.add(size);

        return lst;
    }
}
