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

import android.app.Activity;
import android.content.DialogInterface;
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
	// title of MemoFragment; used in {@link FragmentPagerAdapter#getPageTitle(int)}
	public static final String MEMO_FRAGMENT_TITLE = "MEMO";
	
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;

	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView mListView;

	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 */
	private ListAdapter mAdapter;
	
	private ArrayList<KVPair> kvpairs_list = new ArrayList<>();

	// TODO: Rename and change types of parameters
	public static MemoFragment newInstance(String param1, String param2)
	{
		MemoFragment fragment = new MemoFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
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

		if (getArguments() != null)
		{
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}

		
		// TODO: Change Adapter to display your content
		this.mAdapter = new ArrayAdapter<KVPair>(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1,
				this.kvpairs_list);
	}

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
		this.mListView = (AbsListView) view.findViewById(android.R.id.list);
		((AdapterView<ListAdapter>) this.mListView).setAdapter(this.mAdapter);

		// Set OnItemClickListener so we can be notified on item clicks
		this.mListView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try
		{
			this.mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		this.mListener = null;
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
				KVRequestDialog kv_request_dialog = new KVPutRequestDialog();
				kv_request_dialog.show(getFragmentManager(), getString(R.string.tag_put_dialog));
			}
		});
    	
    	view.findViewById(R.id.imgbtn_get_memo).setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				KVRequestDialog kv_request_dialog = new KVGetRequestDialog();
				kv_request_dialog.show(getFragmentManager(), getString(R.string.tag_get_dialog));				
			}
		});
    	
    	view.findViewById(R.id.imgbtn_delete_memo).setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				KVRequestDialog kv_request_dialog = new KVRemoveRequestDialog();
				kv_request_dialog.show(getFragmentManager(), getString(R.string.tag_remove_dialog));				
			}
		});
    }
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		if (null != mListener)
		{
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.
//			mListener.onFragmentInteraction(this.kvpairs_list.get(position).id);
		}
	}

	/**
	 * The default content for this Fragment has a TextView that is shown when
	 * the list is empty. If you would like to change the text, call this method
	 * to supply the text it should use.
	 */
	public void setEmptyText(CharSequence emptyText)
	{
		View emptyView = mListView.getEmptyView();

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
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener
	{
		// TODO: Update argument type and name
		public void onFragmentInteraction(String id);
	}

}
