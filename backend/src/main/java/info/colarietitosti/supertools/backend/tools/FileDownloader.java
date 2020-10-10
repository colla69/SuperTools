package info.colarietitosti.supertools.backend.tools;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

@Slf4j
@Getter
public class FileDownloader implements Runnable {

    protected String link;
    protected String savePath;
    protected String title;
    protected boolean running;
    protected boolean done;
    protected String doneCmd = "";

    public FileDownloader(String link, String savePath, String title, String doneCmd) {
        this.link = link;
        this.savePath = savePath;
        this.title = title;
        this.doneCmd = doneCmd;
    }

    public void download(String link, String savePath, String title){
        new File(savePath).mkdirs();
        String newFilePath = makeSavePath();

        try (BufferedInputStream in = new BufferedInputStream(new URL(link).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(newFilePath))
        {
            executeDownload(newFilePath, in, fileOutputStream);
            executeDoneCmd();
        } catch (IOException e) {
            log.error("\n!!!!downloaderror", e);
        }
    }

    private void executeDownload(String newFilePath, BufferedInputStream in, FileOutputStream fileOutputStream)
            throws IOException {
        log.info("starting download: "+newFilePath);
        byte dataBuffer[] = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            fileOutputStream.write(dataBuffer, 0, bytesRead);
        }
    }

    private String makeSavePath() {
        return this.savePath.concat(this.title.replace("/",""));
    }

    protected void executeDoneCmd() {
        if (!this.doneCmd.equals("")) {
            ShellExecuter.execShellCmd(this.doneCmd);
            log.info("done!".concat(this.doneCmd));
        }
        this.running = false;
        this.done = true;
        log.info("done!");
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
