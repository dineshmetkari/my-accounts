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
package org.amphiprion.myaccount.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.amphiprion.myaccount.ApplicationConstants;
import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.database.entity.Operation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "amphiprion_myaccount";
	private static final int DATABASE_VERSION = 1;
	private static SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL("create table ACCOUNT (" + Account.DbField.ID + " text primary key, " + Account.DbField.NAME
					+ " text not null, " + Account.DbField.BALANCE + " double not null, "
					+ Account.DbField.LAST_OPERATION + " date) ");
			db.execSQL("create table OPERATION (" + Operation.DbField.ID + " text primary key, "
					+ Operation.DbField.DATE + " date not null , " + Operation.DbField.AMOUNT + " double not null, "
					+ Operation.DbField.DESCRIPTION + " string not null, " + Operation.DbField.TAG + " text not null, "
					+ Operation.DbField.ICON + " text, " + Operation.DbField.FK_ACCOUNT + " text not null) ");

		} catch (Throwable e) {
			Log.e(ApplicationConstants.PACKAGE, "Can not create database.", e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	/**
	 * Return the Date for the given iso8601 date string
	 * 
	 * @param dbDate
	 *            the string representation
	 * @return the date
	 */
	public static Date stringToDate(String dbDate) {
		if (dbDate == null || "".equals(dbDate)) {
			return null;
		}
		try {
			return iso8601Format.parse(dbDate);
		} catch (ParseException e) {
			Log.e(ApplicationConstants.PACKAGE, "Can not parse date", e);
			return null;
		}
	}

	/**
	 * Return the iso8601 string representation of the given date.
	 * 
	 * @param date
	 *            the date
	 * @return the string representation
	 */
	public static String dateToString(Date date) {
		if (date == null) {
			return null;
		}
		return iso8601Format.format(date);
	}
}
