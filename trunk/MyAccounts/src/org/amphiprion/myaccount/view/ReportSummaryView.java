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

import org.amphiprion.myaccount.R;
import org.amphiprion.myaccount.database.entity.Report;
import org.amphiprion.myaccount.database.entity.Report.Type;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View used to display a category in the category list.
 * 
 * @author amphiprion
 * 
 */
public class ReportSummaryView extends LinearLayout {
	/** the linked report. */
	private Report report;

	/**
	 * Construct an category view.
	 * 
	 * @param context
	 *            the context
	 * @param report
	 *            the report entity
	 */
	public ReportSummaryView(Context context, Report report) {
		super(context);
		this.report = report;

		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_background_states));

		addView(createIcon());

		addView(createReportLayout());
	}

	/**
	 * @return the report
	 */
	public Report getReport() {
		return report;
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
		imglp.topMargin = 5;
		imglp.bottomMargin = 5;
		img.setLayoutParams(imglp);

		if (report.getType() == Type.DAILY_BALANCE) {
			img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.pie_chart));
		} else {
			img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.line_chart));
		}
		return img;
	}

	/**
	 * Create the report layout view
	 * 
	 * @return the view
	 */
	private View createReportLayout() {
		LinearLayout accountLayout = new LinearLayout(getContext());
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3);
		accountLayout.setOrientation(VERTICAL);
		accountLayout.setLayoutParams(aclp);
		TextView t = new TextView(getContext());
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		t.setLayoutParams(tlp);
		t.setText(report.getName());
		t.setTextSize(16);
		t.setTypeface(Typeface.DEFAULT_BOLD);
		t.setTextColor(getContext().getResources().getColor(R.color.black));
		accountLayout.addView(t);

		// TextView desc = new TextView(getContext());
		// desc.setText("" + DateUtil.format(operation.getDate()));
		// accountLayout.addView(desc);
		return accountLayout;
	}

}
