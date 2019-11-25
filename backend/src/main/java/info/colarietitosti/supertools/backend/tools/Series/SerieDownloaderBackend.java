package info.colarietitosti.supertools.backend.tools.Series;

import info.colarietitosti.supertools.backend.tools.Config;
import info.colarietitosti.supertools.backend.downloaderQueue.DownloadQueue;
import info.colarietitosti.supertools.backend.tools.FileDownloader;
import info.colarietitosti.supertools.backend.tools.FirefoxDriverFactory;
import info.colarietitosti.supertools.backend.tools.Series.Entity.Episode;
import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Log
@Component
public class SerieDownloaderBackend {

    @Autowired
    DownloadQueue downloadQueue;

    @Autowired
    FirefoxDriverFactory firefoxDriverFactory;

    @Autowired
    Config config;

    private ExecutorService executor
            = Executors.newSingleThreadExecutor();

    public Future<Boolean> asyncDownloadFromVshare(List<String> watchLinks, Episode episode){
        return executor.submit(() -> downloadFromVshare(watchLinks, episode));
    }

    public Boolean downloadFromVshare(List<String> watchLinks, Episode episode){
        log.info("\tStarting vshare search...");
        watchLinks = filterLinksByName(watchLinks, "vshare.eu");
        for (String link : watchLinks){
            Document doc = null;
            try {
                doc = Jsoup.connect(link).get();
                Element button = doc.getElementsByClass("center").get(0);
                FormElement form = (FormElement) button;
                Document videopage = form.submit().execute().parse();
                Element source = videopage.selectFirst("source");
                try{
                    String dlink = source.attr("src");
                    if (checkAndDownload(dlink, episode)){
                        return Boolean.TRUE;
                    } else {
                        continue;
                    }
                } catch (MalformedURLException | IndexOutOfBoundsException | NullPointerException ex){
                    continue;
                }
            } catch (IOException | ClassCastException e) {
                e.printStackTrace();
            }
        }


        log.info("\tNothing to download from vshare");
        return Boolean.FALSE;
    }

    public Future<Boolean> asyncDownloadFromVidoza(List<String> watchLinks, Episode episode){
        return executor.submit(() -> downloadFromVidoza(watchLinks, episode));
    }

    public Boolean downloadFromVidoza(List<String> watchLinks, Episode episode){
        log.info("\tStarting vidoza search...");
        watchLinks = filterLinksByName(watchLinks, "vidoza.net");
        for (String link : watchLinks){
            Document doc = null;
            try {
                doc = Jsoup.connect(link).get();
                Elements d_link = doc.select("source");
                try{
                    String dlink = d_link.get(0).attr("src");
                    if (checkAndDownload(dlink, episode)){
                        return Boolean.TRUE;
                    } else {
                        continue;
                    }
                } catch (MalformedURLException | IndexOutOfBoundsException ex){
                    ex.printStackTrace();;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("\tNothing to download from vidoza");
        return Boolean.FALSE;
    }

    public Future<Boolean> asyncDownloadFromVidtodo(List<String> watchLinks, Episode episode){
        return executor.submit(() -> downloadFromVidoza(watchLinks, episode));
    }

    public Boolean downloadFromVidotodo(List<String> watchLinks, Episode episode){
        log.info("\tStarting vidtodo search...");
        watchLinks = filterLinksByName(watchLinks, "vidtodo.com");
        FirefoxDriver driver = null;
        try {
            driver = firefoxDriverFactory.getFirefoxDriverHeadless();
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        for (String link : watchLinks){
            Document doc = null;
            try {
                try {
                    driver.get(link);
                    log.info(driver.toString());
                    WebDriverWait wait = new WebDriverWait(driver, 15);
                    wait.until(ExpectedConditions.elementToBeClickable(By.className("vjs-big-play-button")));
                    WebElement el = driver.findElement(By.className("vjs-big-play-button"));
                    driver.executeScript("arguments[0].click();", el);
                    WebElement video = driver.findElement(By.className("vjs-tech"));
                    String dlink = video.getAttribute("src");

                    if (checkAndDownload(dlink, episode)){
                        driver.close();
                        return Boolean.TRUE;
                    } else {
                        continue;
                    }

                } catch (MalformedURLException | IndexOutOfBoundsException | TimeoutException ex){
                    ex.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        driver.close();
        log.info("\tNothing to download from vidtodo");
        return Boolean.FALSE;
    }

    private boolean checkAndDownload(String dlink, Episode episode) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(dlink).openConnection();
        connection.setRequestMethod("HEAD");
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            log.info("\tadding to Download Queue: ".concat(dlink));
            String donePath = config.getSeriesOutPath().concat("done/").concat(episode.getName());
            String savePath = config.getSeriesOutPath()
                    .concat(episode.getSerie().getName()).concat("/s")
                    .concat(episode.getSerie().getNo().toString()).concat("/");
            downloadQueue.put(new FileDownloader(dlink, savePath, episode.getName(),"touch \"".concat(donePath).concat("\"")));
            return true;
        }
        return false;
    }

    private List<String> filterLinksByName(List<String> watchLinks, String s) {
        watchLinks = watchLinks.parallelStream().filter(link -> link.contains(s)).collect(Collectors.toList());
        return watchLinks;
    }
}
