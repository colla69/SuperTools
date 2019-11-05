package info.colarietitosti.supertools.backend.dashapps;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter @Setter
@Entity
@Table(name="dashapps", schema = "public")
public class DashApp implements Comparable<DashApp> {

    @Id @GeneratedValue
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
