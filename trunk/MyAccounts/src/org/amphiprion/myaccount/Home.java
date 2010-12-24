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

import org.amphiprion.myaccount.database.AccountDao;
import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.driver.file.FileDriverManager;
import org.amphiprion.myaccount.util.CurrencyUtil;
import org.amphiprion.myaccount.view.AccountSummaryView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Home extends Activity {
	/** the file drivers class names. */
	private static String[] fileDrivers;

	/** The list of existing accounts. */
	private List<Account> accounts;

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
		buildAccountList();
	}

	private void buildAccountList() {
		accounts = new ArrayList<Account>();

		LinearLayout ln = (LinearLayout) findViewById(R.id.account_list);
		ln.removeAllViews();
		//
		// Account account = new Account();
		// account.setName("BNP Compte Join");
		// account.setBalance(3021.25);
		// account.setLastOperation(new Date());
		// account.setCurrency("EUR\u20ac");
		// accounts.add(account);
		//
		// account = new Account();
		// account.setName("Livret A");
		// account.setBalance(10000);
		// account.setCurrency("EUR\u20ac");
		// accounts.add(account);
		//
		accounts.addAll(AccountDao.getInstance(this).getAccounts());

		TextView lblTotalCurrency = (TextView) findViewById(R.id.lblTotalCurrency);
		lblTotalCurrency.setTypeface(CurrencyUtil.currencyFace);
		TextView lblTotalBalance = (TextView) findViewById(R.id.lblTotalBalance);

		String tmpCurrency = null;
		boolean canComputeTotal = false;
		double total = 0;
		for (Account a : accounts) {
			AccountSummaryView view = new AccountSummaryView(this, a);
			view.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					registerForContextMenu(v);
					openContextMenu(v);
					unregisterForContextMenu(v);
					return true;
				}
			});
			view.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO use color scheme instead
					v.setBackgroundColor(getResources().getColor(android.R.color.tab_indicator_text));
					return false;
				}
			});
			ln.addView(view);
			if (a.isExcluded()) {
				continue;
			}
			if (tmpCurrency == null) {
				tmpCurrency = a.getCurrency();
				canComputeTotal = true;
			} else if (!tmpCurrency.equals(a.getCurrency())) {
				canComputeTotal = false;
			}
			if (canComputeTotal) {
				total += a.getBalance();
			}
		}
		if (canComputeTotal) {
			if (tmpCurrency.length() > 3) {
				tmpCurrency = tmpCurrency.substring(3);
			}
			lblTotalBalance.setText("" + total);
			lblTotalCurrency.setText(tmpCurrency);
		} else {
			lblTotalBalance.setText(" - ");
			lblTotalCurrency.setText("");
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		MenuItem addAccount = menu.add(0, ApplicationConstants.MENU_ID_ADD_ACCOUNT, 1, R.string.add_account);
		addAccount.setIcon(android.R.drawable.ic_menu_add);
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
	 *      android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		Log.d(ApplicationConstants.PACKAGE, "context menu:" + v);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_ADD_ACCOUNT) {
			Intent i = new Intent(this, EditAccount.class);
			// i.putExtra("ACCOUNT", account);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_CREATE_ACCOUNT);
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
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_ACCOUNT) {
				Account account = (Account) data.getExtras().get("ACCOUNT");
				AccountDao.getInstance(this).createAccount(account);
				buildAccountList();
			}
		}
	}
}