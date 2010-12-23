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
		String sql = "SELECT " + Account.DbField.ID + "," + Account.DbField.NAME + "," + Account.DbField.BALANCE + ","
				+ Account.DbField.LAST_OPERATION + " from ACCOUNT order by " + Account.DbField.NAME + " asc";
		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		ArrayList<Account> result = new ArrayList<Account>();
		if (cursor.moveToFirst()) {
			do {
				Account a = new Account(cursor.getString(0));
				a.setName(cursor.getString(1));
				a.setBalance(cursor.getDouble(2));
				a.setLastOperation(DatabaseHelper.stringToDate(cursor.getString(3)));

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
					+ Account.DbField.BALANCE + "," + Account.DbField.LAST_OPERATION + ") values ('" + account.getId()
					+ "','" + encodeString(account.getName()) + "'," + account.getBalance() + ",'"
					+ encodeString(DatabaseHelper.dateToString(account.getLastOperation())) + "')";

			execSQL(sql);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}
}
