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

import java.util.Date;
import java.util.List;

import org.amphiprion.myaccount.adapter.AccountAdapter;
import org.amphiprion.myaccount.adapter.DateAdapter;
import org.amphiprion.myaccount.adapter.ReportPeriodTypeAdapter;
import org.amphiprion.myaccount.adapter.ReportTypeAdapter;
import org.amphiprion.myaccount.database.AccountDao;
import org.amphiprion.myaccount.database.CategoryDao;
import org.amphiprion.myaccount.database.ReportDao;
import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.database.entity.Category;
import org.amphiprion.myaccount.database.entity.Report;
import org.amphiprion.myaccount.database.entity.ReportCategory;
import org.amphiprion.myaccount.database.entity.Report.PeriodType;
import org.amphiprion.myaccount.database.entity.Report.Type;
import org.amphiprion.myaccount.view.ReportCategoryView;
import org.amphiprion.myaccounts.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

	/** The full categories list. */
	private List<Category> allCategories;

	/** The report category links of the report. */
	private List<ReportCategory> reportCategories;

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_report);

		allCategories = CategoryDao.getInstance(this).getCategories();

		List<Account> accounts = AccountDao.getInstance(this).getAccounts();
		final Spinner cbAccount = (Spinner) findViewById(R.id.cbAccount);
		cbAccount.setAdapter(new AccountAdapter(this, accounts));

		final Spinner cbReportType = (Spinner) findViewById(R.id.cbReportType);
		cbReportType.setAdapter(new ReportTypeAdapter(this));

		final Spinner cbReportPeriodType = (Spinner) findViewById(R.id.cbReportPeriodType);
		cbReportPeriodType.setAdapter(new ReportPeriodTypeAdapter(this));

		final TextView txtName = (TextView) findViewById(R.id.txtReportName);

		final Spinner cbFromDate = (Spinner) findViewById(R.id.cbFromDate);
		cbFromDate.setAdapter(new DateAdapter(this));

		final Spinner cbToDate = (Spinner) findViewById(R.id.cbToDate);
		cbToDate.setAdapter(new DateAdapter(this));

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

		if (report.getFrom() != null) {
			((DateAdapter) cbFromDate.getAdapter()).add(report.getFrom());
		} else {
			((DateAdapter) cbFromDate.getAdapter()).add(new Date());
		}
		if (report.getTo() != null) {
			((DateAdapter) cbToDate.getAdapter()).add(report.getTo());
		} else {
			((DateAdapter) cbToDate.getAdapter()).add(new Date());
		}

		cbReportType.setSelection(report.getType().ordinal());
		cbReportPeriodType.setSelection(report.getPeriodType().ordinal());
		if (report.getAccountId() != null) {
			cbAccount.setSelection(accounts.indexOf(new Account(report.getAccountId())));
		}
		updateVisibility(report.getPeriodType());
		updateListVisibility(report.getType());
		cbReportPeriodType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				updateVisibility((PeriodType) cbReportPeriodType.getSelectedItem());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		cbReportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				updateListVisibility((Type) cbReportType.getSelectedItem());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		Button btSave = (Button) findViewById(R.id.btSave);
		btSave.setOnClickListener(new ViewGroup.OnClickListener() {
			@Override
			public void onClick(View v) {
				report.setName("" + txtName.getText());
				report.setType((Type) cbReportType.getSelectedItem());
				report.setPeriodType((PeriodType) cbReportPeriodType.getSelectedItem());
				report.setAccountId(((Account) cbAccount.getSelectedItem()).getId());
				report.setFrom((Date) cbFromDate.getSelectedItem());
				report.setTo((Date) cbToDate.getSelectedItem());
				updateReportCategoryFilters();
				report.setReportCategories(reportCategories);
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

		buildReportCategoryList();

	}

	private void updateVisibility(PeriodType periodType) {
		final LinearLayout panelDates = (LinearLayout) findViewById(R.id.pReportDates);
		if (periodType == PeriodType.CUSTOM) {
			panelDates.setVisibility(View.VISIBLE);
		} else {
			panelDates.setVisibility(View.GONE);
		}
	}

	private void updateListVisibility(Type type) {
		LinearLayout ln = (LinearLayout) findViewById(R.id.pReport_category_list);
		if (type == Type.DAILY_BALANCE || type == Type.TREND) {
			ln.setVisibility(View.INVISIBLE);
		} else {
			ln.setVisibility(View.VISIBLE);
		}
	}

	private void buildReportCategoryList() {
		reportCategories = ReportDao.getInstance(this).getReportCategories(report);

		LinearLayout ln = (LinearLayout) findViewById(R.id.report_category_list);
		ln.removeAllViews();
		for (ReportCategory reportCategory : reportCategories) {
			ln.addView(new ReportCategoryView(this, reportCategory, this, allCategories));
		}
		ln.addView(new ReportCategoryView(this, null, this, allCategories));
	}

	/**
	 * Ask the rule views to update its underlying rule.
	 */
	private void updateReportCategoryFilters() {
		LinearLayout ln = (LinearLayout) findViewById(R.id.report_category_list);
		for (int i = 0; i < ln.getChildCount(); i++) {
			ReportCategoryView v = (ReportCategoryView) ln.getChildAt(i);
			v.updateReportCategoryFilter();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.amphiprion.myaccount.view.ReportCategoryView.OnCategoryClickedListener#categoryClicked(org.amphiprion.myaccount.view.ReportCategoryView)
	 */
	@Override
	public void reportCategoryClicked(ReportCategoryView view) {
		LinearLayout ln = (LinearLayout) findViewById(R.id.report_category_list);
		if (view.getReportCategory() == null) {
			ln.addView(new ReportCategoryView(this, null, this, allCategories));
			ReportCategory reportCategory = new ReportCategory(report.getId());
			view.setReportCategory(reportCategory);
			reportCategories.add(reportCategory);
		} else {
			ln.removeView(view);
			reportCategories.remove(view.getReportCategory());
		}
	}
}
