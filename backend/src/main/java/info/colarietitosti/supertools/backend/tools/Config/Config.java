package info.colarietitosti.supertools.backend.tools.Config;

import info.colarietitosti.supertools.backend.tools.Series.Entity.Serie;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Log
@Getter
@Component
@Scope("singleton")
public class Config {

    @Autowired
    SeriesConfigRepository seriesConfigRepository;

    private String seriesOutPath = "/run/media/cola/Transfer/PlexContent/tvshows/";
    private String musicOutPath = "/run/media/cola/Transfer/PlexContent/Music/";
    private String watchSeriesLink = "https://www1.swatchseries.to/";
    private String done_command = "/run/media/cola/Transfer/PlexContent/syncTvShows";
    private List<Serie> series = new ArrayList<>();

    @PostConstruct
    public void loadSeries(){
        series.clear();
        List<SeriesConfigE> seriesConfigs = seriesConfigRepository.findAll();
        seriesConfigs.forEach(sc -> addSeries(
                sc.getLabel(),
                sc.getLinkpart(),
                sc.getActive(),
                sc.getStartSeriesNo(),
                sc.getEndSeriesNo()
        ));
        /*
        addSeries("Rick and Morty", "rick_and_morty", 4);
        addSeries("Shameless USA", "Shameless_USA", 10);
        addSeries("Young Sheldon", "young_sheldon", 3);
        addSeries("Doctor Who (2005)", "doctor_who_2005", 12);
        addSeries("The Big Bang Theory", "big_bang_theory", 12);
        addSeries("The Walking Dead", "The_Walking_Dead_%282011%29", 10,9);
        addSeries("Supernatural", "supernatural", 15);
        addSeries("The Good Doctor", "the_good_doctor", 2,3);
        addSeries("The Simpsons", "the_simpsons", 30,31);
        addSeries("The Blacklist", "The_Blacklist", 7);
        */
        //addSeries("The Mandalorian", "the_mandalorian", 1);
        //addSeries("The Flash (2014)", "the_flash_2014_", 6);
        //addSeries("Mr Robot", "mr_robot", 4);
        //addSeries("Timeless (2016)", "timeless-2", 2);
        //addSeries("Your Pretty Face is Going to Hell", "Your_Pretty_Face_Is_Going_to_Hell", 1,2,3);
        //addSeries("Constantine", "constantine", 1);
    }

    private void addSeries(String name, String linkpart, Boolean active, Integer startNum, Integer endNum ){
        for (Integer n = startNum; n <= endNum; n++){
            series.add(new Serie(n, linkpart, name, active));
        }
    }
}
