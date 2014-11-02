package com.leec.tools.apps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.app.FragmentManager;
import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ExpandableListView.OnChildClickListener;

import com.leec.tools.common.AppUtils;
import com.leec.tools.common.CheckListAdapter;


public class AppsActivity extends ActionBarActivity {
	
	private static final String TAG = AppsActivity.class.getSimpleName();

    private DrawerLayout mDrawerLayout;
    private ExpandableListView mLeftDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftDrawer = (ExpandableListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mLeftDrawer.setAdapter(new SimpleExpandableListAdapter(this, getGroupDatas(), R.layout.drawer_list_group, new String[] { "group_title" },  new int[] { R.id.group_title }, getGroupChildDatas(), R.layout.drawer_list_item, new String[] { "item_title" }, new int[] { R.id.item_title }));
        
        mLeftDrawer.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				selectItem(parent, groupPosition, childPosition);
				return true;
			}
		});
        // expand all
		int groupCount = mLeftDrawer.getCount();
		for (int i = 0; i < groupCount; i++) {
			mLeftDrawer.expandGroup(i);
		}

        // Set up the action bar.
        final android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            // enable ActionBar app icon to behave as action to toggle nav drawer
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                //R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                if (actionBar != null) {
                    actionBar.setTitle(mTitle);
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                if (actionBar != null) {
                    actionBar.setTitle(mDrawerTitle);
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(mLeftDrawer, 0, 0);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mLeftDrawer);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@SuppressWarnings("unchecked")
	private void selectItem(ExpandableListView parent, int groupPosition, int childPosition) {
    	
    	ExpandableListAdapter adapter = parent.getExpandableListAdapter();
    	
    	if (groupPosition < 2) {
    		// update the main content by replacing fragments
    		Fragment fragment;
    		if (groupPosition == 0) {
    			fragment = new PackageFragment();
    		} else {
    			fragment = new ActionFragment();
    		}
    		fragment.setHasOptionsMenu(true);
	        
	        Bundle args = new Bundle();
	        args.putInt(PackageFragment.ARG_PLANET_NUMBER, childPosition);
	        fragment.setArguments(args);
	
	        FragmentManager fragmentManager = getFragmentManager();
	        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	
	        // update selected nd title, then close the drawer
	        long packedPosition = ExpandableListView.getPackedPositionForChild(groupPosition, childPosition);
	        parent.setItemChecked(parent.getFlatListPosition(packedPosition), true);
	        setTitle((String)((Map<String, Object>)adapter.getChild(groupPosition, childPosition)).get("item_title"));
	        mDrawerLayout.closeDrawer(mLeftDrawer);
    	}
    }
	
	private List<Map<String, Object>> getGroupDatas() {
		List<Map<String, Object>> groupDatas = new ArrayList<Map<String, Object>>();
		String[] groups = getResources().getStringArray(R.array.planets_groups);
    	for (String title : groups) {
    		Map<String, Object> map = new HashMap<String, Object>();
			map.put("group_title", title);
			groupDatas.add(map);
    	}

    	return groupDatas;
    }
    
    private List<List<Map<String, Object>>> getGroupChildDatas() {
    	List<List<Map<String, Object>>> childDatas = new ArrayList<List<Map<String, Object>>>();
    	int[] groupChildIds = new int[]{R.array.planets_app_items, R.array.planets_action_items};
    	for (int i = 0; i < groupChildIds.length; i++) {
    		List<Map<String, Object>> childData = new ArrayList<Map<String, Object>>();
    		String[] items = getResources().getStringArray(groupChildIds[i]);
        	for (String title : items) {
        		Map<String, Object> map = new HashMap<String, Object>();
    			map.put("item_title", title);
    			childData.add(map);
        	}
        	childDatas.add(childData);
    	}
    	return childDatas;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTitle);
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
	public static class PackageFragment extends Fragment implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

		public static final String ARG_PLANET_NUMBER = "planet_number";
        
		private SwipeRefreshLayout mSwipeRefreshLayout;
		private ListView mListView;
		private CheckListAdapter mAdapter;
		private int mIndex;
		private PackageManager mPackageManager;
		private List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();

        public PackageFragment() {
            // Empty constructor required for fragment subclasses
        }
        
        @Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        	super.onCreateOptionsMenu(menu, menuInflater);
            //SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
    		searchView.setOnQueryTextListener(this);
    		searchView.setQueryHint(getString(R.string.search_hint));
		}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	super.onCreateView(inflater, container, savedInstanceState);

        	mPackageManager = getActivity().getPackageManager();
        	
        	mIndex = getArguments().getInt(ARG_PLANET_NUMBER);	
        	
            View rootView = inflater.inflate(R.layout.activity_app, container, false);
            
            mSwipeRefreshLayout = (SwipeRefreshLayout) rootView;
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                    android.R.color.holo_green_light, 
                    android.R.color.holo_orange_light, 
                    android.R.color.holo_red_light);

            mAdapter = new CheckListAdapter(inflater.getContext(),
    				datas, R.layout.app_list_item, new String[] { "application_icon", "application_label",
    						"package_name"}, new int[] { R.id.application_icon, R.id.application_label,
    						R.id.package_name });
            
            mAdapter.setQueryFields(new String[]{"application_label", "package_name"});
            
            mAdapter.setViewBinder(new AppViewBinder());
            mAdapter.setViewPost(new StateViewPost());

    		mListView = (ListView) rootView.findViewById(R.id.app_list_view);  
    		mListView.setAdapter(mAdapter);
    		
    		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> listView, View view, int index, long arg3) {
					String packageName = ((TextView)view.findViewById(R.id.package_name)).getText().toString();
					startPackageDetail(packageName);
				}
			});
    		
    		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			mListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
					// Here you can do something when items are
					// selected/de-selected,
					// such as update the title in the CAB
					mAdapter.getCheckState()[position] = checked ? 1 : 0;
					mAdapter.notifyDataSetChanged();
					
					//mListView.getChildAt(position).setBackgroundColor(Color.BLUE);

					int count = mListView.getCheckedItemCount();
					mode.setTitle(String.valueOf(count));
					
					//设置只有单选的时候显示
					mode.getMenu().findItem(R.id.action_about).setVisible(count == 1);
				}

    		    @Override
    		    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    		        // Respond to clicks on the actions in the CAB
    		    	@SuppressWarnings("unchecked")
					final List<String> packages = (List<String>)mAdapter.getCheckedValues("package_name");
    		        switch (item.getItemId()) {
    		            case R.id.action_enable:
    		            	AppUtils.processWithSecurity(getActivity(), new AppUtils.Processor() {
								@Override
								public void process(Context context) {
									AppUtils.setApplicationEnabledState(packages, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, mPackageManager);
									Toast.makeText(context, packages.size() + " packages enabled success", Toast.LENGTH_LONG).show();
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
									AppUtils.setApplicationEnabledState(packages, PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER, mPackageManager);
									Toast.makeText(context, packages.size() + " packages disabled success", Toast.LENGTH_LONG).show();
								}
							});
    		            	mAdapter.uncheckAll();
    		                mode.finish(); // Action picked, so close the CAB
    		                reloadListDatas();
    		                return true;
    		            case R.id.action_about:
    		            	startAppDetails(packages.get(0));
    		            	mAdapter.uncheckAll();
    		                mode.finish(); // Action picked, so close the CAB
    		            	return true;
    		            default:
    		                return false;
    		        }
    		    }

    		    @Override
    		    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    		        // Inflate the menu for the CAB
    		        MenuInflater inflater = mode.getMenuInflater();
    		        inflater.inflate(R.menu.app_action_bar, menu);
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
        public void onRefresh() {
        	reloadListDatas();
        }

		protected void reloadListDatas() {
			mSwipeRefreshLayout.setEnabled(false);
			new AsyncTaskExtension().execute();
    	}
        
        protected void startAppDetails(String packageName) {
    		Intent intent = new Intent();
    		intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
    		Uri uri = Uri.fromParts("package", packageName, null);
    		intent.setData(uri);
    		startActivity(intent);
    	}
        
        protected void startPackageDetail(String packageName) {
        	Intent intent = new Intent(getActivity(), DetailsActivity.class);
        	intent.putExtra(DetailsActivity.ARG_PACKAGE_NAME, packageName);
        	startActivity(intent);
    	}

		@Override
		public boolean onQueryTextChange(String newText) {
			//possible onCreateOptionsMenu called before onCreate
			if (mAdapter != null) 
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
		
		private final class AsyncTaskExtension extends AsyncTask<Object, Object, List<Map<String, Object>>> {
			@Override
			protected List<Map<String, Object>> doInBackground(Object... arg0) {
				Log.d(TAG, "Load packages info...");
				return AppUtils.getPackageInfos((Boolean)AppUtils.decode(mIndex, 0, null, 1, true, 2, false), mPackageManager);
			}

			@Override
			protected void onPostExecute(List<Map<String, Object>> result) {
				super.onPostExecute(result);
				datas.clear();
	    		datas.addAll(result);
	    		mAdapter.notifyDataSetChanged();
				mSwipeRefreshLayout.setRefreshing(false);
				mSwipeRefreshLayout.setEnabled(true);
			}
		}
    }
	
	public static class ActionFragment extends Fragment implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
		public static final String ARG_PLANET_NUMBER = "planet_number";
        
		private SwipeRefreshLayout mSwipeRefreshLayout;
        private ListView mListView;
        private CheckListAdapter mAdapter;
        private int mIndex;
        private PackageManager mPackageManager;
        private List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();

        public ActionFragment() {
            // Empty constructor required for fragment subclasses
        }
        
        @Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        	super.onCreateOptionsMenu(menu, menuInflater);
            //SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
    		searchView.setOnQueryTextListener(this);
    		searchView.setQueryHint(getString(R.string.search_hint));
		}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	super.onCreateView(inflater, container, savedInstanceState);

        	mPackageManager = getActivity().getPackageManager();
        	
        	mIndex = getArguments().getInt(ARG_PLANET_NUMBER);	
        	
            View rootView = inflater.inflate(R.layout.activity_app, container, false);
            
            mSwipeRefreshLayout = (SwipeRefreshLayout) rootView;
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light, 
                    android.R.color.holo_orange_light, 
                    android.R.color.holo_red_light);

            mAdapter = new CheckListAdapter(getActivity(),
    				datas, R.layout.app_list_item, new String[] { "component_icon", "component_label",
    						"component_name" }, new int[] { R.id.application_icon, R.id.application_label,
    						R.id.package_name });
            
            mAdapter.setQueryFields(new String[]{"component_label", "component_name"});
            
            mAdapter.setViewBinder(new AppViewBinder());
            mAdapter.setViewPost(new StateViewPost());

    		mListView = (ListView) rootView.findViewById(R.id.app_list_view);  
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

					int count = mListView.getCheckedItemCount();
					mode.setTitle(String.valueOf(count));
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
    		        inflater.inflate(R.menu.app_details_action_bar, menu);
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
        public void onRefresh() {
        	reloadListDatas();
        }

        protected void reloadListDatas() {
        	mSwipeRefreshLayout.setEnabled(false);
        	new AsyncTaskExtension().execute();
    	}

		@Override
		public boolean onQueryTextChange(String newText) {
			//possible onCreateOptionsMenu called before onCreate
			if (mAdapter != null) 
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
		
		private final class AsyncTaskExtension extends AsyncTask<Object, Object, List<Map<String, Object>>> {
			@Override
			protected List<Map<String, Object>> doInBackground(Object... arg0) {
				Log.d(TAG, "Load components info...");
				return AppUtils.queryActionActivities((String)AppUtils.decode(mIndex, 0, Intent.ACTION_BOOT_COMPLETED,
                        1, Intent.ACTION_SEND, 2, AppWidgetManager.ACTION_APPWIDGET_UPDATE,
                        3, Intent.ACTION_MAIN, 4, Intent.ACTION_SET_WALLPAPER), mPackageManager);
			}

			@Override
			protected void onPostExecute(List<Map<String, Object>> result) {
				super.onPostExecute(result);
				datas.clear();
	    		datas.addAll(result);
				mAdapter.notifyDataSetChanged();
				mSwipeRefreshLayout.setRefreshing(false);
				mSwipeRefreshLayout.setEnabled(true);
			}
		}
	}

}