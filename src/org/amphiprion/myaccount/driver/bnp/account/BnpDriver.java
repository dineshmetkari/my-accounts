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
package org.amphiprion.myaccount.driver.bnp.account;

import org.amphiprion.myaccount.driver.account.AccountDriver;
import org.amphiprion.myaccount.driver.account.AccountDriverManager;

/**
 * This driver is used to manage BNP account.
 * 
 * @author amphiprion
 * 
 */
public class BnpDriver implements AccountDriver {
	static {
		AccountDriverManager.register(new BnpDriver());
	}

	/**
	 * Hide constructor.
	 */
	private BnpDriver() {

	}
}
