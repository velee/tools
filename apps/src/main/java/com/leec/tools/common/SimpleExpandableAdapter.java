package com.leec.tools.common;

import java.util.List;
import java.util.Map;

import com.leec.tools.common.CheckListAdapter.ViewPost;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class SimpleExpandableAdapter extends SimpleExpandableListAdapter {
	
	private List<? extends Map<String, ?>> mGroupData;
    private String[] mGroupFrom;
    private int[] mGroupTo;
    
    private List<? extends List<? extends Map<String, ?>>> mChildData;
    private String[] mChildFrom;
    private int[] mChildTo;
    
    private ViewBinder mViewBinder;
    private ViewPost mGroupViewPost;

	public SimpleExpandableAdapter(Context context, List<? extends Map<String, ?>> groupData, int groupLayout, String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData, int childLayout, String[] childFrom, int[] childTo) {
		super(context, groupData, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
		
		mGroupData = groupData;
        mGroupFrom = groupFrom;
        mGroupTo = groupTo;
        
        mChildData = childData;
        mChildFrom = childFrom;
        mChildTo = childTo;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = newChildView(isLastChild, parent);
        } else {
            v = convertView;
        }
        bindView(v, mChildData.get(groupPosition).get(childPosition), mChildFrom, mChildTo);
        return v;
    }
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = newGroupView(isExpanded, parent);
        } else {
            v = convertView;
        }
        
        Map<String, ?> dataSet = mGroupData.get(groupPosition);
        bindView(v, dataSet, mGroupFrom, mGroupTo);
        
        if (mGroupViewPost != null) {
        	mGroupViewPost.onViewCreated(groupPosition, dataSet, v);
        }
        
        return v;
    }
	
	private void bindView(View view, Map<String, ?> dataSet, String[] from, int[] to) {
        int len = to.length;

        for (int i = 0; i < len; i++) {
        	
        	View v = view.findViewById(to[i]);
        	
        	if (v != null) {
        		Object data = dataSet.get(from[i]);

            	boolean bound = false;
                if (mViewBinder != null) {
                	String text = data == null ? "" : data.toString();
                    bound = mViewBinder.setViewValue(v, data, text);
                }

                if (!bound) {
                	((TextView)v).setText((String)data);
                }
        	}
        	
        }
    }
	
	public void setViewBinder(ViewBinder viewBinder) {
		mViewBinder = viewBinder;
	}
	
	public void setGroupViewPost(ViewPost groupViewPost) {
		mGroupViewPost = groupViewPost;
	}
	

}
