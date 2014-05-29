/**
 * @author hengxin
 * @date 2014-04-22; 2014-05-17
 * @description generate workload with different statistical distributions;
 *   Hope: it will support real workload collected from open-source/commercial data stores 
 */
package ics.mobilememo.benchmark.workload;

import ics.mobilememo.benchmark.executor.Executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.ExponentialGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;

import android.util.Log;

public class PoissonWorkloadGenerator implements Runnable
{
	private static final String LOG = PoissonWorkloadGenerator.class.getName();
	
	// synchronous blocking queue between {@link PoissonWorkloadGenerator} and {@link Executor}
	private BlockingQueue<Request> request_queue = new LinkedBlockingDeque<Request>();
	
	// role: writer [0], reader [1]
	private int role = -1; 
	// how many requests to generate
	private int total_requests = 0;
	// arrival rate of requests (Poisson process)
	private int rate = 0;
	
	// used to generate number sequence accordance with some specified distribution
	private NumberGenerator<Double> exp_interarrival_gen = null;
	private final long oneMinute = 1000;
	
	/**
	 * constructor of {@link PoissonWorkloadGenerator}
	 * 
	 * @param request_queue {@link #request_queue}: synchronous blocking queue 
	 * 	between {@link PoissonWorkloadGenerator} and {@link Executor}
	 * @param total_requests {@link #total_requests}: total number of requests in the workload to generate
	 * @param rate {@link #rate}: arrival rate of requests (Poisson process)
	 */
	public PoissonWorkloadGenerator(BlockingQueue<Request> request_queue, int role, int total_requests, int rate)
	{
		this.request_queue = request_queue;
		this.role = role;
		this.total_requests = total_requests;
		this.rate = rate;
		
		this.exp_interarrival_gen = new ExponentialGenerator(this.rate, new MersenneTwisterRNG());
		
	}
	
	/**
	 * generate inter-arrival time
	 * @return inter-arrival time
	 * @throws InterruptedException thread is interrupted
	 * 
	 * TODO: high-level api
	 */
	private Request generateNextRequest() throws InterruptedException
	{
		long interval = Math.round(exp_interarrival_gen.nextValue() * oneMinute);
		Log.d(LOG, "The inter-arrival time is " + interval);
		
		Thread.sleep(interval);
		// TODO: generate requests randomly
		return RequestFactory.INSTANCE.generateRequest(role);
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
