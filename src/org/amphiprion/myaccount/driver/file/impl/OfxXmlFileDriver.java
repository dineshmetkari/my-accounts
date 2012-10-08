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
package org.amphiprion.myaccount.driver.file.impl;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.amphiprion.myaccount.ApplicationConstants;
import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.database.entity.Operation;
import org.amphiprion.myaccount.driver.file.FileDriver;
import org.amphiprion.myaccount.driver.file.Parameter;
import org.amphiprion.myaccount.driver.file.Parameter.Type;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/**
 * This the driver for OFX 1.x files.
 * 
 * @author amphiprion
 * 
 */
public class OfxXmlFileDriver extends DefaultHandler implements FileDriver {
	/** The decimal separator parameter name. */
	private static final String DECIMAL_SEPARATOR_NAME = "DECIMAL";
	/** The from parameter name. */
	private static final String FROM_NAME = "FROM";
	/** The to parameter name. */
	private static final String TO_NAME = "TO";
	/** The to parameter file name. */
	private static final String FILE_NAME = "FILE";

	private Parameter<String> decimalSeparator;
	private Parameter<Date> from;
	private Parameter<Date> to;
	private Parameter<URI> file;

	/**
	 * The parameter list.
	 */
	@SuppressWarnings("unchecked")
	private List<Parameter> parameters;

	@SuppressWarnings("unchecked")
	public OfxXmlFileDriver() {
		decimalSeparator = new Parameter<String>(DECIMAL_SEPARATOR_NAME, Type.DECIMAL_SEPARATOR, ".");
		from = new Parameter<Date>(FROM_NAME, Type.DATE_PICKER);
		to = new Parameter<Date>(TO_NAME, Type.DATE_PICKER);
		file = new Parameter<URI>(FILE_NAME, Type.FILE_URI);

		parameters = new ArrayList<Parameter>();
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
		return "OFX 2.x";
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

		try {
			FileInputStream fis = new FileInputStream(new File(file.getValue()));

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser;
			OfxXmlFileHandler handler = new OfxXmlFileHandler(decimalSeparator, from, to);

			parser = factory.newSAXParser();
			parser.parse(fis, handler);
			operations = handler.getOperations();

		} catch (Exception e) {
			Log.e(ApplicationConstants.PACKAGE, "Error parsing OFX 2.x", e);
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
		return "ofx";
	}

}
