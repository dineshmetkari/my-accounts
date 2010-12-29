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

import java.util.Date;
import java.util.List;

import org.amphiprion.myaccount.adapter.FileDriverAdapter;
import org.amphiprion.myaccount.database.OperationDao;
import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.database.entity.Operation;
import org.amphiprion.myaccount.driver.file.FileDriverManager;
import org.amphiprion.myaccount.util.CurrencyUtil;
import org.amphiprion.myaccount.util.DateUtil;
import org.amphiprion.myaccount.view.OperationSummaryView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This activity manage the tags.
 * 
 * @author amphiprion
 * 
 */
public class OperationList extends Activity {
	private Account account;
	private Date[] period;

	enum PeriodType {
		THIS_MONTH, LAST_MONTH, CUSTOM
	}

	private PeriodType periodType = PeriodType.THIS_MONTH;

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		account = (Account) getIntent().getSerializableExtra("ACCOUNT");
		setTitle(getResources().getText(R.string.operations_title) + " " + account.getName());
		setContentView(R.layout.operation_list);

		buildOperationList(account);
	}

	private void buildOperationList(Account account) {

		TextView tvPeriod = (TextView) findViewById(R.id.lblPeriod);
		if (periodType == PeriodType.THIS_MONTH) {
			period = DateUtil.getThisMonthRange();
			tvPeriod.setText(getResources().getString(R.string.period_this_month, DateUtil.getThisMonth()));
		} else if (periodType == PeriodType.LAST_MONTH) {
			period = DateUtil.getLastMonthRange();
			tvPeriod.setText(getResources().getString(R.string.period_last_month, DateUtil.getLastMonth()));
		} else {
			tvPeriod.setText(getResources().getString(R.string.period_custom_label,
					DateUtil.defaultDateFormat.format(period[0]), DateUtil.defaultDateFormat.format(period[1])));
		}

		TextView lblTotalCurrency = (TextView) findViewById(R.id.lblTotalCurrency);
		lblTotalCurrency.setTypeface(CurrencyUtil.currencyFace);
		TextView lblTotalBalance = (TextView) findViewById(R.id.lblTotalBalance);

		lblTotalBalance.setText("" + account.getBalance());
		String tmpCurrency = account.getCurrency();
		if (tmpCurrency.length() > 3) {
			tmpCurrency = tmpCurrency.substring(3);
		}
		lblTotalCurrency.setText(tmpCurrency);

		List<Operation> operations = OperationDao.getInstance(this).getOperations(account, period[0], period[1]);
		TextView tvNbRecord = (TextView) findViewById(R.id.lblNbRecord);
		tvNbRecord.setText(getResources().getString(R.string.nb_record, operations.size()));

		LinearLayout ln = (LinearLayout) findViewById(R.id.operation_list);
		ln.removeAllViews();
		for (Operation op : operations) {
			ln.addView(new OperationSummaryView(this, op, account.getCurrency()));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		MenuItem importOp = menu.add(0, ApplicationConstants.MENU_ID_IMPORT_OPERATION, 1, R.string.import_operation);
		importOp.setIcon(android.R.drawable.ic_menu_recent_history);

		MenuItem changePeriod = menu.add(1, ApplicationConstants.MENU_ID_CHANGE_PERIOD_OPERATION, 1,
				R.string.period_change);
		changePeriod.setIcon(android.R.drawable.ic_menu_my_calendar);

		MenuItem instantChart = menu.add(2, ApplicationConstants.MENU_ID_INSTANT_CHART_OPERATION, 1,
				R.string.instant_chart);
		instantChart.setIcon(android.R.drawable.ic_menu_slideshow);

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_IMPORT_OPERATION) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.file_import_choice_title));
			builder.setSingleChoiceItems(new FileDriverAdapter(OperationList.this, FileDriverManager.getDrivers()), -1,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							dialog.dismiss();
							Intent i = new Intent(OperationList.this, DefineImportParameter.class);
							i.putExtra("ACCOUNT", account);
							i.putExtra("FILE_DRIVER_INDEX", item);
							startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_IMPORT_OPERATION);
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_CHANGE_PERIOD_OPERATION) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.period_change));
			String[] items = new String[3];
			items[0] = getResources().getString(R.string.period_this_month, DateUtil.getThisMonth());
			items[1] = getResources().getString(R.string.period_last_month, DateUtil.getLastMonth());
			items[2] = getResources().getString(R.string.period_custom);
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					dialog.dismiss();
					switch (PeriodType.values()[item]) {
					case THIS_MONTH:
						periodType = PeriodType.THIS_MONTH;
						buildOperationList(account);
						break;
					case LAST_MONTH:
						periodType = PeriodType.LAST_MONTH;
						buildOperationList(account);
						break;
					case CUSTOM:
						chooseStartCustomRange();
						break;
					}
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_INSTANT_CHART_OPERATION) {
			Intent i = new Intent(OperationList.this, Chart.class);
			i.putExtra("ACCOUNT", account);
			i.putExtra("FROM", period[0]);
			i.putExtra("TO", period[1]);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_INSTANT_CHART);

		}
		return true;
	}

	private void chooseStartCustomRange() {
		Date date = new Date();
		DatePickerDialog dlg = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Date start = new Date(year - 1900, monthOfYear, dayOfMonth);
				chooseEndCustomRange(start);
			}
		}, date.getYear() + 1900, date.getMonth(), date.getDate());
		dlg.setTitle(R.string.period_start_date);
		dlg.show();
	}

	private void chooseEndCustomRange(final Date start) {
		Date date = new Date();
		DatePickerDialog dlg = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Date end = new Date(year - 1900, monthOfYear, dayOfMonth);
				period[0] = start;
				period[1] = end;
				periodType = PeriodType.CUSTOM;
				buildOperationList(account);
			}
		}, date.getYear() + 1900, date.getMonth(), date.getDate());
		dlg.setTitle(R.string.period_end_date);
		dlg.show();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_IMPORT_OPERATION) {
				account = (Account) data.getSerializableExtra("ACCOUNT");
				buildOperationList(account);
			}
		}

	}
}
