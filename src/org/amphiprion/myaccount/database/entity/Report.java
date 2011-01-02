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
import java.util.List;
import java.util.UUID;

import org.amphiprion.myaccount.util.DateUtil;

/**
 * The report entity.
 * 
 * @author amphiprion
 * 
 */
public class Report implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	public enum DbField {
		ID, NAME, TYPE_REPORT, TYPE_PERIOD, FROM_DATE, TO_DATE, FK_ACCOUNT
	}

	public enum Type {
		CATEGORY_AMOUNT_BY_MONTH(0), DAILY_BALANCE(1), CATEGORY_AMOUNT(2), TREND(3);

		private int dbValue;

		private Type(int dbValue) {
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
		public static Type fromDbValue(int value) {
			Type[] values = Type.values();
			for (Type t : values) {
				if (t.getDbValue() == value) {
					return t;
				}
			}
			return null;
		}
	}

	public enum PeriodType {
		THIS_YEAR(0), LAST_YEAR(1), LAST_6_MONTH(2), CUSTOM(3);

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

	/** The report category links. */
	private List<ReportCategory> reportCategories;

	/** The report type. */
	private Type type;
	/** The period type. */
	private PeriodType periodType;

	/** The from date for custom start date. */
	private Date from;
	/** The to date for custom end date. */
	private Date to;
	/** the account id. */
	private String accountId;

	/**
	 * Default constructor.
	 */
	public Report() {
		this(UUID.randomUUID().toString());
	}

	/**
	 * Default constructor.
	 * 
	 * @param id
	 *            the uuid
	 */
	public Report(String id) {
		this.id = id;
		type = Type.CATEGORY_AMOUNT_BY_MONTH;
		periodType = PeriodType.THIS_YEAR;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
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
	 * @return the reportCategories
	 */
	public List<ReportCategory> getReportCategories() {
		return reportCategories;
	}

	/**
	 * @param reportCategories
	 *            the reportCategories to set
	 */
	public void setReportCategories(List<ReportCategory> reportCategories) {
		this.reportCategories = reportCategories;
	}

	/**
	 * @return the from
	 */
	public Date getFrom() {
		if (periodType == PeriodType.LAST_YEAR) {
			return DateUtil.getLastYearRange()[0];
		} else if (periodType == PeriodType.THIS_YEAR) {
			return DateUtil.getThisYearRange()[0];
		} else if (periodType == PeriodType.LAST_6_MONTH) {
			return DateUtil.getLast6MonthRange()[0];
		} else {
			return from;
		}
	}

	/**
	 * @return the to
	 */
	public Date getTo() {
		if (periodType == PeriodType.LAST_YEAR) {
			return DateUtil.getLastYearRange()[1];
		} else if (periodType == PeriodType.THIS_YEAR) {
			return DateUtil.getThisYearRange()[1];
		} else if (periodType == PeriodType.LAST_6_MONTH) {
			return DateUtil.getLast6MonthRange()[1];
		} else {
			return to;
		}
	}

	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(Date from) {
		this.from = from;
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
	 * @param to
	 *            the to to set
	 */
	public void setTo(Date to) {
		this.to = to;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

}
