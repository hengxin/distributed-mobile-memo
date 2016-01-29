/**
 * @author hengxin
 * @date May 29, 2014
 * @description to configure "android-logging-log4j"
 * <a href>https://code.google.com/p/android-logging-log4j/</a>
 */
package io.github.hengxin.distributed_mobile_memo.logging;

import android.os.Environment;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public enum ConfigureLog4J {
    INSTANCE;

    private LogConfigurator log_config = null;

    private final String directory = Environment.getExternalStorageDirectory() + File.separator + "/single_execution";
    private final String file_name = this.directory + "/execution.txt";

    public void configure() {
        if (this.log_config == null) {
            log_config = new LogConfigurator();

            log_config.setFileName(file_name);
            log_config.setMaxFileSize(50 * 1024 * 1024);    // 50MB
            log_config.setRootLevel(Level.DEBUG);
            log_config.setFilePattern("%m%n");
            log_config.setUseLogCatAppender(false);

            /**
             * for <i>possible</i> performance improvement
             * @author hengxin
             * @date Jul 15, 2014
             */
            log_config.setImmediateFlush(false);

            // Set log level of a specific logger
            log_config.setLevel("ics.mobilememo", Level.DEBUG);

            log_config.configure();
        }
    }

    /**
     * @return {@link #directory}: directory containing all the execution-related files
     */
    public String getDirectory() {
        return this.directory;
    }

    /**
     * @return {@link #file_name}: name of file in which the logs are stored
     */
    public String getFileName() {
        return this.file_name;
    }

    /**
     * Shut down the logger and let all buffered logs get flushed.
     * See http://stackoverflow.com/a/3078377/1833118
     */
    public void shutdown() {
        LogManager.shutdown();
    }
}
