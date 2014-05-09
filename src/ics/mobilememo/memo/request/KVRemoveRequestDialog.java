/**
 * @author hengxin
 * @date May 9, 2014
 * @description {@link KVRemoveRequestDialog} is for the "remove" request on server replica.
 */
package ics.mobilememo.memo.request;

import ics.mobilememo.R;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

public class KVRemoveRequestDialog extends KVRequestDialog
{
	/**
	 * constructor of {@link KVRemoveRequestDialog}:
	 * set its title; 
	 * the value input box is required
	 * 
	 * @see KVRequestDialog#is_val_required
	 */
	public KVRemoveRequestDialog()
	{
		super.dialog_title_id = R.string.kv_remove_dialog_title;
	}

	/**
	 * perform the "remove" request
	 */
	@Override
	public VersionValue onRequestPerformed()
	{
		return null;

		// TODO invoke the "remove" operation
	}

}
