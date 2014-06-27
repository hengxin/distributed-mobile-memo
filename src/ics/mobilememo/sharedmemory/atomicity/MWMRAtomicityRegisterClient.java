/**
 * @author hengxin
 * @date Jun 27, 2014
 * @description Implementing atomicity register supporting multiple writers and multiple readers (MWMR).
 */
package ics.mobilememo.sharedmemory.atomicity;

import java.util.Map;

import android.util.Log;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityMessage;
import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.Version;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

public class MWMRAtomicityRegisterClient extends
		AbstractAtomicityRegisterClient
{
	// Log for class {@link MWMRAtomicityRegisterClient}
	private final static String TAG = MWMRAtomicityRegisterClient.class.getName();

	/**
	 * Using the Singleton design pattern
	 * It is not allowed for an Enum to extend an abstract class.
	 * Therefore, I have to implement it explicitly.
	 * 
	 * Here, we put "Synchronized" on method level because there are no much concurrent accesses.
	 * See <a href = "http://en.wikipedia.org/wiki/Singleton_pattern">Singleton Pattern [wiki]</a> 
	 */
	private MWMRAtomicityRegisterClient() { }
	
	private static MWMRAtomicityRegisterClient instance = null;
	
	public static synchronized MWMRAtomicityRegisterClient INSTANCE()
	{
		if (instance == null)
			instance = new MWMRAtomicityRegisterClient();
		return instance;
	}
	
	/**
	 * {@link #get(Key)} method supporting MWMR:
	 * it consists of three phases: read_phase, local computation, and write_phase
	 */
	@Override
	public VersionValue get(Key key)
	{
		Log.d(TAG, "Client issues a GET request ...");
		
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

	/**
	 * {@link #put(Key, String)} method supporting MWMR:
	 * it consists of three phases: read_phase, local computation, and write_phase
	 */
	@Override
	public VersionValue put(Key key, String val)
	{
		Log.d(TAG, "Client issues a PUT request ...");
		
		this.op_cnt++;

		// read phase: contact a quorum of the server replicas for the latest value and version
		Map<String, AtomicityMessage> read_phase_acks = this.readPhase(key);

		// local computation: extract the latest VersionValue; increment the version; construct the new VersionValue to write
		VersionValue max_vval = this.extractMaxVValFromAcks(read_phase_acks);

		Version max_version = max_vval.getVersion();
		VersionValue new_vval = new VersionValue(this.getNextVersion(max_version), val);

		// write phase: write-back the VersionValue into a quorum of the server replicas
		this.writePhase(key, new_vval);

		return new_vval;
	}

}
