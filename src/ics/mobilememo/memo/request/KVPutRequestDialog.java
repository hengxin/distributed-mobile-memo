/**
 * @author hengxin
 * @date May 9, 2014
 * @description {@link KVPutRequestDialog} is for the "put" request on server replica.
 */
package ics.mobilememo.memo.request;

import ics.mobilememo.R;
import ics.mobilememo.sharedmemory.atomicity.AtomicityRegisterClient;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

public class KVPutRequestDialog extends KVRequestDialog
{

	/**
	 * constructor of {@link KVPutRequestDialog}:
	 * set its title; 
	 * the value input box is required
	 * 
	 * @see KVRequestDialog#is_val_required
	 */
	public KVPutRequestDialog()
	{
		super.dialog_title_id = R.string.kv_put_dialog_title;
		super.is_val_required = true;
	}

	/**
	 * perform the "put" request
	 */
	@Override
	public VersionValue onRequestPerformed()
	{
		return AtomicityRegisterClient.INSTANCE.put(super.request_key, val_str);
	}

}
