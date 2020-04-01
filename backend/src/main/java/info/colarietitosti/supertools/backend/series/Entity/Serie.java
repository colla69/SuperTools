package info.colarietitosti.supertools.backend.series.Entity;

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
    private Boolean active;


    public Serie(Integer no, String link, String name, Boolean active) {
        this.no = no;
        this.epis = epis;
        this.link = link;
        this.name = name;
        this.active = active;
    }

    public boolean isSerieNumber(Integer seriesNo){
        return seriesNo.equals(no);
    }

    public void addEpisodes(List<Episode> e){
        this.epis.addAll(e);
    }

    public void clearEpisodes(){
        epis.clear();
    }

    public void addEpisode(Episode e){
        this.epis.add(e);
    }
}
