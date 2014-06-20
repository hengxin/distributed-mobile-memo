/**
 * reader for execution log:
 * parse requests ( {@link RequestRecord} ) stored in log file 
 * and create instances for them 
 */
package ics.mobilememo.execution;

import ics.mobilememo.benchmark.workload.RequestRecord;
import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.Version;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExecutionLogReader
{
	private String file_name = null;
	
	/**
	 * constructor of {@link ExecutionLogReader}
	 * @param file 
	 *  the file in which {@link RequestRecord}s are stored
	 */
	public ExecutionLogReader(String file)
	{
		this.file_name = file;
	}
	
	/**
	 * parse the {@link RequestRecord}s stored in the file named {@link #file_name}
	 * and create instances for them
	 * @return A list of {@link RequestRecord}s 
	 */
	public List<RequestRecord> loadRequestRecords()
	{
		List<RequestRecord> request_record_list = new ArrayList<>();
		
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(this.file_name));
		} catch (FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		
		String raw_rr_line = null;
		try
		{
			while ((raw_rr_line = br.readLine()) != null) 
			{
				request_record_list.add(this.parseRequestRecord(raw_rr_line));
			}
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
		
		return request_record_list;
	}
	
	/**
	 * parse the raw string of requests into instance of {@link RequestRecord}
	 * @param raw_rr raw string of request to parse
	 * @return {@link RequestRecord} corresponding to @param raw_rr
	 */
	private RequestRecord parseRequestRecord(String raw_rr)
	{
		String[] rr_fields = raw_rr.split("\t");
		
		// parse
		String field = rr_fields[0];
		int type = Integer.parseInt(field.split("\\s+")[1]);
		
		field = rr_fields[1];
		long start_time = Long.parseLong(field.split("\\s+")[1]);
		
		field = rr_fields[2];
		long finish_time = Long.parseLong(field.split("\\s+")[1]);
		
		field = rr_fields[3];
		long delay = Long.parseLong(field.split("\\s+")[1]);
		
		field = rr_fields[4];
		Key key = new Key(field.split("\\s+")[1]);
		
		field = rr_fields[5].replaceAll("\\D+"," ").trim();
		String[] ver_parts = field.split("\\s+");
		Version version = new Version(Integer.parseInt(ver_parts[0]), Integer.parseInt(ver_parts[1]));
		
		field = rr_fields[6];
		String value = field.split("\\s+")[1];
		
		return new RequestRecord(type, start_time, finish_time, delay, key, version, value);
	}
}
