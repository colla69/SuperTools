package info.colarietitosti.supertools.backend.series;

import info.colarietitosti.supertools.backend.config.BackendConfigutation;
import info.colarietitosti.supertools.backend.series.Entity.Episode;
import info.colarietitosti.supertools.backend.series.Entity.Serie;
import info.colarietitosti.supertools.backend.tools.FirefoxDriverUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class WatchseriesSearch {

    @Autowired
    BackendConfigutation config;

    @Autowired
    StreamServiceDownloaderBackend streamServiceDownloaderBackend;

    private String savePath = "";
    private String doneFilesPath = "";
    private List<String> doneList = null;
    private List<Serie> todo = null;

    private static Episode extractEpisodeFromEpisodeLine(Element episodeLine, Serie serie){
        String epilink = episodeLine.select("a").get(0).attr("href");
        String epiNo = episodeLine.select("meta").get(0).attr("content");
        return new Episode(serie, Integer.parseInt(epiNo), epilink);
    }

    public List<Serie> searchSeriesForDownload(){
        initializeSearch();

        todo.stream()
            .filter(Serie::getActive)
            .forEach(serie -> {
                makeSeriesSavePath(serie);
                serie.clearEpisodes();
                List<Episode> epis = searchEpisodes(serie.getLink(), serie.getNo(), serie);
                epis.forEach(episode -> {
                    String fileName = episode.getSavePath(doneFilesPath);
                    if (episodeAlreadyDownloaded(doneList, fileName)) {
                        serie.addEpisode(episode);
                    }

                });
        });
        return todo;
    }

    private boolean episodeAlreadyDownloaded(List<String> doneList, String fileName) {
        return doneList.parallelStream().noneMatch(d -> d.contains(fileName));
    }

    private void initializeSearch() {
        initPaths();
        refreshConfig();
        todo = loadTodoList();
        doneList = loadDoneList();
    }

    private List<String> loadDoneList() {
        try (Stream<Path> walk = Files.walk(Paths.get(doneFilesPath))) {
            return walk.filter(Files::isRegularFile)
                        .map(x -> x.toString())
                        .collect(Collectors.toList());
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

    private List<Serie> loadTodoList() {
        return config.getSeries();
    }

    private void initPaths() {
        savePath = config.getSeriesOutPath();
        doneFilesPath = savePath.concat("done/");
        new File(doneFilesPath).mkdirs();
    }

    private void refreshConfig() {
        config.loadSeries();
    }

    private void makeSeriesSavePath(Serie s) {
        String serieOutPath = savePath.concat(s.getName()).concat("/");
        new File(serieOutPath).mkdirs();
    }

    public List<Episode> searchEpisodes(String linkpart, Integer sno, Serie serie){
        List<Episode> result = new ArrayList<>();
        String link = config.getCompleteWatchSeriesLink(linkpart);

        Elements table = extractEpisodeTableFromSeriePage(link);
        return loadSingleEpisodeLinks(serie, table);
    }

    private List<Episode> loadSingleEpisodeLinks(Serie serie, Elements table) {
        List<Elements> seriesTables = table
                .stream()
                .filter(serieBlock -> {
                    Integer blockSerieNumber = Integer.parseInt(serieBlock.text().substring(7,9).trim());
                    return serie.isSerieNumber(blockSerieNumber);
                })
                .map(serieBlock -> serieBlock.select("li"))
                .collect(Collectors.toList());

        Elements serieTablePart = new Elements();
        if (!seriesTables.isEmpty()){
            serieTablePart = seriesTables.get(0);
        }

        return serieTablePart.stream()
                .map(episodeLine -> extractEpisodeFromEpisodeLine(episodeLine, serie))
                .collect(Collectors.toList());
    }

    private Elements extractEpisodeTableFromSeriePage(String link) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            return null;
            //e.printStackTrace();
        }
        return doc.getElementsByAttributeValue("itemprop","season");
    }

    public void downloadFirstAvailableFromEpiPage(Episode episode){
        Document doc = null;
        try {
            doc = Jsoup.connect(episode.getLink()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> epiLinks = Collections.synchronizedList(new ArrayList<String>());
        Elements liness = doc.select("tr");
        boolean success = false;
        List<String> list = getPrePageLinks(liness);
        FirefoxDriver driver = null;
        try {
            driver = FirefoxDriverUtils.getFirefoxDriverHeadless();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String line : list){
            try {
                driver.get(line);
                if (FirefoxDriverUtils.tryWaitingForPageToLoad(driver, 3, By.className("push_button"))) continue;

                String epiLink = extractElementFromPage(driver);

                log.info("trying download from : {}", epiLink);
                if (streamServiceDownloaderBackend.downloadLink(epiLink, episode)){
                    success = true;
                    break;
                }
                epiLinks.add(epiLink);
                logProgress(epiLinks);
            } catch (Exception e) {
                log.error("error downloading {}", e.getMessage());
                continue;
            }
//            try {
//                sleep(2000);
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
        }
        FirefoxDriverUtils.killDriver(driver);
        FirefoxDriverUtils.cleanup();
        if (!success) log.info("search failed :( ");
    }

    private String extractElementFromPage(FirefoxDriver driver) {
        WebElement el = driver.findElement(By.className("push_button"));
        return el.getAttribute("href");
    }

    private void logProgress(List<String> epiLinks) {
        if (!epiLinks.isEmpty() && epiLinks.size() % 5 == 0) {
            log.info(String.format("tryed %d links..", epiLinks.size()));
        }
    }

    private List<String> getPrePageLinks(Elements liness) {
        return liness.stream()
                .filter(line -> WatchseriesConstants.isAllowedStream(line.text()))
                .map( l -> {
                    Elements el = l.getElementsByClass("watchlink");
                    return el.get(0).attr("href");
                })
                .collect(Collectors.toList());
    }

}
