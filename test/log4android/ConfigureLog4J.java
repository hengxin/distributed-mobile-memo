/**
 * @author hengxin
 * @date May 29, 2014
 * @description to configure "android-logging-log4j"
 *  <url>https://code.google.com/p/android-logging-log4j/</url>
 */
package log4android;

import java.io.File;

import org.apache.log4j.Level;

import de.mindpipe.android.logging.log4j.LogConfigurator;
import android.os.Environment;
import android.util.Log;

public class ConfigureLog4J
{
	private static final String TAG = ConfigureLog4J.class.getName();
	
    public static void configure() 
    {
        final LogConfigurator log_config = new LogConfigurator();
                
        String file_name = Environment.getExternalStorageDirectory() + File.separator + "execution.log";
        Log.d(TAG, "Write logs into file: " + file_name);
        log_config.setFileName(file_name);
        log_config.setRootLevel(Level.DEBUG);
        
        // Set log level of a specific logger
        log_config.setLevel("org.apache", Level.DEBUG);
        
        log_config.configure();
    }
}
