package info.colarietitosti.supertools.backend.series;

import info.colarietitosti.supertools.backend.config.BackendConfiguration;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static info.colarietitosti.supertools.backend.series.WatchseriesConstants.isAllowedStream;

@Slf4j
@Component
public class WatchseriesSearch {

    public static final String WATCHLINK = "watchlink";
    public static final String HREF = "href";
    public static final String PUSH_BUTTON = "push_button";
    public static final String TR = "tr";
    public static final String LI = "li";
    public static final String A = "a";
    public static final String META = "meta";
    public static final String CONTENT = "content";
    public static final String PATH_SEPARATOR = "/";
    public static final String DONE_PATH = "done" + PATH_SEPARATOR;

    @Autowired
    BackendConfiguration config;

    @Autowired
    StreamServiceDownloaderBackend streamServiceDownloaderBackend;

    private String savePath = "";
    private String doneFilesPath = "";
    private List<String> doneList = null;
    private List<Serie> todo = null;

    private static Episode extractEpisodeFromEpisodeLine(Element episodeLine, Serie serie){
        String epilink = episodeLine.select(A).get(0).attr(HREF);
        String epiNo = episodeLine.select(META).get(0).attr(CONTENT);
        return new Episode(serie, Integer.parseInt(epiNo), epilink);
    }

    public List<Serie> searchSeriesForDownload(){
        initializeSearch();

        todo.stream()
            .filter(Serie::getActive)
            .forEach(serie -> {
                makeSeriesSavePath(serie);
                serie.clearEpisodes();
                List<Episode> episodes = searchEpisodes(serie.getLink(), serie.getNo(), serie);
                episodes.forEach(episode -> {
                    if (episodeNotAlreadyDownloaded(doneList, episode.getSavePath(doneFilesPath))) {
                        serie.addEpisode(episode);
                    }
                });
        });
        return todo;
    }

    private boolean episodeNotAlreadyDownloaded(List<String> doneList, String fileName) {
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
        doneFilesPath = savePath.concat(DONE_PATH);
        new File(doneFilesPath).mkdirs();
    }

    private void refreshConfig() {
        config.loadSeries();
    }

    private void makeSeriesSavePath(Serie s) {
        String serieOutPath = savePath.concat(s.getName()).concat(PATH_SEPARATOR);
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
                .map(serieBlock -> serieBlock.select(LI))
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
        }
        return doc.getElementsByAttributeValue("itemprop","season");
    }

    public void downloadFirstAvailableFromEpiPage(Episode episode){
        Document doc = null;
        try {
            doc = Jsoup.connect(episode.getLink()).get();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        List<String> epiLinks = new ArrayList<String>();
        Elements lines = doc.select(TR);

        List<String> prePageLinks = getPrePageLinks(lines);
        FirefoxDriver driver = null;
        try {
            driver = FirefoxDriverUtils.getFirefoxDriverHeadless();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        boolean success = tryDownloadingFromPrePageLinks(episode, epiLinks, prePageLinks, driver);
        FirefoxDriverUtils.killDriver(driver);
        FirefoxDriverUtils.cleanup();
        if (!success) log.info("search failed :( ");
    }

    private List<String> getPrePageLinks(Elements lines) {
        return lines.stream()
                .filter(line -> isAllowedStream(line.text()))
                .map( l -> {
                    Elements el = l.getElementsByClass(WATCHLINK);
                    return el.get(0).attr(HREF);
                })
                .collect(Collectors.toList());
    }

    private boolean tryDownloadingFromPrePageLinks(Episode episode, List<String> epiLinks, List<String> list, FirefoxDriver driver) {
        for (String line : list){
            try {
                driver.get(line);
                if (FirefoxDriverUtils.tryWaitingForPageToLoad(driver, 3, By.className(PUSH_BUTTON))) {
                    continue;
                }

                String epiLink = extractElementFromPage(driver);

                log.info("trying download from : {}", epiLink);
                if (streamServiceDownloaderBackend.downloadLink(epiLink, episode)){
                    return true;
                }
                epiLinks.add(epiLink);
                logProgress(epiLinks);
            } catch (Exception e) {
                log.error("error downloading {}", e.getMessage());
                continue;
            }
        }
        return false;
    }

    private String extractElementFromPage(FirefoxDriver driver) {
        WebElement el = driver.findElement(By.className(PUSH_BUTTON));
        return el.getAttribute(HREF);
    }

    private void logProgress(List<String> epiLinks) {
        if (!epiLinks.isEmpty() && epiLinks.size() % 5 == 0) {
            log.info(String.format("tryed %d links..", epiLinks.size()));
        }
    }

}
