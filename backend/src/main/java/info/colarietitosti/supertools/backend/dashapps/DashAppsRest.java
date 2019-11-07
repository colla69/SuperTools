package info.colarietitosti.supertools.backend.dashapps;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashAppsRest {

    @Autowired
    DashAppsRepository dashAppsRepository;

    @RequestMapping(method = RequestMethod.GET, value="/dashApps")
    @ResponseBody
    public DashAppsDO getAllApps() {

        DashAppsDO dashAppsDO = new DashAppsDO();

        List<DashApp> apps = dashAppsRepository.findAll();
        dashAppsDO.setDashApps(apps.stream().filter(a -> a.getType().equals("app")).sorted().collect(Collectors.toList()));
        dashAppsDO.setDashUtils(apps.stream().filter(a -> a.getType().equals("util")).sorted().collect(Collectors.toList()));
        dashAppsDO.setDashTv(apps.stream().filter(a -> a.getType().equals("tv")).sorted().collect(Collectors.toList()));

        return dashAppsDO;
    }

}
