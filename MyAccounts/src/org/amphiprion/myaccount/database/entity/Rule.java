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
 * The rule entity.
 * 
 * @author amphiprion
 * 
 */
public class Rule implements Serializable {

	/**
	 * Serial UUID.
	 */
	private static final long serialVersionUID = 1L;

	public enum DbState {
		CREATE, UPDATE, DELETE
	}

	public enum DbField {
		ID, FILTER, FK_CATEGORY
	}

	/** The UUID. */
	private String id;

	/** The filter. */
	private String filter;
	/** the category id. */
	private String categoryId;
	/** The db state. */
	private DbState state;

	/**
	 * Default constructor.
	 */
	public Rule() {
		id = UUID.randomUUID().toString();
		state = DbState.CREATE;
	}

	/**
	 * Default constructor for an existing rule.
	 * 
	 * @param id
	 *            the uuid
	 */
	public Rule(String id) {
		this.id = id;
		state = DbState.UPDATE;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * @param filter
	 *            the filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
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
		Rule other = (Rule) obj;
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
	 * @param state
	 *            the state to set
	 */
	public void setState(DbState state) {
		this.state = state;
	}

	/**
	 * @return the state
	 */
	public DbState getState() {
		return state;
	}
}
