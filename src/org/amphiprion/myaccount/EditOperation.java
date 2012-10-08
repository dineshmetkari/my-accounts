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

import org.amphiprion.myaccount.adapter.CategoryAdapter;
import org.amphiprion.myaccount.adapter.DateAdapter;
import org.amphiprion.myaccount.database.CategoryDao;
import org.amphiprion.myaccount.database.entity.Category;
import org.amphiprion.myaccount.database.entity.Operation;
import org.amphiprion.myaccount.view.DatePickerSpinner;
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
 * This activity manage the reports.
 * 
 * @author amphiprion
 * 
 */
public class EditOperation extends Activity {
	private Operation operation;
	private int menuId;

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_operation);

		Intent i = getIntent();
		operation = (Operation) i.getSerializableExtra("OPERATION");
		menuId = i.getIntExtra("MENU_ID",
				ApplicationConstants.MENU_ID_EDIT_OPERATION);

		final DatePickerSpinner cbDate = (DatePickerSpinner) findViewById(R.id.cbDateOperation);
		cbDate.setEnabled(menuId != ApplicationConstants.MENU_ID_EDIT_OP_MONTHLY);

		final Spinner cbCategory = (Spinner) findViewById(R.id.cbCategory);
		final TextView txtName = (TextView) findViewById(R.id.txtOperationName);
		final TextView txtAmout = (TextView) findViewById(R.id.txtAmount);

		List<Category> allCategories = CategoryDao.getInstance(this)
				.getCategories();
		allCategories.add(0, new Category(""));
		cbCategory.setAdapter(new CategoryAdapter(this, allCategories));
		if (operation.getCategory() != null) {
			cbCategory.setSelection(allCategories.indexOf(operation
					.getCategory()));
		}

		txtName.setText(operation.getDescription());
		txtAmout.setText("" + operation.getAmount());

		DateAdapter dateAdapter = new DateAdapter(this);
		cbDate.setAdapter(dateAdapter);
		dateAdapter.add(operation.getDate());

		Button btSave = (Button) findViewById(R.id.btSave);
		btSave.setOnClickListener(new ViewGroup.OnClickListener() {
			@Override
			public void onClick(View v) {
				double amount = 0;

				Category category = (Category) cbCategory.getSelectedItem();
				operation.setDescription("" + txtName.getText());
				if (category.getId() == null || "".equals(category.getId())) {
					operation.setCategory(null);
				} else {
					operation.setCategory(category);
				}
				try {
					amount = Double.parseDouble("" + txtAmout.getText());
				} catch (NumberFormatException e) {
				}
				operation.setAmount(amount);
				operation.setDate((Date) cbDate.getSelectedItem());

				Intent i = new Intent();
				i.putExtra("OPERATION", operation);
				i.putExtra("MENU_ID", menuId);

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
