package info.colarietitosti.supertools.backend.tools;

import java.io.IOException;

public class ShellExecuter {

    public static void execShellCmd(String cmd) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", cmd);
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

