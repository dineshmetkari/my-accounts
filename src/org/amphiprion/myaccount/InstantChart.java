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

import org.amphiprion.myaccount.chart.PieChart;
import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.util.DateUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * This activity display a chart.
 * 
 * @author amphiprion
 * 
 */
public class InstantChart extends Activity {
	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		Account account = (Account) i.getExtras().get("ACCOUNT");
		Date[] period = new Date[2];
		period[0] = (Date) i.getExtras().get("FROM");
		period[1] = (Date) i.getExtras().get("TO");

		setTitle(getResources().getString(R.string.period_custom_label, DateUtil.defaultDateFormat.format(period[0]),
				DateUtil.defaultDateFormat.format(period[1])));
		setContentView(new PieChart(getApplicationContext(), account, period));
	}

}
