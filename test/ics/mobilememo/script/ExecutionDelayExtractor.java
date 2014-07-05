package ics.mobilememo.script;

import ics.mobilememo.benchmark.workload.RequestRecord;
import ics.mobilememo.execution.ExecutionLogHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Extracting "delay" from "execution.txt" file
 * @author hengxin
 * @date Jul 5, 2014
 */
public class ExecutionDelayExtractor
{
	private String execution_directory = null;
	private static final String EXECUTION_FILE_NAME = "execution.txt";
	private static final String EXECUTION_DELAY_FILE_NAME = "execution_delay.txt";

	public ExecutionDelayExtractor(String execution_directory)
	{
		this.execution_directory = execution_directory;
	}

	public void extract()
	{
		for (String sub_directory : this.getSubDirectories())
			this.extractFromSubDirectory(new File(this.execution_directory
					+ "\\" + sub_directory));
	}

	private void extractFromSubDirectory(File directory)
	{
		File[] files = directory.listFiles();
		for (File file : files)
			if (file.getName().equals(ExecutionDelayExtractor.EXECUTION_FILE_NAME))
				this.extractFromFile(file);
	}

	private void extractFromFile(File execution_file)
	{
		String execution_delay_file = execution_file.getAbsolutePath().replace(
				ExecutionDelayExtractor.EXECUTION_FILE_NAME,
				ExecutionDelayExtractor.EXECUTION_DELAY_FILE_NAME);
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(execution_delay_file));
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}

		ExecutionLogHandler exe_handler = new ExecutionLogHandler(
				execution_file.getAbsolutePath());

		try
		{
			for (RequestRecord rr : exe_handler.loadRequestRecords())
				bw.write(rr.getDelay() + "\n");
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		} finally
		{
			try
			{
				bw.close();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		
		System.out.println("Extract delay values from " + execution_file.getAbsolutePath() + " and store them in " + execution_delay_file);
	}

	/**
	 * @return sub-directories of the {@link #execution_directory} It is only a
	 *         relative path, instead of the absolute path.
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
		new ExecutionDelayExtractor("C:\\Users\\ics-ant\\Desktop\\executions\\allinonetest").extract();
	}
}
