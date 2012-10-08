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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.amphiprion.myaccount.database.BudgetDao;
import org.amphiprion.myaccount.database.entity.Budget;
import org.amphiprion.myaccount.util.DateUtil;
import org.amphiprion.myaccount.view.BudgetSummaryView;
import org.amphiprion.myaccounts.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This activity manage the reports.
 * 
 * @author amphiprion
 * 
 */
public class BudgetList extends Activity {
	private Budget current;
	private Date[] period;

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setContentView(new XYLineChart(getApplicationContext()));
		setContentView(R.layout.budget_list);
		period = DateUtil.getThisMonthRange();

		final Button btPeriodPrev = (Button) findViewById(R.id.budget_prev);
		btPeriodPrev.setText("<");
		btPeriodPrev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				period = DateUtil.getPreviousMonthRange(period[0]);
				buildBudgetList();
			}
		});

		final Button btPeriod = (Button) findViewById(R.id.budget_periode);
		btPeriod.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chooseCustomDate();
			}
		});
		final Button btPeriodNext = (Button) findViewById(R.id.budget_next);
		btPeriodNext.setText(">");
		btPeriodNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				period = DateUtil.getNextMonthRange(period[0]);
				buildBudgetList();
			}
		});

		buildBudgetList();
	}

	private void chooseCustomDate() {
		Date date = period[0];
		DatePickerDialog dlg = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Date start = new Date(year - 1900, monthOfYear, dayOfMonth);
				period = DateUtil.getMonthRange(start);
				buildBudgetList();
			}
		}, date.getYear() + 1900, date.getMonth(), date.getDate());
		dlg.setTitle(R.string.period_start_date);
		dlg.show();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		MenuItem addBudget = menu.add(0, ApplicationConstants.MENU_ID_ADD_BUDGET, 1, R.string.add_budget);
		addBudget.setIcon(android.R.drawable.ic_menu_add);
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_ADD_BUDGET) {
			Intent i = new Intent(this, EditBudget.class);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_CREATE_BUDGET);
		}
		return true;
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
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_BUDGET) {
				Budget budget = (Budget) data.getExtras().get("BUDGET");
				BudgetDao.getInstance(this).createBudget(budget);
				buildBudgetList();
			} else if (requestCode == ApplicationConstants.ACTIVITY_RETURN_EDIT_BUDGET) {
				Budget budget = (Budget) data.getExtras().get("BUDGET");
				BudgetDao.getInstance(this).updateBudget(budget);
				buildBudgetList();
			}
		}
	}

	/**
	 * Build the budget list.
	 */
	private void buildBudgetList() {
		final Button btPeriod = (Button) findViewById(R.id.budget_periode);
		btPeriod.setText(DateUtil.budgetDateFormat.format(period[0]));

		List<Budget> budgets = new ArrayList<Budget>();

		LinearLayout ln = (LinearLayout) findViewById(R.id.budget_list);
		ln.removeAllViews();

		budgets.addAll(BudgetDao.getInstance(this).getBudgets(period[0], period[1]));

		for (Budget c : budgets) {
			BudgetSummaryView view = new BudgetSummaryView(this, c);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					edit(((BudgetSummaryView) v).getBudget());
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

		if (budgets.isEmpty()) {
			TextView tv = new TextView(this);
			tv.setText(R.string.empty_budget_list);
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.clear();
		if (v instanceof BudgetSummaryView) {
			current = ((BudgetSummaryView) v).getBudget();
			menu.add(1, ApplicationConstants.MENU_ID_EDIT_BUDGET, 0, R.string.edit_budget_title);
			menu.add(1, ApplicationConstants.MENU_ID_DELETE_BUDGET, 1, R.string.delete_budget_title);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_EDIT_BUDGET) {
			edit(current);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_DELETE_BUDGET) {
			BudgetDao.getInstance(this).delete(current);
			buildBudgetList();
		}
		return true;
	}

	/**
	 * Edit the given budget.
	 * 
	 * @param budget
	 *            the budget to edit
	 */
	private void edit(Budget budget) {
		Intent i = new Intent(this, EditBudget.class);
		i.putExtra("BUDGET", budget);
		startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_EDIT_BUDGET);
	}

}
