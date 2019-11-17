package info.colarietitosti.supertools.frontend.ui.dashApps;



import feign.RequestLine;
import info.colarietitosti.supertools.frontend.ui.GenericClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@FeignClient(name = "dashApps")
@Scope("prototype")
public interface DashAppsClient extends GenericClient {

    //@RequestMapping(method = RequestMethod.GET, value = "/dashApps")
    @RequestLine("GET /dashApps")
    DashAppsDO getDashApps();
}
