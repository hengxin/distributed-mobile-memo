/**
 * @author hengxin
 * @date 2014-04-24
 * @description executor responsible for issuing the requests from workload benchmarks
 */
package ics.mobilememo.benchmark.executor;

import ics.mobilememo.benchmark.workload.Request;
import ics.mobilememo.benchmark.workload.RequestRecord;
import ics.mobilememo.sharedmemory.atomicity.AtomicityRegisterClient;
import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import log4android.ConfigureLog4J;

import org.apache.log4j.Logger;

public class Executor implements Runnable
{
//	static private final Logger LOG = LoggerFactory.getLogger(Executor.class);
	
	/**
	 * use "android-logging-log4j"
	 * <url>https://code.google.com/p/android-logging-log4j/</url>
	 */
	private final Logger log4android = Logger.getLogger(Executor.class);
	
	private BlockingQueue<Request> request_queue = new LinkedBlockingDeque<Request>();
	AtomicityRegisterClient client = AtomicityRegisterClient.INSTANCE;
	
	/**
	 * using the producer-consumer synchronization mechanism
	 * @param request_queue queue of RequestRecord (s) between producer and consumer (Executor)
	 */
	public Executor(BlockingQueue<Request> request_queue)
	{
		ConfigureLog4J.INSTANCE.configure();
		
		this.request_queue = request_queue;
	}
	
	/**
	 * issue the request and record statistical information
	 * @param request request to issue
	 */
	private void issue(Request request)
	{
		int type = request.getType();
		Key key = request.getKey();
		String val = request.getValue();
		VersionValue vvalue = null;
		
		long invocation_time = System.currentTimeMillis();
		if (type == Request.WRITE_TYPE)	// it is W[0]
			vvalue = client.put(key, val);
		else // it is R[1]
			vvalue = client.get(key);
		long response_time = System.currentTimeMillis();
		
		// the delay = response_time - invocation_time is calculated and recorded
		RequestRecord rr = new RequestRecord(type, invocation_time, response_time, key, vvalue);
		log4android.debug(rr.toString());
	}
	
	/**
	 * take requests from workload benchmarks, issue them, and record statistical information
	 */
	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				this.issue(request_queue.take());
			} catch (InterruptedException ie)
			{
				// TODO
				ie.printStackTrace();
			}
		}
	}
}
