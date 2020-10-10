package info.colarietitosti.supertools.backend.series;

import info.colarietitosti.supertools.backend.config.BackendConfigutation;
import info.colarietitosti.supertools.backend.downloaderQueue.DownloadQueue;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
@Slf4j
class StreamServiceDownloaderBackendTest {


    @Mock
    DownloadQueue downloadQueue;
    @Mock
    BackendConfigutation config;

    StreamServiceDownloaderBackend streamServiceDownloaderBackend = new StreamServiceDownloaderBackend();

    @Test
    void downloadLink() {

        //String lonk = "http://vidtodo.com/ga1fbdqyj5l6";
        //streamServiceDownloaderBackend.downloadLink(lonk, new Episode(new Serie(2, "", "", true), 1, lonk));

        //String cmd = "ffmpeg -i \"https://c1.videobin.co/hls/oudvgh7vjztk2yixv4q6ecbbi4sck2ovytvmcpfthleivilagugn4bsz6sjq/index-v1-a1.m3u8\" -c copy -y /home/cola/ttest.mp4";
        //log.info("executing \n" + cmd);

        //String link = "https://vidtodo.com/va9c84rk03gu";
        String link = "http://vshare.eu/tcyqt8g0oc3h";
        //streamServiceDownloaderBackend.downloadLink(link, null);
/*
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("ffmpeg", "-i", link, "-c", "copy", "-loglevel", "quiet", "-y","/home/cola/ttest.mp4");
        Process process = null;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
*/

    }

}