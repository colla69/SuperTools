package info.colarietitosti.supertools.backend.config;

import info.colarietitosti.supertools.backend.config.series.SeriesConfig;
import info.colarietitosti.supertools.backend.config.series.SeriesConfigRepository;
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
public class BackendConfigutation {

    @Autowired
    SeriesConfigRepository seriesConfigRepository;

    private String seriesOutPath = "/run/media/cola/NASData/PlexContent/tvshows/";
    private String musicOutPath = "/run/media/cola/NASData/PlexContent/Music/";
    private String watchSeriesLink = "https://www1.swatchseries.to/";
    private String done_command = "/run/media/cola/NASData/PlexContent/syncTvShows";
    private List<Serie> series = new ArrayList<>();

    @PostConstruct
    public void loadSeries(){
        series.clear();
        List<SeriesConfig> seriesConfigs = seriesConfigRepository.findAll();
        seriesConfigs.forEach(sc -> addSeries(
                sc.getLabel(),
                sc.getLinkpart(),
                sc.getActive(),
                sc.getStartSeriesNo(),
                sc.getEndSeriesNo()
        ));
    }

    private void addSeries(String name, String linkpart, Boolean active, Integer startNum, Integer endNum ){
        for (Integer n = startNum; n <= endNum; n++){
            series.add(new Serie(n, linkpart, name, active));
        }
    }
}
