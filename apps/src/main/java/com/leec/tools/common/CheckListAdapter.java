package com.leec.tools.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import com.leec.tools.apps.R;

public class CheckListAdapter extends SimpleAdapter {
	
	private int[] checkState;
	private Context mContext;
	private String mQuery;
	private String[] mQueryFields;
	private int mQueryPosition;
	private List<? extends Map<String, ?>> mData;
	private ViewPost mViewPost;

	public CheckListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		mContext = context;
		mData = data;
		checkState = new int[getCount()];
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		mQueryPosition = -1;
		if (checkState.length != getCount()) {
			checkState = new int[getCount()];
		}
	}

	@Override
	public void notifyDataSetInvalidated() {
		super.notifyDataSetInvalidated();
		mQueryPosition = -1;
		if (checkState.length != getCount()) {
			checkState = new int[getCount()];
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		int background = 0;

		if (checkState[position] == 1)
			background = R.drawable.selected_state_drawable;
		else if (getQueryState(position) == 1)
			background = R.drawable.queryed_state_drawable;
		else
			background = R.drawable.background;

		view.setBackground(mContext.getResources().getDrawable(background));
		
		if (mViewPost != null) {
			mViewPost.onViewCreated(position, mData.get(position), view);
		}
		
        return view;
    }
	
	public int[] getCheckState() {
		return checkState;
	}
	
	public int getQueryState(int position) {
		if (mQueryFields != null && mQuery != null && !"".equals(mQuery)) {
			Map<String, ?> row = mData.get(position);
			Object value;
			for (String field : mQueryFields) {
				value = row.get(field);
				if (value != null && value.toString().toLowerCase().contains(mQuery)) {
					return 1;
				}
			}
		}
		return 0;
	}
	
	public int getNextQueryPosition() {
		for (int i = mQueryPosition + 1; i < mData.size(); i++) {
			if (getQueryState(i) == 1) {
				mQueryPosition = i;
				return mQueryPosition;
			}
		}
		return -1;
	}
	
	public void uncheckAll() {
		for (int i = 0; i < checkState.length; i++) {
			checkState[i] = 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<? extends Object> getCheckedValues(String key) {
		List<Object> result = new ArrayList<Object>();
		for (int i = 0; i < checkState.length; i++) {
			if (checkState[i] == 1) {
				result.add(((Map<String, Object>)getItem(i)).get(key));
			}
		}
		return result;
	}

	public boolean isAllChecked() {
		for (int i : checkState) {
			if (i == 0)
				return false;
		}
		return true;
	}

	public void checkAll() {
		for (int i = 0; i < checkState.length; i++) {
			checkState[i] = 1;
		}
	}
	
	public void setQueryFields(String[] queryFields) {
		this.mQueryFields = queryFields;
	}
	
	public void setQuery(String query) {
		this.mQuery = query;
		this.notifyDataSetChanged();
	}

	public void setViewPost(ViewPost viewPost) {
		this.mViewPost = viewPost;
	}

	public static interface ViewPost {
		public void onViewCreated(int position, Map<String, ?> data, View view);
	}

	
}