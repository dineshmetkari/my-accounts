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
import org.amphiprion.myaccount.adapter.StandardCategoryImageAdapter;
import org.amphiprion.myaccount.database.CategoryDao;
import org.amphiprion.myaccount.database.RuleDao;
import org.amphiprion.myaccount.database.entity.Category;
import org.amphiprion.myaccount.database.entity.Rule;
import org.amphiprion.myaccount.view.RuleView;
import org.amphiprion.myaccounts.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The activity used to edit/create a category.
 * 
 * @author amphiprion
 * 
 */
public class EditCategory extends Activity implements RuleView.OnRuleClickedListener {
	private Category category;

	/** The rules of the category. */
	private List<Rule> rules;

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
				defineImage(category.getImage());
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
				updateRuleFilters();
				category.setRules(rules);

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

		buildRuleList();

		ImageButton btImage = (ImageButton) findViewById(R.id.btImageCategory);
		btImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showImageChooser();
			}
		});
	}

	/**
	 * Show the image chooser.
	 */
	private void showImageChooser() {
		final Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.standard_category_images);
		dialog.setTitle(getResources().getString(R.string.choose_icon));

		GridView grid = (GridView) dialog.findViewById(R.id.grid_image);
		final StandardCategoryImageAdapter adapter = new StandardCategoryImageAdapter(this);
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				imageChoosen((String) adapter.getItem(position));
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	/**
	 * An image have been chosen.
	 * 
	 * @param image
	 *            the image name
	 */
	private void imageChoosen(String image) {
		if ("#none".equals(image)) {
			image = null;
		}
		category.setImage(image);
		defineImage(image);
	}

	private void defineImage(String image) {
		ImageButton btImage = (ImageButton) findViewById(R.id.btImageCategory);
		if (image == null) {
			btImage.setImageResource(R.drawable.none);
		} else if (image.startsWith("#")) {
			btImage.setImageResource(getResources().getIdentifier(image.substring(1), "drawable",
					ApplicationConstants.PACKAGE));
		}
	}

	private void buildRuleList() {
		rules = RuleDao.getInstance(this).getRules(category);

		LinearLayout ln = (LinearLayout) findViewById(R.id.rule_list);
		ln.removeAllViews();
		for (Rule rule : rules) {
			ln.addView(new RuleView(this, rule, this));
		}
		ln.addView(new RuleView(this, null, this));
	}

	/**
	 * Ask the rule views to update its underlying rule.
	 */
	private void updateRuleFilters() {
		LinearLayout ln = (LinearLayout) findViewById(R.id.rule_list);
		for (int i = 0; i < ln.getChildCount(); i++) {
			RuleView v = (RuleView) ln.getChildAt(i);
			v.updateRuleFilter();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.amphiprion.myaccount.view.RuleView.OnRuleClickedListener#ruleClicked(org.amphiprion.myaccount.view.RuleView)
	 */
	@Override
	public void ruleClicked(RuleView view) {
		LinearLayout ln = (LinearLayout) findViewById(R.id.rule_list);
		if (view.getRule() == null) {
			ln.addView(new RuleView(this, null, this));
			Rule rule = new Rule();
			rule.setCategoryId(category.getId());
			view.setRule(rule);
			rules.add(rule);
		} else if (view.getRule().getState() == Rule.DbState.CREATE) {
			ln.removeView(view);
			rules.remove(view.getRule());
		} else {
			view.getRule().setState(Rule.DbState.DELETE);
			ln.removeView(view);
		}
	}
}
