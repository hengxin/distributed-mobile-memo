/**
 * @author hengxin
 * @date May 12, 2014
 * @description (not used yet)
 */
package ics.mobilememo.utility;

import ics.mobilememo.R;
import android.app.AlertDialog;
import android.content.Context;

public class AlertDialogManager
{
	/**
	 * display simple {@link AlertDialog}
	 * 
	 * @param context - application context
	 * @param title - alert dialog title
	 * @param message - alert message
	 * @param status - success/failure (used to set icon) - pass null if you don't want icon
	 */
	public void showAlertDialog(Context context, String title, String message, Boolean status)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		alertDialog.setTitle(title);
		alertDialog.setMessage(message);

		if (status != null)
			alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
//
//		// Setting OK Button
//		alertDialog.setButton("OK", new DialogInterface.OnClickListener()
//		{
//			public void onClick(DialogInterface dialog, int which)
//			{
//			}
//		});

		alertDialog.show();
	}
}
