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
package org.amphiprion.myaccount.driver.file;

import java.util.ArrayList;
import java.util.List;

import org.amphiprion.myaccount.ApplicationConstants;

import android.util.Log;

/**
 * This class is used to provide the list of registered driver and give an
 * instance of a specific driver.
 * 
 * @author amphiprion
 * 
 */
public class FileDriverManager {
	private static List<FileDriver> drivers = new ArrayList<FileDriver>();

	/**
	 * This method register the given drivers.
	 * 
	 * @param drivers
	 *            the list of FileDriver class name
	 */
	@SuppressWarnings("unchecked")
	public static void init(String[] drivers) {
		for (String clazz : drivers) {
			Class<FileDriver> cls;
			try {
				cls = (Class<FileDriver>) Class.forName(clazz);
				register(cls.newInstance());
			} catch (Exception e) {
				Log.e(ApplicationConstants.PACKAGE, "Unable to register file driver " + clazz, e);
			}
		}
	}

	/**
	 * Register an FileDriver.
	 * 
	 * @param driver
	 *            the driver
	 */
	private static void register(FileDriver driver) {
		drivers.add(driver);
	}

	/**
	 * Return the list of registered file drivers.
	 * 
	 * @return the drivers
	 */
	public static List<FileDriver> getDrivers() {
		return drivers;
	}
}
