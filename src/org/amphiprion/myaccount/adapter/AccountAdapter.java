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
package org.amphiprion.myaccount.adapter;

import java.util.List;

import org.amphiprion.myaccount.database.entity.Account;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * This is the adapter for the File driver date format chooser.
 * 
 * @author amphiprion
 * 
 */
public class AccountAdapter extends ArrayAdapter<Account> {

	/**
	 * Default constructor.
	 */
	public AccountAdapter(Context context, List<Account> accounts) {
		super(context, android.R.layout.simple_spinner_item, accounts);
		setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

}
