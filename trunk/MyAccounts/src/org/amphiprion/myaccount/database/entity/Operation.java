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

import java.util.Date;
import java.util.UUID;

/**
 * This class represent an account operation.
 * 
 * @author amphiprion
 * 
 */
public class Operation {
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	public enum DbField {
		ID, AMOUNT, DATE, DESCRIPTION, TAG, FK_ACCOUNT, ICON
	}

	/** The uuid. */
	private String id;
	/** The amount. */
	private double amount;
	/** The date. */
	private Date date;
	/** The description. */
	private String description;
	/** The tag. */
	private String tagId;
	/** The icon. */
	private String icon;
	/** The owner account uuid. */
	private String accountId;

	/**
	 * Constructor for a new entity.
	 */
	public Operation() {
		id = UUID.randomUUID().toString();
	}

	/**
	 * Constructor for an existing entity.
	 * 
	 * @param id
	 *            the uuid
	 */
	public Operation(String id) {
		this.id = id;
	}

	/**
	 * @return the tagId
	 */
	public String getTagId() {
		return tagId;
	}

	/**
	 * @param tagId
	 *            the tagId to set
	 */
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	/**
	 * @return the accountId
	 */
	public String getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId
	 *            the accountId to set
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return amount + " - " + date + " - " + description;
	}
}
