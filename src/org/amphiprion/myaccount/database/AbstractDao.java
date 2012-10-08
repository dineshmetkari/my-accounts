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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractDao {
	private static DatabaseHelper helper;
	private static SQLiteDatabase db;
	protected Context context;

	protected AbstractDao(Context context) {
		this.context = context;
	}

	protected SQLiteDatabase getDatabase() {
		if (helper == null) {
			helper = new DatabaseHelper(context);
			db = helper.getWritableDatabase();
		}
		return db;
	}

	protected Context getContext() {
		return context;
	}

	protected void execSQL(String sql) {
		execSQL(sql, null);
	}

	protected void execSQL(String sql, String[] args) {
		boolean joinTransaction = getDatabase().inTransaction();
		if (!joinTransaction) {
			getDatabase().beginTransaction();
		}
		try {
			if (args == null) {
				getDatabase().execSQL(sql);
			} else {
				getDatabase().execSQL(sql, args);
			}
			if (!joinTransaction) {
				getDatabase().setTransactionSuccessful();
			}
		} finally {
			if (!joinTransaction) {
				getDatabase().endTransaction();
			}
		}
	}

	public String encodeString(String value) {
		if (value == null) {
			return "";
		} else {
			return value.replace("'", "''");
		}

	}
}
