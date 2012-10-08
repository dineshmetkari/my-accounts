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
import org.amphiprion.myaccount.database.AccountDao;
import org.amphiprion.myaccount.database.OperationDao;
import org.amphiprion.myaccount.database.RecurentOperationDao;
import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.database.entity.Operation;
import org.amphiprion.myaccount.driver.file.FileDriverManager;
import org.amphiprion.myaccount.util.CurrencyUtil;
import org.amphiprion.myaccount.util.DateUtil;
import org.amphiprion.myaccount.view.OperationSummaryView;
import org.amphiprion.myaccounts.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
	private Operation current;
	double originalOperationAmout;
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
		DateUtil.init(this);
		account = (Account) getIntent().getSerializableExtra("ACCOUNT");
		setTitle(getResources().getText(R.string.operations_title) + " "
				+ account.getName());
		setContentView(R.layout.operation_list);

		buildOperationList(account);
	}

	private void buildOperationList(Account account) {

		TextView tvPeriod = (TextView) findViewById(R.id.lblPeriod);
		if (periodType == PeriodType.THIS_MONTH) {
			period = DateUtil.getThisMonthRange();
			tvPeriod.setText(getResources().getString(
					R.string.period_this_month, DateUtil.getThisMonth()));
		} else if (periodType == PeriodType.LAST_MONTH) {
			period = DateUtil.getLastMonthRange();
			tvPeriod.setText(getResources().getString(
					R.string.period_last_month, DateUtil.getLastMonth()));
		} else {
			tvPeriod.setText(getResources().getString(
					R.string.period_custom_label,
					DateUtil.defaultDateFormat.format(period[0]),
					DateUtil.defaultDateFormat.format(period[1])));
		}

		TextView lblTotalCurrency = (TextView) findViewById(R.id.lblTotalCurrency);
		lblTotalCurrency.setTypeface(CurrencyUtil.currencyFace);
		TextView lblCurrencyEnd = (TextView) findViewById(R.id.lblCurrencyEnd);
		lblCurrencyEnd.setTypeface(CurrencyUtil.currencyFace);
		TextView lblCurrencyStart = (TextView) findViewById(R.id.lblCurrencyStart);
		lblCurrencyStart.setTypeface(CurrencyUtil.currencyFace);

		TextView lblTotalBalance = (TextView) findViewById(R.id.lblTotalBalance);
		TextView lblBalanceEnd = (TextView) findViewById(R.id.lblBalanceEnd);
		TextView lblDateEnd = (TextView) findViewById(R.id.lblDateEnd);
		TextView lblBalanceStart = (TextView) findViewById(R.id.lblBalanceStart);
		TextView lblDateStart = (TextView) findViewById(R.id.lblDateStart);

		lblTotalBalance.setText("" + account.getBalance());
		String tmpCurrency = account.getCurrency();
		if (tmpCurrency.length() > 3) {
			tmpCurrency = tmpCurrency.substring(3);
		}
		lblTotalCurrency.setText(tmpCurrency);
		if (account.getBalance() < 0) {
			lblTotalBalance.setTextColor(getResources().getColor(
					R.color.negative));
			lblTotalCurrency.setTextColor(getResources().getColor(
					R.color.negative));
		} else {
			lblTotalBalance.setTextColor(getResources().getColor(
					R.color.positive));
			lblTotalCurrency.setTextColor(getResources().getColor(
					R.color.positive));
		}

		lblCurrencyEnd.setText(tmpCurrency);
		lblCurrencyStart.setText(tmpCurrency);

		double amountStart = OperationDao.getInstance(this).getAmountAfter(
				account, period[0], true);
		double amountEnd = OperationDao.getInstance(this).getAmountAfter(
				account, period[1], false);

		lblBalanceEnd.setText(""
				+ Math.round((account.getBalance() - amountEnd) * 100.0)
				/ 100.0);
		lblDateEnd.setText(getResources().getString(
				R.string.operation_balance_at_end,
				DateUtil.defaultDateFormat.format(period[1])));

		lblBalanceStart.setText(""
				+ Math.round((account.getBalance() - amountStart) * 100.0)
				/ 100.0);
		lblDateStart.setText(getResources().getString(
				R.string.operation_balance_at_start,
				DateUtil.defaultDateFormat.format(period[0])));

		List<Operation> operations = OperationDao.getInstance(this)
				.getOperations(account, period[0], period[1]);
		TextView tvNbRecord = (TextView) findViewById(R.id.lblNbRecord);
		tvNbRecord.setText(getResources().getString(R.string.nb_record,
				operations.size()));

		LinearLayout ln = (LinearLayout) findViewById(R.id.operation_list);
		ln.removeAllViews();
		for (Operation op : operations) {
			OperationSummaryView view = new OperationSummaryView(this, op,
					account.getCurrency());
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (v instanceof OperationSummaryView) {
						Operation operation = ((OperationSummaryView) v)
								.getOperation();
						edit(operation,
								ApplicationConstants.MENU_ID_EDIT_OPERATION);
					}
				}
			});
			view.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					registerForContextMenu(v);
					openContextMenu(v);
					unregisterForContextMenu(v);
					return true;
				}
			});

			ln.addView(view);
		}
		if (operations.isEmpty()) {
			TextView tv = new TextView(this);
			tv.setText(R.string.empty_operation_list);
			ln.addView(tv);
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
	 *      android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.clear();
		if (v instanceof OperationSummaryView) {
			current = ((OperationSummaryView) v).getOperation();
			menu.add(1, ApplicationConstants.MENU_ID_EDIT_OPERATION, 0,
					R.string.edit_operation_title);
			if (current.getFkRecurent() != null) {
				menu.add(1, ApplicationConstants.MENU_ID_EDIT_OP_MONTHLY, 1,
						R.string.edit_monthly_operation_title);
				menu.add(
						1,
						ApplicationConstants.MENU_ID_EDIT_OPERATION_AND_MONTHLY,
						2, R.string.edit_monthly_and_operation_title);
			}
			menu.add(1, ApplicationConstants.MENU_ID_DELETE_OPERATION, 3,
					R.string.delete_operation_title);
			if (current.getFkRecurent() == null) {
				menu.add(
						1,
						ApplicationConstants.MENU_ID_CONVERT_RECURENT_OPERATION,
						4, R.string.convert_recupent_operation_title);
			} else {
				menu.add(1,
						ApplicationConstants.MENU_ID_STOP_RECURENT_OPERATION,
						5, R.string.stop_recupent_operation_title);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_EDIT_OPERATION) {
			edit(current, ApplicationConstants.MENU_ID_EDIT_OPERATION);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_EDIT_OP_MONTHLY) {
			edit(current, ApplicationConstants.MENU_ID_EDIT_OP_MONTHLY);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_EDIT_OPERATION_AND_MONTHLY) {
			edit(current,
					ApplicationConstants.MENU_ID_EDIT_OPERATION_AND_MONTHLY);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_DELETE_OPERATION) {
			double newBalance = Math.round((account.getBalance() - current
					.getAmount()) * 100.0) / 100.0;
			OperationDao.getInstance(this).delete(current, newBalance);
			account.setBalance(newBalance);
			buildOperationList(account);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_CONVERT_RECURENT_OPERATION) {
			current.setFkRecurent("MUST_CREATE");
			OperationDao.getInstance(this)
					.update(current, account.getBalance());
			RecurentOperationDao.getInstance(this)
					.createMissingRecurentInstance(current);
			account = AccountDao.getInstance(this).getAccount(account.getId());
			buildOperationList(account);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_STOP_RECURENT_OPERATION) {
			RecurentOperationDao.getInstance(this).delete(current);
			buildOperationList(account);
		}
		return true;
	}

	private void edit(Operation operation, int menuId) {
		originalOperationAmout = operation.getAmount();
		Intent i = new Intent(OperationList.this, EditOperation.class);
		if (menuId == ApplicationConstants.MENU_ID_EDIT_OP_MONTHLY) {
			Operation recurOp = RecurentOperationDao.getInstance(this)
					.getRecurentOperation(operation.getFkRecurent());
			i.putExtra("OPERATION", recurOp);
		} else {
			i.putExtra("OPERATION", operation);
		}
		i.putExtra("MENU_ID", menuId);
		startActivityForResult(i,
				ApplicationConstants.ACTIVITY_RETURN_EDIT_OPERATION);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		MenuItem importOp = menu.add(0,
				ApplicationConstants.MENU_ID_IMPORT_OPERATION, 1,
				R.string.import_operation);
		importOp.setIcon(android.R.drawable.ic_menu_recent_history);

		MenuItem addOp = menu.add(1,
				ApplicationConstants.MENU_ID_ADD_OPERATION, 0,
				R.string.add_operation);
		addOp.setIcon(android.R.drawable.ic_menu_add);

		MenuItem changePeriod = menu.add(1,
				ApplicationConstants.MENU_ID_CHANGE_PERIOD_OPERATION, 2,
				R.string.period_change);
		changePeriod.setIcon(android.R.drawable.ic_menu_my_calendar);

		MenuItem instantChart = menu.add(1,
				ApplicationConstants.MENU_ID_INSTANT_CHART_OPERATION, 3,
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
			builder.setTitle(getResources().getString(
					R.string.file_import_choice_title));
			builder.setAdapter(new FileDriverAdapter(OperationList.this,
					FileDriverManager.getDrivers()),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int item) {
							dialog.dismiss();
							Intent i = new Intent(OperationList.this,
									DefineImportParameter.class);
							i.putExtra("ACCOUNT", account);
							i.putExtra("FILE_DRIVER_INDEX", item);
							startActivityForResult(
									i,
									ApplicationConstants.ACTIVITY_RETURN_IMPORT_OPERATION);
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_CHANGE_PERIOD_OPERATION) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.period_change));
			String[] items = new String[3];
			items[0] = getResources().getString(R.string.period_this_month,
					DateUtil.getThisMonth());
			items[1] = getResources().getString(R.string.period_last_month,
					DateUtil.getLastMonth());
			items[2] = getResources().getString(R.string.period_custom);
			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
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
			Intent i = new Intent(OperationList.this, InstantChart.class);
			i.putExtra("ACCOUNT", account);
			i.putExtra("FROM", period[0]);
			i.putExtra("TO", period[1]);
			startActivityForResult(i,
					ApplicationConstants.ACTIVITY_RETURN_INSTANT_CHART);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_ADD_OPERATION) {
			originalOperationAmout = 0;
			Intent i = new Intent(OperationList.this, EditOperation.class);
			Operation op = new Operation();
			op.setAccountId(account.getId());
			op.setDate(new Date());

			i.putExtra("OPERATION", op);
			startActivityForResult(i,
					ApplicationConstants.ACTIVITY_RETURN_CREATE_OPERATION);
		}
		return true;
	}

	private void chooseStartCustomRange() {
		Date date = new Date();
		DatePickerDialog dlg = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						Date start = new Date(year - 1900, monthOfYear,
								dayOfMonth);
						chooseEndCustomRange(start);
					}
				}, date.getYear() + 1900, date.getMonth(), date.getDate());
		dlg.setTitle(R.string.period_start_date);
		dlg.show();
	}

	private void chooseEndCustomRange(final Date start) {
		Date date = new Date();
		DatePickerDialog dlg = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						Date end = new Date(year - 1900, monthOfYear,
								dayOfMonth);
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
			} else if (requestCode == ApplicationConstants.ACTIVITY_RETURN_EDIT_OPERATION) {
				Operation operation = (Operation) data
						.getSerializableExtra("OPERATION");
				int menuId = data.getIntExtra("MENU_ID",
						ApplicationConstants.MENU_ID_EDIT_OPERATION);

				if (menuId != ApplicationConstants.MENU_ID_EDIT_OP_MONTHLY) {
					double newBalance = Math
							.round((account.getBalance()
									+ operation.getAmount() - originalOperationAmout) * 100.0) / 100.0;
					OperationDao.getInstance(this)
							.update(operation, newBalance);
					account.setBalance(newBalance);
				}
				if (menuId != ApplicationConstants.MENU_ID_EDIT_OPERATION) {
					Operation recurOp = RecurentOperationDao
							.getInstance(this)
							.getRecurentOperation(
									menuId == ApplicationConstants.MENU_ID_EDIT_OP_MONTHLY ? operation
											.getId() : operation
											.getFkRecurent());
					recurOp.setAmount(operation.getAmount());
					recurOp.setCategory(operation.getCategory());
					recurOp.setDescription(operation.getDescription());
					Date date = operation.getDate();
					Date dateRecur = recurOp.getDate();
					dateRecur.setDate(date.getDay());
					recurOp.setDate(dateRecur);

					RecurentOperationDao.getInstance(this).update(operation);
				}
				buildOperationList(account);
			} else if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_OPERATION) {
				Operation operation = (Operation) data
						.getSerializableExtra("OPERATION");

				double newBalance = Math
						.round((account.getBalance() + operation.getAmount()) * 100.0) / 100.0;
				OperationDao.getInstance(this).create(operation, newBalance);
				account.setBalance(newBalance);
				buildOperationList(account);
			}
		}

	}
}
