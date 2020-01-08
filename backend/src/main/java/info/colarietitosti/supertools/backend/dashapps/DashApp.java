package info.colarietitosti.supertools.backend.dashapps;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter @Setter
@Entity
@Table(name="dash_apps")
public class DashApp implements Comparable<DashApp> {

    @Id @GeneratedValue
    private Integer id;

    private String name;
    private String link;

    @Column(name="img_link")
    private String imglink;
    private String type;

    @Override
    public int compareTo(DashApp o) {
        return this.getId().compareTo(o.getId());
    }
}
