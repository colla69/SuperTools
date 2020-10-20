package info.colarietitosti.supertools.backend.series;

import info.colarietitosti.supertools.backend.config.profiling.Profiled;
import info.colarietitosti.supertools.backend.downloaderQueue.DownloadQueue;
import info.colarietitosti.supertools.backend.series.Entity.Serie;
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
    WatchseriesSearch watchseriesSearch;

    @Autowired
    StreamServiceDownloaderBackend streamServiceDownloaderBackend;

    @Autowired
    DownloadQueue downloadQueue;

    private boolean running;
    private List<Serie> series = null;

    @Profiled
    public void updateSeries() {
        startRunning();
        series = watchseriesSearch.searchSeriesForDownload();
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
        this.series.forEach(serie -> {
            serie.getEpis().forEach(episode -> {
                log.info("Starting Episode Search for: ".concat(episode.getNameWithFileExt()));
                watchseriesSearch.downloadFirstAvailableFromEpiPage(episode);
                log.info("Search completed!");
            });
        });
    }

    private void stopRunning() {
        this.running = false;
    }
}