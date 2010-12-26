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

/**
 * This class is used to define a parameter for a file driver.
 * 
 * @author amphiprion
 * 
 */
public class Parameter<T> {
	/**
	 * List of possible parameter types.
	 */
	public enum Type {
		DATE_FORMAT, DECIMAL_SEPARATOR, DATE_PICKER, FILE_URI
	}

	private String name;
	private T value;
	private Type type;
	private boolean nullable;

	/**
	 * Construct a new parameter.
	 * 
	 * @param name
	 *            the name (unique key, it is also used as property key for
	 *            displaying label)
	 * @param type
	 *            the parameter type
	 */
	public Parameter(String name, Type type) {
		this(name, type, null);
	}

	/**
	 * Construct a new parameter with a default value.
	 * 
	 * @param name
	 *            the name (unique key, it is also used as property key for
	 *            displaying label)
	 * @param type
	 *            the parameter type
	 * @param value
	 *            the default value
	 */
	public Parameter(String name, Type type, T value) {
		this(name, type, value, false);
	}

	/**
	 * Construct a new parameter with a default value.
	 * 
	 * @param name
	 *            the name (unique key, it is also used as property key for
	 *            displaying label)
	 * @param type
	 *            the parameter type
	 * @param value
	 *            the default value
	 * @param nullable
	 *            true if nullable
	 */
	public Parameter(String name, Type type, T value, boolean nullable) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.nullable = nullable;
	}

	/**
	 * @return the nullable
	 */
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * Get the parameter value.
	 * 
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Set the parameter value.
	 * 
	 * @param value
	 *            the value to set
	 */
	public void setValue(T value) {
		this.value = value;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

}
