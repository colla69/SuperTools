package info.colarietitosti.supertools.backend.series;

import info.colarietitosti.supertools.backend.config.BackendConfiguration;
import info.colarietitosti.supertools.backend.downloaderQueue.DownloadQueue;
import info.colarietitosti.supertools.backend.series.Entity.Episode;
import info.colarietitosti.supertools.backend.tools.FileDownloader;
import info.colarietitosti.supertools.backend.tools.FirefoxDriverUtils;
import info.colarietitosti.supertools.backend.tools.VideoPlaylistDownloader;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
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

    private final String REQUEST_HEAD = "HEAD";
    private final String SOURCE = "source";
    private final String SRC = "src";
    private final String M3U8_EXT = ".m3u8";
    public static final String VJS_BUTTON_ICON = "vjs-button-icon";
    public static final String VJS_TECH = "vjs-tech";
    private final String CENTER = "center";
    private final String CSS_JW_BUTTON_COLOR = "div.jw-icon-display.jw-button-color.jw-reset";
    private final String CSS_JW_VIDEO = "jw_video";
    private final String JS_BUTTON_CLICK = "arguments[0].click();";
    private final Pattern HTTP_M3U8_PATTERN = Pattern.compile("http://.*.m3u8");
    private final Pattern HTTPS_M3U8_PATTERN = Pattern.compile("https://.*.m3u8");
    private final Integer WAIT_TIMEOUT = 10;
    @Autowired
    DownloadQueue downloadQueue;
    @Autowired
    BackendConfiguration config;

    public Boolean downloadLink(String link, Episode episode) {
        if (link.contains(WatchseriesConstants.VIDTODO)) {
            return downloadFromVidotodo(link, episode);
        }
        if (link.contains(WatchseriesConstants.VIDUP)) {
            return downloadFromVidup(link, episode);
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
            Element button = doc.getElementsByClass(CENTER).get(0);
            FormElement form = (FormElement) button;
            Document videopage = form.submit().execute().parse();
            Element source = videopage.selectFirst(SOURCE);
            String dlink = source.attr(SRC);
            if (checkAndDownload(dlink, episode,false)) {
                return Boolean.TRUE;
            }
        } catch (HttpStatusException e) {
            log.warn("{} on {}", e.getStatusCode(), e.getUrl());
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        return Boolean.FALSE;
    }

    private Boolean downloadFromVidoza(String link, Episode episode) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
            Elements d_link = doc.select(SOURCE);
            try {
                String dlink = d_link.get(0).attr(SRC);
                if (checkAndDownload(dlink, episode, false)) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            } catch (MalformedURLException | IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        } catch (HttpStatusException e) {
            log.warn("{} on {}", e.getStatusCode(), e.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Boolean downloadFromVidotodo(String link, Episode episode) {
        FirefoxDriver driver = null;
        try {
            driver = FirefoxDriverUtils.getFirefoxDriverHeadless();
        } catch (Exception e) {
            return Boolean.FALSE;
        }
        Document doc = null;
        try {
            driver.get(link);

            FirefoxDriverUtils.tryWaitingForPageToLoad(driver, WAIT_TIMEOUT, By.cssSelector(CSS_JW_BUTTON_COLOR) );
            WebElement el = driver.findElement(By.cssSelector(CSS_JW_BUTTON_COLOR));
            driver.executeScript(JS_BUTTON_CLICK, el);
            WebElement video = driver.findElement(By.className(CSS_JW_VIDEO));
            String dlink = video.getAttribute(SRC);
            if (checkAndDownload(dlink, episode, false)) {
                FirefoxDriverUtils.killDriver(driver);
                return Boolean.TRUE;
            }
        } catch (HttpStatusException e) {
            log.warn("{} on {}", e.getStatusCode(), e.getUrl());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        FirefoxDriverUtils.killDriver(driver);
        return Boolean.FALSE;
    }

    private Boolean downloadFromVidup(String link, Episode episode) {
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
            FirefoxDriverUtils.tryWaitingForPageToLoad(driver, WAIT_TIMEOUT, By.className(VJS_BUTTON_ICON));

            WebElement el = driver.findElement(By.className(VJS_BUTTON_ICON));
            driver.executeScript(JS_BUTTON_CLICK, el);
            WebElement video = driver.findElement(By.className(VJS_TECH));
            String dlink = video.getAttribute(SRC);
            if (checkAndDownload(dlink, episode, false)) {
                FirefoxDriverUtils.killDriver(driver);
                return Boolean.TRUE;
            }
        } catch (HttpStatusException e) {
            log.warn("{} on {}", e.getStatusCode(), e.getUrl());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        FirefoxDriverUtils.killDriver(driver);
        return Boolean.FALSE;
    }

    private Boolean downloadFromSimpleBlobSource(Episode episode, String link){
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
            String dlink = extractM3u8LinkFromHtmlpage(doc);
            if (checkAndDownload(dlink, episode, true)) {
                return Boolean.TRUE;
            }
        } catch (HttpStatusException e) {
            log.warn("{} on {}", e.getStatusCode(), e.getUrl());
        } catch (IOException e) {
            log.error("this should not happen ", e);
        }
        return Boolean.FALSE;
    }

    private String extractM3u8LinkFromHtmlpage(Document doc) {
        Integer plausibleStartIndex = doc.toString().indexOf(M3U8_EXT)-500;
        Matcher matcher = HTTP_M3U8_PATTERN.matcher(doc.toString());

        if (matcher.find()){
            return matcher.group(0);
        } else {
            matcher = HTTPS_M3U8_PATTERN.matcher(doc.toString());
            if (matcher.find(plausibleStartIndex)){
                return matcher.group(0);
            }
        }
        return "";
    }

    private boolean checkAndDownload(String dlink, Episode episode, Boolean playlist) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(dlink).openConnection();
        connection.setRequestMethod(REQUEST_HEAD);
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
