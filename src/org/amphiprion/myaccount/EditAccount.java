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

import org.amphiprion.myaccount.adapter.CurrencyAdapter;
import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.util.CurrencyUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * The activity used to edit/create an account.
 * 
 * @author amphiprion
 * 
 */
public class EditAccount extends Activity {
	private Account account;

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_account);

		final Spinner cbCurrency = (Spinner) findViewById(R.id.cbCurrency);
		final TextView txtName = (TextView) findViewById(R.id.txtAccountName);
		final TextView txtBalance = (TextView) findViewById(R.id.txtBalance);
		final CheckBox chkExclude = (CheckBox) findViewById(R.id.chkExclude);

		String iso = getResources().getString(R.string.default_currency);
		if (account == null) {
			Intent intent = getIntent();
			if (intent.getExtras() != null) {
				account = (Account) intent.getExtras().getSerializable("ACCOUNT");
				iso = account.getCurrency().substring(0, 3);
				txtName.setText(account.getName());
				txtBalance.setText("" + account.getBalance());
				chkExclude.setChecked(account.isExcluded());
			}
		}
		int selectedIndex = CurrencyUtil.getCurrencyIndex(iso);
		if (account == null) {
			// its a creation
			account = new Account();
			account.setCurrency(CurrencyUtil.currencies[selectedIndex]);
		}

		cbCurrency.setAdapter(new CurrencyAdapter(this));
		cbCurrency.setSelection(selectedIndex);

		Button btSave = (Button) findViewById(R.id.btSave);
		btSave.setOnClickListener(new ViewGroup.OnClickListener() {
			@Override
			public void onClick(View v) {
				account.setName("" + txtName.getText());
				account.setBalance(Double.parseDouble("" + txtBalance.getText()));
				account.setCurrency(CurrencyUtil.currencies[cbCurrency.getSelectedItemPosition()]);
				account.setExcluded(chkExclude.isChecked());

				Intent i = new Intent();
				i.putExtra("ACCOUNT", account);
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
	}
}
