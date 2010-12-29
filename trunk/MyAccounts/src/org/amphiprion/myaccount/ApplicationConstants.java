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
package org.amphiprion.myaccount;

/**
 * This interface hosts all application contants.
 * 
 * @author amphiprion
 * 
 */
public interface ApplicationConstants {
	public final static String PACKAGE = "org.amphiprion.myaccount";
	public final static String NAME = "MyAccounts";
	public static final String IMPORT_DRIRECTORY = "import";

	public static final int MENU_ID_ADD_ACCOUNT = 1;
	public static final int MENU_ID_ADD_CATEGORY = 2;
	public static final int MENU_ID_EDIT_CATEGORY = 3;
	public static final int MENU_ID_DELETE_CATEGORY = 4;
	public static final int MENU_ID_IMPORT_OPERATION = 5;
	public static final int MENU_ID_EDIT_ACCOUNT = 6;
	public static final int MENU_ID_DELETE_ACCOUNT = 7;
	public static final int MENU_ID_CHANGE_PERIOD_OPERATION = 8;

	public static final int ACTIVITY_RETURN_CREATE_ACCOUNT = 1;
	public static final int ACTIVITY_RETURN_EDIT_ACCOUNT = 2;
	public static final int ACTIVITY_RETURN_CREATE_CATEGORY = 3;
	public static final int ACTIVITY_RETURN_EDIT_CATEGORY = 4;
	public static final int ACTIVITY_RETURN_IMPORT_OPERATION = 5;
	public static final int ACTIVITY_RETURN_CHOOSE_FILE = 6;
	public static final int ACTIVITY_RETURN_VIEW_OPERATION_LIST = 7;

}
