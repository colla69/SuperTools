package info.colarietitosti.supertools.backend.tools.Series.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Serie {

    private Integer no;
    private List<Episode> epis = new ArrayList<>();
    private String link;
    private String name;


    public Serie(Integer no, String link, String name) {
        this.no = no;
        this.epis = epis;
        this.link = link;
        this.name = name;
    }

    public void addEpisodes(List<Episode> e){
        this.epis.addAll(e);
    }

    public void addEpisode(Episode e){
        this.epis.add(e);
    }
}
