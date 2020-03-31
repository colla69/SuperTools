package info.colarietitosti.supertools.backend.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class PlexSyncFacade {

    @GetMapping(value = "syncTvShows")
    @ResponseBody
    public String syncTvShows() {
        log.info("staring plex Sync!");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ShellExecuter.execShellCmd("/run/media/cola/NASData/PlexContent/syncTvShows");
            }
        });
        t.setDaemon(true);
        t.start();
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                ShellExecuter.execShellCmd("/run/media/cola/NASData/PlexContent/syncMusic");
            }
        });
        t2.setDaemon(true);
        t2.start();
        return String.valueOf(200);
    }
}
