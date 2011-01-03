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
		ID, NAME, BALANCE, LAST_OPERATION, CURRENCY, EXCLUDED
	}

	/** the uuid. */
	private String id;
	/** The account balance. */
	private double balance;
	/** The account name. */
	private String name;
	/** The last operation date. */
	private Date lastOperation;
	/** The currency. */
	private String currency;
	/** Excluded from total balance. */
	private boolean excluded;

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
	 * @return the excluded
	 */
	public boolean isExcluded() {
		return excluded;
	}

	/**
	 * @param excluded
	 *            the excluded to set
	 */
	public void setExcluded(boolean excluded) {
		this.excluded = excluded;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
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

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Account other = (Account) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
}
