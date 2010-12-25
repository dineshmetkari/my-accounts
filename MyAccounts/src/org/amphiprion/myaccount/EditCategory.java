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
import org.amphiprion.myaccount.database.entity.Category;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * The activity used to edit/create a category.
 * 
 * @author amphiprion
 * 
 */
public class EditCategory extends Activity {
	private Category category;

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_category);

		final Spinner cbParentCategory = (Spinner) findViewById(R.id.cbParentCategory);
		final TextView txtName = (TextView) findViewById(R.id.txtCategoryName);

		if (category == null) {
			Intent intent = getIntent();
			if (intent.getExtras() != null) {
				category = (Category) intent.getExtras().getSerializable("CATEGORY");
				txtName.setText(category.getName());
			}
		}

		if (category == null) {
			// its a creation
			category = new Category();
		}
		List<Category> parents = CategoryDao.getInstance(this).getPossibleParentFor(category);
		parents.add(0, new Category(""));
		cbParentCategory.setAdapter(new CategoryAdapter(this, parents));
		if (category.getParent() != null) {
			cbParentCategory.setSelection(parents.indexOf(category.getParent()));
		}

		Button btSave = (Button) findViewById(R.id.btSave);
		btSave.setOnClickListener(new ViewGroup.OnClickListener() {
			@Override
			public void onClick(View v) {
				category.setName("" + txtName.getText());
				Category parent = (Category) cbParentCategory.getSelectedItem();
				if ("".equals(parent.getId())) {
					category.setParent(null);
				} else {
					category.setParent(parent);
				}
				Intent i = new Intent();
				i.putExtra("CATEGORY", category);
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
