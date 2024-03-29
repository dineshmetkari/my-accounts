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
import java.util.UUID;

/**
 * The report entity.
 * 
 * @author amphiprion
 * 
 */
public class Budget implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	public enum DbField {
		ID, NAME, TYPE_PERIOD, FK_CATEGORY, AMOUNT
	}

	public enum PeriodType {
		MONTH(0), WEEK(1), YEAR(2);

		private int dbValue;

		private PeriodType(int dbValue) {
			this.dbValue = dbValue;
		}

		/**
		 * @return the dbValue
		 */
		public int getDbValue() {
			return dbValue;
		}

		/**
		 * 
		 * @param value
		 * @return
		 */
		public static PeriodType fromDbValue(int value) {
			PeriodType[] values = PeriodType.values();
			for (PeriodType t : values) {
				if (t.getDbValue() == value) {
					return t;
				}
			}
			return null;
		}
	}

	/** The uuid. */
	private String id;

	/** The name. */
	private String name;

	/** The period type. */
	private PeriodType periodType;

	/** The category id. */
	private String categoryId;
	/** The amount. */
	private double amount;

	/** the computed expenses. */
	private double computedExpense;

	/**
	 * Default constructor.
	 */
	public Budget() {
		this(UUID.randomUUID().toString());
	}

	/**
	 * Default constructor.
	 * 
	 * @param id
	 *            the uuid
	 */
	public Budget(String id) {
		this.id = id;
		periodType = PeriodType.MONTH;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the categoryId
	 */
	public String getCategoryId() {
		return categoryId;
	}

	/**
	 * @param categoryId
	 *            the categoryId to set
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the periodType
	 */
	public PeriodType getPeriodType() {
		return periodType;
	}

	/**
	 * @param periodType
	 *            the periodType to set
	 */
	public void setPeriodType(PeriodType periodType) {
		this.periodType = periodType;
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
	 * @return the computedExpense
	 */
	public double getComputedExpense() {
		return computedExpense;
	}

	/**
	 * @param computedExpense
	 *            the computedExpense to set
	 */
	public void setComputedExpense(double computedExpense) {
		this.computedExpense = computedExpense;
	}

}
