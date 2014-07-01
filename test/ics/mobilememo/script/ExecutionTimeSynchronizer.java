package ics.mobilememo.script;

import ics.mobilememo.execution.ExecutionLogHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * pre-processing the execution of benchmark for further use: adjust the
 * timestamps (i.e., start_time, finish_time) of operations according to the
 * offset of the system time of device to the prescribed "perfect time" (e.g.,
 * of a PC)
 * 
 * @author hengxin
 * @date Jul 1, 2014
 */
public class ExecutionTimeSynchronizer
{
	private String destination_directory = null;
	private final String execution_file_name = "execution.txt";
	private final String sync_time_file_name = "sync_time.txt";
	
	public ExecutionTimeSynchronizer(String path)
	{
		this.destination_directory = path;
	}
	
	/**
	 * For each pair of "execution.txt" file and "sync_time.txt" file,
	 * sync. the operations in the former according to the "time diff"
	 * in the latter.
	 */
	public void sync()
	{
		for (String sub_directory : this.getSubDirectories())
			this.sync(new File(this.destination_directory + "\\" + sub_directory));
	}
	
	/**
     * In the directory specified by @param sub_directory,
     * sync. the operations in "execution.txt" file according to
     * the "time offset" in the "sync_time.txt" file
     * 
     * @param directory directory containing both "execution.txt" 
     * and "sync_time.txt" files
	 */
	public void sync(File directory)
	{
		System.out.println("Sync this directory: " + directory);
		
		File[] files = directory.listFiles();
		
		// retrieve the "time diff" from the "sync_time.txt"
		long time_diff = 0L;
		for (File file : files)
			if (file.getName().equals(this.sync_time_file_name))
				time_diff = this.getTimeDiff(file.getAbsolutePath());

		// sync. the operations in "execution.txt" according to "time diff"
		for (File file : files)
			if (file.getName().equals(this.execution_file_name))
				new ExecutionLogHandler(file.getAbsolutePath()).sync(time_diff);
	}
	
	/**
	 * @return sub-directories of the {@link #destination_directory}
	 * It is only a relative path, instead of the absolute path.
	 */
	private String[] getSubDirectories()
	{
		File file = new File(this.destination_directory);
		
		String[] sub_directories = file.list(new FilenameFilter() 
		{
			  @Override
			  public boolean accept(File current, String name) 
			  {
			    return new File(current, name).isDirectory();
			  }
		});
		
		return sub_directories;
	}
	
    /**
     * retrieve "time diff value" from the file specified by @param sync_time_file
     * 
     * @param sync_time_file sync_time.txt which contains the "time diff value"
     * @return time diff value in millisecond
     */
    private long getTimeDiff(String sync_time_file)
    {
//		String sync_time_file_name = Environment.getExternalStorageDirectory() + File.separator + "sync_time.txt";
		BufferedReader br = null;
		long diff = 0;
		
		try
		{
			br = new BufferedReader(new FileReader(sync_time_file));
			// the first line reads like "diff 1000"
			String diff_line = br.readLine();
			diff = Integer.parseInt(diff_line.substring(5));
		} catch (FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		} finally
		{
			try
			{
				br.close();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		
		return diff;
    }
    
    public static void main(String[] args)
	{
		new ExecutionTimeSynchronizer("C:\\Users\\ics-ant\\Desktop\\executions\\allinonetest").sync();
	}
}
