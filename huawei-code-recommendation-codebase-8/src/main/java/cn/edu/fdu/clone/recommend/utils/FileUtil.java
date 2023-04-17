package cn.edu.fdu.clone.recommend.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class FileUtil {

    public static void deleteFileIfExists(File file) {
        try {
            FileUtils.forceDeleteOnExit(file);
        } catch (IOException e) {
            log.error("delete file " + file.getAbsolutePath() + " error!", e);
        }
    }

    public static byte[] getBytes(
            String inputFileName,
            long start,
            long end
    ) {
        byte[] bytes = new byte[(int) (end-start)];
        try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(Paths.get(inputFileName)))) {
            long skipped = 0;

            while (skipped < start) {
                skipped += bis.skip(start - skipped);
            }
            bis.read(bytes);
//            bytes = bis.re((int) (end - start));
            return bytes;
        } catch (IOException e) {
            log.error("read bytes from " + inputFileName + " error!", e);
            return null;
        }
    }

    public static long writeBytes(
            String outputFileName,
            byte[] bytes
    ) {
        try {
            File outputFile = new File(outputFileName);
            long res = outputFile.length();
            OutputStream os = FileUtils.openOutputStream(outputFile, true);
            os.write(bytes);

            os.flush();
            os.close();
            return res;
        } catch (IOException e) {
            log.error("write bytes to " + outputFileName + " error!", e);
            return -1L;
        }
    }
}
