package ics.mobilememo.group;

import ics.mobilememo.R;
import ics.mobilememo.group.JoinGroupDialog.IJoinGroupListener;
import ics.mobilememo.group.member.ServerReplica;
import ics.mobilememo.test.unittest.UnitTestConfig;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * A fragment representing a list of {@link ServerReplica}s.
 * the data source is an ArrayList of {@link ServerReplica}s maintained
 * by {@link GroupConfig} 
 * 
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 * 
 * The fragment implements the {@link IJoinGroupListener} to listen to the results from 
 * its pop dialog
 * 
 * @see IJoinGroupListener
 */
public class GroupFragment extends Fragment implements 
						AbsListView.OnItemClickListener,
						IJoinGroupListener
{
	/**
	 *  communication between this fragment and its poped dialog
	 *  @see JoinGroupDialog
	 *  @see #addButtonListener(View)
	 */
	protected static final int FRAGMENT_REQUEST_CODE = 0;

	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView server_replica_listview;

	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 */
	private ArrayAdapter<ServerReplica> server_replica_list_adapter;
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public GroupFragment()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// TODO: Change Adapter to display your content
		this.server_replica_list_adapter = new ArrayAdapter<ServerReplica>(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1,
				GroupConfig.INSTANCE.getGroupMembers());
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_group, container, false);

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
			GroupConfig.INSTANCE.getGroupMembers().add(new ServerReplica("10.0.0.1", "testreplica"));
		
		// Set the adapter
		this.server_replica_listview = (AbsListView) view.findViewById(android.R.id.list);
		((AdapterView<ListAdapter>) this.server_replica_listview).setAdapter(this.server_replica_list_adapter);

		// Set OnItemClickListener so we can be notified on item clicks
		this.server_replica_listview.setOnItemClickListener(this);

		return view;
	}
	
    /**
     * add and handle with button listeners: adding/removing server replicas
     */
    private void addButtonListener(View view)
    {
    	view.findViewById(R.id.imgbtn_add_group).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				/**
				 * show the "adding new replica dialog"
				 */
				JoinGroupDialog join_group_dialog = new JoinGroupDialog();
				join_group_dialog.setTargetFragment(GroupFragment.this, FRAGMENT_REQUEST_CODE);
				join_group_dialog.show(getFragmentManager(), getString(R.string.tag_join_group_dialog));
			}
		});
    	
    	/**
    	 *  // TODO: move to the onItemClick() method? 
    	 */
    	view.findViewById(R.id.imgbtn_delete_group).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
			}
		});
    }

	@Override
	public void onDetach()
	{
		super.onDetach();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @inheritDoc
	 * update the {@link ListView} of {@link ServerReplica}s
	 */
	@Override
	public void onJoinGroup(ServerReplica replica)
	{
		GroupConfig.INSTANCE.addReplica(replica);
		this.server_replica_list_adapter.notifyDataSetChanged();
	}

}
