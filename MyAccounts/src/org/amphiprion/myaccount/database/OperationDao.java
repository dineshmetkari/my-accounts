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

import net.droidsolutions.droidcharts.core.data.CategoryDataset;
import net.droidsolutions.droidcharts.core.data.DefaultCategoryDataset;
import net.droidsolutions.droidcharts.core.data.DefaultPieDataset;
import net.droidsolutions.droidcharts.core.data.PieDataset;

import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.database.entity.Category;
import org.amphiprion.myaccount.database.entity.Operation;
import org.amphiprion.myaccount.database.entity.Report;
import org.amphiprion.myaccount.database.entity.ReportCategory;
import org.amphiprion.myaccounts.R;

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
	public List<Operation> getOperations(Account account, Date from, Date to) {
		String sql = "SELECT o." + Operation.DbField.ID + ", o." + Operation.DbField.AMOUNT + ", o." + Operation.DbField.DESCRIPTION + ", o." + Operation.DbField.DATE + ", o."
				+ Operation.DbField.FK_CATEGORY + ", c." + Category.DbField.NAME + ", c." + Category.DbField.IMAGE_NAME + ", o." + Operation.DbField.FK_RECURENT
				+ " from OPERATION o left outer join CATEGORY c on o." + Operation.DbField.FK_CATEGORY + "=c." + Category.DbField.ID + " WHERE o." + Operation.DbField.FK_ACCOUNT
				+ "='" + encodeString(account.getId()) + "' and " + Operation.DbField.DATE + " BETWEEN '" + DatabaseHelper.dateToString(from) + "' AND '"
				+ DatabaseHelper.dateToString(to) + "' order by o." + Operation.DbField.DATE + " desc";
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
					c.setImage(cursor.getString(6));
					a.setCategory(c);
				}
				a.setFkRecurent(cursor.getString(7));
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
		String sql = "insert into OPERATION (" + Operation.DbField.ID + "," + Operation.DbField.AMOUNT + "," + Operation.DbField.DATE + "," + Operation.DbField.DESCRIPTION + ","
				+ Operation.DbField.FK_ACCOUNT + "," + Operation.DbField.FK_CATEGORY + ") values ('" + encodeString(operation.getId()) + "'," + operation.getAmount() + ",'"
				+ DatabaseHelper.dateToString(operation.getDate()) + "','" + encodeString(operation.getDescription()) + "','" + encodeString(operation.getAccountId()) + "',"
				+ (operation.getCategory() == null ? "null" : "'" + encodeString(operation.getCategory().getId()) + "'") + ","
				+ (operation.getFkRecurent() == null ? "null" : "'" + encodeString(operation.getFkRecurent()) + "'") + ")";

		execSQL(sql);
	}

	/**
	 * Update an existing operation
	 * 
	 * @param operation
	 *            the operation to update
	 * @return newBalance use to update the account balance
	 */
	public void create(Operation operation, double newBalance) {
		create(operation, newBalance, true);
	}

	public void create(Operation operation, double newBalance, boolean useTransaction) {
		if (useTransaction) {
			getDatabase().beginTransaction();
		}
		try {

			String sql = "insert into OPERATION (" + Operation.DbField.ID + "," + Operation.DbField.AMOUNT + "," + Operation.DbField.DATE + "," + Operation.DbField.DESCRIPTION
					+ "," + Operation.DbField.FK_ACCOUNT + "," + Operation.DbField.FK_CATEGORY + "," + Operation.DbField.FK_RECURENT + ") values ('"
					+ encodeString(operation.getId()) + "'," + operation.getAmount() + ",'" + DatabaseHelper.dateToString(operation.getDate()) + "','"
					+ encodeString(operation.getDescription()) + "','" + encodeString(operation.getAccountId()) + "',"
					+ (operation.getCategory() == null ? "null" : "'" + encodeString(operation.getCategory().getId()) + "'") + ","
					+ (operation.getFkRecurent() == null ? "null" : "'" + encodeString(operation.getFkRecurent()) + "'") + ")";

			execSQL(sql);

			sql = "UPDATE ACCOUNT set " + Account.DbField.BALANCE + "=" + newBalance + " WHERE " + Account.DbField.ID + "='" + encodeString(operation.getAccountId()) + "'";
			execSQL(sql);

			if (useTransaction) {
				getDatabase().setTransactionSuccessful();
			}
		} finally {
			if (useTransaction) {
				getDatabase().endTransaction();
			}
		}

	}

	/**
	 * Update an existing operation
	 * 
	 * @param operation
	 *            the operation to update
	 * @return newBalance use to update the account balance
	 */
	public void update(Operation operation, double newBalance) {
		getDatabase().beginTransaction();
		try {
			String sql = null;
			if ("MUST_CREATE".equals(operation.getFkRecurent())) {
				operation.setFkRecurent(null);
				RecurentOperationDao.getInstance(getContext()).create(operation);
				operation.setFkRecurent(operation.getId()); // reccurent Id is
															// the id of the
															// operation source
			}

			sql = "update OPERATION set " + Operation.DbField.AMOUNT + "=" + operation.getAmount() + "," + Operation.DbField.DATE + "='"
					+ DatabaseHelper.dateToString(operation.getDate()) + "'," + Operation.DbField.DESCRIPTION + "='" + encodeString(operation.getDescription()) + "',"
					+ Operation.DbField.FK_ACCOUNT + "='" + encodeString(operation.getAccountId()) + "'," + Operation.DbField.FK_RECURENT + "='"
					+ encodeString(operation.getFkRecurent()) + "' ";
			if (operation.getCategory() != null) {
				sql += "," + Operation.DbField.FK_CATEGORY + "='" + encodeString(operation.getCategory().getId()) + "'";
			} else {
				sql += "," + Operation.DbField.FK_CATEGORY + "=null";
			}
			sql += " WHERE " + Operation.DbField.ID + "='" + encodeString(operation.getId()) + "'";

			execSQL(sql);

			sql = "UPDATE ACCOUNT set " + Account.DbField.BALANCE + "=" + newBalance + " WHERE " + Account.DbField.ID + "='" + encodeString(operation.getAccountId()) + "'";
			execSQL(sql);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}

	}

	/**
	 * Delete the given operation.
	 * 
	 * @param operation
	 *            the operation to delete
	 */
	public void delete(Operation operation, double newBalance) {
		getDatabase().beginTransaction();
		try {
			String sql = "DELETE FROM OPERATION WHERE " + Operation.DbField.ID + "='" + encodeString(operation.getId()) + "'";
			execSQL(sql);
			sql = "UPDATE ACCOUNT set " + Account.DbField.BALANCE + "=" + newBalance + " WHERE " + Account.DbField.ID + "='" + encodeString(operation.getAccountId()) + "'";
			execSQL(sql);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
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

	/**
	 * Return the amount after the given date.
	 * 
	 * @param account
	 *            the account
	 * @param date
	 *            the date
	 * @param include
	 *            true if the date is inclusive
	 * @return the amount (cash flow)
	 */
	public double getAmountAfter(Account account, Date date, boolean include) {
		String sql = "SELECT sum(" + Operation.DbField.AMOUNT + ") from OPERATION WHERE " + Operation.DbField.FK_ACCOUNT + "='" + encodeString(account.getId()) + "' AND "
				+ Operation.DbField.DATE + (include ? ">=" : ">") + "'" + DatabaseHelper.dateToString(date) + "'";
		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		double result = 0;
		if (cursor.moveToFirst()) {
			String amount = cursor.getString(0);
			if (amount != null) {
				result = Double.parseDouble(amount);
			}
		}
		cursor.close();
		return result;
	}

	public PieDataset getPieDataset(Account account, Date from, Date to, boolean income) {
		String sql = "select res.sum, cat." + Category.DbField.NAME + " from (";

		sql += "SELECT sum(o." + Operation.DbField.AMOUNT + ") as sum, ";
		sql += "ifnull(c." + Category.DbField.PARENT + ", c." + Category.DbField.ID + ") as catId";
		sql += " from OPERATION o left outer join CATEGORY c on o." + Operation.DbField.FK_CATEGORY + "=c." + Category.DbField.ID + " WHERE o." + Operation.DbField.AMOUNT;
		if (income) {
			sql += ">=0";
		} else {
			sql += "<0";
		}
		sql += " AND o." + Operation.DbField.FK_ACCOUNT + "='" + encodeString(account.getId()) + "' and " + Operation.DbField.DATE + " BETWEEN '"
				+ DatabaseHelper.dateToString(from) + "' AND '" + DatabaseHelper.dateToString(to) + "' group by 2";

		sql += ") res left outer join CATEGORY cat on res.catId=cat." + Category.DbField.ID + " order by cat." + Category.DbField.NAME;
		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		DefaultPieDataset dataset = new DefaultPieDataset();
		if (cursor.moveToFirst()) {
			do {
				String name = cursor.getString(1);
				double value = Math.abs(cursor.getDouble(0));
				if (name == null) {
					name = getContext().getResources().getString(R.string.category_unknown);
				}
				dataset.setValue(name, value);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return dataset;
	}

	/**
	 * @param account
	 * @param report
	 * @param b
	 * @return
	 */
	public PieDataset getPieDataset(Report report) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		Date from = report.getFrom();
		Date to = report.getTo();
		List<ReportCategory> reportCategories = ReportDao.getInstance(getContext()).getReportCategories(report);
		String accountId = report.getAccountId();
		for (ReportCategory rc : reportCategories) {
			String sql = "select res.sum, cat." + Category.DbField.NAME + " from (";

			sql += "SELECT sum(o." + Operation.DbField.AMOUNT + ") as sum, '" + rc.getCategoryId() + "' as catId";
			sql += " from OPERATION o left outer join CATEGORY c on o." + Operation.DbField.FK_CATEGORY + "=c." + Category.DbField.ID + " WHERE o." + Operation.DbField.FK_ACCOUNT
					+ "='" + encodeString(accountId) + "'";
			sql += " and (c." + Category.DbField.ID + "='" + rc.getCategoryId() + "' or c." + Category.DbField.PARENT + "='" + rc.getCategoryId() + "')";
			sql += " and o." + Operation.DbField.DATE + " BETWEEN '" + DatabaseHelper.dateToString(from) + "' AND '" + DatabaseHelper.dateToString(to) + "'";
			sql += " group by 2";
			sql += ") res left outer join CATEGORY cat on res.catId=cat." + Category.DbField.ID + " order by 2";

			Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
			if (cursor.moveToFirst()) {
				double value = Math.abs(cursor.getDouble(0));
				String name = cursor.getString(1);
				if (name == null) {
					name = getContext().getResources().getString(R.string.category_unknown);
				}

				dataset.setValue(name, value);
			}
			cursor.close();
		}
		return dataset;
	}

	/**
	 * @param account
	 * @param report
	 * @param b
	 * @return
	 */
	public CategoryDataset getLineDataset(Report report) {
		Date from = report.getFrom();
		Date to = report.getTo();

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		List<ReportCategory> reportCategories = ReportDao.getInstance(getContext()).getReportCategories(report);
		String accountId = report.getAccountId();
		for (ReportCategory rc : reportCategories) {
			String sql = "select res.sum, res.per, cat." + Category.DbField.NAME + " from (";

			sql += "SELECT sum(o." + Operation.DbField.AMOUNT + ") as sum, strftime('%Y-%m',o." + Operation.DbField.DATE + ",'localtime') as per, '" + rc.getCategoryId()
					+ "' as catId";
			sql += " from OPERATION o left outer join CATEGORY c on o." + Operation.DbField.FK_CATEGORY + "=c." + Category.DbField.ID + " WHERE o." + Operation.DbField.FK_ACCOUNT
					+ "='" + encodeString(accountId) + "'";
			sql += " and (c." + Category.DbField.ID + "='" + rc.getCategoryId() + "' or c." + Category.DbField.PARENT + "='" + rc.getCategoryId() + "')";
			sql += " and o." + Operation.DbField.DATE + " BETWEEN '" + DatabaseHelper.dateToString(from) + "' AND '" + DatabaseHelper.dateToString(to) + "'";
			sql += " group by 2";
			sql += ") res left outer join CATEGORY cat on res.catId=cat." + Category.DbField.ID + " order by 2";

			Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
			if (cursor.moveToFirst()) {
				do {
					double value = Math.abs(cursor.getDouble(0));
					String name = cursor.getString(2);
					if (name == null) {
						name = getContext().getResources().getString(R.string.category_unknown);
					}

					dataset.addValue(value, name, cursor.getString(1));

				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return dataset;
	}

	/**
	 * @param account
	 * @param report
	 * @param b
	 * @return
	 */
	public CategoryDataset getIncomeExpenseDataset(Report report) {
		Date from = report.getFrom();
		Date to = report.getTo();

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		String accountId = report.getAccountId();
		for (int i = 0; i < 2; i++) {

			String sql = "SELECT sum(o." + Operation.DbField.AMOUNT + ") as sum, strftime('%Y-%m',o." + Operation.DbField.DATE + ",'localtime') as per";
			sql += " from OPERATION o WHERE o." + Operation.DbField.FK_ACCOUNT + "='" + encodeString(accountId) + "'";
			sql += " and o.amount" + (i == 0 ? ">=0" : "<0");
			sql += " and o." + Operation.DbField.DATE + " BETWEEN '" + DatabaseHelper.dateToString(from) + "' AND '" + DatabaseHelper.dateToString(to) + "'";
			sql += " group by 2";

			Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
			if (cursor.moveToFirst()) {
				do {
					double value = Math.abs(cursor.getDouble(0));
					String name = i == 0 ? getContext().getResources().getString(R.string.income) : getContext().getResources().getString(R.string.expense);

					dataset.addValue(value, name, cursor.getString(1));

				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return dataset;
	}

	/**
	 * @param account
	 * @param report
	 * @param b
	 * @return
	 */
	public CategoryDataset getBalanceDataset(Report report) {
		Date from = report.getFrom();
		Date to = report.getTo();

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		String accountId = report.getAccountId();
		Account account = AccountDao.getInstance(getContext()).getAccount(accountId);

		String sql = "SELECT sum(o." + Operation.DbField.AMOUNT + ") as sum, strftime('%Y-%m-%d',o." + Operation.DbField.DATE + ",'localtime') as per";
		sql += " from OPERATION o left outer join CATEGORY c on o." + Operation.DbField.FK_CATEGORY + "=c." + Category.DbField.ID + " WHERE o." + Operation.DbField.FK_ACCOUNT
				+ "='" + encodeString(accountId) + "'";
		sql += " and o." + Operation.DbField.DATE + " BETWEEN '" + DatabaseHelper.dateToString(from) + "' AND '" + DatabaseHelper.dateToString(to) + "'";
		sql += " group by 2";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		if (cursor.moveToFirst()) {

			double amount = account.getBalance() - getAmountAfter(account, from, true);
			do {
				double value = cursor.getDouble(0);
				amount += value;

				dataset.addValue(amount, "e", cursor.getString(1));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return dataset;
	}

}
