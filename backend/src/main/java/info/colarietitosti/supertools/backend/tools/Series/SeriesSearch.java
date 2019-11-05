package info.colarietitosti.supertools.backend.tools.Series;

import info.colarietitosti.supertools.backend.tools.Config;
import info.colarietitosti.supertools.backend.tools.Series.Entity.Episode;
import info.colarietitosti.supertools.backend.tools.Series.Entity.Serie;
import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Log
@Component
public class SeriesSearch {

    @Autowired
    Config config;

    public List<Serie> searchSeriesForDownload(){
        String outPath = config.getOutPath();
        String doneOutput = outPath.concat("done/");
        new File(doneOutput).mkdirs();
        List<Serie> todo = config.getSeries();
        List<String> doneList = getDoneList(doneOutput);
        todo.parallelStream().forEach(s -> {
            String serieOutPath = outPath.concat(s.getName()).concat("/");
            new File(serieOutPath).mkdirs();
            List<Episode> epis = searchEpisodes(s.getLink(), s.getNo(), s);
            epis.forEach(e -> {
                String fileName = doneOutput.concat(e.getName());
                boolean notDone = doneList.parallelStream().filter(d -> d.contains(fileName)).collect(Collectors.toList()).isEmpty();
                if (notDone) {
                    s.addEpisode(e);
                }
            });
        });
        return todo;
    }

    private List<String> getDoneList(String doneOutput) {
        List<String> result = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get(doneOutput))) {
                 result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Episode> searchEpisodes(String linkpart, Integer sno, Serie serie){
        List<Episode> result = new ArrayList<>();
        String link = config.getWatchSeriesLink().concat("serie/").concat(linkpart);
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements table = doc.getElementsByAttributeValue("itemprop","season");
        for (Element s : table){
            String strNo = s.text().substring(7,9);
            Integer no = Integer.parseInt(strNo.trim());
            if (sno.equals(no)){
                for (Element epiline : s.select("li")){
                    String epilink = epiline.select("a").get(0).attr("href");
                    String epiNo = epiline.select("meta").get(0).attr("content");
                    result.add(new Episode(serie,Integer.parseInt(epiNo),epilink));
                }
            }
        }
        return result;
    }

    public List<String> getLinksFromEpiPage(Episode episode){
        Document doc = null;
        try {
            doc = Jsoup.connect(episode.getLink()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> epiLinks = new ArrayList<>();
        Elements lines = doc.getElementsByClass("watchlink");
        for (Element line : lines){
            String epilinkEnc = line.attr("href");
            String epiLinkBase = epilinkEnc.substring(epilinkEnc.indexOf("r=")+2,epilinkEnc.length());
            try{
                String epiLink =  new String(Base64.getDecoder().decode(epiLinkBase));
                epiLinks.add(epiLink);
            } catch (Exception e) {
                continue;
            }
        }
        return epiLinks;
    }


}
