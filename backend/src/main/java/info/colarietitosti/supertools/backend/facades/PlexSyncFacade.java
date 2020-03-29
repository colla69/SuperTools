package info.colarietitosti.supertools.backend.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PlexSyncFacade {

    @RequestMapping(value = "syncTvShows", method = RequestMethod.GET)
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
