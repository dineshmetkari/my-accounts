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
package org.amphiprion.myaccount.util;

import org.amphiprion.myaccount.ApplicationConstants;
import org.amphiprion.myaccount.R;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

/**
 * This is the Currency utility class.
 * 
 * @author amphiprion
 */
public class CurrencyUtil {
	public static Typeface currencyFace;
	public static String[] currencies;

	/**
	 * Initialize the currency utility.
	 * 
	 * @param context
	 *            the android context
	 */
	public static void init(Context context) {
		if (currencies == null) {
			currencies = context.getResources().getStringArray(R.array.currency_list);
			currencyFace = Typeface.createFromAsset(context.getAssets(), "tahomabd.ttf");
		}
	}

	/**
	 * Retrieve the full currency string for the given iso4217 code.
	 * 
	 * @param code
	 *            the iso4217 code
	 * @return the full currency string representation.
	 */
	public static String getCurrency(String code) {
		int index = getCurrencyIndex(code);
		if (index != -1) {
			return currencies[getCurrencyIndex(code)];
		} else {
			return null;
		}
	}

	/**
	 * Retrieve the full currency string for the given iso4217 code.
	 * 
	 * @param code
	 *            the iso4217 code
	 * @return the full currency string representation.
	 */
	public static int getCurrencyIndex(String code) {
		for (int i = 0; i < currencies.length; i++) {

			if (code.equals(currencies[i].substring(0, 3))) {
				return i;
			}
		}
		Log.e(ApplicationConstants.PACKAGE, "Currency " + code + " does not exists.");
		return -1;
	}
}
