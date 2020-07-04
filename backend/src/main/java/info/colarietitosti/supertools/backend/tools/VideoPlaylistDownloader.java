package info.colarietitosti.supertools.backend.tools;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class VideoPlaylistDownloader extends FileDownloader {

    public VideoPlaylistDownloader(String link, String savePath, String title, String doneCmd) {
        super(link, savePath, title, doneCmd);
    }

    @Override
    public void download(String link, String savePath, String title){
        new File(savePath).mkdirs();
        String path = savePath.concat(title.replace("/",""));
        String cmd = "ffmpeg -i \""+link+"\" -c copy -y "+path+".mp4";
        log.info(cmd);

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("ffmpeg", "-i", link, "-c", "copy", "-loglevel", "quiet", "-y",path+".mp4");
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
        executeDoneCmd();
    }

    @Override
    public void run() {
        running = true;
        download(this.link, this.savePath, this.title);
    }

    public boolean isRunning() {
        return running;
    }
    public boolean isDone() {
        return done;
    }
}
