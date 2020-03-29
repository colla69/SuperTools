package info.colarietitosti.supertools.backend.tools.Series;

import info.colarietitosti.supertools.backend.downloaderQueue.DownloadQueue;
import info.colarietitosti.supertools.backend.tools.Series.Entity.Serie;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Log @Getter
@Component
public class SeriesWorker {

    @Autowired
    SeriesSearch seriesSearch;

    @Autowired
    SerieDownloaderBackend serieDownloaderBackend;

    @Autowired
    DownloadQueue downloadQueue;

    private boolean running;

    public void updateSeries(){
        this.running = true;
        List<Serie> series = seriesSearch.searchSeriesForDownload();
        series.forEach(s ->{
            s.getEpis().forEach(e -> {
                log.info("Starting Episode Search for: ".concat(e.getName()));
                List<String> links = seriesSearch.getLinksFromEpiPage(e);
                log.info("Starting Download Search for: ".concat(e.getName()));
                Boolean res =
                        serieDownloaderBackend.downloadFromVidotodo(links, e) ||
                        serieDownloaderBackend.downloadFromVidup(links, e) ||
                        serieDownloaderBackend.downloadFromVshare(links, e) ||
                        serieDownloaderBackend.downloadFromVidoza(links, e) ;
                log.info("Search completed!");
            });
        });
        this.running = false;
    }
}