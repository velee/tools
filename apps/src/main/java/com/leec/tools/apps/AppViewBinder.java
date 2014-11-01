package com.leec.tools.apps;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter.ViewBinder;

public class AppViewBinder implements ViewBinder {
	
	public boolean setViewValue(View v, Object data, String text) {
		if (v instanceof ImageView) {
			((ImageView)v).setImageDrawable((Drawable)data);
			return true;
		}
		
		return false;
	}
}
