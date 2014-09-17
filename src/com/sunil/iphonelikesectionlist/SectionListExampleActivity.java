package com.sunil.iphonelikesectionlist;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.sunil.iphonelikesectionlist.item.EntryItem;
import com.sunil.iphonelikesectionlist.item.Item;
import com.sunil.iphonelikesectionlist.item.SectionItem;

public class SectionListExampleActivity extends Activity implements
		OnTouchListener {
	/** Called when the activity is first created. */
	boolean isFocusChanged = false;

	private GestureDetector mGestureDetector;
	private static float sideIndexX;
	private static float sideIndexY;
	ArrayList<Object[]> indexList;
	// height of side index
	private int heightOfScreen;
	private int heightOfTextView = 0;
	// number of items in the side index
	private int indexListSize;
	int tmpIndexListSize = 0;
	private TextView tmpTV = null;
	private ArrayList<Item> items = null;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		//TODO dump data
		items = dataDump();
		

		EntryAdapter adapter = new EntryAdapter(this, items);
		final ListView lv1 = (ListView) findViewById(R.id.ListView01);
		lv1.setAdapter(adapter);

		mGestureDetector = new GestureDetector(this,
				new SideIndexGestureListener());
		// setListAdapter(adapter);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		} else {
			return false;
		}
	}

	private ArrayList<Object[]> createIndex(Context context) {
		String[] letters = context.getResources().getStringArray(R.array.letters);
		ArrayList<Object[]> tmpIndexList = new ArrayList<Object[]>();
		Object[] tmpIndexItem = null;

		int tmpPos = 0;
		String tmpLetter = "";
		String currentLetter = null;

		String entryItem = null;
		for (int j = 0; j < letters.length; j++) {

			entryItem = letters[j];

			currentLetter = entryItem.substring(0, 1);

			tmpIndexItem = new Object[3];
			tmpIndexItem[0] = tmpLetter;
			tmpIndexItem[1] = tmpPos - 1;
			tmpIndexItem[2] = j - 1;

			tmpLetter = currentLetter;

			tmpIndexList.add(tmpIndexItem);
			// }

		}
		// save also last letter
		tmpIndexItem = new Object[3];
		tmpIndexItem[0] = tmpLetter;
		tmpIndexItem[1] = tmpPos - 1;
		tmpIndexItem[2] = entryItem.length() - 1;
		tmpIndexList.add(tmpIndexItem);

		// and remove first temporary empty entry
		if (tmpIndexList != null && tmpIndexList.size() > 0) {
			tmpIndexList.remove(0);
		}

		return tmpIndexList;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		super.onWindowFocusChanged(hasFocus);

		LinearLayout sideIndex = (LinearLayout) findViewById(R.id.sideIndex);
		heightOfScreen = getHeightOfScreen(this);

		sideIndex.removeAllViews();

		indexList = createIndex(this);

		indexListSize = indexList.size();

		if (indexListSize > 0) {
			heightOfTextView = heightOfScreen / indexListSize;
		}

		tmpIndexListSize = indexListSize;

		if (tmpIndexListSize != 0) {
			double delta = indexListSize / tmpIndexListSize;

			String tmpLetter = null;
			Object[] tmpIndexItem = null;

			// show every m-th letter
			for (double i = 1; i <= indexListSize; i = i + delta) {
				tmpIndexItem = indexList.get((int) i - 1);
				tmpLetter = tmpIndexItem[0].toString();
				tmpTV = new TextView(this);

				tmpTV.setText(tmpLetter);

				tmpTV.setGravity(Gravity.CENTER_HORIZONTAL
						| Gravity.CENTER_VERTICAL);

				tmpTV.setTextColor(getResources().getColor(
						R.color.iphone_index_color));

				LayoutParams params = new LayoutParams(70, heightOfTextView, 1);
				tmpTV.setLayoutParams(params);
				sideIndex.addView(tmpTV);
				tmpTV.setOnTouchListener(this);
			}

		}
	}

	class SideIndexGestureListener extends
			GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			sideIndexX = sideIndexX - distanceX;
			sideIndexY = sideIndexY - distanceY;

			if (sideIndexX >= 0 && sideIndexY >= 0) {
				displayListItem();
			}

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	public void displayListItem() {

		double pixelPerIndexItem = (double) heightOfScreen / indexListSize;

		int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

		ListView listView = (ListView) findViewById(R.id.ListView01);

		int subitemPosition = getSelectedIndex(this, itemPosition);

		if (subitemPosition >= 0) {
			listView.setSelection(subitemPosition);
		}

	}

	public void displayListItem(String selectedHeader) {

		ListView listView = (ListView) findViewById(R.id.ListView01);
		int subitemPosition = getSelectedIndex(selectedHeader);
		if (subitemPosition >= 0) {
			listView.setSelection(subitemPosition);
		}

	}

	public static int getHeightOfScreen(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return dm.heightPixels;
	}

	/*
	 * void getSelectedIndex(int itemPosition){ String selectedHeader =
	 * strArr[itemPosition]; Iterator<> }
	 */

	int getSelectedIndex(Context context, int selectedHeaderPosition) {
		String selectedHeader = context.getResources().getStringArray(R.array.letters)[selectedHeaderPosition];
		Item item = null;
		int i = 0;
		boolean isMatchFound = false;
		while (i < items.size()) {
			item = items.get(i);
			if ((item.isSection())
					&& (selectedHeader.equalsIgnoreCase(((SectionItem) item)
							.getTitle()))) {
				isMatchFound = true;
				break;
			}
			i++;
		}

		if (isMatchFound) {
			return i;
		} else {
			return -1;
		}
	}

	int getSelectedIndex(String selectedHeader) {

		Item item = null;
		int i = 0;
		boolean isMatchFound = false;
		while (i < items.size()) {
			item = items.get(i);
			if ((item.isSection())
					&& (selectedHeader.equalsIgnoreCase(((SectionItem) item)
							.getTitle()))) {
				isMatchFound = true;
				break;
			}
			i++;
		}

		if (isMatchFound) {
			return i;
		} else {
			return -1;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (v instanceof TextView) {

			displayListItem(((TextView) v).getText().toString());

		}
		return false;
	}

	
	private ArrayList<Item> dataDump(){
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new SectionItem("A"));
		items.add(new EntryItem("AItem 1"));
		items.add(new EntryItem("AItem 2"));
		items.add(new EntryItem("AItem 3"));

		items.add(new SectionItem("B"));
		items.add(new EntryItem("BItem 4"));
		items.add(new EntryItem("BItem 5"));
		items.add(new EntryItem("BItem 6"));
		items.add(new EntryItem("BItem 7"));

		items.add(new SectionItem("C"));
		items.add(new EntryItem("CItem 8"));
		items.add(new EntryItem("CItem 9"));
		items.add(new EntryItem("CItem 10"));
		items.add(new EntryItem("CItem 11"));
		items.add(new EntryItem("CItem 12"));

		items.add(new SectionItem("D"));
		items.add(new EntryItem("DCItem 8"));
		items.add(new EntryItem("DCItem 9"));
		items.add(new EntryItem("DCItem 10"));
		items.add(new EntryItem("DCItem 11"));
		items.add(new EntryItem("DCItem 12"));

		items.add(new SectionItem("E"));
		items.add(new EntryItem("ECItem 8"));
		items.add(new EntryItem("ECItem 9"));
		items.add(new EntryItem("ECItem 10"));
		items.add(new EntryItem("ECItem 11"));
		items.add(new EntryItem("ECItem 12"));

		items.add(new SectionItem("F"));
		items.add(new EntryItem("FCItem 8"));
		items.add(new EntryItem("FItem 9"));
		items.add(new EntryItem("FCItem 10"));
		items.add(new EntryItem("FCItem 11"));
		items.add(new EntryItem("FCItem 12"));

		items.add(new SectionItem("G"));
		items.add(new EntryItem("GItem 8"));
		items.add(new EntryItem("GItem 9"));

		items.add(new SectionItem("H"));
		items.add(new EntryItem("HItem 8"));
		items.add(new EntryItem("HItem 9"));

		items.add(new SectionItem("I"));
		items.add(new EntryItem("IItem 8"));
		items.add(new EntryItem("IItem 9"));

		items.add(new SectionItem("J"));
		items.add(new EntryItem("JItem 8"));
		items.add(new EntryItem("JItem 9"));

		items.add(new SectionItem("K"));
		items.add(new EntryItem("KCItem 8"));
		items.add(new EntryItem("KItem 9"));

		items.add(new SectionItem("L"));
		items.add(new EntryItem("LItem 8"));
		items.add(new EntryItem("LItem 9"));

		items.add(new SectionItem("M"));
		items.add(new EntryItem("MItem 8"));
		items.add(new EntryItem("MItem 9"));

		items.add(new SectionItem("N"));
		items.add(new EntryItem("NItem 8"));
		items.add(new EntryItem("NItem 9"));

		items.add(new SectionItem("O"));
		items.add(new EntryItem("OItem 8"));
		items.add(new EntryItem("OItem 9"));

		items.add(new SectionItem("P"));
		items.add(new EntryItem("PItem 8"));
		items.add(new EntryItem("PItem 9"));
		
		items.add(new SectionItem("1"));
		items.add(new EntryItem("1Item 2"));
		items.add(new EntryItem("1Item 3"));
	
		return items;
	}
	
	
}
