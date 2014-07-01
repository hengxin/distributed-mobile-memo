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
 * combine the separately synchronized executions into one
 * 
 * @author hengxin
 * @date Jul 1, 2014
 */
public class SyncedExecutionCombiner
{
	private String execution_directory = null;

	private final String execution_sync_file = "execution_sync.txt";
	
	public SyncedExecutionCombiner(String directory)
	{
		this.execution_directory = directory;
	}

	public void combine()
	{
		System.out.println("Combine executions in this directory: " + this.execution_directory);
		
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(this.execution_directory
					+ "\\" + "execution.txt"));
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
