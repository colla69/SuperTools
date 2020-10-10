package info.colarietitosti.supertools.backend.tools;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class ShellExecuter {

    public static boolean isRunning(String progName){
        String cmd = String.format("ps ax | grep -v grep | grep %s", progName);
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", cmd);

        return readProcessStatus(processBuilder);
    }

    private static boolean readProcessStatus(ProcessBuilder processBuilder) {
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            return isBufferEmpty(reader);
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private static boolean isBufferEmpty(BufferedReader reader) throws IOException {
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }
        if (output.length() == 0) {
            return false;
        } else {
            return true;
        }
    }

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

