package info.colarietitosti.supertools.backend.tools.Series.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Episode {

    private Serie serie;
    private Integer no;
    private String link;

    public Episode(Serie serie, Integer no, String link) {
        this.serie = serie;
        this.no = no;
        this.link = link;
    }

    public String getName(){
        return serie.getName().concat(" S").concat(serie.getNo().toString())
                .concat(" E").concat(this.getNo().toString()).concat(".mp4");
    }
}
