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

import org.amphiprion.myaccount.database.entity.Category;
import org.amphiprion.myaccount.database.entity.Rule;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database rule access.
 * 
 * @author amphiprion
 * 
 */
public class RuleDao extends AbstractDao {
	/** The singleton. */
	private static RuleDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private RuleDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static RuleDao getInstance(Context context) {
		if (instance == null) {
			instance = new RuleDao(context);
		}
		return instance;
	}

	/**
	 * Return all rules of a category.
	 * 
	 * @return the rule list
	 */
	public List<Rule> getRules(Category category) {
		String sql = "SELECT " + Rule.DbField.ID + ", " + Rule.DbField.FILTER + " from RULE WHERE "
				+ Rule.DbField.FK_CATEGORY + "='" + encodeString(category.getId()) + "'  order by "
				+ Rule.DbField.FILTER + " asc";
		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		ArrayList<Rule> result = new ArrayList<Rule>();
		if (cursor.moveToFirst()) {
			do {
				Rule a = new Rule(cursor.getString(0));
				a.setFilter(cursor.getString(1));
				a.setCategoryId(category.getId());
				result.add(a);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new rule.
	 * 
	 * @param rule
	 *            the new rule
	 */
	public void create(Rule rule) {
		String sql = "insert into RULE (" + Rule.DbField.ID + "," + Rule.DbField.FILTER + ","
				+ Rule.DbField.FK_CATEGORY + ") values ('" + encodeString(rule.getId()) + "','"
				+ encodeString(rule.getFilter()) + "','" + encodeString(rule.getCategoryId()) + "')";

		execSQL(sql);
	}

	/**
	 * Update an existing rule
	 * 
	 * @param rule
	 *            the rule to update
	 */
	public void update(Rule rule) {
		String sql = "update RULE set " + Rule.DbField.FILTER + "='" + encodeString(rule.getFilter()) + "' WHERE "
				+ Rule.DbField.ID + "='" + encodeString(rule.getId()) + "'";

		execSQL(sql);
	}

	/**
	 * Delete the given rule.
	 * 
	 * @param rule
	 *            the rule to delete
	 */
	public void delete(Rule rule) {
		String sql = "DELETE FROM RULE WHERE " + Rule.DbField.ID + "='" + encodeString(rule.getId()) + "'";
		execSQL(sql);
	}
}
