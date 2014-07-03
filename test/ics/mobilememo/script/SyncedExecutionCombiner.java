package ics.mobilememo.script;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * combine the separately synchronized sub-executions into one
 * 
 * @author hengxin
 * @date Jul 1, 2014
 */
public class SyncedExecutionCombiner
{
	private String execution_directory = null;

	// file containing sub-execution
	private final String execution_sync_file = "execution_sync.txt";
	// file to store the combined execution
	private final String execution_file = "execution.txt";
	
	/**
	 * Constructor of {@link SyncedExecutionCombiner}
	 *
	 * @param directory directory on which the combination is performed
	 */
	public SyncedExecutionCombiner(String directory)
	{
		this.execution_directory = directory;
	}

	/**
	 * Combine the separately synchronized sub-executions into one
	 * 
	 * @return the (absolute) path of the execution file containing combined execution
	 */
	public String combine()
	{
		System.out.println("Combine executions in this directory: " + this.execution_directory);
		
		BufferedWriter bw = null;
		String combined_execution_file = this.execution_directory + "\\" + this.execution_file;
		
		try
		{
			bw = new BufferedWriter(new FileWriter(combined_execution_file));
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		BufferedReader br = null;
		String raw_rr_line = null;
		
		// check each sub-directory
		for (String sub_directory : this.getSubDirectories())
		{
			File[] files = new File(this.execution_directory + "\\" + sub_directory).listFiles();
			
			for (File file : files)
			{
				// find the "execution_sync.txt" file
				if (file.getName().equals(this.execution_sync_file))
				{
					// read from "execution_sync.txt" and write them to "execution.txt"
					try
					{
						br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
						while ((raw_rr_line = br.readLine()) != null) 
						{
							bw.write(raw_rr_line + "\n");
						}
					} catch (FileNotFoundException fnfe)
					{
						fnfe.printStackTrace();
					} catch (IOException ioe)
					{
						ioe.printStackTrace();
					}
				}
			}
		}
		
		try
		{
			br.close();
			bw.close();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		System.out.println("Combination Finished.");
		return combined_execution_file;
	}
	
	/**
	 * @return sub-directories of the {@link #destination_directory}
	 * It is only a relative path, instead of the absolute path.
	 */
	private String[] getSubDirectories()
	{
		File file = new File(this.execution_directory);
		
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
	
	public static void main(String[] args)
	{
		new SyncedExecutionCombiner("C:\\Users\\ics-ant\\Desktop\\executions\\allinonetest").combine();
	}
}
