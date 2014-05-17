/**
 * @author hengxin
 * @date 2014-04-22
 * @description generate workload with different statistical distributions;
 *   Hope it will support real workload collected from open-source/commercial data stores 
 */
package ics.mobilememo.benchmark.workload;

import ics.mobilememo.sharedmemory.data.kvs.Key;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.ExponentialGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;

public class PoissonWorkloadGenerator implements Runnable
{
	private BlockingQueue<Request> request_queue = new LinkedBlockingDeque<Request>();
	
	// role: writer [0], reader [1]
	private int role = -1; 
	// how many requests to generate
	private int total_requests = 0;
	// arrival rate of requests (Poisson process)
	private int rate = 0;
	
	private NumberGenerator<Double> gen = null;
	private final long oneMinute = 60000;
	
	public PoissonWorkloadGenerator(BlockingQueue<Request> request_queue, int total_requests, int rate)
	{
		this.request_queue = request_queue;
		this.total_requests = total_requests;
		this.rate = rate;
		
		this.gen = new ExponentialGenerator(rate, new MersenneTwisterRNG());
	}
	
	/**
	 * inter-arrival time
	 * @return inter-arrival time
	 * @throws InterruptedException thread is interrupted
	 * 
	 * TODO: high-level api
	 */
	private Request generateNextRequest() throws InterruptedException
	{
		long interval = Math.round(gen.nextValue() * oneMinute);
		Thread.sleep(interval);
		Request request = new Request(role, new Key("test"), null);
		return request;
	}

	/**
	 * generate requests and put them into a synchronized queue
	 */
	@Override
	public void run()
	{
		for (int num = 0; num < this.total_requests; num++)
		{
			try
			{
				this.request_queue.put(this.generateNextRequest());
			} catch (InterruptedException ie)
			{
				ie.printStackTrace();
			}
		}
		
	}
}
