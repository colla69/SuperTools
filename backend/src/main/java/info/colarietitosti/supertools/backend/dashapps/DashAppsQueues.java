package info.colarietitosti.supertools.backend.dashapps;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class DashAppsQueues {

    private List<DashApp> dashTv;
    private List<DashApp> dashApps;
    private List<DashApp> dashUtils;

}
