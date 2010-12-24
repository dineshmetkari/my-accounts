package org.amphiprion.myaccount.database;

import java.util.ArrayList;
import java.util.List;

import org.amphiprion.myaccount.database.entity.Account;

import android.content.Context;
import android.database.Cursor;

public class AccountDao extends AbstractDao {
	private static AccountDao instance;

	private AccountDao(Context context) {
		super(context);
	}

	public static AccountDao getInstance(Context context) {
		if (instance == null) {
			instance = new AccountDao(context);
		}
		return instance;
	}

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

	public void createAccount(Account account) {
		getDatabase().beginTransaction();
		try {
			String sql = "insert into ACCOUNT (" + Account.DbField.ID + "," + Account.DbField.NAME + ","
					+ Account.DbField.CURRENCY + "," + Account.DbField.BALANCE + "," + Account.DbField.EXCLUDED + ","
					+ Account.DbField.LAST_OPERATION + ") values ('" + account.getId() + "','"
					+ encodeString(account.getName()) + "','" + encodeString(account.getCurrency()) + "',"
					+ account.getBalance() + "," + (account.isExcluded() ? "1" : "0") + ",'"
					+ encodeString(DatabaseHelper.dateToString(account.getLastOperation())) + "')";

			execSQL(sql);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}
}
