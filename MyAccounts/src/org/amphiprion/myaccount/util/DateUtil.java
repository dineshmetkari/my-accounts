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
import java.util.Calendar;
import java.util.Date;

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
	/** The default month format. */
	public static SimpleDateFormat monthFormat;

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
			monthFormat = new SimpleDateFormat("MMMM");
		}
	}

	public static Date[] getThisMonthRange() {
		Calendar cFrom = Calendar.getInstance();
		cFrom.set(Calendar.DAY_OF_MONTH, 1);
		cFrom.set(Calendar.HOUR_OF_DAY, 0);
		cFrom.set(Calendar.MINUTE, 0);
		cFrom.set(Calendar.SECOND, 0);
		cFrom.set(Calendar.MILLISECOND, 0);

		Calendar cTo = Calendar.getInstance();
		cTo.set(Calendar.DAY_OF_MONTH, 1);
		cTo.set(Calendar.HOUR_OF_DAY, 23);
		cTo.set(Calendar.MINUTE, 59);
		cTo.set(Calendar.SECOND, 59);
		cTo.set(Calendar.MILLISECOND, 0);
		cTo.add(Calendar.MONTH, 1);
		cTo.add(Calendar.DAY_OF_MONTH, -1);

		return new Date[] { cFrom.getTime(), cTo.getTime() };
	}

	public static Date[] getLastMonthRange() {
		Calendar cFrom = Calendar.getInstance();
		cFrom.set(Calendar.DAY_OF_MONTH, 1);
		cFrom.add(Calendar.MONTH, -1);
		cFrom.set(Calendar.HOUR_OF_DAY, 0);
		cFrom.set(Calendar.MINUTE, 0);
		cFrom.set(Calendar.SECOND, 0);
		cFrom.set(Calendar.MILLISECOND, 0);

		Calendar cTo = Calendar.getInstance();
		cTo.set(Calendar.DAY_OF_MONTH, 1);
		cTo.set(Calendar.HOUR_OF_DAY, 23);
		cTo.set(Calendar.MINUTE, 59);
		cTo.set(Calendar.SECOND, 59);
		cTo.set(Calendar.MILLISECOND, 0);
		cTo.add(Calendar.DAY_OF_MONTH, -1);

		return new Date[] { cFrom.getTime(), cTo.getTime() };
	}

	public static Date[] getLastYearRange() {
		Calendar cFrom = Calendar.getInstance();
		cFrom.set(Calendar.DAY_OF_MONTH, 1);
		cFrom.set(Calendar.MONTH, Calendar.JANUARY);
		cFrom.add(Calendar.YEAR, -1);
		cFrom.set(Calendar.HOUR_OF_DAY, 0);
		cFrom.set(Calendar.MINUTE, 0);
		cFrom.set(Calendar.SECOND, 0);
		cFrom.set(Calendar.MILLISECOND, 0);

		Calendar cTo = Calendar.getInstance();
		cTo.set(Calendar.DAY_OF_MONTH, 1);
		cTo.set(Calendar.MONTH, Calendar.JANUARY);
		cTo.set(Calendar.HOUR_OF_DAY, 23);
		cTo.set(Calendar.MINUTE, 59);
		cTo.set(Calendar.SECOND, 59);
		cTo.set(Calendar.MILLISECOND, 0);
		cTo.add(Calendar.DAY_OF_MONTH, -1);

		return new Date[] { cFrom.getTime(), cTo.getTime() };
	}

	public static Date[] getThisYearRange() {
		Calendar cFrom = Calendar.getInstance();
		cFrom.set(Calendar.DAY_OF_MONTH, 1);
		cFrom.set(Calendar.MONTH, Calendar.JANUARY);
		cFrom.set(Calendar.HOUR_OF_DAY, 0);
		cFrom.set(Calendar.MINUTE, 0);
		cFrom.set(Calendar.SECOND, 0);
		cFrom.set(Calendar.MILLISECOND, 0);

		Calendar cTo = Calendar.getInstance();
		cTo.set(Calendar.DAY_OF_MONTH, 1);
		cTo.set(Calendar.MONTH, Calendar.JANUARY);
		cFrom.add(Calendar.YEAR, 1);
		cTo.set(Calendar.HOUR_OF_DAY, 23);
		cTo.set(Calendar.MINUTE, 59);
		cTo.set(Calendar.SECOND, 59);
		cTo.set(Calendar.MILLISECOND, 0);
		cTo.add(Calendar.DAY_OF_MONTH, -1);

		return new Date[] { cFrom.getTime(), cTo.getTime() };
	}

	public static String getThisMonth() {
		String s = monthFormat.format(new Date());
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static String getLastMonth() {
		Calendar cFrom = Calendar.getInstance();
		cFrom.set(Calendar.DAY_OF_MONTH, 1);
		cFrom.add(Calendar.MONTH, -1);
		String s = monthFormat.format(cFrom.getTime());
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
}
