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

import org.amphiprion.myaccount.chart.BalanceChart;
import org.amphiprion.myaccount.chart.LineChart;
import org.amphiprion.myaccount.chart.PieChart;
import org.amphiprion.myaccount.database.entity.Report;
import org.amphiprion.myaccount.database.entity.Report.Type;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * This activity display a chart.
 * 
 * @author amphiprion
 * 
 */
public class Chart extends Activity {
	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		Report report = (Report) i.getExtras().get("REPORT");

		// setTitle(getResources().getString(R.string.period_custom_label,
		// DateUtil.defaultDateFormat.format(period[0]),
		// DateUtil.defaultDateFormat.format(period[1])));

		if (report.getType() == Type.CATEGORY_AMOUNT_BY_MONTH) {
			setContentView(new LineChart(getApplicationContext(), report));
		} else if (report.getType() == Type.CATEGORY_AMOUNT) {
			setContentView(new PieChart(getApplicationContext(), report));
		} else if (report.getType() == Type.DAILY_BALANCE) {
			setContentView(new BalanceChart(getApplicationContext(), report));
		}
	}

}
