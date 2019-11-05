package info.colarietitosti.supertools.frontend.ui.downloadQueue;

import info.colarietitosti.supertools.frontend.ui.SuperGenericBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Scope;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import java.util.List;

@Log
@Getter @Setter
@ManagedBean("downloadQueueMB")
@Scope("prototype")
public class DownloadQueueMB extends SuperGenericBean {

    DownloadQueueClient downloadQueue;

    private String toDo;
    private String downloading;
    private String done;
    private List<String> todoList;
    private List<String> downloadingList;
    private List<String> doneList;

    @PostConstruct
    public void init(){
        downloadQueue = (DownloadQueueClient) makeDataSource(DownloadQueueClient.class);

        this.todoList = downloadQueue.getQueues().getTodo();
        this.downloadingList = downloadQueue.getQueues().getDownloading();
        this.doneList = downloadQueue.getQueues().getDone();
        this.toDo = "To Do: ".concat(String.valueOf(this.todoList.size()));
        this.downloading = "In Progress: ".concat(String.valueOf(this.downloadingList.size()));
        this.done = "Done: ".concat(String.valueOf(this.doneList.size()));
    }

    public void refresh(){
        init();
    }

    public void start(){
        downloadQueue.startUpdate();
    }
}
