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

import java.io.File;

import org.amphiprion.myaccount.driver.file.FileDriverManager;
import org.amphiprion.myaccount.util.CurrencyUtil;
import org.amphiprion.myaccount.util.DateUtil;
import org.amphiprion.myaccounts.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TabHost;

public class Home extends TabActivity {
	/** the file drivers class names. */
	private static String[] fileDrivers;

	/**
	 * Called when the activity is first created. {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (fileDrivers == null) {
			fileDrivers = getResources().getStringArray(R.array.file_driver);
			FileDriverManager.init(fileDrivers);
			CurrencyUtil.init(this);
			DateUtil.init(this);

			File file = new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.NAME + "/" + ApplicationConstants.BACKUP_DRIRECTORY);
			file.mkdirs();
		}

		setContentView(R.layout.main);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, AccountList.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("accountlist").setIndicator(res.getString(R.string.account_tab), res.getDrawable(R.drawable.bank_tab_icon)).setContent(intent);

		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, CategoryList.class);
		spec = tabHost.newTabSpec("categorylist").setIndicator(res.getString(R.string.category_tab), res.getDrawable(R.drawable.tag_tab_icon)).setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, BudgetList.class);
		spec = tabHost.newTabSpec("budgetlist").setIndicator(res.getString(R.string.budget_tab), res.getDrawable(R.drawable.budget_tab_icon)).setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, ReportList.class);
		spec = tabHost.newTabSpec("reportlist").setIndicator(res.getString(R.string.report_tab), res.getDrawable(R.drawable.report_tab_icon)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}
}