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

import java.util.ArrayList;
import java.util.List;

import org.amphiprion.myaccount.database.ReportDao;
import org.amphiprion.myaccount.database.entity.Report;
import org.amphiprion.myaccount.view.ReportSummaryView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.LinearLayout;

/**
 * This activity manage the reports.
 * 
 * @author amphiprion
 * 
 */
public class ReportList extends Activity {
	private Report current;

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setContentView(new XYLineChart(getApplicationContext()));
		setContentView(R.layout.report_list);

		buildReportList();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		MenuItem addAccount = menu.add(0, ApplicationConstants.MENU_ID_ADD_REPORT, 1, R.string.add_report);
		addAccount.setIcon(android.R.drawable.ic_menu_add);
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_ADD_REPORT) {
			Intent i = new Intent(this, EditReport.class);
			// i.putExtra("REPORT", report);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_CREATE_REPORT);
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_REPORT) {
				Report report = (Report) data.getExtras().get("REPORT");
				ReportDao.getInstance(this).createReport(report);
				buildReportList();
			} else if (requestCode == ApplicationConstants.ACTIVITY_RETURN_EDIT_CATEGORY) {
				Report report = (Report) data.getExtras().get("REPORT");
				ReportDao.getInstance(this).updateReport(report);
				buildReportList();
			}
		}
	}

	/**
	 * Build the report list.
	 */
	private void buildReportList() {
		List<Report> reports = new ArrayList<Report>();

		LinearLayout ln = (LinearLayout) findViewById(R.id.report_list);
		ln.removeAllViews();

		reports.addAll(ReportDao.getInstance(this).getReports());

		for (Report c : reports) {
			ReportSummaryView view = new ReportSummaryView(this, c);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					edit(((ReportSummaryView) v).getReport());
				}
			});

			view.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					registerForContextMenu(v);
					openContextMenu(v);
					unregisterForContextMenu(v);
					return true;
				}
			});

			ln.addView(view);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
	 *      android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.clear();
		if (v instanceof ReportSummaryView) {
			current = ((ReportSummaryView) v).getReport();
			menu.add(1, ApplicationConstants.MENU_ID_EDIT_REPORT, 0, R.string.edit_report_title);
			menu.add(1, ApplicationConstants.MENU_ID_DELETE_REPORT, 1, R.string.delete_report_title);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_EDIT_REPORT) {
			edit(current);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_DELETE_REPORT) {
			ReportDao.getInstance(this).delete(current);
			buildReportList();
		}
		return true;
	}

	/**
	 * Edit the given report.
	 * 
	 * @param report
	 *            the report to edit
	 */
	private void edit(Report report) {
		Intent i = new Intent(this, EditReport.class);
		i.putExtra("REPORT", report);
		startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_EDIT_REPORT);
	}

}
