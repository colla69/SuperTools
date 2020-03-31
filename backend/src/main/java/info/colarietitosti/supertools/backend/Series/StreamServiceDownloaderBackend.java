package info.colarietitosti.supertools.backend.Series;

import info.colarietitosti.supertools.backend.Series.Entity.Episode;
import info.colarietitosti.supertools.backend.config.BackendConfigutation;
import info.colarietitosti.supertools.backend.downloaderQueue.DownloadQueue;
import info.colarietitosti.supertools.backend.tools.FileDownloader;
import info.colarietitosti.supertools.backend.tools.FirefoxDriverUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StreamServiceDownloaderBackend {

    @Autowired
    DownloadQueue downloadQueue;

    @Autowired
    BackendConfigutation config;

    public Boolean downloadLink(String link, Episode episode) {
        if (link.contains(WatchseriesConstants.VIDTODO)) {
            return downloadFromVidotodo(episode, link);
        }
        if (link.contains(WatchseriesConstants.VIDUP)) {
            return downloadFromVidup(episode, link);
        }
        if (link.contains(WatchseriesConstants.VIDOZA)) {
            return downloadFromVidoza(link, episode);
        }
        if (link.contains(WatchseriesConstants.VSHARE)) {
            return downloadFromVshare(link, episode);
        }
        return false;
    }

    public Boolean downloadFromVshare(List<String> watchLinks, Episode episode) {
        log.debug("\tStarting vshare search...");
        watchLinks = filterLinksByName(watchLinks, "vshare.eu");
        for (String link : watchLinks) {
            if (downloadFromVshare(link, episode)) {
                break;
            } else {
                continue;
            }
        }
        log.debug("\tNothing to download from vshare");
        return Boolean.FALSE;
    }

    private Boolean downloadFromVshare(String link, Episode episode) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
            Element button = doc.getElementsByClass("center").get(0);
            FormElement form = (FormElement) button;
            Document videopage = form.submit().execute().parse();
            Element source = videopage.selectFirst("source");
            String dlink = source.attr("src");
            if (StreamServiceDownloaderBackend.this.downloadFromVshare(episode, dlink)) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return Boolean.FALSE;
        }
    }

    private boolean downloadFromVshare(Episode episode, String dlink) throws IOException {
        return checkAndDownload(dlink, episode);
    }

    public Boolean downloadFromVidoza(List<String> watchLinks, Episode episode) {
        log.debug("\tStarting vidoza search...");
        watchLinks = filterLinksByName(watchLinks, "vidoza.net");
        for (String link : watchLinks) {
            Boolean x = downloadFromVidoza(link, episode);
            if (x != null) return x;
        }
        log.debug("\tNothing to download from vidoza");
        return Boolean.FALSE;
    }

    private Boolean downloadFromVidoza(String link, Episode episode) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
            Elements d_link = doc.select("source");
            try {
                String dlink = d_link.get(0).attr("src");
                return downloadFromVidoza(episode, dlink);
            } catch (MalformedURLException | IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Boolean downloadFromVidoza(Episode episode, String dlink) throws IOException {
        if (checkAndDownload(dlink, episode)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public Boolean downloadFromVidotodo(List<String> watchLinks, Episode episode) {
        log.debug("\tStarting vidtodo search...");
        watchLinks = filterLinksByName(watchLinks, "vidtodo.com");

        for (String link : watchLinks) {
            if (downloadFromVidotodo(episode, link)) {
                break;
            } else {
                continue;
            }
        }
        log.debug("\tNothing to download from vidtodo");
        return Boolean.FALSE;
    }

    private Boolean downloadFromVidotodo(Episode episode, String link) {
        FirefoxDriver driver = null;
        try {
            driver = FirefoxDriverUtils.getFirefoxDriverHeadless();
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        Document doc = null;
        try {
            driver.get(link);
            WebDriverWait wait = new WebDriverWait(driver, 15);
            wait.until(ExpectedConditions.elementToBeClickable(By.className("vjs-big-play-button")));
            WebElement el = driver.findElement(By.className("vjs-big-play-button"));
            driver.executeScript("arguments[0].click();", el);
            WebElement video = driver.findElement(By.className("vjs-tech"));
            String dlink = video.getAttribute("src");
            if (checkAndDownload(dlink, episode)) {
                FirefoxDriverUtils.killDriver(driver);
                return Boolean.TRUE;
            } else {
                FirefoxDriverUtils.killDriver(driver);
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            FirefoxDriverUtils.killDriver(driver);
            return Boolean.FALSE;
        }
    }

    public Boolean downloadFromVidup(List<String> watchLinks, Episode episode) {
        log.debug("\tStarting vidup search...");
        watchLinks = filterLinksByName(watchLinks, "vidup.io");
        for (String link : watchLinks) {
            if (downloadFromVidup(episode, link)) {
                break;
            } else {
                continue;
            }
        }
        log.debug("\tNothing to download from vidup");
        return Boolean.FALSE;
    }

    private Boolean downloadFromVidup(Episode episode, String link) {
        FirefoxDriver driver = null;
        try {
            driver = FirefoxDriverUtils.getFirefoxDriverHeadless();
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        Document doc = null;
        try {
            driver.get(link);
            //log.info(driver.toString());
            WebDriverWait wait = new WebDriverWait(driver, 15);
            wait.until(ExpectedConditions.elementToBeClickable(By.className("vjs-big-play-button")));
            WebElement el = driver.findElement(By.className("vjs-big-play-button"));
            driver.executeScript("arguments[0].click();", el);
            WebElement video = driver.findElement(By.className("vjs-tech"));
            String dlink = video.getAttribute("src");
            if (checkAndDownload(dlink, episode)) {
                FirefoxDriverUtils.killDriver(driver);
                return Boolean.TRUE;
            } else {
                FirefoxDriverUtils.killDriver(driver);
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            FirefoxDriverUtils.killDriver(driver);
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

    private boolean checkAndDownload(String dlink, Episode episode) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(dlink).openConnection();
        connection.setRequestMethod("HEAD");
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            log.debug("\tadding to Download Queue: ".concat(dlink));
            String donePath = config.getSeriesOutPath().concat("done/").concat(episode.getNameWithFileExt());
            String savePath = config.getSeriesOutPath()
                    .concat(episode.getSerie().getName()).concat("/s")
                    .concat(episode.getSerie().getNo().toString()).concat("/");
            downloadQueue.put(new FileDownloader(dlink, savePath, episode.getNameWithFileExt(), "touch \"".concat(donePath).concat("\"")));
            return true;
        }
        return false;
    }

    private List<String> filterLinksByName(List<String> watchLinks, String s) {

        try {
            watchLinks = watchLinks.parallelStream().filter(link -> link.contains(s)).collect(Collectors.toList());
            return watchLinks;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
