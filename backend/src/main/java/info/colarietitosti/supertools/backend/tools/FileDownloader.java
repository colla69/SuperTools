package info.colarietitosti.supertools.backend.tools;

import lombok.Getter;
import lombok.extern.java.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

@Log
@Getter
public class FileDownloader implements Runnable {

    private String link;
    private String savePath;
    private String title;
    private boolean running;
    private boolean done;
    private String doneCmd = "";

    public FileDownloader(String link, String savePath, String title, String doneCmd) {
        this.link = link;
        this.savePath = savePath;
        this.title = title;
        this.doneCmd = doneCmd;
    }

    public void download(String link, String savePath, String title){
        new File(savePath).mkdirs();
        String path = savePath.concat(title);
        try (BufferedInputStream in = new BufferedInputStream(new URL(link).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(path))
        {
            log.info("starting download: "+path);
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            if (!this.doneCmd.equals("")) {
                this.execShellCmd(this.doneCmd);
                log.info("done!".concat(this.doneCmd));
            }
            this.running = false;
            this.done = true;
            log.info("done!");
        } catch (IOException e) {
            // handle exception
            log.warning("couldn't download " + link);
            e.printStackTrace();
        }
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

    private void execShellCmd(String cmd) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", cmd);
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
