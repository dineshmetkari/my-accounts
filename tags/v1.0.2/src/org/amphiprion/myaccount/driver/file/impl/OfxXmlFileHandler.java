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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.amphiprion.myaccount.ApplicationConstants;
import org.amphiprion.myaccount.database.entity.Operation;
import org.amphiprion.myaccount.driver.file.Parameter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/**
 * This the driver for OFX 1.x files.
 * 
 * @author amphiprion
 * 
 */
public class OfxXmlFileHandler extends DefaultHandler {

	private Parameter<String> decimalSeparator;
	private Parameter<Date> from;
	private Parameter<Date> to;

	private StringBuilder builder;
	SimpleDateFormat sdf;
	private List<Operation> operations;
	private Operation current;
	private boolean inTransactionList = false;
	private boolean inEntry;

	public OfxXmlFileHandler(Parameter<String> decimalSeparator, Parameter<Date> from, Parameter<Date> to) {
		this.decimalSeparator = decimalSeparator;
		this.from = from;
		this.to = to;

		operations = new ArrayList<Operation>();
		sdf = new SimpleDateFormat("yyyyMMdd");

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		super.endElement(uri, localName, name);
		if (localName.equalsIgnoreCase("BANKTRANLIST")) {
			inTransactionList = false;
		} else if (inTransactionList) {
			if (localName.equalsIgnoreCase("STMTTRN")) {
				current = new Operation();
				inEntry = true;
			} else if (inEntry) {
				if (localName.equalsIgnoreCase("DTPOSTED")) {
					try {
						current.setDate(sdf.parse(builder.toString()));
					} catch (Throwable e) {
						Log.e(ApplicationConstants.PACKAGE, "", e);
					}
				} else if (localName.equalsIgnoreCase("TRNAMT")) {
					String amount = builder.toString();
					if (",".equals(decimalSeparator.getValue())) {
						amount = amount.replace(',', '.');
					}
					current.setAmount(Double.parseDouble(amount));
				} else if (localName.equalsIgnoreCase("NAME")) {
					current.setDescription(builder.toString());
				} else if (localName.equalsIgnoreCase("MEMO")) {
					String s = builder.toString();
					if (!s.equals(".")) {
						current.setDescription(current.getDescription() + "\n" + s);
					}
				}
			}
		}
		builder.setLength(0);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		builder = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (localName.equalsIgnoreCase("BANKTRANLIST")) {
			inTransactionList = true;
		} else if (localName.equalsIgnoreCase("STMTTRN")) {
			inEntry = false;
			if (current.getAmount() != 0 && current.getDate() != null) {
				if ((to.getValue() == null || !current.getDate().after(to.getValue()))
						&& (from.getValue() == null || !current.getDate().before(from.getValue()))) {
					operations.add(current);
				}
			}
			current = null;
		}
	}

	/**
	 * @return
	 */
	public List<Operation> getOperations() {
		return operations;
	}

}
