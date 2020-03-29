package info.colarietitosti.supertools.backend.tools.Series;

import info.colarietitosti.supertools.backend.downloaderQueue.DownloadQueue;
import info.colarietitosti.supertools.backend.tools.Series.Entity.Serie;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Getter
@Component
public class SeriesWorker {

    @Autowired
    SeriesSearch seriesSearch;

    @Autowired
    SerieDownloaderBackend serieDownloaderBackend;

    @Autowired
    DownloadQueue downloadQueue;

    private boolean running;
    private List<Serie> series = null;

    public void updateSeries() {
        startRunning();
        series = seriesSearch.searchSeriesForDownload();
        if (seriesWereFound()) {
            downloadFoundSeries();
        }
        stopRunning();
    }

    private void startRunning() {
        this.running = true;
    }

    private boolean seriesWereFound() {
        return false == this.series.isEmpty();
    }

    private void downloadFoundSeries() {
        this.series.forEach(s -> {
            s.getEpis().forEach(e -> {
                log.info("Starting Episode Search for: ".concat(e.getName()));
                List<String> links = seriesSearch.getLinksFromEpiPage(e);
                log.info("Starting Download Search for: ".concat(e.getName()));
                Boolean res =
                        serieDownloaderBackend.downloadFromVidotodo(links, e) ||
                                serieDownloaderBackend.downloadFromVidup(links, e) ||
                                serieDownloaderBackend.downloadFromVshare(links, e) ||
                                serieDownloaderBackend.downloadFromVidoza(links, e);
                log.info("Search completed!");
            });
        });
    }

    private void stopRunning() {
        this.running = false;
    }
}