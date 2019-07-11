/**
 * @author hengxin
 * @date May 29, 2014
 * @description to configure "android-logging-log4j"
 * <a href>https://code.google.com/p/android-logging-log4j/</a>
 */
package io.github.hengxin.distributed_mobile_memo.logging;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import de.mindpipe.android.logging.log4j.LogConfigurator;
import io.github.hengxin.distributed_mobile_memo.pc.PCConstants;

/**
 * Log for android.
 */
// TODO: 16-3-16 Eliminate the Singleton design pattern.
public enum ConfigureLog4J {
    INSTANCE;

    private LogConfigurator log_config = null;

    public void configure() {
        if (this.log_config == null) {
            log_config = new LogConfigurator();

            log_config.setFileName(PCConstants.INDIVIDUAL_EXECUTION_FILE_PATH_IN_SDCARD);
            log_config.setMaxFileSize(50 * 1024 * 1024);    // 50MB
            log_config.setRootLevel(Level.DEBUG);
            log_config.setFilePattern("%m%n");
            log_config.setUseLogCatAppender(false);

            log_config.setImmediateFlush(false);    // for performance
            log_config.setLevel("ics.mobilememo", Level.DEBUG);
            log_config.configure();
        }
    }

    /**
     * Shut down the logger and let all buffered logs get flushed.
     * See http://stackoverflow.com/a/3078377/1833118
     */
    public void shutdown() {
        LogManager.shutdown();
    }
}
