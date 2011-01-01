/*
 * @copyright 2010 Gerald Jacobson
 * @license GNU General Public License
 * 
 * This file is part of My Accounts.
 *
 * My Accounts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * My Accounts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with My Accounts.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.amphiprion.myaccount.adapter;

import org.amphiprion.myaccount.ApplicationConstants;
import org.amphiprion.myaccount.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * This adapter is used to display a choice all standard category images.
 * 
 * @author amphiprion
 * 
 */
public class StandardCategoryImageAdapter extends BaseAdapter {
	private Context mContext;
	private Bitmap[] bitmaps;
	private String[] images;

	public StandardCategoryImageAdapter(Context c) {
		mContext = c;
		images = c.getResources().getStringArray(R.array.category_images);
		bitmaps = new Bitmap[images.length];
		for (int i = 0; i < images.length; i++) {
			bitmaps[i] = BitmapFactory.decodeResource(c.getResources(), c.getResources().getIdentifier(images[i],
					"drawable", ApplicationConstants.PACKAGE));
		}
	}

	public int getCount() {
		return images.length;
	}

	public Object getItem(int position) {
		return "#" + images[position];
	}

	public long getItemId(int position) {
		return position;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) { // if it's not recycled, initialize some
			// attributes
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(42, 42));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		} else {
			imageView = (ImageView) convertView;
		}

		imageView.setImageBitmap(bitmaps[position]);
		return imageView;
	}
}