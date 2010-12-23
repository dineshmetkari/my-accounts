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
import org.amphiprion.myaccount.database.entity.Account;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View used to display an account in the account list.
 * 
 * @author amphiprion
 * 
 */
public class AccountSummaryView extends LinearLayout {
	/**
	 * Construct an account view.
	 * 
	 * @param context
	 *            the context
	 * @param account
	 *            the account entity
	 */
	public AccountSummaryView(Context context, Account account) {
		super(context);
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_background));

		LinearLayout accountLayout = new LinearLayout(context);
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3);
		accountLayout.setOrientation(VERTICAL);
		accountLayout.setLayoutParams(aclp);
		TextView t = new TextView(context);
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		t.setLayoutParams(tlp);
		t.setText(account.getName());
		t.setTextSize(16);
		t.setTypeface(Typeface.DEFAULT_BOLD);
		t.setTextColor(context.getResources().getColor(R.color.black));
		accountLayout.addView(t);
		TextView desc = new TextView(context);
		if (account.getLastOperation() != null) {
			desc.setText("" + account.getLastOperation());
		}
		accountLayout.addView(desc);

		addView(accountLayout);

		TextView balance = new TextView(context);
		LayoutParams blp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		balance.setGravity(Gravity.RIGHT);
		balance.setLayoutParams(blp);
		balance.setText("" + account.getBalance());
		balance.setTextSize(16);
		balance.setTypeface(Typeface.DEFAULT_BOLD);
		if (account.getBalance() < 0) {
			balance.setTextColor(context.getResources().getColor(R.color.negative));
		} else {
			balance.setTextColor(context.getResources().getColor(R.color.positive));
		}
		addView(balance);
	}
}
