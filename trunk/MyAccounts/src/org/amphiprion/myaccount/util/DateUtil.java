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

import java.text.SimpleDateFormat;

import org.amphiprion.myaccount.R;

import android.content.Context;

/**
 * This is an utility class for managing date.
 * 
 * @author amphiprion
 * 
 */
public class DateUtil {
	/** The default date format. */
	public static SimpleDateFormat defaultDateFormat;
	/** The default operation date format. */
	public static SimpleDateFormat defaultOperationDateFormat;

	/**
	 * Initialize the currency utility.
	 * 
	 * @param context
	 *            the android context
	 */
	public static void init(Context context) {
		if (defaultDateFormat == null) {
			String format = context.getResources().getString(R.string.default_date_format);
			defaultDateFormat = new SimpleDateFormat(format);
			String opFormat = context.getResources().getString(R.string.default_operation_date_format);
			defaultOperationDateFormat = new SimpleDateFormat(opFormat);
		}
	}

}
