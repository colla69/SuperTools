package info.colarietitosti.supertools.backend.downloaderQueue;

import info.colarietitosti.supertools.backend.config.BackendConfigutation;
import info.colarietitosti.supertools.backend.music.MusicDownloader;
import info.colarietitosti.supertools.backend.series.SeriesWorker;
import info.colarietitosti.supertools.backend.tools.FileDownloader;
import info.colarietitosti.supertools.backend.tools.ShellExecuter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class DownloaderFacade {

    public static final String ALBUM = "album";
    public static final String ARTIST = "artist";
    public static final String LINKPART = "linkpart";
    public static final String TITLE = "title";
    public static final String RSYNC = "rsync";

    @Autowired
    DownloadQueue downloadQueue;

    @Autowired
    SeriesWorker seriesWorker;

    @Autowired
    BackendConfigutation config;

    @Autowired
    MusicDownloader musicDownloader;

    @GetMapping("/queues")
    @ResponseBody
    public DownloadQueueDO getAllApps() {
        DownloadQueueDO result =  new DownloadQueueDO();
        result.setDone(downloadQueue.getDoneQueue().stream().map(FileDownloader::getTitle).collect(Collectors.toList()));
        result.setDownloading(downloadQueue.getDownloadingQueue().stream().map(FileDownloader::getTitle).collect(Collectors.toList()));
        result.setTodo(downloadQueue.getToDoQueue().stream().map(FileDownloader::getTitle).collect(Collectors.toList()));
        result.setSyncRunning(ShellExecuter.isRunning(RSYNC));
        result.setQueueRunning(downloadQueue.getRunning());
        result.setSearchRunning(seriesWorker.isRunning());
        return result;
    }

    @GetMapping("/clearQueue")
    public HttpStatus clearQueue(){
        downloadQueue.clearDone();
        return HttpStatus.OK;
    }

    @PostMapping("/startDownloads")
    @ResponseBody
    public HttpStatus startUpdate(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                seriesWorker.updateSeries();
            }
        });
        t.setDaemon(true);
        t.start();
        return HttpStatus.OK;
    }

    @PostMapping(value = "/startArtistDownload", consumes = "application/json")
    @ResponseBody
    public HttpStatus startArtistDownload(@RequestBody Map<String, String> payload){
        Thread t = new Thread(() -> musicDownloader.downloadArtistAndTag(
                payload.get(ARTIST),
                payload.get(LINKPART)
        ));
        t.setDaemon(true);
        t.start();
        return HttpStatus.OK;
    }

    @PostMapping(value = "/startAlbumDownload", consumes = "application/json")
    @ResponseBody
    public HttpStatus startAlbumDownload(@RequestBody Map<String, String> payload){
        Thread t = new Thread(() -> musicDownloader.downloadAlbumAndTag(
                payload.get(ARTIST),
                payload.get(ALBUM),
                payload.get(LINKPART)
        ));
        t.setDaemon(true);
        t.start();
        return HttpStatus.OK;
    }

    @PostMapping(value = "/startTrackDownload", consumes = "application/json")
    @ResponseBody
    public HttpStatus startSingleTrackDownload(@RequestBody Map<String, String> payload){
        Thread t = new Thread(() -> musicDownloader.downloadSingleTrackAndTag(
                payload.get(ARTIST),
                payload.get(ALBUM),
                payload.get(TITLE)
        ));
        t.setDaemon(true);
        t.start();
        return HttpStatus.OK;
    }

    @PostMapping(value = "/startMusicTag", consumes = "application/json")
    @ResponseBody
    public HttpStatus startMusicTags(@RequestBody List<String> payload){
        System.out.println(payload);
        Thread t = new Thread(() -> {
            log.info("starting tag process");
            payload.forEach(artist -> musicDownloader.tag(artist));
            log.info("music tag terminated");
        });
        t.setDaemon(true);
        t.start();
        return HttpStatus.OK;
    }

    @GetMapping(value = "/downloadedArtists")
    @ResponseBody
    public List<String> startMusicDownloads()  {
        List<String> artists = new ArrayList<>();
        String musicOutPath = config.getMusicOutPath().concat("downloads/");
        try {
            Files.list(new File(musicOutPath).toPath())
                    .forEach(path -> {
                        artists.add(path.getFileName().toString());
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return artists;
    }
}
