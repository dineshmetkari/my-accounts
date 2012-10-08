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
 * This class is responsible of all database recurent operation access.
 * 
 * @author amphiprion
 * 
 */
public class RecurentOperationDao extends AbstractDao {
	/** The singleton. */
	private static RecurentOperationDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private RecurentOperationDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static RecurentOperationDao getInstance(Context context) {
		if (instance == null) {
			instance = new RecurentOperationDao(context);
		}
		return instance;
	}

	/**
	 * Return all Recurent operation of an account.
	 * 
	 * @param account
	 *            the account
	 * @return the operation list
	 */
	public List<Operation> getRecurentOperations() {
		String sql = "SELECT o." + Operation.DbField.ID + ", o." + Operation.DbField.AMOUNT + ", o." + Operation.DbField.DESCRIPTION + ", o." + Operation.DbField.DATE + ", o."
				+ Operation.DbField.FK_CATEGORY + ", c." + Category.DbField.NAME + ", c." + Category.DbField.IMAGE_NAME + ", o." + Operation.DbField.FK_ACCOUNT
				+ " from RECURENT_OPE o left outer join CATEGORY c on o." + Operation.DbField.FK_CATEGORY + "=c." + Category.DbField.ID;
		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		ArrayList<Operation> result = new ArrayList<Operation>();
		if (cursor.moveToFirst()) {
			do {
				Operation a = new Operation(cursor.getString(0));
				a.setAccountId(cursor.getString(7));
				a.setAmount(Double.parseDouble(cursor.getString(1)));
				a.setDescription(cursor.getString(2));
				a.setDate(DatabaseHelper.stringToDate(cursor.getString(3)));
				String cId = cursor.getString(4);
				if (cId != null) {
					Category c = new Category(cId);
					c.setName(cursor.getString(5));
					c.setImage(cursor.getString(6));
					a.setCategory(c);
				}
				result.add(a);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	public void createMissingRecurentInstance(Operation recurentOperation) {
		// Log.d(ApplicationConstants.PACKAGE, "Test creation recurent: " +
		// recurentOperation.getDescription());
		getDatabase().beginTransaction();
		try {
			Account account = AccountDao.getInstance(context).getAccount(recurentOperation.getAccountId());

			Date now = new Date();
			Date date = new Date(recurentOperation.getDate().getTime());
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			while (true) {
				if (date.getMonth() == 11) {
					date.setMonth(0);
					date.setYear(date.getYear() + 1);
				} else {
					date.setMonth(date.getMonth() + 1);
				}

				// Log.d(ApplicationConstants.PACKAGE, "now=" + now + "  date="
				// + date);
				if (date.after(now)) {
					// Log.d(ApplicationConstants.PACKAGE, "Dans le futur");
					break;
				}

				Operation op = new Operation();
				op.setAccountId(recurentOperation.getAccountId());
				op.setAmount(recurentOperation.getAmount());
				op.setCategory(recurentOperation.getCategory());
				op.setDate(date);
				op.setDescription(recurentOperation.getDescription());
				op.setFkRecurent(recurentOperation.getId());

				double newBalance = Math.round((account.getBalance() + op.getAmount()) * 100.0) / 100.0;
				account.setBalance(newBalance);
				OperationDao.getInstance(context).create(op, newBalance, false);

				recurentOperation.setDate(date);
				update(recurentOperation);
			}
			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	/**
	 * Persist a new operation.
	 * 
	 * @param operation
	 *            the new operation
	 */
	public void create(Operation operation) {
		String sql = "insert into RECURENT_OPE (" + Operation.DbField.ID + "," + Operation.DbField.AMOUNT + "," + Operation.DbField.DATE + "," + Operation.DbField.DESCRIPTION
				+ "," + Operation.DbField.FK_ACCOUNT + "," + Operation.DbField.FK_CATEGORY + ") values ('" + encodeString(operation.getId()) + "'," + operation.getAmount() + ",'"
				+ DatabaseHelper.dateToString(operation.getDate()) + "','" + encodeString(operation.getDescription()) + "','" + encodeString(operation.getAccountId()) + "',"
				+ (operation.getCategory() == null ? "null" : "'" + encodeString(operation.getCategory().getId()) + "'") + ")";

		execSQL(sql);
	}

	// /**
	// * Update an existing operation
	// *
	// * @param operation
	// * the operation to update
	// * @return newBalance use to update the account balance
	// */
	// public void create(Operation operation, double newBalance) {
	// getDatabase().beginTransaction();
	// try {
	//
	// String sql = "insert into RECURENT_OPE (" + Operation.DbField.ID + "," +
	// Operation.DbField.AMOUNT + "," + Operation.DbField.DATE + "," +
	// Operation.DbField.DESCRIPTION
	// + "," + Operation.DbField.FK_ACCOUNT + "," +
	// Operation.DbField.FK_CATEGORY + ") values ('" +
	// encodeString(operation.getId()) + "'," + operation.getAmount()
	// + ",'" + DatabaseHelper.dateToString(operation.getDate()) + "','" +
	// encodeString(operation.getDescription()) + "','" +
	// encodeString(operation.getAccountId())
	// + "'," + (operation.getCategory() == null ? "null" : "'" +
	// encodeString(operation.getCategory().getId()) + "'") + ")";
	//
	// execSQL(sql);
	//
	// // sql = "UPDATE ACCOUNT set " + Account.DbField.BALANCE + "="
	// // + newBalance + " WHERE " + Account.DbField.ID + "='"
	// // + encodeString(operation.getAccountId()) + "'";
	// // execSQL(sql);
	//
	// getDatabase().setTransactionSuccessful();
	// } finally {
	// getDatabase().endTransaction();
	// }
	//
	// }

	/**
	 * Update an existing operation
	 * 
	 * @param operation
	 *            the operation to update
	 * @return newBalance use to update the account balance
	 */
	public void update(Operation operation) {

		String sql = "update RECURENT_OPE set " + Operation.DbField.AMOUNT + "=" + operation.getAmount() + "," + Operation.DbField.DATE + "='"
				+ DatabaseHelper.dateToString(operation.getDate()) + "'," + Operation.DbField.DESCRIPTION + "='" + encodeString(operation.getDescription()) + "',"
				+ Operation.DbField.FK_ACCOUNT + "='" + encodeString(operation.getAccountId()) + "'";
		if (operation.getCategory() != null) {
			sql += "," + Operation.DbField.FK_CATEGORY + "='" + encodeString(operation.getCategory().getId()) + "'";
		} else {
			sql += "," + Operation.DbField.FK_CATEGORY + "=null";
		}
		sql += " WHERE " + Operation.DbField.ID + "='" + encodeString(operation.getId()) + "'";

		execSQL(sql);

	}

	/**
	 * Delete the given operation.
	 * 
	 * @param operation
	 *            the operation to delete
	 */
	public void delete(Operation operation) {
		getDatabase().beginTransaction();
		try {
			String sql = "DELETE FROM RECURENT_OPE WHERE " + Operation.DbField.ID + "='" + encodeString(operation.getId()) + "'";
			execSQL(sql);

			sql = "UPDATE OPERATION set " + Operation.DbField.FK_RECURENT + "=null" + " WHERE " + Operation.DbField.FK_RECURENT + "='" + encodeString(operation.getFkRecurent())
					+ "'";
			execSQL(sql);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}
}
