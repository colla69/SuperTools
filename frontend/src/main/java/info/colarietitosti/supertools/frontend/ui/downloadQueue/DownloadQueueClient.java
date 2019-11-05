package info.colarietitosti.supertools.frontend.ui.downloadQueue;

import feign.RequestLine;
import info.colarietitosti.supertools.frontend.ui.GenericClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@FeignClient(name = "downloadQueue")
@Scope("prototype")
public interface DownloadQueueClient  extends GenericClient {

    //@RequestMapping(method = RequestMethod.GET, value = "/queues")
    @RequestLine("GET /queues")
    DownloadQueueDO getQueues();

    //@RequestMapping(method = RequestMethod.POST, value = "/startDownloads")
    @RequestLine("POST /startDownloads")
    String startUpdate();
}
