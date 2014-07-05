package ics.mobilememo;

import ics.mobilememo.benchmark.ui.BenchmarkFragment;
import ics.mobilememo.group.GroupFragment;
import ics.mobilememo.login.SessionManager;
import ics.mobilememo.memo.MemoFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MobileMemoActivity extends ActionBarActivity implements
		ActionBar.TabListener // , OnFragmentInteractionListener
{

	public static Activity MOBILEMEMO_ACTIVITY; 
	
	/**
	 *  this application consists of 3 fragments/pagers/sections:
	 *  (1) members: class MemberFragment, title "MEMBER"
	 *  (2) memo: class MemoFragment, title "MEMO"
	 *  (3) benchmark: class BenchmarkFragment, title "BENCHMARK"
	 */
	private List<Fragment> fragments = new ArrayList<>();
	private List<String> frament_titles = new ArrayList<String>();
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;
	
	private SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mobile_memo);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		this.fragments = this.addFragments();
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this.fragments, this.frament_titles);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
				{
					@Override
					public void onPageSelected(int position)
					{
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
		{
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		MOBILEMEMO_ACTIVITY = this;
		
		// check login
		this.session = new SessionManager();
		session.checkLogin();
	}

	/**
	 * add fragments AND their titles
	 * @return list of fragments
	 */
	private List<Fragment> addFragments()
	{
		this.fragments.add(new GroupFragment());
		this.frament_titles.add(getString(R.string.title_section_group).toUpperCase(Locale.getDefault()));

		this.fragments.add(new MemoFragment());
		this.frament_titles.add(getString(R.string.title_section_memo).toUpperCase(Locale.getDefault()));

		this.fragments.add(new BenchmarkFragment());
		this.frament_titles.add(getString(R.string.title_section_benchmark).toUpperCase(Locale.getDefault()));
		
	    return this.fragments;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mobile_memo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * these tabs can be clicked to select
	 */
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction)
	{
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction)
	{
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction)
	{
	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{
		private List<Fragment> fragments;
		private List<String> fragment_titles;
		
		public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> fragment_titles)
		{
			super(fm);
			this.fragments = fragments;
			this.fragment_titles = fragment_titles;
		}
		
		@Override
		public Fragment getItem(int position)
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return this.fragments.get(position);
		}

		/**
		 * get the total number of sections/pagers/tabs
		 */
		@Override
		public int getCount()
		{
			return this.fragments.size();
		}

		/**
		 * return title of page according to its position
		 */
		@Override
		public CharSequence getPageTitle(int position)
		{
			return this.fragment_titles.get(position);
		}
	}

	/**
	 * belongs to the {@link OnFragmentInteractionListener} interface;
	 * interact with its Fragments
	 * 
	 * not used for the time being
	 * 
	 * @see GroupFragment
	 * @see MemoFragment
	 */
//	@Override
//	public void onFragmentInteraction(String id)
//	{
//		// TODO Auto-generated method stub
//		
//	}


}
