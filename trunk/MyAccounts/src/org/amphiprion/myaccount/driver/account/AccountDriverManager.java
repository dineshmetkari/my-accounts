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
package org.amphiprion.myaccount.driver.account;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to provide the list of registered driver and give an
 * instance of a specific driver.
 * 
 * @author amphiprion
 * 
 */
public class AccountDriverManager {
	private static List<AccountDriver> drivers = new ArrayList<AccountDriver>();

	/**
	 * Register an FileDriver.
	 * 
	 * @param driver
	 *            the driver
	 */
	public static void register(AccountDriver driver) {
		drivers.add(driver);
	}
}
