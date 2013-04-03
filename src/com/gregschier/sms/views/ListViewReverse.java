package com.gregschier.sms.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

public class ListViewReverse extends ListView {

	private Boolean atBottom = false;

	public ListViewReverse(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				switch (view.getId()) {
				case android.R.id.list:
					final int lastItem = firstVisibleItem + visibleItemCount;
					if (lastItem == totalItemCount) {
						atBottom = true;
					} else {
						atBottom = false;
					}
				}
			}
		});
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		// It is scrolled all the way down here
		if (atBottom) {
			 setSelection(getCount());
		}
	}
}
