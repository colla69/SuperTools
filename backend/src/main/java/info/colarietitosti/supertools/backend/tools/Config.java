package info.colarietitosti.supertools.backend.tools;

import info.colarietitosti.supertools.backend.tools.Series.Entity.Serie;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Getter
@Component
@Scope("singleton")
public class Config {

    private String outPath = "/run/media/cola/Transfer/PlexContent/tvshows/";
    private String watchSeriesLink = "https://www1.swatchseries.to/";
    private String done_command = "/run/media/cola/Transfer/PlexContent/syncTvShows";
    private List<Serie> series = new ArrayList<>();

    @PostConstruct
    public void loadSeries(){
        addSeries("The Big Bang Theory", "big_bang_theory", 12);
        addSeries("Young Sheldon", "young_sheldon", 3);
        addSeries("The Walking Dead", "The_Walking_Dead_%282011%29", 10,9);
        addSeries("The Flash (2014)", "the_flash_2014_", 6);
        addSeries("Supernatural", "supernatural", 15);
        addSeries("The Good Doctor", "the_good_doctor", 2,3);
        addSeries("Mr Robot", "mr_robot", 4);
        addSeries("The Simpsons", "the_simpsons", 30,31);
        addSeries("The Blacklist", "The_Blacklist", 7);
        addSeries("Timeless (2016)", "timeless-2", 2);
        addSeries("Rick and Morty", "rick_and_morty", 4);
        addSeries("Shameless USA", "Shameless_USA", 10);
        //addSeries("Your Pretty Face is Going to Hell", "Your_Pretty_Face_Is_Going_to_Hell", 1,2,3);

    }

    private void addSeries(String name, String linkpart, Integer ... num ){
        for (Integer n : num){
            series.add(new Serie(n, linkpart, name));
        }
    }

}
