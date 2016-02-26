package it.bamboolab.fastboot.dump;

import it.bamboolab.fastboot.context.ApplicationProperties;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UserDumper extends Thread {

    private final static Logger logger = Logger.getLogger(UserDumper.class);

    private final static String productTitle = "Fastboot";
    private final static String BASE_MSG = "See documentation and logs file for further details.";
    private final static String INIT_RESPONSE = "An error occured executing usrdump request";
    public static SimpleDateFormat timestampFmt = new SimpleDateFormat("yyyyMMddHHmmssS");

    private String response = null;
    private int iterationNumber = 1;
    private long intervalInMillis = 0;
    private Writer usrDumpWriter = null;
    private String usrDumpFileName = null;
    private File usrDumpFile = null;
    private String postfixFilename = null;
    private File dumpDir = null;
    private boolean isRunning = true;
    private boolean isSuspended = false;
    private DumpType dumpType = null;

    public UserDumper(
            DumpType dumpType,
            File dumpDir,
            String postfixFilename,
            int iterationNumber,
            int intervalSecs
    ) {
        this.dumpDir = dumpDir;
        this.postfixFilename = postfixFilename;
        this.iterationNumber = iterationNumber;
        this.intervalInMillis = (intervalSecs * 1000L);
        this.dumpType = dumpType;
        this.setName(this.getClass().getSimpleName() + "-" + this.getId());
    }

    public String getResponse() {
        return response;
    }

    public void startThread() {
        super.start();
    }

    public void stopThread() {
        isRunning = false;
        resumeThread();
    }

    public void suspendThread(long waitTimeInMillis) {
        if (isRunning) {
            isSuspended = true;
            if (logger.isDebugEnabled()) {
                logger.debug("Waiting for " + waitTimeInMillis + " millis.");
            }
            //System.out.println("Waiting for " + waitTimeInMillis + " millis.");
            try {
                synchronized (this) {
                    wait(waitTimeInMillis);
                }
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
            isSuspended = false;
        }
    }

    public void resumeThread() {

        if (isSuspended) {
            if (logger.isDebugEnabled()) {
                logger.debug("Sending notify event to resume '" + this.getName() + "' thread.");
            }
            synchronized (this) {
                this.notify();
            }
        }
    }

    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("The " + this.getName() + " thread is started");
        }

        while (isRunning && iterationNumber != 0) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Executing iteration number: " + iterationNumber);
                }

                execute();

                if (intervalInMillis > 0 && iterationNumber > 1) {
                    suspendThread(intervalInMillis);
                }
                iterationNumber--;
            } catch (Exception e) {
                stopThread();
            }

        }

        if (logger.isDebugEnabled()) {
            logger.debug("The " + this.getName() + " thread is stopped");
        }
        //System.out.println("The " + this.getName() + " thread is stopped");
    }

    public void execute() throws Exception {
        ThreadDumper threadDump = null;
        boolean isComplete = false;
        response = INIT_RESPONSE;

        try {
            buildUrsDumpWriter();

            threadDump = new ThreadDumper(usrDumpWriter);
            threadDump.insertBanner("General");

            String productID = productTitle + " v" + ApplicationProperties.FB_VERSION;

            threadDump.insertString("General\\ProductID=" + productID);
            threadDump.insertString("General\\DumpTimestamp=" + timestampFmt.format(new Date(System.currentTimeMillis())));
            threadDump.insertString("General\\DumpName=" + usrDumpFileName);
            threadDump.insertString("General\\DumpType=usr");
            threadDump.insertNewLine();

            //eventuali statistiche su thread....
//			String cliResponse = AbsIXPMonMain
//					.cliStatiticsResponseMessage(false);
//			threadDump.insertBanner("Statistics");
//			threadDump.insertString(cliResponse);
//			threadDump.insertNewLine();

            threadDump.insertBanner("Dump");

            //scrittura statistiche prodotte sopra
            //AbsIXPMonMain.writeOnDump(threadDump);

            threadDump.insertNewLine();

            threadDump.dumpAllMXBean();

            isComplete = true;

            if (dumpType == DumpType.console) {
                response = usrDumpWriter.toString();
            }

        } catch (IOException e) {
            String errMsg = e.getMessage();
            if (errMsg != null) {
                response = INIT_RESPONSE + errMsg + ". " + BASE_MSG;
            } else {
                response = INIT_RESPONSE + ". " + BASE_MSG;
            }
            logger.error(errMsg);
            throw e;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            if (errMsg != null) {
                response = INIT_RESPONSE + errMsg + ". " + BASE_MSG;
            } else {
                response = INIT_RESPONSE + ". " + BASE_MSG;
            }
            logger.error(errMsg);
            throw e;
        } finally {
            if (threadDump != null) {
                threadDump.closeWriter();
            }
            if (!isComplete && usrDumpFile != null) {
                usrDumpFile.delete();
            }
        }
    }

    private void buildUrsDumpWriter() throws IOException {

        if (dumpType == DumpType.console) {
            usrDumpWriter = new StringWriter();
            response = "print user dump.";

        } else {

            usrDumpFileName = timestampFmt.format(new Date(System.currentTimeMillis()))
                    + "." + postfixFilename + ".log";

            usrDumpFile = new File(dumpDir, usrDumpFileName);
            usrDumpWriter = new FileWriter(usrDumpFile);
            response = "Wrote user dump to file '"
                    + usrDumpFile.getAbsolutePath() + "'";

        }
    }
}
