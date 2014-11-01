package com.leec.tools.apps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leec.tools.common.AppUtils;
import com.leec.tools.common.CheckListAdapter.ViewPost;

public class StateViewPost implements ViewPost {

	@Override
	public void onViewCreated(int position, Map<String, ?> data, View view) {
		List<TextView> textViews = new ArrayList<TextView>();
		findAllTextView(textViews, (ViewGroup)view);
		
		if (AppUtils.COMPONENT_ENABLED_STATE_DISABLED.equals(data.get("enable_state"))) {
			for (TextView textView : textViews) {
				textView.setTextColor(view.getContext().getResources().getColor(R.color.disabled_text_color));
			}
		} else {
			for (TextView textView : textViews) {
				textView.setTextColor(view.getContext().getResources().getColor(R.color.text_color));
			}
		}
	}
	
	protected void findAllTextView(List<TextView> textViews, ViewGroup parent) {
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = parent.getChildAt(i);
			if (view instanceof TextView) {
				textViews.add((TextView) view);
			} else if (view instanceof ViewGroup) {
				findAllTextView(textViews, (ViewGroup) view);
			}
		}
	}

}
