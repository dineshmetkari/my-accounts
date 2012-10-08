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

import org.amphiprion.myaccount.util.CurrencyUtil;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * This is the adapter for the Currency spinner.
 * 
 * @author amphiprion
 * 
 */
public class CurrencyAdapter extends ArrayAdapter<String> {

	/**
	 * Default constructor.
	 */
	public CurrencyAdapter(Context context) {
		super(context, android.R.layout.simple_spinner_item, CurrencyUtil.currencies);
		setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public String getItem(int arg0) {
		return CurrencyUtil.currencies[arg0].substring(0, 3);
	}

}
