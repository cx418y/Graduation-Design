package fudan.design.clone.utils.sagaUtils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

@Slf4j
public class ScriptUtil {

    public static void executeJarFile(File processDirectory, File jarFile, String parameters) {
        ProcessBuilder builder = new ProcessBuilder("java", "-jar", jarFile.getName(), parameters);
        System.out.println("param: " + parameters);
        builder.redirectErrorStream(true);
        builder.directory(processDirectory);
        try {
            Process process = builder.start();
            Scanner s = new Scanner(process.getInputStream());
            while (s.hasNextLine()) {
                s.nextLine();
            }
            s.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            log.error("execute jar file " + jarFile.getName() + " fail!", e);
        }
    }

}
