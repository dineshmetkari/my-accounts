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
import java.util.List;

import org.amphiprion.myaccount.database.CategoryDao;
import org.amphiprion.myaccount.database.entity.Category;
import org.amphiprion.myaccount.view.CategorySummaryView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.LinearLayout;

/**
 * This activity manage the tags.
 * 
 * @author amphiprion
 * 
 */
public class CategoryList extends Activity {
	/** The current category for context menu. */
	private Category current;

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_list);

		buildCategoryList();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		MenuItem addAccount = menu.add(0, ApplicationConstants.MENU_ID_ADD_CATEGORY, 1, R.string.add_category);
		addAccount.setIcon(android.R.drawable.ic_menu_add);
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_ADD_CATEGORY) {
			Intent i = new Intent(this, EditCategory.class);
			// i.putExtra("CATEGORY", category);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_CREATE_CATEGORY);
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
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CREATE_CATEGORY) {
				Category category = (Category) data.getExtras().get("CATEGORY");
				CategoryDao.getInstance(this).createCategory(category);
				buildCategoryList();
			} else if (requestCode == ApplicationConstants.ACTIVITY_RETURN_EDIT_CATEGORY) {
				Category category = (Category) data.getExtras().get("CATEGORY");
				CategoryDao.getInstance(this).updateCategory(category);
				buildCategoryList();
			}
		}
	}

	/**
	 * Build the category list.
	 */
	private void buildCategoryList() {
		List<Category> categories = new ArrayList<Category>();

		LinearLayout ln = (LinearLayout) findViewById(R.id.category_list);
		ln.removeAllViews();

		categories.addAll(CategoryDao.getInstance(this).getCategories());

		for (Category c : categories) {
			CategorySummaryView view = new CategorySummaryView(this, c);
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
		if (v instanceof CategorySummaryView) {
			current = ((CategorySummaryView) v).getCategory();
			menu.add(1, ApplicationConstants.MENU_ID_EDIT_CATEGORY, 0, R.string.edit_category_title);
			menu.add(1, ApplicationConstants.MENU_ID_DELETE_CATEGORY, 1, R.string.delete_category_title);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == ApplicationConstants.MENU_ID_EDIT_CATEGORY) {
			Intent i = new Intent(this, EditCategory.class);
			i.putExtra("CATEGORY", current);
			startActivityForResult(i, ApplicationConstants.ACTIVITY_RETURN_EDIT_CATEGORY);
		} else if (item.getItemId() == ApplicationConstants.MENU_ID_DELETE_CATEGORY) {
			CategoryDao.getInstance(this).delete(current);
			buildCategoryList();
		}
		return true;
	}
}
