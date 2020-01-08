package info.colarietitosti.supertools.backend.tools.Config;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Log
@Controller
public class SeriesConfigRest {

    @Autowired
    Config config;

    @Autowired
    SeriesConfigRepository seriesConfigRepository;

    @GetMapping("/seriesConfig")
    @ResponseBody
    public List<SeriesConfigE> getSeriesConfigJson(){
        return seriesConfigRepository.findAll();
    }

    @PostMapping("/saveSeriesConfig")
    @ResponseBody
    public String saveSeriesConfig(@RequestBody List<SeriesConfigE> series){
        try {
            series.forEach(s -> {
                if (s.getId() != null){
                    SeriesConfigE conf = new SeriesConfigE();
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
    public String delSeriesConfig(@RequestBody SeriesConfigE serie){
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
    public List<SeriesConfigE> getSeriesConfigActive(){
        return seriesConfigRepository.findAll().stream().filter(SeriesConfigE::getActive).collect(Collectors.toList());
    }

}
