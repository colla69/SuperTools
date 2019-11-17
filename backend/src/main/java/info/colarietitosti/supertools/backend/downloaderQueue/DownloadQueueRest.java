package info.colarietitosti.supertools.backend.downloaderQueue;

import info.colarietitosti.supertools.backend.dashapps.DashApp;
import info.colarietitosti.supertools.backend.tools.FileDownloader;
import info.colarietitosti.supertools.backend.tools.Series.SeriesWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DownloadQueueRest {

    @Autowired
    DownloadQueue downloadQueue;

    @Autowired
    SeriesWorker seriesWorker;

    @GetMapping("/queues")
    @ResponseBody
    public DownloadQueueDO getAllApps() {
        DownloadQueueDO result =  new DownloadQueueDO();
        result.setDone(downloadQueue.getDoneQueue().stream().map(FileDownloader::getTitle).collect(Collectors.toList()));
        result.setDownloading(downloadQueue.getDownloadingQueue().stream().map(FileDownloader::getTitle).collect(Collectors.toList()));
        result.setTodo(downloadQueue.getToDoQueue().stream().map(FileDownloader::getTitle).collect(Collectors.toList()));
        return result;
    }

    @RequestMapping(method = RequestMethod.GET, value="/queueRunning")
    @ResponseBody
    public Boolean getRunning(){
        return downloadQueue.getRunning();
    }

    @RequestMapping(method = RequestMethod.GET, value="/clearQueue")
    public void clearQueue(){
        downloadQueue.clearDone();
    }

    @PostMapping(path="/startDownloads")
    @ResponseBody
    public String startUpdate(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                seriesWorker.updateSeries();
            }
        });
        t.start();
        return String.valueOf(200);
    }

}
