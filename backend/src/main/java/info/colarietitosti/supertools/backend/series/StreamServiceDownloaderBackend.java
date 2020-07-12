package info.colarietitosti.supertools.backend.series;

import info.colarietitosti.supertools.backend.config.BackendConfigutation;
import info.colarietitosti.supertools.backend.downloaderQueue.DownloadQueue;
import info.colarietitosti.supertools.backend.series.Entity.Episode;
import info.colarietitosti.supertools.backend.tools.FileDownloader;
import info.colarietitosti.supertools.backend.tools.FirefoxDriverUtils;
import info.colarietitosti.supertools.backend.tools.VideoPlaylistDownloader;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (link.contains(WatchseriesConstants.VIDLOX) ||
            link.contains(WatchseriesConstants.VIDEOBIN)) {
            return downloadFromSimpleBlobSource(episode, link);
        }
        return false;
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
            if (checkAndDownload(dlink, episode,false)) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return Boolean.FALSE;
        }
    }

    private Boolean downloadFromVidoza(String link, Episode episode) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
            Elements d_link = doc.select("source");
            try {
                String dlink = d_link.get(0).attr("src");
                if (checkAndDownload(dlink, episode, false)) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            } catch (MalformedURLException | IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Boolean downloadFromVidotodo(Episode episode, String link) {
        FirefoxDriver driver = null;
        try {
            driver = FirefoxDriverUtils.getFirefoxDriverHeadless();
        } catch (Exception e) {
            return Boolean.FALSE;
        }
        Document doc = null;
        try {
            driver.get(link);

            FirefoxDriverUtils.tryWaitingForPageToLoad(driver, 15, By.cssSelector("div.jw-icon.jw-icon-display.jw-button-color") );
            WebElement el = driver.findElement(By.cssSelector("div.jw-icon.jw-icon-display.jw-button-color"));
            driver.executeScript("arguments[0].click();", el);
            WebElement video = driver.findElement(By.className("jw-video"));
            String dlink = video.getAttribute("src");
            if (checkAndDownload(dlink, episode, false)) {
                FirefoxDriverUtils.killDriver(driver);
                return Boolean.TRUE;
            } else {
                FirefoxDriverUtils.killDriver(driver);
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            FirefoxDriverUtils.killDriver(driver);
            return Boolean.FALSE;
        }
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
            FirefoxDriverUtils.tryWaitingForPageToLoad(driver, 15, By.className("vjs-big-play-button"));

            WebElement el = driver.findElement(By.className("vjs-big-play-button"));
            driver.executeScript("arguments[0].click();", el);
            WebElement video = driver.findElement(By.className("vjs-tech"));
            String dlink = video.getAttribute("src");
            if (checkAndDownload(dlink, episode, false)) {
                FirefoxDriverUtils.killDriver(driver);
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        FirefoxDriverUtils.killDriver(driver);
        return Boolean.FALSE;
    }

    private Boolean downloadFromSimpleBlobSource(Episode episode, String link){
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
            String dlink = extractM38uLinkFromHtmlpage(doc);
            if (checkAndDownload(dlink, episode, true)) {
                return Boolean.TRUE;
            }
        } catch (IOException e) {
            log.error("this should not happen ", e);
        }
        return Boolean.FALSE;
    }

    private String extractM38uLinkFromHtmlpage(Document doc) {
        Integer plausibleStartIndex = doc.toString().indexOf(".m38u")-500;

        Pattern pattern = Pattern.compile("https://.*.m3u8");
        Matcher matcher = pattern.matcher(doc.toString());

        if (matcher.find()){
            return matcher.group(0);
        } else {
            pattern = Pattern.compile("https://.*.m3u8");
            matcher = pattern.matcher(doc.toString());

            if (matcher.find(plausibleStartIndex)){
                return matcher.group(0);
            }
        }
        return "";
    }

    private boolean checkAndDownload(String dlink, Episode episode, Boolean playlist) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(dlink).openConnection();
        connection.setRequestMethod("HEAD");
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            log.debug("\tadding to Download Queue: ".concat(dlink));
            String savePath = makeSavePath(episode);
            String touchCmd = makeTouchCmd(episode);
            if (playlist){
                downloadQueue.put(new VideoPlaylistDownloader(dlink, savePath, episode.getNameWithFileExt(), touchCmd));
            } else {
                downloadQueue.put(new FileDownloader(dlink, savePath, episode.getNameWithFileExt(), touchCmd));
            }
            return true;
        }
        return false;
    }

    private String makeTouchCmd(Episode episode){
        String donePath = makeDonePath(episode);
        return  "touch \"".concat(donePath).concat("\"");
    }

    private String makeDonePath(Episode episode) {
        return  config.getSeriesOutPath().concat("done/").concat(episode.getNameWithFileExt());
    }

    private String makeSavePath(Episode episode) {
        return config.getSeriesOutPath()
                .concat(episode.getSerie().getName()).concat("/s")
                .concat(episode.getSerie().getNo().toString()).concat("/");
    }
}
