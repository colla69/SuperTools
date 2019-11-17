package info.colarietitosti.supertools.frontend.ui.cvvisitors;

import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Component
@FeignClient(name = "cvvisitors", url="https://cv.colarietitosti.info/")
@Scope("prototype")
public interface CvVisitorsClient {

    @RequestLine("GET /visitors")
    List<CvVisitor> getAll();
}
