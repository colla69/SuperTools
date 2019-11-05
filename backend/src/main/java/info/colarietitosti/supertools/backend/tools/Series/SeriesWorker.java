package info.colarietitosti.supertools.backend.tools.Series;

import info.colarietitosti.supertools.backend.downloaderQueue.DownloadQueue;
import info.colarietitosti.supertools.backend.tools.FileDownloader;
import info.colarietitosti.supertools.backend.tools.Series.Entity.Serie;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Log
@Component
public class SeriesWorker {

    @Autowired
    SeriesSearch seriesSearch;

    @Autowired
    SerieDownloaderBackend serieDownloaderBackend;

    @Autowired
    DownloadQueue downloadQueue;

    public void updateSeries(){
        downloadQueue.put(new FileDownloader("https://cv.colarietitosti.info/files/CV_Docs.zip#editwhatever", "~/", "test"));

        List<Serie> series = seriesSearch.searchSeriesForDownload();
        series.forEach(s ->{
            s.getEpis().forEach(e -> {
                log.info("Starting Episode Search for: ".concat(e.getName()));
                List<String> links = seriesSearch.getLinksFromEpiPage(e);
                Boolean res =
                        serieDownloaderBackend.downloadFromVshare(links, e) ||
                        serieDownloaderBackend.downloadFromVidoza(links, e) ||
                        serieDownloaderBackend.downloadFromVidotodo(links, e);
                /*
                Future<Boolean> futureVidoza = serieDownloaderBackend.asyncDownloadFromVidoza(links);
                Future<Boolean> futureVshare = serieDownloaderBackend.asyncDownloadFromVshare(links);
                while (!(futureVidoza.isDone() && futureVshare.isDone())) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                */
                log.info("Search completed!");
            });
        });
    }
}
