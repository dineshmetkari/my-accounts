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

import org.amphiprion.myaccount.adapter.FileDriverAdapter;
import org.amphiprion.myaccount.database.OperationDao;
import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.database.entity.Operation;
import org.amphiprion.myaccount.driver.file.FileDriverManager;
import org.amphiprion.myaccount.util.CurrencyUtil;
import org.amphiprion.myaccount.view.OperationSummaryView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This activity manage the tags.
 * 
 * @author amphiprion
 * 
 */
public class OperationList extends Activity {
	private Account account;

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		account = (Account) getIntent().getSerializableExtra("ACCOUNT");
		setTitle(getResources().getText(R.string.operations_title) + " " + account.getName());
		setContentView(R.layout.operation_list);

		buildOperationList(account);
	}

	private void buildOperationList(Account account) {
		TextView lblTotalCurrency = (TextView) findViewById(R.id.lblTotalCurrency);
		lblTotalCurrency.setTypeface(CurrencyUtil.currencyFace);
		TextView lblTotalBalance = (TextView) findViewById(R.id.lblTotalBalance);

		lblTotalBalance.setText("" + account.getBalance());
		String tmpCurrency = account.getCurrency();
		if (tmpCurrency.length() > 3) {
			tmpCurrency = tmpCurrency.substring(3);
		}
		lblTotalCurrency.setText(tmpCurrency);

		List<Operation> operations = OperationDao.getInstance(this).getOperations(account);

		LinearLayout ln = (LinearLayout) findViewById(R.id.operation_list);
		ln.removeAllViews();
		for (Operation op : operations) {
			ln.addView(new OperationSummaryView(this, op, account.getCurrency()));
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

		MenuItem addAccount = menu.add(0, ApplicationConstants.MENU_ID_IMPORT_OPERATION, 1, R.string.import_operation);
		addAccount.setIcon(android.R.drawable.ic_menu_recent_history);
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_IMPORT_OPERATION) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.file_import_choice_title));
			builder.setSingleChoiceItems(new FileDriverAdapter(OperationList.this, FileDriverManager.getDrivers()), -1,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							dialog.dismiss();
							Intent i = new Intent(OperationList.this, DefineImportParameter.class);
							i.putExtra("ACCOUNT", account);
							i.putExtra("FILE_DRIVER_INDEX", item);
							startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_IMPORT_OPERATION);
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
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
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_IMPORT_OPERATION) {
				account = (Account) data.getSerializableExtra("ACCOUNT");
				buildOperationList(account);
			}
		}

	}
}
