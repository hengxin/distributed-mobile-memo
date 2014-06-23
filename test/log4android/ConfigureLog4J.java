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

public enum ConfigureLog4J
{
	INSTANCE;
	
	private LogConfigurator log_config = null;
	private String file_name = Environment.getExternalStorageDirectory() + File.separator + "execution.txt";
	
    public void configure() 
    {
    	if (this.log_config == null)
    	{
	        log_config = new LogConfigurator();
	                
	        log_config.setFileName(file_name);
	        log_config.setRootLevel(Level.DEBUG);
	        log_config.setFilePattern("%m%n");
	        log_config.setUseLogCatAppender(false);
	        
	        // Set log level of a specific logger
	        log_config.setLevel("ics.mobilememo", Level.DEBUG);
	        
	        log_config.configure();
    	}
    }
    
    /**
     * @return {@link #file_name}: name of file in which the logs are stored
     */
    public String getFileName()
    {
    	return this.file_name;
    }
}
