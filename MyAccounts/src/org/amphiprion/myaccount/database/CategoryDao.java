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

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database category access.
 * 
 * @author amphiprion
 * 
 */
public class CategoryDao extends AbstractDao {
	/** The singleton. */
	private static CategoryDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private CategoryDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static CategoryDao getInstance(Context context) {
		if (instance == null) {
			instance = new CategoryDao(context);
		}
		return instance;
	}

	/**
	 * Return all existing categories.
	 * 
	 * @return the category list
	 */
	public List<Category> getCategories() {
		String sql = "SELECT c." + Category.DbField.ID + ",c." + Category.DbField.NAME + ",p." + Category.DbField.ID
				+ ",p." + Category.DbField.NAME + " from CATEGORY c left outer join CATEGORY p on c."
				+ Category.DbField.PARENT + "=p." + Category.DbField.ID + " order by c." + Category.DbField.NAME
				+ " asc";
		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		ArrayList<Category> result = new ArrayList<Category>();
		if (cursor.moveToFirst()) {
			do {
				Category a = new Category(cursor.getString(0));
				a.setName(cursor.getString(1));
				String pId = cursor.getString(2);
				if (pId != null) {
					Category p = new Category(pId);
					p.setName(cursor.getString(3));
					a.setParent(p);
				}
				result.add(a);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new category.
	 * 
	 * @param category
	 *            the new category
	 */
	public void createCategory(Category category) {
		String sql = "insert into CATEGORY (" + Category.DbField.ID + "," + Category.DbField.NAME + ","
				+ Category.DbField.PARENT + ") values ('" + category.getId() + "','" + encodeString(category.getName())
				+ "'," + (category.getParent() != null ? "'" + encodeString(category.getParent().getId()) + "'" : null)
				+ ")";

		execSQL(sql);
	}

	/**
	 * Update an existing category
	 * 
	 * @param category
	 *            the category to update
	 */
	public void updateCategory(Category category) {
		String sql = "update CATEGORY set " + Category.DbField.NAME + "='" + encodeString(category.getName()) + "',"
				+ Category.DbField.PARENT + "="
				+ (category.getParent() != null ? "'" + encodeString(category.getParent().getId()) + "'" : null)
				+ " WHERE " + Category.DbField.ID + "='" + encodeString(category.getId()) + "'";

		execSQL(sql);
	}

	public List<Category> getPossibleParentFor(Category category) {
		String sql = "SELECT " + Category.DbField.ID + "," + Category.DbField.NAME + "," + Category.DbField.PARENT
				+ " from CATEGORY WHERE " + Category.DbField.PARENT + " is null and " + Category.DbField.ID + "<>'"
				+ encodeString(category.getId()) + "' order by " + Category.DbField.NAME + " asc";
		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		ArrayList<Category> result = new ArrayList<Category>();
		if (cursor.moveToFirst()) {
			do {
				Category a = new Category(cursor.getString(0));
				a.setName(cursor.getString(1));
				result.add(a);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	/**
	 * Delete the given category.
	 * 
	 * @param category
	 *            the category to delete
	 */
	public void delete(Category category) {
		getDatabase().beginTransaction();
		try {

			String sql = "UPDATE CATEGORY SET " + Category.DbField.PARENT + "=null WHERE " + Category.DbField.PARENT
					+ "='" + encodeString(category.getId()) + "'";
			execSQL(sql);

			sql = "DELETE FROM CATEGORY WHERE " + Category.DbField.ID + "='" + encodeString(category.getId()) + "'";
			execSQL(sql);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}
}
