package info.colarietitosti.supertools.backend.tools;

import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
public class PlexSyncFacade {

    @RequestMapping(value = "syncTvShows", method = RequestMethod.GET)
    @ResponseBody
    public String syncTvShows(){
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
