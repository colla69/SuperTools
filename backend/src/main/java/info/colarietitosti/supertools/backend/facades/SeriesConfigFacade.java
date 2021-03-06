package info.colarietitosti.supertools.backend.Config;

import info.colarietitosti.supertools.backend.config.BackendConfiguration;
import info.colarietitosti.supertools.backend.config.series.SeriesConfig;
import info.colarietitosti.supertools.backend.config.series.SeriesConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class SeriesConfigFacade {

    @Autowired
    BackendConfiguration config;

    @Autowired
    SeriesConfigRepository seriesConfigRepository;

    @GetMapping("/seriesConfig")
    @ResponseBody
    public List<SeriesConfig> getSeriesConfigJson(){
        return seriesConfigRepository.findAll();
    }

    @PostMapping("/saveSeriesConfig")
    @ResponseBody
    public String saveSeriesConfig(@RequestBody List<SeriesConfig> series){
        try {
            series.forEach(s -> {
                if (s.getId() != null){
                    SeriesConfig conf = new SeriesConfig();
                    conf.setId(s.getId());
                    conf.setActive(s.getActive());
                    conf.setLabel(s.getLabel());
                    conf.setLinkpart(s.getLinkpart());
                    conf.setStartSeriesNo(s.getStartSeriesNo());
                    conf.setEndSeriesNo(s.getEndSeriesNo());
                    seriesConfigRepository.save(conf);
                } else {
                    seriesConfigRepository.save(s);
                }
            });

            return String.valueOf(202);
        } catch (Exception e){
            e.printStackTrace();
            return String.valueOf(500);
        }
    }

    @PostMapping("/deleteSeriesConfig")
    @ResponseBody
    public String delSeriesConfig(@RequestBody SeriesConfig serie){
        try {
            seriesConfigRepository.delete(serie);
            seriesConfigRepository.flush();
            return String.valueOf(202);
        } catch (Exception e){
            e.printStackTrace();
            return String.valueOf(500);
        }
    }

    @GetMapping("/seriesConfigActive")
    @ResponseBody
    public List<SeriesConfig> getSeriesConfigActive(){
        return seriesConfigRepository.findAll().stream().filter(SeriesConfig::getActive).collect(Collectors.toList());
    }

}
