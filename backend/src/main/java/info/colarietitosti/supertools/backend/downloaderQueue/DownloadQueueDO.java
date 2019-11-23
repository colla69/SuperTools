package info.colarietitosti.supertools.backend.downloaderQueue;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class DownloadQueueDO {

    private List<String> todo;
    private List<String> downloading;
    private List<String> done;
    private boolean syncRunning;
    private boolean queueRunning;
    private boolean searchRunning;

}
