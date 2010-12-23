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
package org.amphiprion.myaccount.database.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * This entity is used to represent an bank account.
 * 
 * @author amphiprion
 * 
 */
public class Account implements Serializable {
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	public enum DbField {
		ID, NAME, BALANCE, LAST_OPERATION
	}

	/** the uuid. */
	private String id;
	/** The account balance. */
	private double balance;
	/** The account name. */
	private String name;
	/** The last operation date. */
	private Date lastOperation;

	/**
	 * Constructor for a new entity.
	 */
	public Account() {
		id = UUID.randomUUID().toString();
	}

	/**
	 * Constructor for an existing entity.
	 * 
	 * @param id
	 *            the uuid
	 */
	public Account(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the balance
	 */
	public double getBalance() {
		return balance;
	}

	/**
	 * @param balance
	 *            the balance to set
	 */
	public void setBalance(double balance) {
		this.balance = balance;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the lastOperation
	 */
	public Date getLastOperation() {
		return lastOperation;
	}

	/**
	 * @param lastOperation
	 *            the lastOperation to set
	 */
	public void setLastOperation(Date lastOperation) {
		this.lastOperation = lastOperation;
	}

}
