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

import java.util.ArrayList;
import java.util.List;

import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.database.entity.Operation;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsable of all database account access.
 * 
 * @author amphiprion
 * 
 */
public class AccountDao extends AbstractDao {
	/** The singleton. */
	private static AccountDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private AccountDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static AccountDao getInstance(Context context) {
		if (instance == null) {
			instance = new AccountDao(context);
		}
		return instance;
	}

	/**
	 * Return all existing accounts.
	 * 
	 * @return the account list
	 */
	public List<Account> getAccounts() {
		String sql = "SELECT " + Account.DbField.ID + "," + Account.DbField.NAME + "," + Account.DbField.CURRENCY + ","
				+ Account.DbField.BALANCE + "," + Account.DbField.LAST_OPERATION + "," + Account.DbField.EXCLUDED
				+ " from ACCOUNT order by " + Account.DbField.NAME + " asc";
		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		ArrayList<Account> result = new ArrayList<Account>();
		if (cursor.moveToFirst()) {
			do {
				Account a = new Account(cursor.getString(0));
				a.setName(cursor.getString(1));
				a.setCurrency(cursor.getString(2));
				a.setBalance(cursor.getDouble(3));
				a.setLastOperation(DatabaseHelper.stringToDate(cursor.getString(4)));
				a.setExcluded("1".equals(cursor.getString(5)));
				result.add(a);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new account.
	 * 
	 * @param account
	 *            the new account
	 */
	public void createAccount(Account account) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into ACCOUNT ("
					+ Account.DbField.ID
					+ ","
					+ Account.DbField.NAME
					+ ","
					+ Account.DbField.CURRENCY
					+ ","
					+ Account.DbField.BALANCE
					+ ","
					+ Account.DbField.EXCLUDED
					+ ","
					+ Account.DbField.LAST_OPERATION
					+ ") values ('"
					+ account.getId()
					+ "','"
					+ encodeString(account.getName())
					+ "','"
					+ encodeString(account.getCurrency())
					+ "',"
					+ account.getBalance()
					+ ","
					+ (account.isExcluded() ? "1" : "0")
					+ ","
					+ (account.getLastOperation() == null ? "null" : "'"
							+ encodeString(DatabaseHelper.dateToString(account.getLastOperation())) + "'") + ")";

			execSQL(sql);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	/**
	 * Update an existing account
	 * 
	 * @param account
	 *            the accoun to update
	 */
	public void updateAccount(Account account) {
		String sql = "update ACCOUNT set "
				+ Account.DbField.NAME
				+ "='"
				+ encodeString(account.getName())
				+ "',"
				+ Account.DbField.CURRENCY
				+ "='"
				+ encodeString(account.getCurrency())
				+ "',"
				+ Account.DbField.BALANCE
				+ "="
				+ account.getBalance()
				+ ","
				+ Account.DbField.EXCLUDED
				+ "="
				+ (account.isExcluded() ? "1" : "0")
				+ ","
				+ Account.DbField.LAST_OPERATION
				+ "="
				+ (account.getLastOperation() == null ? "null" : "'"
						+ encodeString(DatabaseHelper.dateToString(account.getLastOperation())) + "'") + " WHERE "
				+ Account.DbField.ID + "='" + encodeString(account.getId()) + "'";

		execSQL(sql);
	}

	/**
	 * Delete the given account.
	 * 
	 * @param account
	 *            the account
	 */
	public void delete(Account account) {
		getDatabase().beginTransaction();
		try {
			String sql = "DELETE FROM OPERATION WHERE " + Operation.DbField.FK_ACCOUNT + "='" + account.getId() + "'";
			execSQL(sql);

			sql = "DELETE FROM ACCOUNT WHERE " + Account.DbField.ID + "='" + account.getId() + "'";
			execSQL(sql);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}
}
