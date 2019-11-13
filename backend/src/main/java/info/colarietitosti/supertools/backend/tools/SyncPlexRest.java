package info.colarietitosti.supertools.backend.tools;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SyncPlexRest {

    @RequestMapping(value = "syncTvShows", method = RequestMethod.GET)
    public void syncTvShows(){
        ShellExecuter.execShellCmd("/run/media/cola/Transfer/PlexContent/syncTvShows");
    }
}
