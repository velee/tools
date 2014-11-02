package com.leec.tools.apps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.leec.tools.common.AppUtils;
import com.leec.tools.common.SimpleExpandableAdapter;

public class SameAppsActivity extends ActionBarActivity {
	
	private static final String TAG = SameAppsActivity.class.getSimpleName();
	
	public static final String ARG_PERMISSION_NAME = "permission_name";
	
	private ExpandableListView mListView;
    private SimpleExpandableAdapter mAdapter;
    private PackageManager mPackageManager;
    private String mPermissionName;
    private List<Map<String, Object>> groupDatas = new ArrayList<Map<String, Object>>();
    private List<List<Map<String, Object>>> childDatas = new ArrayList<List<Map<String, Object>>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_permissions);

        Intent intent = getIntent();
        mPermissionName = intent.getStringExtra(ARG_PERMISSION_NAME);

		// Set up the action bar.
        final android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setSubtitle(mPermissionName);
        }

		mPackageManager = getPackageManager();
		mListView = (ExpandableListView) findViewById(R.id.app_permissions_expand_list_view);
		loadListDatas();
		mAdapter = new SimpleExpandableAdapter(this, groupDatas, R.layout.app_list_item, new String[] {"application_icon", "application_label",
				"package_name"}, new int[] { R.id.application_icon, R.id.application_label, R.id.package_name },
				childDatas, R.layout.app_details_list_item, new String[] { "permission_name" }, new int[] { R.id.component_name });
		
		mAdapter.setViewBinder(new AppViewBinder());
		mAdapter.setGroupViewPost(new StateViewPost());
		
		mListView.setAdapter(mAdapter);
		
		mListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				@SuppressWarnings("unchecked")
				Map<String, Object> groupData = (Map<String, Object>)mAdapter.getGroup(groupPosition);
				List<Map<String, Object>> childData = childDatas.get(groupPosition);
				if (childData.isEmpty()) { 
					childData.addAll(AppUtils.getRequestedPermissions((String)groupData.get("package_name"), mPackageManager));
					mAdapter.notifyDataSetChanged();
				}
				mListView.setSelectedGroup(groupPosition);
				collapseOtherGroup(groupPosition);
			}
		});
		
		mListView.setGroupIndicator(null);

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	protected void collapseOtherGroup(int groupPosition) {
		int count = mAdapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			if (i != groupPosition)
				mListView.collapseGroup(i);
		}
	}
	
	protected void loadListDatas() {
    	Log.d(TAG, "Load permissions details...");
    	groupDatas.clear();
    	groupDatas.addAll(AppUtils.queryPackageForPermission(mPermissionName, mPackageManager));
    	
    	childDatas.clear();
		for (int i = 0; i < groupDatas.size(); i++) {
			childDatas.add(new ArrayList<Map<String, Object>>());
		}
	}


}
