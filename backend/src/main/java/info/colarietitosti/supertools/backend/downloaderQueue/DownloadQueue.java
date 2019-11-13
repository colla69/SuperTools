package info.colarietitosti.supertools.backend.downloaderQueue;

import info.colarietitosti.supertools.backend.tools.FileDownloader;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Log
@Getter
@Service
public class DownloadQueue implements Runnable {

    private final LinkedList<FileDownloader> toDoQueue = new LinkedList<>();
    private final LinkedList<FileDownloader> downloadingQueue = new LinkedList<>();
    private final LinkedList<FileDownloader> doneQueue = new LinkedList<>();
    private Boolean running = false;

    private void downloadNext(){
        FileDownloader todo = toDoQueue.get(0);
        toDoQueue.removeFirst();
        downloadingQueue.add(todo);
        Thread t = new Thread( todo );
        t.start();
    }

    public void put(FileDownloader el){
        toDoQueue.add(el);
    }

    @PostConstruct
    public void start(){
        Thread t = new Thread(this);
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
            this.running = downloading.size() > 0;
            /*
            log.info("Queue status: ");
            log.info("\ttodo: "+toDoQueue.size());
            log.info("\tdownloading: "+downloadingQueue.size());
            log.info("\tdone: "+doneQueue.size());
            */
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
}
