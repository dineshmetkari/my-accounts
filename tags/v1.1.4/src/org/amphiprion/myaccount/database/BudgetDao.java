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

import org.amphiprion.myaccount.database.entity.Budget;
import org.amphiprion.myaccount.database.entity.Budget.PeriodType;
import org.amphiprion.myaccount.database.entity.Category;
import org.amphiprion.myaccount.database.entity.Operation;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database budget access.
 * 
 * @author amphiprion
 * 
 */
public class BudgetDao extends AbstractDao {
	/** The singleton. */
	private static BudgetDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private BudgetDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static BudgetDao getInstance(Context context) {
		if (instance == null) {
			instance = new BudgetDao(context);
		}
		return instance;
	}

	/**
	 * Return all existing budgets.
	 * 
	 * @return the budget list
	 */
	public List<Budget> getBudgets(Date from, Date to) {
		String sql = "SELECT b." + Budget.DbField.ID + ",b." + Budget.DbField.NAME + ",b." + Budget.DbField.TYPE_PERIOD + ",b." + Budget.DbField.FK_CATEGORY + ",b."
				+ Budget.DbField.AMOUNT;

		sql += ", (select sum(o." + Operation.DbField.AMOUNT + ") from OPERATION o join CATEGORY c on o." + Operation.DbField.FK_CATEGORY + "=c." + Category.DbField.ID;
		sql += " where (c." + Category.DbField.ID + "=b." + Budget.DbField.FK_CATEGORY + " or c." + Category.DbField.PARENT + "=b." + Budget.DbField.FK_CATEGORY + ")";
		sql += " and o." + Operation.DbField.DATE + " BETWEEN '" + DatabaseHelper.dateToString(from) + "' AND '" + DatabaseHelper.dateToString(to) + "')";

		sql += " from BUDGET b order by b." + Budget.DbField.NAME + " asc";

		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		ArrayList<Budget> result = new ArrayList<Budget>();
		if (cursor.moveToFirst()) {
			do {
				Budget a = new Budget(cursor.getString(0));
				a.setName(cursor.getString(1));
				a.setPeriodType(PeriodType.fromDbValue(Integer.parseInt(cursor.getString(2))));
				a.setCategoryId(cursor.getString(3));
				a.setAmount(cursor.getDouble(4));
				a.setComputedExpense(-cursor.getDouble(5));
				result.add(a);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new budget.
	 * 
	 * @param budget
	 *            the new budget
	 */
	public void createBudget(Budget budget) {
		String sql = "insert into BUDGET (" + Budget.DbField.ID + "," + Budget.DbField.NAME + "," + Budget.DbField.TYPE_PERIOD + "," + Budget.DbField.FK_CATEGORY + ","
				+ Budget.DbField.AMOUNT + ") values ('" + budget.getId() + "','" + encodeString(budget.getName()) + "'," + budget.getPeriodType().getDbValue() + ",'"
				+ budget.getCategoryId() + "'," + budget.getAmount() + ")";

		execSQL(sql);
	}

	/**
	 * Update an existing budget
	 * 
	 * @param budget
	 *            the budget to update
	 */
	public void updateBudget(Budget budget) {
		String sql = "update BUDGET set " + Budget.DbField.NAME + "='" + encodeString(budget.getName()) + "'," + Budget.DbField.TYPE_PERIOD + "="
				+ budget.getPeriodType().getDbValue() + "," + Budget.DbField.FK_CATEGORY + "='" + budget.getCategoryId() + "'," + Budget.DbField.AMOUNT + "=" + budget.getAmount()
				+ " WHERE " + Budget.DbField.ID + "='" + budget.getId() + "'";

		execSQL(sql);
	}

	/**
	 * Delete the given budget.
	 * 
	 * @param budget
	 *            the budget to delete
	 */
	public void delete(Budget budget) {
		String sql = "DELETE FROM BUDGET WHERE " + Budget.DbField.ID + "='" + encodeString(budget.getId()) + "'";
		execSQL(sql);
	}

}
