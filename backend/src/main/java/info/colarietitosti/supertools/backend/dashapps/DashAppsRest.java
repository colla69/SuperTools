package info.colarietitosti.supertools.backend.dashapps;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class DashAppsRest {

    @Autowired
    DashAppsRepository dashAppsRepository;

    @RequestMapping(method = RequestMethod.GET, value="/dashApps")
    @ResponseBody
    public List<DashApp> getAllApps() {
        return dashAppsRepository.findAll();
    }
}
