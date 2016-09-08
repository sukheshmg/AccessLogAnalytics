package sukhesh.accessloganalytics.api;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(value = "query")
    public List<Object> query(@RequestParam(value = "startTime", required = true) String startTime,
                                                   @RequestParam(value = "endTime", required = false) String endTime) throws ValidationException {
        return new LinkedList<>();
    }
}
