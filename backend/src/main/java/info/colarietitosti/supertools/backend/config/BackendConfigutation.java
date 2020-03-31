package info.colarietitosti.supertools.backend.config;

import info.colarietitosti.supertools.backend.Series.Entity.Serie;
import info.colarietitosti.supertools.backend.config.series.SeriesConfig;
import info.colarietitosti.supertools.backend.config.series.SeriesConfigRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
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
        seriesConfigs.forEach(seriesConfig -> addSeries(
                seriesConfig.getLabel(),
                seriesConfig.getLinkpart(),
                seriesConfig.getActive(),
                seriesConfig.getStartSeriesNo(),
                seriesConfig.getEndSeriesNo()
        ));
    }

    public String getCompleteWatchSeriesLink(String serieLinkPart){
        return watchSeriesLink.concat("serie/").concat(serieLinkPart);
    }

    private void addSeries(String name, String linkpart, Boolean active, Integer startNum, Integer endNum ){
        for (Integer n = startNum; n <= endNum; n++){
            series.add(new Serie(n, linkpart, name, active));
        }
    }
}
