/**
 * @author hengxin
 * @date May 29, 2014
 * @description to configure "android-logging-log4j"
 *  <url>https://code.google.com/p/android-logging-log4j/</url>
 */
package log4android;

import java.io.File;

import org.apache.log4j.Level;

import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class ConfigureLog4J
{
    public static void configure() 
    {
        final LogConfigurator log_config = new LogConfigurator();
                
        String file_name = Environment.getExternalStorageDirectory() + File.separator + "execution.txt";
        log_config.setFileName(file_name);
        log_config.setRootLevel(Level.DEBUG);
        log_config.setFilePattern("%m%n");
        log_config.setUseLogCatAppender(false);
        
        // Set log level of a specific logger
        log_config.setLevel("ics.mobilememo", Level.DEBUG);
        
        log_config.configure();
    }
}
