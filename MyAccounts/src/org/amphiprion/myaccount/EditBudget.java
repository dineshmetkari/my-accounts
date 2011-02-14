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

import java.util.List;

import org.amphiprion.myaccount.adapter.CategoryAdapter;
import org.amphiprion.myaccount.database.CategoryDao;
import org.amphiprion.myaccount.database.entity.Budget;
import org.amphiprion.myaccount.database.entity.Category;
import org.amphiprion.myaccounts.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * This activity is used to create or edit a budget.
 * 
 * @author amphiprion
 * 
 */
public class EditBudget extends Activity {
	private Budget budget;

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_budget);

		Intent i = getIntent();
		budget = (Budget) i.getSerializableExtra("BUDGET");

		final Spinner cbCategory = (Spinner) findViewById(R.id.cbCategory);
		final TextView txtName = (TextView) findViewById(R.id.txtBudgetName);
		final TextView txtAmout = (TextView) findViewById(R.id.txtAmount);

		// final Spinner cbBudgetPeriodType = (Spinner)
		// findViewById(R.id.cbBudgetPeriodType);
		// cbBudgetPeriodType.setAdapter(new BudgetPeriodTypeAdapter(this));

		List<Category> allCategories = CategoryDao.getInstance(this).getCategories();
		cbCategory.setAdapter(new CategoryAdapter(this, allCategories));
		if (budget != null) {
			txtName.setText(budget.getName());

			cbCategory.setSelection(allCategories.indexOf(new Category(budget.getCategoryId())));
			// cbBudgetPeriodType.setSelection(budget.getPeriodType().ordinal());
		} else {
			budget = new Budget();
		}
		txtAmout.setText("" + budget.getAmount());

		Button btSave = (Button) findViewById(R.id.btSave);
		btSave.setOnClickListener(new ViewGroup.OnClickListener() {
			@Override
			public void onClick(View v) {
				budget.setName("" + txtName.getText());
				Category category = (Category) cbCategory.getSelectedItem();
				budget.setCategoryId(category.getId());
				// budget.setPeriodType((PeriodType)
				// cbBudgetPeriodType.getSelectedItem());
				budget.setAmount(Double.parseDouble("" + txtAmout.getText()));

				Intent i = new Intent();
				i.putExtra("BUDGET", budget);
				setResult(RESULT_OK, i);
				finish();
			}
		});

		Button btCancel = (Button) findViewById(R.id.btCancel);
		btCancel.setOnClickListener(new ViewGroup.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}
}
