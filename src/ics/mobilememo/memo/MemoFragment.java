package ics.mobilememo.memo;

import ics.mobilememo.R;
import ics.mobilememo.memo.request.KVGetRequestDialog;
import ics.mobilememo.memo.request.KVPutRequestDialog;
import ics.mobilememo.memo.request.KVRemoveRequestDialog;
import ics.mobilememo.memo.request.KVRequestDialog;
import ics.mobilememo.memo.request.KVRequestDialog.IRequestResultListener;
import ics.mobilememo.sharedmemory.data.kvs.KVPair;
import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.Version;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;
import ics.mobilememo.test.unittest.UnitTestConfig;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the
 * ListView with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 * 
 * It implements {@link AbsListView#OnItemClickListener} to handle with 
 * the click events on items in its {@link ListView}.  // TODO
 * 
 * It implements {@link IRequestResultListener} to handle with
 * the return values from its dialogs: update its {@link ListView} with
 * the returned {@link VersionValue}.
 * @see KVRequestDialog
 * @see IRequestResultListener
 */
public class MemoFragment extends Fragment implements
		AbsListView.OnItemClickListener,	//
		IRequestResultListener	// 
{
	private static final String TAG = MemoFragment.class.getName();
	
	/**
	 * communication between this fragment between its popped dialog
	 * @see KVRequestDialog
	 * @see #addButtonListener(View)
	 */
	public static final int FRAGMENT_REQUEST_CODE = 1;
	
	// title of MemoFragment; used in {@link FragmentPagerAdapter#getPageTitle(int)}
	public static final String MEMO_FRAGMENT_TITLE = "MEMO";
	
//	private OnFragmentInteractionListener mListener;	// not used for the time being

	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView data_listview;

	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 *  // TODO: to extend to support other data types than {@link KVPair}
	 */
	private ArrayAdapter<KVPair> data_list_adapter;
	
	/**
	 * list of {@link KVPair}s to display
	 * this is only a client cache of {@link KVPair}s
	 */
	private ArrayList<KVPair> kvpairs_list = new ArrayList<>();

	/**
	 * this method is reserved (not used yet) for construct an instance
	 * of {@link MemoFragment} with possible parameters.
	 * if you are not going to instantiate it with parameters,
	 * you can use its default constructor {@link #MemoFragment()}
	 * 
	 * @see #MemoFragment()
	 * 
	 * TODO: Rename and change types of parameters
	 * @param param1
	 * @param param2
	 * @return an instance of {@link MemoFragment}
	 */
	public static MemoFragment newInstance(String param1, String param2)
	{
		MemoFragment fragment = new MemoFragment();
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public MemoFragment()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		/**
		 * option menu
		 * @TODO: it does not work properly.
		 */
		setHasOptionsMenu(true);

		// TODO: Change Adapter to display your content
		this.data_list_adapter = new ArrayAdapter<KVPair>(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1,
				this.kvpairs_list);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_memo, container, false);

		/**
		 *  add and handle with button click listeners
		 *  this consists of the main task of this MemoFragment;
		 *  also of the main task of this app
		 */
		this.addButtonListener(view);
		
		/**
		 * TODO: test of ListView; It is OK.
		 */
		if(UnitTestConfig.isUnittestEnabled)
			this.kvpairs_list.add(new KVPair(new Key("TestKey"), 
					new VersionValue(new Version(1, 1), "TestValue")));
		
		// Set the adapter
		this.data_listview = (AbsListView) view.findViewById(android.R.id.list);
		((AdapterView<ListAdapter>) this.data_listview).setAdapter(this.data_list_adapter);

		// Set OnItemClickListener so we can be notified on item clicks
		this.data_listview.setOnItemClickListener(this);

		return view;
	}

//	@Override
//	public void onAttach(Activity activity)
//	{
//		super.onAttach(activity);
//		try
//		{
//			this.mListener = (OnFragmentInteractionListener) activity;
//		} catch (ClassCastException e)
//		{
//			throw new ClassCastException(activity.toString()
//					+ " must implement OnFragmentInteractionListener");
//		}
//	}

	@Override
	public void onDetach()
	{
		super.onDetach();
//		this.mListener = null;
	}
	
	/**
	 * option menu for "adding" and "removing" key-value pair
	 * @TODO: does not work properly
	 */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
    	super.onCreateOptionsMenu(menu, inflater);
    	menu.clear();
        inflater.inflate(R.menu.menu_memo, menu);
    }
    
    /**
     * add and handle with button listeners
     * they are requests on this MobileMemo shared memory including
     * add, get, and remove.
     */
    private void addButtonListener(View view)
    {
    	view.findViewById(R.id.imgbtn_add_memo).setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				KVRequestDialog kv_put_request_dialog = new KVPutRequestDialog();
				kv_put_request_dialog.setTargetFragment(MemoFragment.this, FRAGMENT_REQUEST_CODE);
				kv_put_request_dialog.show(getFragmentManager(), getString(R.string.tag_put_dialog));
			}
		});
    	
    	view.findViewById(R.id.imgbtn_get_memo).setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				KVRequestDialog kv_get_request_dialog = new KVGetRequestDialog();
				kv_get_request_dialog.setTargetFragment(MemoFragment.this, FRAGMENT_REQUEST_CODE);
				kv_get_request_dialog.show(getFragmentManager(), getString(R.string.tag_get_dialog));				
			}
		});
    	
    	view.findViewById(R.id.imgbtn_delete_memo).setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				KVRequestDialog kv_remove_request_dialog = new KVRemoveRequestDialog();
				kv_remove_request_dialog.setTargetFragment(MemoFragment.this, FRAGMENT_REQUEST_CODE);
				kv_remove_request_dialog.show(getFragmentManager(), getString(R.string.tag_remove_dialog));				
			}
		});
    }
    
    /**
     *  // TODO: handle with the click events on list items: supporting remove request?
     */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
//		if (null != mListener)
//		{
//			// Notify the active callbacks interface (the activity, if the
//			// fragment is attached to one) that an item has been selected.
////			mListener.onFragmentInteraction(this.kvpairs_list.get(position).id);
//		}
	}

	/**
	 * The default content for this Fragment has a TextView that is shown when
	 * the list is empty. If you would like to change the text, call this method
	 * to supply the text it should use.
	 */
	public void setEmptyText(CharSequence emptyText)
	{
		View emptyView = data_listview.getEmptyView();

		if (emptyText instanceof TextView)
		{
			((TextView) emptyView).setText(emptyText);
		}
	}

	/**
	 * It implements {@link IRequestResultListener} to handle with
	 * the return values ({@link Key} and {@link VersionValue}) 
	 * from its dialogs: update its {@link ListView} with
	 * the returned {@link VersionValue}.
	 * 
	 * @inheritDoc
	 */
	@Override
	public void onRequestRusultReturned(Key key, VersionValue vval)
	{
		this.kvpairs_list.add(new KVPair(key, vval));
		this.data_list_adapter.notifyDataSetChanged();
	}
	
	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 * 
	 * Not used for the time being
	 * 
	 * @see MobileMemoActivity
	 */
//	public interface OnFragmentInteractionListener
//	{
//		// TODO: Update argument type and name
//		public void onFragmentInteraction(String id);
//	}

}
