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
package org.amphiprion.myaccount.view;

import org.amphiprion.myaccount.database.entity.Budget;
import org.amphiprion.myaccounts.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View used to display a budget in the budget list.
 * 
 * @author amphiprion
 * 
 */
public class BudgetSummaryView extends LinearLayout {
	/** the linked budget. */
	private Budget budget;

	/**
	 * Construct an category view.
	 * 
	 * @param context
	 *            the context
	 * @param budget
	 *            the budget entity
	 */
	public BudgetSummaryView(Context context, Budget budget) {
		super(context);
		this.budget = budget;

		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setOrientation(VERTICAL);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_background_states));

		addView(createTitle());

		addView(createBudgetLayout());
	}

	/**
	 * @return the budget
	 */
	public Budget getBudget() {
		return budget;
	}

	/**
	 * Create the category icon view.
	 * 
	 * @return the view
	 */
	private View createTitle() {
		LinearLayout accountLayout = new LinearLayout(getContext());
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		accountLayout.setLayoutParams(aclp);
		TextView t = new TextView(getContext());
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);

		t.setLayoutParams(tlp);
		t.setText(budget.getName());
		t.setTextSize(16);
		t.setTypeface(Typeface.DEFAULT_BOLD);
		t.setTextColor(getContext().getResources().getColor(R.color.black));
		accountLayout.addView(t);

		t = new TextView(getContext());
		tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		t.setLayoutParams(tlp);
		t.setText("" + Math.round(budget.getComputedExpense() * 100.0) / 100.0 + " / " + Math.round(budget.getAmount() * 100.0) / 100.0);
		t.setTextSize(16);
		// t.setTypeface(Typeface.DEFAULT_BOLD);
		t.setTextColor(getContext().getResources().getColor(R.color.black));
		accountLayout.addView(t);
		return accountLayout;
	}

	/**
	 * Create the budget layout view
	 * 
	 * @return the view
	 */
	private View createBudgetLayout() {
		LinearLayout accountLayout = new LinearLayout(getContext());
		LayoutParams aclp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		// accountLayout.setOrientation(VERTICAL);
		accountLayout.setLayoutParams(aclp);

		boolean exceed = false;
		float pct = 0;
		if (budget.getComputedExpense() > budget.getAmount()) {
			exceed = true;
			pct = (float) (budget.getAmount() / budget.getComputedExpense());
		} else {
			if (budget.getAmount() != 0) {
				pct = (float) (budget.getComputedExpense() / budget.getAmount());
			}
		}
		ImageView bar = new ImageView(getContext());
		LayoutParams tlp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, pct);
		tlp.bottomMargin = 5;

		bar.setLayoutParams(tlp);
		bar.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.progress_left));
		accountLayout.addView(bar);

		ImageView barRight = new ImageView(getContext());
		LayoutParams tlpRight = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f - pct);
		tlpRight.bottomMargin = 5;
		barRight.setLayoutParams(tlpRight);
		if (exceed) {
			barRight.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.progress_right_red));
		} else {
			barRight.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.progress_right));
		}
		accountLayout.addView(barRight);

		return accountLayout;
	}

}
