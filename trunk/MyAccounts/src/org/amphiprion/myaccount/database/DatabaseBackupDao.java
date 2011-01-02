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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.amphiprion.myaccount.ApplicationConstants;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * This class is used to to backup and restore database data to SD card.
 * 
 * @author amphiprion
 * 
 */
public class DatabaseBackupDao extends AbstractDao {
	private String[] tables = new String[] { "ACCOUNT", "OPERATION", "CATEGORY", "RULE", "REPORT", "REPORT_CATEGORY" };

	/** The singleton. */
	private static DatabaseBackupDao instance;

	/**
	 * Hidden constructor.
	 * 
	 * @param context
	 *            the application context
	 */
	private DatabaseBackupDao(Context context) {
		super(context);
	}

	/**
	 * Return the singleton.
	 * 
	 * @param context
	 *            the application context
	 * @return the singleton
	 */
	public static DatabaseBackupDao getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseBackupDao(context);
		}
		return instance;
	}

	/**
	 * Call this method to backup the database content into the given xml file.
	 * 
	 * @param path
	 *            the filename to export
	 * @return true if backup is successful
	 */
	public boolean backupDatabase(String path) {
		BufferedWriter fos = null;
		File file = new File(path);
		try {
			fos = new BufferedWriter(new FileWriter(file));
			fos.write("<database version='" + DatabaseHelper.DATABASE_VERSION + "' id='" + getDatabase().getPath()
					+ "'>");
			for (String table : tables) {
				fos.write("\n  <table id='" + table + "'>");
				String sql = "select * from " + table;
				Cursor cur = getDatabase().rawQuery(sql, null);
				int numcols = cur.getColumnCount();

				cur.moveToFirst();
				while (cur.getPosition() < cur.getCount()) {
					fos.write("\n    <row>");
					String name;
					String val;
					for (int idx = 0; idx < numcols; idx++) {
						name = cur.getColumnName(idx);
						val = cur.getString(idx);
						fos.write("\n      <col id='" + name + "'>");
						fos.write("<![CDATA[");
						fos.write(val);
						fos.write("]]>");
						fos.write("\n      </col>");
					}
					cur.moveToNext();
					fos.write("\n    </row>");
				}
				cur.close();
				fos.write("\n  </table>");
			}
			fos.write("</database>");
			fos.close();
			return true;
		} catch (IOException e) {
			Log.e(ApplicationConstants.PACKAGE, "Backup Error", e);
			try {
				fos.close();
				file.delete();
			} catch (IOException e1) {
			}
			return false;
		}
	}
}
