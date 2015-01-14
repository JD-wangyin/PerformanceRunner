package com.wangyin.ci.performance;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * add it as a java native logger, maybe performance is less sufficient than
 * logback.
 * 
 * @author wyliangxiaowu
 * @date 2014年7月22日
 */
public class FileLogger {
    private Logger log = null;

	public FileLogger() {
	}

    public FileLogger(String logName) {
        log = Logger.getLogger(logName);

        String today = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        today = sdf.format(cal.getTime());
        // today = java.text.DateFormat.getDateInstance().format(new
        // java.util.Date());
        String logFileName = logName + "-" + today + ".log";

        FileHandler fileHandler = null;

        try {
            String dirPath = System.getProperty("user.home");
            String os = System.getProperty("os.name");

            if (os.toUpperCase().startsWith("WIN")) {
                dirPath = dirPath + "\\CILog";
            } else {
                // linux
                dirPath = dirPath + "/CILog";
            }

            File file = new File(dirPath);
            if (!file.exists()) {
                file.mkdir();
            }

            if (os.toUpperCase().startsWith("WIN")) {
                fileHandler = new FileHandler(dirPath + "\\" + logFileName);
            } else {
                // linux
                fileHandler = new FileHandler(dirPath + "/" + logFileName);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(new LogFormatter());

        log.addHandler(fileHandler);
    }

    private void log(Level level, String msg) {
        log.log(level, msg);
    }

    public void logSevere(String msg) {
        log(Level.SEVERE, msg);
    }

    public void logFine(String msg) {
        log(Level.FINE, msg);
    }

    public void logInfo(String msg) {
        log(Level.INFO, msg);
    }

    public void logWarning(String msg) {
        log(Level.WARNING, msg);
    }

    class LogFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            String time = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
            Calendar cal = Calendar.getInstance();
            time = sdf.format(cal.getTime());

            String lineBreak = null;
            Properties prop = System.getProperties();
            String os = prop.getProperty("os.name");
            if (os.toUpperCase().startsWith("WIN")) {
                lineBreak = "\r\n";
            } else {
                lineBreak = "\n";
            }

            return time + ":    " + record.getLevel() + ": " + record.getMessage() + lineBreak;
        }
    }
}
