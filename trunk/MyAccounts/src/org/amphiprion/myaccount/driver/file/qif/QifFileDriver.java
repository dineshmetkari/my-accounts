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
package org.amphiprion.myaccount.driver.file.qif;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.amphiprion.myaccount.ApplicationConstants;
import org.amphiprion.myaccount.database.entity.Operation;
import org.amphiprion.myaccount.driver.file.FileDriver;
import org.amphiprion.myaccount.driver.file.Parameter;
import org.amphiprion.myaccount.driver.file.Parameter.Type;

import android.util.Log;

/**
 * This the driver for QIF files.
 * 
 * @author amphiprion
 * 
 */
public class QifFileDriver implements FileDriver {
	/** The date format parameter name. */
	private static final String DATE_FORMAT_NAME = "DATE_FORMAT";
	/** The decimal separator parameter name. */
	private static final String DECIMAL_SEPARATOR_NAME = "DECIMAL";
	/** The from parameter name. */
	private static final String FROM_NAME = "FROM";
	/** The to parameter name. */
	private static final String TO_NAME = "TO";

	private Parameter<String> dateFormat;
	private Parameter<String> decimalSeparator;
	private Parameter<Date> from;
	private Parameter<Date> to;
	/**
	 * The parameter list.
	 */
	@SuppressWarnings("unchecked")
	private List<Parameter> parameters;

	@SuppressWarnings("unchecked")
	public QifFileDriver() {
		dateFormat = new Parameter<String>(DATE_FORMAT_NAME, Type.DATE_FORMAT, "dd/MM/yy");
		decimalSeparator = new Parameter<String>(DECIMAL_SEPARATOR_NAME, Type.DECIMAL_SEPARATOR, ".");
		dateFormat = new Parameter<String>(DATE_FORMAT_NAME, Type.DATE_FORMAT, "dd/MM/yy");
		from = new Parameter<Date>(FROM_NAME, Type.DATE_PICKER);
		to = new Parameter<Date>(TO_NAME, Type.DATE_PICKER);

		parameters = new ArrayList<Parameter>();
		parameters.add(dateFormat);
		parameters.add(decimalSeparator);
		parameters.add(from);
		parameters.add(to);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.amphiprion.myaccount.driver.file.FileDriver#getParameters()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getParameters() {
		return parameters;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.amphiprion.myaccount.driver.file.FileDriver#getName()
	 */
	@Override
	public String getName() {
		return "QIF";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.amphiprion.myaccount.driver.file.FileDriver#parse(java.io.InputStream,
	 *      java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Operation> parse(InputStream data, List<Parameter> parameters) {
		List<Operation> operations = new ArrayList<Operation>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat.getValue());
			// to.setValue(sdf.parse("17/12/10"));
			BufferedReader br = new BufferedReader(new InputStreamReader(data));

			String ligne;
			Operation op = new Operation();
			while ((ligne = br.readLine()) != null) {
				if (ligne.startsWith("^")) {
					if (op.getAmount() != 0 && op.getDate() != null) {
						if ((to.getValue() == null || !op.getDate().after(to.getValue()))
								&& (from.getValue() == null || !op.getDate().before(from.getValue()))) {
							operations.add(op);
							Log.d(ApplicationConstants.PACKAGE, op.toString());
						}
					}
					op = new Operation();
				} else if (ligne.startsWith("T")) {
					String amount = ligne.substring(1);
					if (",".equals(decimalSeparator.getValue())) {
						amount = amount.replace(',', '.');
					}
					op.setAmount(Double.parseDouble(amount));
				} else if (ligne.startsWith("D")) {
					String date = ligne.substring(1);
					op.setDate(sdf.parse(date));
				} else if (ligne.startsWith("M")) {
					String desc = ligne.substring(1);
					op.setDescription(desc);
				}
			}
		} catch (Exception e) {
			Log.e(ApplicationConstants.PACKAGE, "Error parsing QIF", e);
		}
		return operations;
	}
}
