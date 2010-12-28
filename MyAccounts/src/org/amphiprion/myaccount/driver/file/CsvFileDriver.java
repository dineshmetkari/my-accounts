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
package org.amphiprion.myaccount.driver.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.amphiprion.myaccount.ApplicationConstants;
import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.database.entity.Operation;
import org.amphiprion.myaccount.driver.file.Parameter.Type;

import android.util.Log;

/**
 * This the driver for QIF files.
 * 
 * @author amphiprion
 * 
 */
public class CsvFileDriver implements FileDriver {
	/** The date format parameter name. */
	private static final String DATE_FORMAT_NAME = "DATE_FORMAT";
	/** The decimal separator parameter name. */
	private static final String DECIMAL_SEPARATOR_NAME = "DECIMAL";
	/** The decimal separator parameter name. */
	private static final String FIELD_SEPARATOR_NAME = "FIELD";
	/** The from parameter name. */
	private static final String FROM_NAME = "FROM";
	/** The to parameter name. */
	private static final String TO_NAME = "TO";
	/** The to parameter file name. */
	private static final String FILE_NAME = "FILE";

	private Parameter<String> dateFormat;
	private Parameter<String> decimalSeparator;
	private Parameter<String> fieldSeparator;
	private Parameter<Date> from;
	private Parameter<Date> to;
	private Parameter<URI> file;
	/**
	 * The parameter list.
	 */
	@SuppressWarnings("unchecked")
	private List<Parameter> parameters;

	@SuppressWarnings("unchecked")
	public CsvFileDriver() {
		fieldSeparator = new Parameter<String>(FIELD_SEPARATOR_NAME, Type.FIELD_SEPARATOR, "Tab");
		dateFormat = new Parameter<String>(DATE_FORMAT_NAME, Type.DATE_FORMAT, "dd/MM/yy");
		decimalSeparator = new Parameter<String>(DECIMAL_SEPARATOR_NAME, Type.DECIMAL_SEPARATOR, ".");
		from = new Parameter<Date>(FROM_NAME, Type.DATE_PICKER);
		to = new Parameter<Date>(TO_NAME, Type.DATE_PICKER);
		file = new Parameter<URI>(FILE_NAME, Type.FILE_URI);
		parameters = new ArrayList<Parameter>();
		parameters.add(fieldSeparator);
		parameters.add(dateFormat);
		parameters.add(decimalSeparator);
		parameters.add(from);
		parameters.add(to);
		parameters.add(file);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.amphiprion.myaccount.driver.file.FileDriver#getParameters()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getParameters(Account account) {
		long fromValue = 0;
		if (account.getLastOperation() == null) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(System.currentTimeMillis());
			c.add(Calendar.MONTH, -1);
			fromValue = c.getTimeInMillis();
		} else {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(account.getLastOperation().getTime());
			c.add(Calendar.DAY_OF_MONTH, +1);
			fromValue = c.getTimeInMillis();
		}
		from.setValue(new Date(fromValue));
		return parameters;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.amphiprion.myaccount.driver.file.FileDriver#getName()
	 */
	@Override
	public String getName() {
		return "CSV";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.amphiprion.myaccount.driver.file.FileDriver#parse(java.io.InputStream,
	 *      java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Operation> parse(List<Parameter> parameters) {

		List<Operation> operations = new ArrayList<Operation>();
		if (true) {
			return operations;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat.getValue());
			FileInputStream fis = new FileInputStream(new File(file.getValue()));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			String ligne;
			Operation op = new Operation();
			while ((ligne = br.readLine()) != null) {
				if (ligne.startsWith("^")) {
					if (op.getAmount() != 0 && op.getDate() != null) {
						if ((to.getValue() == null || !op.getDate().after(to.getValue()))
								&& (from.getValue() == null || !op.getDate().before(from.getValue()))) {
							operations.add(op);
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
			operations.clear();
		}
		return operations;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.amphiprion.myaccount.driver.file.FileDriver#getSubDirectory()
	 */
	@Override
	public String getSubDirectory() {
		return "csv";
	}

}
