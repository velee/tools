/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leec.tools.apps;

import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.*;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.*;
import android.widget.ExpandableListView.OnChildClickListener;
import com.leec.tools.common.AppUtils;
import com.leec.tools.common.CheckListAdapter;

import java.util.*;

public class DetailsActivity extends ActionBarActivity implements ActionBar.TabListener {
	
	private static final String TAG = DetailsActivity.class.getSimpleName();
	
	public static final String ARG_PACKAGE_NAME = "package_name";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
	private AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
	private ViewPager mViewPager;

	private String mPackageName;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details_pager);

        // Set up the action bar.
        final android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();

        Intent intent = getIntent();
		this.mPackageName = intent.getStringExtra(ARG_PACKAGE_NAME);

		PackageManager pm = getPackageManager();

        if (actionBar != null) {
            //show icon false
            //actionBar.setDisplayShowHomeEnabled(false);
            //back action
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setSubtitle(mPackageName);
            try {
                PackageInfo p = pm.getPackageInfo(mPackageName, 0);
                actionBar.setTitle(pm.getApplicationLabel(p.applicationInfo));
                actionBar.setIcon(pm.getApplicationIcon(p.applicationInfo));
            } catch (NameNotFoundException e) {
            }
        }

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), mPackageName, this);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.app_details_pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);

        PagerTabStrip tabStrip = (PagerTabStrip)mViewPager.findViewById(R.id.pager_title);
        //tabStrip.setTabIndicatorColor();
        tabStrip.setTabIndicatorColorResource(R.color.tab_indicator_color);

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        
        
    }

    /* -- replace with android.support.v7.app.ActionBar
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }*/


    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				return true;
			case R.id.action_app_info:
				openAppInfo(mPackageName);
				return true;
			case R.id.action_google_play:
				openInPlay(mPackageName);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.app_details_action_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	protected void openAppInfo(String packageName) {
		Intent intent = new Intent();
		intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		Uri uri = Uri.fromParts("package", packageName, null);
		intent.setData(uri);
		startActivity(intent);
	}

	protected void openInPlay(String packageName) {
		try {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + packageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
		}
	}

	/**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {
    	
    	private String mPackageName;
    	private Context mContext;
    	private String[] mAppTabs;

        public AppSectionsPagerAdapter(FragmentManager fm, String packageName, Context context) {
            super(fm);
            mPackageName = packageName;
            mContext = context;
            mAppTabs = mContext.getResources().getStringArray(R.array.app_details_tabs);
        }

        @Override
        public Fragment getItem(int i) {
        	Fragment fragment;
        	Bundle args = new Bundle();
        	if (i < 3) {
        		fragment = new ComponentFragment();
        		fragment.setHasOptionsMenu(true);
        		args.putInt(ComponentFragment.ARG_SECTION_NUMBER, i);
        	} else {
        		fragment = new PermissionFragment();
        	}

            args.putString(ARG_PACKAGE_NAME, mPackageName);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return mAppTabs.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mAppTabs[position];
        }
    }


    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class ComponentFragment extends Fragment implements SearchView.OnQueryTextListener {

        public static final String ARG_SECTION_NUMBER = "section_number";
        
        private String mPackageName;
        private int mSectionNumber;
        private ListView mListView;
        private CheckListAdapter mAdapter;
        private PackageManager mPackageManager;
        private List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_app_details, container, false);

            Bundle args = getArguments();

    		mPackageName = args.getString(ARG_PACKAGE_NAME);
    		mSectionNumber = args.getInt(ARG_SECTION_NUMBER);
    		
    		mPackageManager = getActivity().getPackageManager();
    		
    		mAdapter = new CheckListAdapter(getActivity(), datas, R.layout.app_details_list_item,
    				new String[] { "component_name" }, new int[] { R.id.component_name });
    		
    		mAdapter.setQueryFields(new String[]{"component_name"});
            
    		mAdapter.setViewBinder(new AppViewBinder());
    		mAdapter.setViewPost(new StateViewPost());

    		mListView = (ListView) rootView.findViewById(R.id.app_detail_list_view);  
    		mListView.setAdapter(mAdapter);
    		
    		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    		mListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

    			@Override
    			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
    				// Here you can do something when items are
    				// selected/de-selected,
    				// such as update the title in the CAB
    				mAdapter.getCheckState()[position] = checked ? 1 : 0;
    				mAdapter.notifyDataSetChanged();

    				mode.setTitle(String.valueOf(mListView.getCheckedItemCount()));
    			}

    		    @Override
    		    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    		        // Respond to clicks on the actions in the CAB
    		    	@SuppressWarnings("unchecked")
    		    	final List<ComponentName> components = (List<ComponentName>)mAdapter.getCheckedValues("component");
    		    	
    		        switch (item.getItemId()) {
	    		        case R.id.action_copy:
	    		        	StringBuilder text = new StringBuilder();
			            	for (ComponentName component : components) {
			            		text.append(component.getClassName()).append("\n");
			            	}
			            	ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
			            	clipboardManager.setPrimaryClip(ClipData.newPlainText("component_names", text.toString()));
			            	Toast.makeText(getActivity(), components.size() + " components has copied to clipboard", Toast.LENGTH_LONG).show();
			            	mAdapter.uncheckAll();
			                mode.finish(); // Action picked, so close the CAB
			                reloadListDatas();              
			                return true;
    		            case R.id.action_enable:
    		            	AppUtils.processWithSecurity(getActivity(), new AppUtils.Processor() {
								@Override
								public void process(Context context) {
									AppUtils.setComponentEnabledState(components, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, mPackageManager);
									Toast.makeText(getActivity(), components.size() + " components enabled success", Toast.LENGTH_LONG).show();
								}
							});
    		            	mAdapter.uncheckAll();
    		                mode.finish(); // Action picked, so close the CAB
    		                reloadListDatas();              
    		                return true;
    		            case R.id.action_disable:
    		            	AppUtils.processWithSecurity(getActivity(), new AppUtils.Processor() {
								@Override
								public void process(Context context) {
									AppUtils.setComponentEnabledState(components, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, mPackageManager);
									Toast.makeText(getActivity(), components.size() + " components disabled success", Toast.LENGTH_LONG).show();
								}
							});
    		            	mAdapter.uncheckAll();
    		                mode.finish(); // Action picked, so close the CAB
    		                reloadListDatas();
    		                return true;
    		            default:
    		                return false;
    		        }
    		    }

    		    @Override
    		    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    		        // Inflate the menu for the CAB
    		        MenuInflater inflater = mode.getMenuInflater();
    		        inflater.inflate(R.menu.app_details_action_mode_menu, menu);
    		        return true;
    		    }

    		    @Override
    		    public void onDestroyActionMode(ActionMode mode) {
    		        // Here you can make any necessary updates to the activity when
    		        // the CAB is removed. By default, selected items are deselected/unchecked.
    		    	mAdapter.uncheckAll();
    		    }

    		    @Override
    		    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
    		        // Here you can perform updates to the CAB due to
    		        // an invalidate() request
    		        return false;
    		    }
    		});
            
            return rootView;
        }
        
        @Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			reloadListDatas();
		}
        
        @Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        	super.onCreateOptionsMenu(menu, menuInflater);
        	menuInflater.inflate(R.menu.action_search, menu);
			SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    		searchView.setOnQueryTextListener(this);
    		searchView.setQueryHint(getString(R.string.search_hint));
		}
        
        @Override
		public boolean onQueryTextChange(String newText) {
			mAdapter.setQuery(newText);
			return true;
		}

		@Override
		public boolean onQueryTextSubmit(String query) {
			int next = mAdapter.getNextQueryPosition();
			if (next == -1) { 
				Toast.makeText(getActivity(), "No more search result!", Toast.LENGTH_LONG).show();
			} else {
				mListView.setSelection(next);
			}
			return true;
		}
        
        protected void reloadListDatas() {
        	Log.d(TAG, "Load packages details...");
        	datas.clear();
        	datas.addAll(AppUtils.getPackageDetails(mPackageName, mPackageManager, (Integer)AppUtils.decode(mSectionNumber, 0, PackageManager.GET_ACTIVITIES, 1, PackageManager.GET_SERVICES, 2, PackageManager.GET_RECEIVERS, 0)));
    		mAdapter.notifyDataSetChanged();
    	}
        
        
    }
    
    public static class PermissionFragment extends Fragment {

        private String mPackageName;
        private ExpandableListView mListView;
        private BaseExpandableListAdapter mAdapter;
        private PackageManager mPackageManager;
        private List<Map<String, Object>> groupDatas = new ArrayList<Map<String, Object>>();
        private List<List<Map<String, Object>>> childDatas = new ArrayList<List<Map<String, Object>>>();
        private String[] mGroupTitles;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_app_permissions, container, false);

            Bundle args = getArguments();

    		mPackageName = args.getString(ARG_PACKAGE_NAME);
    		
    		mPackageManager = getActivity().getPackageManager();
    		
    		mGroupTitles = getResources().getStringArray(R.array.app_details_permission_groups);
    		loadListDatas();

    		mAdapter = new SimpleExpandableListAdapter(getActivity(), groupDatas, R.layout.app_details_list_group, new String[] { "permission_group" },  new int[] { R.id.group_title }, childDatas, R.layout.app_details_list_item, new String[] { "permission_name" }, new int[] { R.id.component_name });

    		mListView = (ExpandableListView) rootView.findViewById(R.id.app_permissions_expand_list_view);  
    		mListView.setAdapter(mAdapter);
    		
    		// expand all
    		int groupCount = mListView.getCount();
    		for (int i = 0; i < groupCount; i++) {
    			mListView.expandGroup(i);
    		}
    		mListView.setGroupIndicator(null);
    		
    		mListView.setOnChildClickListener(new OnChildClickListener() {
				@Override
				public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
					@SuppressWarnings("unchecked")
					Map<String, Object> group = (Map<String, Object>)mAdapter.getGroup(groupPosition);
					//USES-FEATURES can't be view permissions.
					if (mGroupTitles[0].equals(group.get("permission_group")))
						return false;
					
					@SuppressWarnings("unchecked")
					Map<String, Object> child = (Map<String, Object>)mAdapter.getChild(groupPosition, childPosition);
					startAppPermissions((String)child.get("permission_name"));
					return true;
				}
			});
    		    		
            return rootView;
        }
        
        protected void loadListDatas() {
        	Log.d(TAG, "Load permissions details...");
        	groupDatas.clear();
        	for (String title : mGroupTitles) {
        		Map<String, Object> map = new HashMap<String, Object>();
				map.put("permission_group", title);
				groupDatas.add(map);
        	}
        	
        	childDatas.clear();
        	childDatas.addAll(AppUtils.getPermissions(mPackageName, mPackageManager));
        	
        	Iterator<List<Map<String, Object>>> it = childDatas.iterator();
        	int i = 0;
        	while (it.hasNext()) {
        		if (it.next().isEmpty()) {
        			it.remove();
        			groupDatas.remove(i);
        		} else {
        			i++;
        		}
        	}
    	}
        
        protected void startAppPermissions(String permissionName) {
        	Intent intent = new Intent(getActivity(), SameAppsActivity.class);
        	intent.putExtra(SameAppsActivity.ARG_PERMISSION_NAME, permissionName);
        	startActivity(intent);
    	}
    	
    }
}
