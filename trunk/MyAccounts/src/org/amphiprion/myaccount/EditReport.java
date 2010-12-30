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
package org.amphiprion.myaccount;

import java.util.List;

import org.amphiprion.myaccount.database.ReportDao;
import org.amphiprion.myaccount.database.entity.Category;
import org.amphiprion.myaccount.database.entity.Report;
import org.amphiprion.myaccount.view.ReportCategoryView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * The activity used to edit/create a report.
 * 
 * @author amphiprion
 * 
 */
public class EditReport extends Activity implements ReportCategoryView.OnReportCategoryClickedListener {
	private Report report;

	/** The categories of the report. */
	private List<Category> categories;

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_report);

		final Spinner cbReportType = (Spinner) findViewById(R.id.cbReportType);
		final TextView txtName = (TextView) findViewById(R.id.txtReportName);

		if (report == null) {
			Intent intent = getIntent();
			if (intent.getExtras() != null) {
				report = (Report) intent.getExtras().getSerializable("REPORT");
				txtName.setText(report.getName());
			}
		}

		if (report == null) {
			// its a creation
			report = new Report();
		}
		// List<Category> parents =
		// CategoryDao.getInstance(this).getPossibleParentFor(category);
		// parents.add(0, new Category(""));
		// cbParentCategory.setAdapter(new CategoryAdapter(this, parents));
		// if (category.getParent() != null) {
		// cbParentCategory.setSelection(parents.indexOf(category.getParent()));
		// }

		Button btSave = (Button) findViewById(R.id.btSave);
		btSave.setOnClickListener(new ViewGroup.OnClickListener() {
			@Override
			public void onClick(View v) {
				report.setName("" + txtName.getText());
				// Category parent = (Category)
				// cbParentCategory.getSelectedItem();
				// if ("".equals(parent.getId())) {
				// category.setParent(null);
				// } else {
				// category.setParent(parent);
				// }
				// updateCategoryFilters();
				report.setCategories(categories);

				Intent i = new Intent();
				i.putExtra("REPORT", report);
				setResult(RESULT_OK, i);
				finish();
			}
		});

		Button btCancel = (Button) findViewById(R.id.btCancel);
		btCancel.setOnClickListener(new ViewGroup.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		buildCategoryList();

	}

	private void buildCategoryList() {
		categories = ReportDao.getInstance(this).getCategories(report);

		LinearLayout ln = (LinearLayout) findViewById(R.id.rule_list);
		ln.removeAllViews();
		for (Category category : categories) {
			ln.addView(new ReportCategoryView(this, category, this));
		}
		ln.addView(new ReportCategoryView(this, null, this));
	}

	// /**
	// * Ask the rule views to update its underlying rule.
	// */
	// private void updateCategoryFilters() {
	// LinearLayout ln = (LinearLayout) findViewById(R.id.category_list);
	// for (int i = 0; i < ln.getChildCount(); i++) {
	// ReportCategoryView v = (ReportCategoryView) ln.getChildAt(i);
	// v.updateCategoryFilter();
	// }
	// }

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.amphiprion.myaccount.view.ReportCategoryView.OnCategoryClickedListener#categoryClicked(org.amphiprion.myaccount.view.ReportCategoryView)
	 */
	@Override
	public void reportCategoryClicked(ReportCategoryView view) {
		// LinearLayout ln = (LinearLayout) findViewById(R.id.rule_list);
		// if (view.getCategory() == null) {
		// ln.addView(new ReportCategoryView(this, null, this));
		// Category category = new Category();
		// rule.setCategoryId(category.getId());
		// view.setRule(rule);
		// rules.add(rule);
		// } else if (view.getRule().getState() == Rule.DbState.CREATE) {
		// ln.removeView(view);
		// rules.remove(view.getRule());
		// } else {
		// view.getRule().setState(Rule.DbState.DELETE);
		// ln.removeView(view);
		// }
	}
}
