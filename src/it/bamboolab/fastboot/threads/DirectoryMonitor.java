package it.bamboolab.fastboot.threads;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class DirectoryMonitor extends Thread{

    public boolean stateActive = true;

    private static Logger logger = Logger.getLogger(DirectoryMonitor.class);

    public void run(){


        Path dir = FileSystems.getDefault().getPath("input");

        List<Path> result = new ArrayList<>();

        while(stateActive){

            logger.debug("Look, I'm monitoring!");

            try {

                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{c,h,cpp,hpp,java,xml}")) {

                    for (Path entry: stream) {


                        logger.debug("File found!");
                        logger.debug("File name: " + entry.getFileName());

                        File file = entry.toFile();
                        logger.debug(file.toString());


                        result.add(entry);

                    }

                } catch (DirectoryIteratorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
