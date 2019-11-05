package info.colarietitosti.supertools.frontend.ui.dashApps;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DashApp implements Comparable<DashApp> {


    private Integer id;
    private String name;
    private String link;
    private String imglink;
    private String type;

    @Override
    public int compareTo(DashApp o) {
        return this.getId().compareTo(o.getId());
    }
}
