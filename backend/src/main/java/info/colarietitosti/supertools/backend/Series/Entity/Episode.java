package info.colarietitosti.supertools.backend.Series.Entity;

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

    public String getNameWithFileExt(){
        return this.getName().concat(".mp4");
    }

    public String getName(){
        return this.serie.getName().concat(" S").concat(this.serie.getNo().toString())
                .concat(" E").concat(this.getNo().toString());
    }

    public String getSavePath(String savepath){
        return savepath.concat( this.getNameWithFileExt() );
    }
}
