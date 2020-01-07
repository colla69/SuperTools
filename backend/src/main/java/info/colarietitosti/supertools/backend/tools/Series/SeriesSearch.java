package info.colarietitosti.supertools.backend.tools.Series;

import info.colarietitosti.supertools.backend.tools.Config;
import info.colarietitosti.supertools.backend.tools.FirefoxDriverFactory;
import info.colarietitosti.supertools.backend.tools.Series.Entity.Episode;
import info.colarietitosti.supertools.backend.tools.Series.Entity.Serie;
import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Log
@Component
public class SeriesSearch {

    @Autowired
    Config config;

    @Autowired
    FirefoxDriverFactory firefoxDriverFactory;


    public List<Serie> searchSeriesForDownload(){
        String outPath = config.getSeriesOutPath();
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
        List<String> epiLinks = Collections.synchronizedList(new ArrayList<String>());
        Elements liness = doc.select("tr");
        List<String> lines = liness.stream().filter(l ->
                l.className().contains("vidtodo") ||
                l.className().contains("vshare") ||
                l.className().contains("vidoza")
        ).map( l -> {
            Elements el = l.getElementsByClass("watchlink");
            return el.get(0).attr("href");
        })
        //.limit(60)
        .collect(Collectors.toList());
        doc.getElementsByClass("watchlink");
        try {
            lines.parallelStream().forEach(line -> {
                final FirefoxDriver driver;
                try {
                    driver = firefoxDriverFactory.getFirefoxDriverHeadless();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                driver.get(line);
                try {
                    WebDriverWait wait = new WebDriverWait(driver, 3);
                    wait.until(ExpectedConditions.elementToBeClickable(By.className("push_button")));
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    try { driver.close(); driver.quit(); } catch (SessionNotCreatedException ex) {}
                }
                WebElement el = driver.findElement(By.className("push_button"));
                try {
                    String epiLink = el.getAttribute("href");
                    log.info(epiLink);
                    epiLinks.add(epiLink);
                    if (epiLinks.size() % 15 == 0) {
                        log.info(String.format("added %d links..", epiLinks.size()));
                    }
                    try {
                        driver.close();
                        driver.quit();
                    } catch (Exception e){

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
        return epiLinks;
    }
}
