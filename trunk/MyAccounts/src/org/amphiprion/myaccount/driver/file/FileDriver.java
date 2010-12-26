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

import java.util.List;

import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.database.entity.Operation;

/**
 * This interface must be implemented by any file driver.
 * 
 * @author amphiprion
 * 
 */
public interface FileDriver {

	/**
	 * Return the list of parameters for this driver.
	 * 
	 * @param account
	 *            the account used for pre-init
	 * @return the list of parameter
	 */
	@SuppressWarnings("unchecked")
	List<Parameter> getParameters(Account account);

	/**
	 * Return the file driver unique name. Used also as string key to display
	 * the label.
	 */
	String getName();

	/**
	 * Parse the given stream and return the parsed operations.
	 * 
	 * @param parameters
	 *            the parameters applied to the parser
	 * @return the parsed operations
	 */
	@SuppressWarnings("unchecked")
	List<Operation> parse(List<Parameter> parameters);
}
