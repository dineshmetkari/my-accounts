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

import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.util.CurrencyUtil;
import org.amphiprion.myaccount.util.DateUtil;
import org.amphiprion.myaccounts.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View used to display an account in the account list.
 * 
 * @author amphiprion
 * 
 */
public class AccountSummaryView extends LinearLayout {
	/** the linked account. */
	private Account account;

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
		this.account = account;
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_background_states));

		addView(createIcon());

		addView(createAccountLayout());

		addView(createBalance());
		addView(createCurrency());
	}

	/**
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * Create the account icon view.
	 * 
	 * @return the view
	 */
	private View createIcon() {
		ImageView img = new ImageView(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imglp.gravity = Gravity.CENTER_VERTICAL;
		imglp.rightMargin = 5;
		img.setLayoutParams(imglp);

		img.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.account));
		return img;
	}

	/**
	 * Create the account layout view
	 * 
	 * @return the view
	 */
	private View createAccountLayout() {
		LinearLayout accountLayout = new LinearLayout(getContext());
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 3);
		accountLayout.setOrientation(VERTICAL);
		accountLayout.setLayoutParams(aclp);
		TextView t = new TextView(getContext());
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		t.setLayoutParams(tlp);
		t.setText(account.getName());
		t.setTextSize(16);
		t.setTypeface(Typeface.DEFAULT_BOLD);
		t.setTextColor(getContext().getResources().getColor(R.color.black));
		accountLayout.addView(t);

		TextView desc = new TextView(getContext());
		if (account.getLastOperation() != null) {
			desc.setText("" + DateUtil.defaultDateFormat.format(account.getLastOperation()));
		}
		accountLayout.addView(desc);
		return accountLayout;
	}

	/**
	 * Create the balance view.
	 * 
	 * @return the view
	 */
	private View createBalance() {
		TextView balance = new TextView(getContext());
		LayoutParams blp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		balance.setGravity(Gravity.RIGHT);
		balance.setLayoutParams(blp);
		balance.setText("" + account.getBalance());
		balance.setTextSize(16);
		balance.setTypeface(Typeface.DEFAULT_BOLD);
		if (account.getBalance() < 0) {
			balance.setTextColor(getContext().getResources().getColor(R.color.negative));
		} else {
			balance.setTextColor(getContext().getResources().getColor(R.color.positive));
		}
		return balance;
	}

	/**
	 * Create the balance view.
	 * 
	 * @return the view
	 */
	private View createCurrency() {
		TextView t = new TextView(getContext());
		LayoutParams blp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		blp.leftMargin = 5;
		t.setLayoutParams(blp);

		if (account.getCurrency().length() > 3) {
			t.setText(account.getCurrency().substring(3));
		} else {
			t.setText(account.getCurrency());
		}
		t.setTextSize(16);
		Typeface font = CurrencyUtil.currencyFace;
		t.setTypeface(font);
		if (account.getBalance() < 0) {
			t.setTextColor(getContext().getResources().getColor(R.color.negative));
		} else {
			t.setTextColor(getContext().getResources().getColor(R.color.positive));
		}
		return t;
	}
}
