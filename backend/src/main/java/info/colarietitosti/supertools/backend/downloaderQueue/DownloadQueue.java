package info.colarietitosti.supertools.backend.downloaderQueue;

import info.colarietitosti.supertools.backend.tools.FileDownloader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service
public class DownloadQueue implements Runnable {

    private final LinkedList<FileDownloader> toDoQueue = new LinkedList<>();
    private final LinkedList<FileDownloader> downloadingQueue = new LinkedList<>();
    private final LinkedList<FileDownloader> doneQueue = new LinkedList<>();

    private void downloadNext(){
        FileDownloader todo = toDoQueue.get(0);
        toDoQueue.removeFirst();
        downloadingQueue.add(todo);
        Thread t = new Thread( todo );
        t.setDaemon(true);
        t.start();
    }

    public void put(FileDownloader el){
        toDoQueue.add(el);
    }

    @PostConstruct
    public void start(){
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void run() {
        this.checkLists();
    }

    private void checkLists(){
        while (true){
            while(!toDoQueue.isEmpty()) downloadNext();
            List<FileDownloader> downloading = downloadingQueue.parallelStream().filter(FileDownloader::isRunning).collect(Collectors.toList());
            List<FileDownloader> done = downloadingQueue.parallelStream().filter(FileDownloader::isDone).collect(Collectors.toList());
            done.stream().forEach(d -> downloadingQueue.remove(d));
            doneQueue.addAll(done);
            try {
                Thread.currentThread().sleep(10_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearDone(){
        this.doneQueue.clear();
    }

    public boolean getRunning(){
        return (toDoQueue.size() != 0) || (downloadingQueue.size() != 0);
    }
}
