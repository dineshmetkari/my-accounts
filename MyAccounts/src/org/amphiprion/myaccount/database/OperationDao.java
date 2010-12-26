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
import java.util.Date;
import java.util.List;

import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.database.entity.Category;
import org.amphiprion.myaccount.database.entity.Operation;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database operation access.
 * 
 * @author amphiprion
 * 
 */
public class OperationDao extends AbstractDao {
	/** The singleton. */
	private static OperationDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private OperationDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static OperationDao getInstance(Context context) {
		if (instance == null) {
			instance = new OperationDao(context);
		}
		return instance;
	}

	/**
	 * Return all operation of an account.
	 * 
	 * @param account
	 *            the account
	 * @return the operation list
	 */
	public List<Operation> getOperations(Account account) {
		String sql = "SELECT o." + Operation.DbField.ID + ", o." + Operation.DbField.AMOUNT + ", o."
				+ Operation.DbField.DESCRIPTION + ", o." + Operation.DbField.DATE + ", o."
				+ Operation.DbField.FK_CATEGORY + ", c." + Category.DbField.NAME
				+ " from OPERATION o left outer join CATEGORY c on o." + Operation.DbField.FK_CATEGORY + "=c."
				+ Category.DbField.ID + " WHERE o." + Operation.DbField.FK_ACCOUNT + "='"
				+ encodeString(account.getId()) + "'  order by o." + Operation.DbField.DATE + " desc";
		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		ArrayList<Operation> result = new ArrayList<Operation>();
		if (cursor.moveToFirst()) {
			do {
				Operation a = new Operation(cursor.getString(0));
				a.setAccountId(account.getId());
				a.setAmount(Double.parseDouble(cursor.getString(1)));
				a.setDescription(cursor.getString(2));
				a.setDate(DatabaseHelper.stringToDate(cursor.getString(3)));
				String cId = cursor.getString(4);
				if (cId != null) {
					Category c = new Category(cId);
					c.setName(cursor.getString(5));
					a.setCategory(c);
				}
				result.add(a);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new operation.
	 * 
	 * @param operation
	 *            the new operation
	 */
	public void create(Operation operation) {
		String sql = "insert into OPERATION ("
				+ Operation.DbField.ID
				+ ","
				+ Operation.DbField.AMOUNT
				+ ","
				+ Operation.DbField.DATE
				+ ","
				+ Operation.DbField.DESCRIPTION
				+ ","
				+ Operation.DbField.FK_ACCOUNT
				+ ","
				+ Operation.DbField.FK_CATEGORY
				+ ") values ('"
				+ encodeString(operation.getId())
				+ "',"
				+ operation.getAmount()
				+ ",'"
				+ DatabaseHelper.dateToString(operation.getDate())
				+ "','"
				+ encodeString(operation.getDescription())
				+ "','"
				+ encodeString(operation.getAccountId())
				+ "',"
				+ (operation.getCategory() == null ? "null" : "'" + encodeString(operation.getCategory().getId()) + "'")
				+ ")";

		execSQL(sql);
	}

	/**
	 * Update an existing operation
	 * 
	 * @param operation
	 *            the operation to update
	 */
	public void update(Operation operation) {
		String sql = "update OPERATION set " + Operation.DbField.AMOUNT + "=" + operation.getAmount() + ","
				+ Operation.DbField.DATE + "='" + DatabaseHelper.dateToString(operation.getDate()) + "',"
				+ Operation.DbField.DESCRIPTION + "='" + encodeString(operation.getDescription()) + "',"
				+ Operation.DbField.FK_ACCOUNT + "='" + encodeString(operation.getAccountId()) + "',"
				+ Operation.DbField.FK_CATEGORY + "='" + encodeString(operation.getCategory().getId()) + "' WHERE "
				+ Operation.DbField.ID + "='" + encodeString(operation.getId()) + "'";

		execSQL(sql);
	}

	/**
	 * Delete the given operation.
	 * 
	 * @param operation
	 *            the operation to delete
	 */
	public void delete(Operation operation) {
		String sql = "DELETE FROM OPERATION WHERE " + Operation.DbField.ID + "='" + encodeString(operation.getId())
				+ "'";
		execSQL(sql);
	}

	/**
	 * @param account
	 * @param operations
	 */
	public void createAll(Account account, List<Operation> operations) {
		double oldBalance = account.getBalance();
		Date oldDate = account.getLastOperation();
		getDatabase().beginTransaction();
		try {
			for (Operation op : operations) {
				op.setAccountId(account.getId());
				account.setBalance(Math.round((account.getBalance() + op.getAmount()) * 100.0) / 100.0);
				if (account.getLastOperation() == null || account.getLastOperation().before(op.getDate())) {
					account.setLastOperation(op.getDate());
				}
				create(op);
			}
			AccountDao.getInstance(getContext()).updateAccount(account);

			getDatabase().setTransactionSuccessful();
		} catch (Throwable e) {
			account.setBalance(oldBalance);
			account.setLastOperation(oldDate);
		} finally {
			getDatabase().endTransaction();
		}
	}
}
