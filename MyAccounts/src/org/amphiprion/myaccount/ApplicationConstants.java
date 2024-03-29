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
	public final static String PACKAGE = "org.amphiprion.myaccounts";
	public final static String NAME = "MyAccounts";
	public static final String IMPORT_DRIRECTORY = "import";
	public static final String BACKUP_DRIRECTORY = "backup";

	public static final int MENU_ID_ADD_ACCOUNT = 1;
	public static final int MENU_ID_ADD_CATEGORY = 2;
	public static final int MENU_ID_EDIT_CATEGORY = 3;
	public static final int MENU_ID_DELETE_CATEGORY = 4;
	public static final int MENU_ID_IMPORT_OPERATION = 5;
	public static final int MENU_ID_EDIT_ACCOUNT = 6;
	public static final int MENU_ID_DELETE_ACCOUNT = 7;
	public static final int MENU_ID_CHANGE_PERIOD_OPERATION = 8;
	public static final int MENU_ID_INSTANT_CHART_OPERATION = 9;
	public static final int MENU_ID_ADD_REPORT = 10;
	public static final int MENU_ID_EDIT_REPORT = 11;
	public static final int MENU_ID_DELETE_REPORT = 12;
	public static final int MENU_ID_BACKUP_DB = 13;
	public static final int MENU_ID_RESTORE_DB = 14;
	public static final int MENU_ID_ADD_OPERATION = 15;
	public static final int MENU_ID_EDIT_OPERATION = 16;
	public static final int MENU_ID_DELETE_OPERATION = 17;
	public static final int MENU_ID_ADD_BUDGET = 18;
	public static final int MENU_ID_EDIT_BUDGET = 19;
	public static final int MENU_ID_DELETE_BUDGET = 20;
	public static final int MENU_ID_CONVERT_RECURENT_OPERATION = 21;
	public static final int MENU_ID_STOP_RECURENT_OPERATION = 22;
	public static final int MENU_ID_EDIT_OP_MONTHLY = 23;
	public static final int MENU_ID_EDIT_OPERATION_AND_MONTHLY = 24;

	public static final int ACTIVITY_RETURN_CREATE_ACCOUNT = 1;
	public static final int ACTIVITY_RETURN_EDIT_ACCOUNT = 2;
	public static final int ACTIVITY_RETURN_CREATE_CATEGORY = 3;
	public static final int ACTIVITY_RETURN_EDIT_CATEGORY = 4;
	public static final int ACTIVITY_RETURN_IMPORT_OPERATION = 5;
	public static final int ACTIVITY_RETURN_CHOOSE_FILE = 6;
	public static final int ACTIVITY_RETURN_VIEW_OPERATION_LIST = 7;
	public static final int ACTIVITY_RETURN_INSTANT_CHART = 8;
	public static final int ACTIVITY_RETURN_EDIT_OPERATION = 9;
	public static final int ACTIVITY_RETURN_CREATE_REPORT = 10;
	public static final int ACTIVITY_RETURN_EDIT_REPORT = 11;
	public static final int ACTIVITY_RETURN_CHOOSE_RESTORE_FILE = 12;
	public static final int ACTIVITY_RETURN_CREATE_OPERATION = 13;
	public static final int ACTIVITY_RETURN_CREATE_BUDGET = 14;
	public static final int ACTIVITY_RETURN_EDIT_BUDGET = 15;
}
