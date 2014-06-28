/**
 * @author hengxin
 * @date May 9, 2014
 * @description {@link KVGetRequestDialog} is for the "get" request on server replica.
 */
package ics.mobilememo.memo.request;

import ics.mobilememo.R;
import ics.mobilememo.sharedmemory.atomicity.AtomicityRegisterClient;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

public class KVGetRequestDialog extends KVRequestDialog
{
	/**
	 * constructor of {@link KVGetRequestDialog}
	 * set its title; 
	 * the value input box is not necessary
	 * 
	 * @see KVRequestDialog#is_val_required
	 */
	public KVGetRequestDialog()
	{
		super.dialog_title_id = R.string.kv_get_dialog_title;
	}
	
	/**
	 * perform the "GET" request
	 */
	@Override
	public VersionValue onRequestPerformed()
	{
		return AtomicityRegisterClient.INSTANCE.get(super.request_key);
	}

}
