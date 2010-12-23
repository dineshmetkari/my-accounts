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

import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.driver.file.FileDriverManager;
import org.amphiprion.myaccount.view.AccountSummaryView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class Home extends Activity {
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
		}

		// String path = Environment.getExternalStorageDirectory() + "/" +
		// ApplicationConstants.NAME;
		// path += "/" + "E3567945.qif";
		// FileInputStream fis = null;
		// try {
		// fis = new FileInputStream(path);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// }
		// FileDriver driver = FileDriverManager.getDrivers().get(0);
		// driver.parse(fis, driver.getParameters());

		setContentView(R.layout.main);

		LinearLayout ln = (LinearLayout) findViewById(R.id.account_list);
		Account account = new Account();
		account.setName("BNP Compte Join");
		account.setBalance(3021.25);
		account.setLastOperation(new Date());
		ln.addView(new AccountSummaryView(this, account));
		account = new Account();
		account.setName("Livret A");
		account.setBalance(10000);
		ln.addView(new AccountSummaryView(this, account));
	}
}