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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.amphiprion.myaccount.ApplicationConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

/**
 * This class is used to to backup and restore database data to SD card.
 * 
 * @author amphiprion
 * 
 */
public class DatabaseBackupDao extends AbstractDao {
	private String[] tables = new String[] { "ACCOUNT", "OPERATION", "CATEGORY", "RULE", "REPORT", "REPORT_CATEGORY", "BUDGET" };

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
		Toast.makeText(getContext(), path, Toast.LENGTH_LONG).show();
		File file = new File(path);
		try {
			fos = new BufferedWriter(new FileWriter(file));
			fos.write("<database version='" + DatabaseHelper.DATABASE_VERSION + "' id='" + getDatabase().getPath() + "'>");
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
						if (val != null) {
							fos.write("\n      <col id='" + name + "'>");
							fos.write("<![CDATA[");
							fos.write(val);
							fos.write("]]>");
							fos.write("</col>");
						}
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
		} catch (Throwable e) {
			Log.e(ApplicationConstants.PACKAGE, "Backup Error", e);
			try {
				fos.close();
				file.delete();
			} catch (IOException e1) {
			}
			return false;
		}
	}

	/**
	 * Restore the database using the given file.
	 * 
	 * @param path
	 *            the file path
	 * @return true if the restore is successful
	 */
	public boolean restoreDatabase(String path) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		RestoreHandler handler = new RestoreHandler();
		try {
			parser = factory.newSAXParser();
			parser.parse(path, handler);
			return true;
		} catch (Throwable e) {
			Log.e(ApplicationConstants.PACKAGE, "Restore Error", e);
			return false;
		}
	}

	private class RestoreHandler extends DefaultHandler {

		private String tableName;
		private List<String> columns;
		private List<String> values;

		private StringBuilder builder;

		public RestoreHandler() {
			columns = new ArrayList<String>();
			values = new ArrayList<String>();
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			super.characters(ch, start, length);
			builder.append(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String name) throws SAXException {
			super.endElement(uri, localName, name);
			if (localName.equalsIgnoreCase("row")) {
				// insert row
				StringBuffer sb = new StringBuffer();
				sb.append("insert into ");
				sb.append(tableName);
				sb.append(" (");
				for (int i = 0; i < columns.size(); i++) {
					if (i > 0) {
						sb.append(",");
					}
					sb.append(columns.get(i));
				}
				sb.append(") values (");
				String[] params = new String[values.size()];
				for (int i = 0; i < values.size(); i++) {
					if (i > 0) {
						sb.append(",");
					}
					sb.append("?");
					params[i] = values.get(i);
				}
				sb.append(")");
				execSQL(sb.toString(), params);

				columns.clear();
				values.clear();

			} else if (localName.equalsIgnoreCase("col")) {
				values.add(builder.toString());
			}
			builder.setLength(0);
		}

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
			builder = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
			super.startElement(uri, localName, name, attributes);
			if (localName.equalsIgnoreCase("table")) {
				tableName = attributes.getValue("id");
				execSQL("delete from " + tableName);

			} else if (localName.equalsIgnoreCase("col")) {
				columns.add(attributes.getValue("id"));
			}
			builder.setLength(0);
		}

	}

}
