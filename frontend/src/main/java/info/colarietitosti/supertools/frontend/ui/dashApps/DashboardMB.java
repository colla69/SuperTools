package info.colarietitosti.supertools.frontend.ui.dashApps;

import info.colarietitosti.supertools.frontend.ui.SuperGenericBean;
import info.colarietitosti.supertools.frontend.ui.feignClient.PingHomeConsumer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Log
@Getter @Setter
@ManagedBean("dashboardMB")
@Scope("prototype")
public class DashboardMB extends SuperGenericBean {


    private DashAppsClient dashAppsClient;
    private List<DashApp> dashTv;
    private List<DashApp> dashApps;
    private List<DashApp> dashUtils;

    @Autowired
    PingHomeConsumer pingHomeConsumer;

    @Value("${backend.ip}")
    String backIP;


    @PostConstruct
    public void init(){
        dashAppsClient = (DashAppsClient) makeDataSource(DashAppsClient.class);

        List<DashApp> apps =dashAppsClient.getDashApps();
        this.dashApps = apps.stream().filter(a -> a.getType().equals("app")).sorted().collect(Collectors.toList());
        this.dashUtils = apps.stream().filter(a -> a.getType().equals("util")).sorted().collect(Collectors.toList());
        this.dashTv = apps.stream().filter(a -> a.getType().equals("tv")).sorted().collect(Collectors.toList());
    }


}
