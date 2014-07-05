package ics.mobilememo.statistic.sync;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

/**
 * Extracting "diff" values from the sync time files.
 * 
 * The directory hierarchy:
 * SyncTime
 *   - Nexus0
 *     - sync_time.txt
 *   - Nexus3
 *     - sync_time.txt
 *   - Nexus33
 *     - sync_time.txt
 *   - ...
 *   
 * @author hengxin
 * @date Jul 5, 2014
 */
public class SyncTimeExtractor extends JPanel
{
	private static final long serialVersionUID = -1788006184493438580L;
	
	private static final String DEFAULT_SYNC_TIME_DIRECTORY = "C:\\Users\\ics-ant\\Desktop\\executions";
	private static final String SYNC_TIME_FILE_NAME = "sync_time.txt";
	private static final String SYNC_TIME_DIFF_FILE_NAME = "sync_time_diff.txt";
	
	private static final int THE_FIRST_LINE = 1;	// line no: starting from 1
	private static final int THE_SECOND_PART = 1;	// array index: starting from 0
	private static final int EVERY_THREE_LINE = 3;
	
	private String sync_time_directory = null;
	
	/**
	 * Constructor of {@link SyncTimeExtractor}:
	 * open "file choose dialog" to choose the directory containing 
	 * the sub-directories containing the sync_time.txt files of mobile phones
	 */
	public SyncTimeExtractor()
	{
		this.sync_time_directory = this.chooseSyncTimeDirectory();
	}
	
	/**
	 * extract "diff" values from all the sync_time.txt files 
	 * in the directory specified {@link #sync_time_directory}
	 */
	public void extract()
	{
		for (String sub_directory : this.getSubDirectories())
			this.extractFromSubdirectory(new File(this.sync_time_directory + "\\" + sub_directory));
	}
	
	/**
	 * extract "diff" values from the sync_time.txt file in some 
	 * specified directory corresponding to some mobile phone
	 * @param directory a directory containing the sync_time.txt file
	 */
	private void extractFromSubdirectory(File directory)
	{
		File[] files = directory.listFiles();
		for (File file : files)
			if (file.getName().equals(SyncTimeExtractor.SYNC_TIME_FILE_NAME))
				this.extract(file);
	}
	
	/**
	 * extract "diff" values from a sync_time.txt file
	 * @param file a sync_time.txt file
	 */
	private void extract(File file)
	{
		String sync_time_diff_path = file.getAbsolutePath().
				replace(SyncTimeExtractor.SYNC_TIME_FILE_NAME, SyncTimeExtractor.SYNC_TIME_DIFF_FILE_NAME);
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(sync_time_diff_path));
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		BufferedReader br = null;
		String sync_data_line = null;
		String[] sync_data_line_parts;
		int line_no = 0;
		
		try
		{
			br = new BufferedReader(new FileReader(file));
			while((sync_data_line = br.readLine()) != null)
			{
				line_no++;
				
				// only the "diff" value in the first line is relevant
				if (line_no % SyncTimeExtractor.EVERY_THREE_LINE == SyncTimeExtractor.THE_FIRST_LINE)
				{
					// parse the "diff" value
					sync_data_line_parts = sync_data_line.split("\\s+");
					bw.write(sync_data_line_parts[SyncTimeExtractor.THE_SECOND_PART] + "\n");
				}
			}				
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
				bw.close();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}
	
	/**
	 * choose the directory containing the sync_time.txt files
	 * via JFileChooser
	 * @return the path of the directory chosen
	 */
	private String chooseSyncTimeDirectory()
	{
		final JFileChooser fc = new JFileChooser(SyncTimeExtractor.DEFAULT_SYNC_TIME_DIRECTORY);
		
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int ret_val = fc.showOpenDialog(SyncTimeExtractor.this);
		if (ret_val == JFileChooser.APPROVE_OPTION)
			this.sync_time_directory = fc.getSelectedFile().getAbsolutePath();
		
		return this.sync_time_directory;
	}
	
	/**
	 * @return sub-directories of the {@link #sync_time_directory}
	 * It is only a relative path, instead of the absolute path.
	 */
	private String[] getSubDirectories()
	{
		File file = new File(this.sync_time_directory);
		
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
		new SyncTimeExtractor().extract();
	}
}
