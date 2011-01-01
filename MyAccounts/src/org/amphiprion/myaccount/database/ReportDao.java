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
import org.amphiprion.myaccount.database.entity.Report;
import org.amphiprion.myaccount.database.entity.ReportCategory;

import android.content.Context;
import android.database.Cursor;

/**
 * This class is responsible of all database category access.
 * 
 * @author amphiprion
 * 
 */
public class ReportDao extends AbstractDao {
	/** The singleton. */
	private static ReportDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private ReportDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static ReportDao getInstance(Context context) {
		if (instance == null) {
			instance = new ReportDao(context);
		}
		return instance;
	}

	/**
	 * Return all existing reports.
	 * 
	 * @return the report list
	 */
	public List<Report> getReports() {
		String sql = "SELECT " + Report.DbField.ID + "," + Report.DbField.NAME + "," + Report.DbField.TYPE_REPORT + ","
				+ Report.DbField.TYPE_PERIOD + "," + Report.DbField.FROM_DATE + "," + Report.DbField.TO_DATE
				+ " from REPORT order by " + Report.DbField.NAME + " asc";
		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		ArrayList<Report> result = new ArrayList<Report>();
		if (cursor.moveToFirst()) {
			do {
				Report a = new Report(cursor.getString(0));
				a.setName(cursor.getString(1));
				a.setType(Integer.parseInt(cursor.getString(2)));
				a.setPeriodType(Integer.parseInt(cursor.getString(3)));
				a.setFrom(DatabaseHelper.stringToDate(cursor.getString(4)));
				a.setTo(DatabaseHelper.stringToDate(cursor.getString(5)));
				result.add(a);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	/**
	 * Persist a new report.
	 * 
	 * @param report
	 *            the new report
	 */
	public void createReport(Report report) {
		getDatabase().beginTransaction();
		try {

			String sql = "insert into REPORT (" + Report.DbField.ID + "," + Report.DbField.NAME + ","
					+ Report.DbField.TYPE_REPORT + "," + Report.DbField.TYPE_PERIOD + "," + Report.DbField.FROM_DATE
					+ "," + Report.DbField.TO_DATE + ") values ('" + report.getId() + "','"
					+ encodeString(report.getName()) + "'," + report.getType() + "," + report.getPeriodType() + ","
					+ (report.getFrom() == null ? "null" : "'" + DatabaseHelper.dateToString(report.getFrom()) + "'")
					+ "," + (report.getTo() == null ? "null" : "'" + DatabaseHelper.dateToString(report.getTo()) + "'")
					+ ")";

			execSQL(sql);

			for (ReportCategory reportCategory : report.getReportCategories()) {
				sql = "insert into REPORT_CATEGORY (" + ReportCategory.DbField.RPT_ID + ", "
						+ ReportCategory.DbField.CAT_ID + ") values ('" + reportCategory.getReportId() + "', '"
						+ reportCategory.getCategoryId() + "')";
				execSQL(sql);
			}
			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	/**
	 * Update an existing report
	 * 
	 * @param report
	 *            the report to update
	 */
	public void updateReport(Report report) {
		getDatabase().beginTransaction();
		try {

			String sql = "update REPORT set " + Report.DbField.NAME + "='" + encodeString(report.getName()) + "',"
					+ Report.DbField.TYPE_REPORT + "=" + report.getType() + "," + Report.DbField.TYPE_PERIOD + "="
					+ report.getPeriodType() + "," + Report.DbField.FROM_DATE + "="
					+ (report.getFrom() == null ? "null" : "'" + DatabaseHelper.dateToString(report.getFrom()) + "'")
					+ "," + Report.DbField.TO_DATE + "="
					+ (report.getTo() == null ? "null" : "'" + DatabaseHelper.dateToString(report.getTo()) + "'")
					+ " WHERE " + Report.DbField.ID + "='" + report.getId() + "'";

			execSQL(sql);

			sql = "delete from REPORT_CATEGORY where " + ReportCategory.DbField.RPT_ID + "='" + report.getId() + "'";
			execSQL(sql);

			for (ReportCategory reportCategory : report.getReportCategories()) {
				sql = "insert into REPORT_CATEGORY (" + ReportCategory.DbField.RPT_ID + ", "
						+ ReportCategory.DbField.CAT_ID + ") values ('" + reportCategory.getReportId() + "', '"
						+ reportCategory.getCategoryId() + "')";
				execSQL(sql);
			}

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	/**
	 * Delete the given report.
	 * 
	 * @param report
	 *            the report to delete
	 */
	public void delete(Report report) {
		getDatabase().beginTransaction();
		try {

			String sql = "delete from REPORT_CATEGORY where " + ReportCategory.DbField.RPT_ID + "='" + report.getId()
					+ "'";
			execSQL(sql);

			sql = "DELETE FROM REPORT WHERE " + Report.DbField.ID + "='" + encodeString(report.getId()) + "'";
			execSQL(sql);

			getDatabase().setTransactionSuccessful();
		} finally {
			getDatabase().endTransaction();
		}
	}

	/**
	 * @param report
	 * @return
	 */
	public List<ReportCategory> getReportCategories(Report report) {
		String sql = "SELECT cat." + Category.DbField.ID + ",cat." + Category.DbField.NAME
				+ " from REPORT_CATEGORY rc join CATEGORY cat on rc." + ReportCategory.DbField.CAT_ID + "=cat."
				+ Category.DbField.ID + " WHERE rc." + ReportCategory.DbField.RPT_ID + "='" + report.getId()
				+ "' order by cat." + Category.DbField.NAME + " asc";
		Cursor cursor = getDatabase().rawQuery(sql, new String[] {});
		ArrayList<ReportCategory> result = new ArrayList<ReportCategory>();
		if (cursor.moveToFirst()) {
			do {
				ReportCategory rc = new ReportCategory(report.getId());
				rc.setCategoryId(cursor.getString(0));
				// Category a = new Category(cursor.getString(0));
				// a.setName(cursor.getString(1));
				result.add(rc);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

}
