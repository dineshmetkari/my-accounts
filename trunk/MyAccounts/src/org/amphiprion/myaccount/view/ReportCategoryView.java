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
package org.amphiprion.myaccount.view;

import java.util.List;

import org.amphiprion.myaccount.R;
import org.amphiprion.myaccount.adapter.CategoryAdapter;
import org.amphiprion.myaccount.database.entity.Category;
import org.amphiprion.myaccount.database.entity.ReportCategory;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * View used to display a category in the report category list.
 * 
 * @author amphiprion
 * 
 */
public class ReportCategoryView extends LinearLayout {
	/** the linked report category. */
	private ReportCategory reportCategory;
	/** The report category clicked listener. */
	private OnReportCategoryClickedListener reportCategoryClickedListener;
	/** the list of existing categories. */
	private List<Category> allCategories;
	/** The imageview. */
	private ImageView img;
	/** The spinner category. */
	private Spinner txt;

	/**
	 * Construct a rule view.
	 * 
	 * @param context
	 *            the context
	 * @param rule
	 *            the rule entity
	 */
	public ReportCategoryView(Context context, ReportCategory reportCategory,
			OnReportCategoryClickedListener reportCategoryClickedListener, List<Category> allCategories) {
		super(context);
		this.reportCategory = reportCategory;
		this.reportCategoryClickedListener = reportCategoryClickedListener;
		this.allCategories = allCategories;
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_background_states));

		addView(createIcon());

		addView(createCategoryLayout());
	}

	/**
	 * @return the reportCategory
	 */
	public ReportCategory getReportCategory() {
		return reportCategory;
	}

	/**
	 * Create the category icon view.
	 * 
	 * @return the view
	 */
	private View createIcon() {
		ImageView img = new ImageView(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imglp.gravity = Gravity.CENTER_VERTICAL;
		imglp.rightMargin = 5;
		img.setLayoutParams(imglp);

		if (reportCategory == null) {
			img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.add));
		} else {
			img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.remove));
		}
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				reportCategoryClickedListener.reportCategoryClicked(ReportCategoryView.this);
			}
		});
		this.img = img;
		return img;
	}

	/**
	 * Create the category layout view
	 * 
	 * @return the view
	 */
	private View createCategoryLayout() {
		LinearLayout accountLayout = new LinearLayout(getContext());
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3);
		accountLayout.setOrientation(VERTICAL);
		accountLayout.setLayoutParams(aclp);
		Spinner t = new Spinner(getContext());

		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		t.setLayoutParams(tlp);
		t.setAdapter(new CategoryAdapter(getContext(), allCategories));

		if (reportCategory != null) {
			t.setSelection(allCategories.indexOf(new Category(reportCategory.getCategoryId())));
		} else {
			t.setVisibility(INVISIBLE);
		}
		accountLayout.addView(t);

		txt = t;
		// TextView desc = new TextView(getContext());
		// desc.setText("" + DateUtil.format(operation.getDate()));
		// accountLayout.addView(desc);
		return accountLayout;
	}

	public interface OnReportCategoryClickedListener {
		void reportCategoryClicked(ReportCategoryView view);
	}

	/**
	 * Set the new report category. Call this method only if the current report
	 * category is null.
	 * 
	 * @param reportCategory
	 *            the new report category
	 */
	public void setReportCategory(ReportCategory reportCategory) {
		if (this.reportCategory == null) {
			this.reportCategory = reportCategory;
			img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.remove));
			txt.setVisibility(VISIBLE);
		}
	}

	/**
	 * Update the filter of the rule entity.
	 */
	public void updateReportCategoryFilter() {
		if (reportCategory != null) {
			reportCategory.setCategoryId(((Category) txt.getSelectedItem()).getId());
		}
	}
}
