package info.colarietitosti.supertools.backend.tools.Config;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter @Setter
@Entity
@Table(name="SeriesConfig")
public class SeriesConfigE {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String label;
    private String linkpart;
    private Integer startSeriesNo;
    private Integer endSeriesNo;
    private Boolean active;

}
