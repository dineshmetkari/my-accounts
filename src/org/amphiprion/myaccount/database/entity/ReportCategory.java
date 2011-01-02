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

/**
 * The report category link entity.
 * 
 * @author amphiprion
 * 
 */
public class ReportCategory implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	public enum DbField {
		RPT_ID, CAT_ID
	}

	/** The uuid. */
	private String reportId;
	/** The uuid. */
	private String categoryId;

	/**
	 * Default constructor.
	 * 
	 * @param categoryId
	 *            the category id
	 */
	public ReportCategory(String reportId) {
		this.reportId = reportId;
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
	 * @return the reportId
	 */
	public String getReportId() {
		return reportId;
	}
}
