/**
 * @author hengxin
 * @date Jun 27, 2014
 * @description Implementing atomic register supporting single writer and multiple readers (SWMR).
 */
package ics.mobilememo.sharedmemory.atomicity;

import ics.mobilememo.login.SessionManager;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityMessage;
import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.Version;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

import java.util.Map;

import android.util.Log;

/**
 * 
 * @author hengxin
 * Implementing atomic register supporting single writer and multiple readers (SWMR).
 */
public class SWMRAtomicityRegisterClient extends
		AbstractAtomicityRegisterClient
{
	// for logging
	private static final String TAG = SWMRAtomicityRegisterClient.class.getName();
	
	/**
	 * Using the Singleton design pattern
	 * It is not allowed for an Enum to extend an abstract class.
	 * Therefore, I have to implement it explicitly.
	 * 
	 * Here, we put "Synchronized" on method level because there are no much concurrent accesses.
	 * See <a href = "http://en.wikipedia.org/wiki/Singleton_pattern">Singleton Pattern [wiki]</a> 
	 */
	
	// "protected" constructor: \link SWMR2AtomicityRegisterClient needs to extend this class.
	protected SWMRAtomicityRegisterClient() { }
	
	private static SWMRAtomicityRegisterClient instance = null;
	
	public static synchronized SWMRAtomicityRegisterClient INSTANCE()
	{
		if (instance == null)
			instance = new SWMRAtomicityRegisterClient();
		return instance;
	}
	
	/**
	 * @author added by hengxin
	 * @date Jun 26, 2014
	 * @description for single writer (SWMR), maintaining its monotonically increasing version;
	 *  In SWMR case, it is not necessary for the writer to query a majority of server replicas
	 *  to get the latest versioned value till that time. It only needs to access its cached version.
	 *  
	 *  Being accessed by the only writer, it is not necessary to synchronize.
	 *  
	 *  It is initialized to (-1, THIS.PID)
	 */
	private Version cached_version = new Version(-1, new SessionManager().getNodeId());
	
	/**
	 * increment the {@link #cached_version} and return the new {@link Version} 
	 * @return a new {@link Version} after increment
	 */
	private Version incrementAndGetCachedVersion()
	{
		this.cached_version = cached_version.increment(new SessionManager().getNodeId());
		return this.cached_version;
	}

	/* 
	 * @see ics.mobilememo.sharedmemory.atomicity.AbstractAtomicityRegisterClient#get(ics.mobilememo.sharedmemory.data.kvs.Key)
	 * 
	 * Get method supporting only one writer.
	 * It is the same as that in {@link MWMRAtomicityRegisterClient} supporting multiple writers.
	 * Namely it consists of three phases: read_phase, local computation, and write_phase
	 */
	@Override
	public VersionValue get(Key key)
	{
//		Log.d(TAG, TAG + " issues a GET request ...");
		
		this.op_cnt++;

		Log.d(TAG, "Begin to get value associated with Key = " + key.toString());
		
		// read phase: contact a quorum of the server replicas for the latest value and version
		Map<String, AtomicityMessage> read_phase_acks = this.readPhase(key);

		// local computation: extract the latest VersionValue (value and its version)
		VersionValue max_vval = this.extractMaxVValFromAcks(read_phase_acks);

		// write phase: write-back the VersionValue into a quorum of the server replicas
		this.writePhase(key, max_vval);

		// return the latest VersionValue
		return max_vval;
	}

	/* 
	 * @see ics.mobilememo.sharedmemory.atomicity.AbstractAtomicityRegisterClient#put(ics.mobilememo.sharedmemory.data.kvs.Key, java.lang.String)
	 * 
	 * Put method supporting only one writer.
	 * In this case, it is not necessary for this writer to obtain the latest 
	 * version (of a particular key) through the read_phrase (@see AbstractAtomicityRegisterClient#read_phase()).
	 * It uses its locally cached version ( # ).
	 * 
	 * @see also MWMRAtomicityRegisterClient#put(Key, String)
	 */
	@Override
	public VersionValue put(Key key, String val)
	{
		Log.d(TAG, TAG + " issues a PUT request ...");
		
		this.op_cnt++;
		
		// get the version to use
		Version max_version = this.incrementAndGetCachedVersion();
		
		// construct the {@link VersionValue} to put
		VersionValue new_vval = new VersionValue(this.getNextVersion(max_version), val);
		
		// write phase: write-back the VersionValue into a quorum of the server replicas
		this.writePhase(key, new_vval);

		return new_vval;
	}

}
