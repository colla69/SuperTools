package info.colarietitosti.supertools.backend.downloaderQueue;

import info.colarietitosti.supertools.backend.tools.Config;
import info.colarietitosti.supertools.backend.tools.FileDownloader;
import info.colarietitosti.supertools.backend.tools.Music.MusicDownload;
import info.colarietitosti.supertools.backend.tools.Series.SeriesWorker;
import info.colarietitosti.supertools.backend.tools.ShellExecuter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
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

@Log
@Controller
public class DownloadQueueRest {

    @Autowired
    DownloadQueue downloadQueue;

    @Autowired
    SeriesWorker seriesWorker;

    @Autowired
    Config config;

    @Autowired
    MusicDownload musicDownload;

    @GetMapping("/queues")
    @ResponseBody
    public DownloadQueueDO getAllApps() {
        DownloadQueueDO result =  new DownloadQueueDO();
        result.setDone(downloadQueue.getDoneQueue().stream().map(FileDownloader::getTitle).collect(Collectors.toList()));
        result.setDownloading(downloadQueue.getDownloadingQueue().stream().map(FileDownloader::getTitle).collect(Collectors.toList()));
        result.setTodo(downloadQueue.getToDoQueue().stream().map(FileDownloader::getTitle).collect(Collectors.toList()));
        result.setSyncRunning(ShellExecuter.isRunning("rsync"));
        result.setQueueRunning(downloadQueue.getRunning());
        result.setSearchRunning(seriesWorker.isRunning());
        return result;
    }

    @GetMapping("/clearQueue")
    public String clearQueue(){
        downloadQueue.clearDone();
        return String.valueOf(200);
    }

    @PostMapping("/startDownloads")
    @ResponseBody
    public String startUpdate(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                seriesWorker.updateSeries();
            }
        });
        t.setDaemon(true);
        t.start();
        return String.valueOf(200);
    }

    @PostMapping(value = "/startMusicDownloads", consumes = "application/json")
    @ResponseBody
    public String startMusicDownloads(@RequestBody Map<String, String> payload){
        System.out.println(payload);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                musicDownload.downloadAndTag(payload.get("artist"), payload.get("linkpart"));
            }
        });
        t.setDaemon(true);
        t.start();
        return String.valueOf(200);
    }

    @PostMapping(value = "/startMusicTag", consumes = "application/json")
    @ResponseBody
    public String startMusicTags(@RequestBody List<String> payload){
        System.out.println(payload);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("starting tag process");
                payload.forEach(artist -> musicDownload.tag(artist));
                log.info("music tag terminated");
            }
        });
        t.setDaemon(true);
        t.start();
        return String.valueOf(200);
    }

    @GetMapping(value = "/downloadedArtists")
    @ResponseBody
    public List<String> startMusicDownloads()  {
        List<String> artists = new ArrayList<>();
        String musicOutPath = config.getMusicOutPath().concat("downloads/");
        try {
            Files.list(new File(musicOutPath).toPath())
                    .limit(10)
                    .forEach(path -> {
                        artists.add(path.getFileName().toString());
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return artists;
    }

}
