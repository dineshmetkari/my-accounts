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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.amphiprion.myaccount.database.AccountDao;
import org.amphiprion.myaccount.database.DatabaseBackupDao;
import org.amphiprion.myaccount.database.DatabaseHelper;
import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.util.CurrencyUtil;
import org.amphiprion.myaccount.view.AccountSummaryView;
import org.amphiprion.myaccounts.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AccountList extends Activity {
	/** The current category for context menu. */
	private Account current;

	/** The list of existing accounts. */
	private List<Account> accounts;

	/**
	 * Called when the activity is first created. {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.account_list);
		buildAccountList();
	}

	private void buildAccountList() {
		accounts = new ArrayList<Account>();

		LinearLayout ln = (LinearLayout) findViewById(R.id.account_list);
		ln.removeAllViews();
		accounts.addAll(AccountDao.getInstance(this).getAccounts());

		TextView lblTotalCurrency = (TextView) findViewById(R.id.lblTotalCurrency);
		lblTotalCurrency.setTypeface(CurrencyUtil.currencyFace);
		TextView lblTotalBalance = (TextView) findViewById(R.id.lblTotalBalance);

		String tmpCurrency = null;
		boolean canComputeTotal = false;
		double total = 0;
		for (Account a : accounts) {
			AccountSummaryView view = new AccountSummaryView(this, a);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (v instanceof AccountSummaryView) {
						Account account = ((AccountSummaryView) v).getAccount();
						Intent i = new Intent(AccountList.this, OperationList.class);
						i.putExtra("ACCOUNT", account);
						startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_VIEW_OPERATION_LIST);
					}
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
			lblTotalBalance.setText("" + (Math.round(total * 100.0) / 100.0));
			lblTotalCurrency.setText(tmpCurrency);
		} else {
			lblTotalBalance.setText(" - ");
			lblTotalCurrency.setText("");
		}
		if (accounts.isEmpty()) {
			TextView tv = new TextView(this);
			tv.setText(R.string.empty_account_list);
			ln.addView(tv);
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

		MenuItem backup = menu.add(1, ApplicationConstants.MENU_ID_BACKUP_DB, 1, R.string.backup_db);
		backup.setIcon(android.R.drawable.ic_menu_more);

		MenuItem restore = menu.add(1, ApplicationConstants.MENU_ID_RESTORE_DB, 2, R.string.restore_db);
		restore.setIcon(android.R.drawable.ic_menu_more);
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
		menu.clear();
		if (v instanceof AccountSummaryView) {
			current = ((AccountSummaryView) v).getAccount();
			menu.add(1, ApplicationConstants.MENU_ID_EDIT_ACCOUNT, 0, R.string.edit_account_title);
			menu.add(1, ApplicationConstants.MENU_ID_DELETE_ACCOUNT, 1, R.string.delete_account_title);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_EDIT_ACCOUNT) {
			edit(current);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_DELETE_ACCOUNT) {
			AccountDao.getInstance(this).delete(current);
			buildAccountList();
		}
		return true;
	}

	/**
	 * Edit the given account.
	 * 
	 * @param account
	 *            the account to edit
	 */
	private void edit(Account account) {
		Intent i = new Intent(this, EditAccount.class);
		i.putExtra("ACCOUNT", account);
		startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_EDIT_ACCOUNT);
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
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_BACKUP_DB) {
			String path = Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.NAME + "/"
					+ ApplicationConstants.BACKUP_DRIRECTORY + "/";
			path += new SimpleDateFormat("yyyyMMdd-hh-mm-ss").format(new Date());
			path += "-dbv" + DatabaseHelper.DATABASE_VERSION + ".xml";
			boolean ok = DatabaseBackupDao.getInstance(this).backupDatabase(path);
			if (ok) {
				Toast.makeText(this, R.string.backup_db_ok, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, R.string.backup_db_ko, Toast.LENGTH_LONG).show();
			}
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_RESTORE_DB) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			File file = new File(Environment.getExternalStorageDirectory() + "/" + ApplicationConstants.NAME + "/"
					+ ApplicationConstants.BACKUP_DRIRECTORY);
			intent.setDataAndType(Uri.parse(file.toString()), "text/*");

			try {
				startActivityForResult(intent, ApplicationConstants.ACTIVITY_RETURN_CHOOSE_RESTORE_FILE);
			} catch (ActivityNotFoundException e) {
				// No activity to handle this mime type.
				// Download for exemple ES File Manager
				askToDownloadFileManager();
			}

		}
		return true;
	}

	public void askToDownloadFileManager() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage(R.string.install_file_chooser_message)
				.create();
		alertDialog.setTitle(R.string.install_file_chooser_title);
		alertDialog.setIcon(android.R.drawable.ic_menu_info_details);
		alertDialog.setButton(getResources().getText(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("market://details?id=lysesoft.andexplorer"));
				startActivity(marketIntent);
				return;
			}
		});
		alertDialog.setButton2(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// do nothing
			}
		});
		alertDialog.show();
		return;

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
			} else if (requestCode == ApplicationConstants.ACTIVITY_RETURN_EDIT_ACCOUNT) {
				Account account = (Account) data.getExtras().get("ACCOUNT");
				AccountDao.getInstance(this).updateAccount(account);
				buildAccountList();
			} else if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CHOOSE_RESTORE_FILE) {
				String path = data.getDataString();
				boolean ok = DatabaseBackupDao.getInstance(this).restoreDatabase(path);
				if (ok) {
					Toast.makeText(this, R.string.restore_db_ok, Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(this, R.string.restore_db_ko, Toast.LENGTH_LONG).show();
				}
				buildAccountList();
			}

		}
		if (requestCode == ApplicationConstants.ACTIVITY_RETURN_VIEW_OPERATION_LIST) {
			buildAccountList();
		}
	}
}