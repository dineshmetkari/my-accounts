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

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.amphiprion.myaccount.adapter.DecimalSepatorAdapter;
import org.amphiprion.myaccount.database.OperationDao;
import org.amphiprion.myaccount.database.entity.Account;
import org.amphiprion.myaccount.database.entity.Operation;
import org.amphiprion.myaccount.driver.file.FileDriver;
import org.amphiprion.myaccount.driver.file.FileDriverManager;
import org.amphiprion.myaccount.driver.file.FileImportTask;
import org.amphiprion.myaccount.driver.file.OnTaskEndListener;
import org.amphiprion.myaccount.driver.file.Parameter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

/**
 * This generic activity is used to provide user parameter for a given
 * FileDriver.
 * 
 * @author amphiprion
 * 
 */
public class DefineImportParameter extends Activity implements OnTaskEndListener {
	private Button fileUril;

	@SuppressWarnings("unchecked")
	private List<Parameter> parameters;
	private Account account;

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.define_import_parameter);

		account = (Account) getIntent().getSerializableExtra("ACCOUNT");
		int index = getIntent().getIntExtra("FILE_DRIVER_INDEX", -1);
		final FileDriver driver = FileDriverManager.getDrivers().get(index);

		buildHMI(driver);

		Button btImport = (Button) findViewById(R.id.btImport);
		btImport.setOnClickListener(new ViewGroup.OnClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				boolean canImport = collectInputs();
				if (canImport) {
					FileImportTask task = new FileImportTask(driver, new ProgressDialog(DefineImportParameter.this));
					task.setOnTaskEnd(DefineImportParameter.this);
					task.execute(parameters);
				}
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

	/**
	 * Build the HMI elements.
	 * 
	 * @param driver
	 */
	@SuppressWarnings("unchecked")
	private void buildHMI(FileDriver driver) {
		LinearLayout root = (LinearLayout) findViewById(R.id.root);
		parameters = driver.getParameters(account);
		for (Parameter param : parameters) {
			TextView tv = new TextView(this);
			LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

			tv.setLayoutParams(lp);
			tv.setText(getResources().getIdentifier(param.getName(), "string", ApplicationConstants.PACKAGE));
			tv.setBackgroundColor(getResources().getColor(R.color.darkGrey));
			root.addView(tv);
			View editView = null;
			switch (param.getType()) {
			case DATE_PICKER:
				editView = new DatePicker(this);
				if (param.getValue() != null) {
					Date date = (Date) param.getValue();
					((DatePicker) editView).init(date.getYear() + 1900, date.getMonth(), date.getDate(), null);
				}
				break;
			case DECIMAL_SEPARATOR:
				editView = new Spinner(this);
				((Spinner) editView).setAdapter(new DecimalSepatorAdapter(this));
				break;
			case DATE_FORMAT:
				editView = new EditText(this);
				((EditText) editView).setText("" + param.getValue());
				break;
			case FILE_URI:
				fileUril = new Button(this);
				editView = fileUril;
				editView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.addCategory(Intent.CATEGORY_OPENABLE);
						intent.setType("application/qif");
						startActivityForResult(intent, ApplicationConstants.ACTIVITY_RETURN_CHOOSE_FILE);
					}
				});
				break;
			}
			lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			editView.setLayoutParams(lp);
			root.addView(editView);

			tv = new TextView(this);
			lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_item_background));
			root.addView(tv);

		}

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
			if (requestCode == ApplicationConstants.ACTIVITY_RETURN_CHOOSE_FILE) {
				fileUril.setText(data.getDataString());
			}
		}
	}

	/**
	 * Collect user input to fill value of parameter.
	 * 
	 * @return true if all data are will entered
	 */
	@SuppressWarnings("unchecked")
	private boolean collectInputs() {
		LinearLayout root = (LinearLayout) findViewById(R.id.root);
		int indexView = 1;

		for (Parameter param : parameters) {

			switch (param.getType()) {
			case DATE_PICKER:
				DatePicker dp = (DatePicker) root.getChildAt(indexView);
				param.setValue(new Date(dp.getYear() - 1900, dp.getMonth(), dp.getDayOfMonth()));
				break;
			case DECIMAL_SEPARATOR:
				Spinner sp = (Spinner) root.getChildAt(indexView);
				param.setValue(sp.getSelectedItem());
				break;
			case DATE_FORMAT:
				EditText et = (EditText) root.getChildAt(indexView);
				param.setValue(et.getText().toString());
				break;
			case FILE_URI:
				Button bt = (Button) root.getChildAt(indexView);
				boolean ok = false;
				try {
					URI uri = new URI("" + bt.getText());
					if (new File(uri).exists()) {
						param.setValue(uri);
						ok = true;
					}
				} catch (Throwable e) {
				}
				if (!ok) {
					Toast.makeText(
							this,
							getResources().getString(R.string.mandatory_field,
									((TextView) root.getChildAt(indexView - 1)).getText()), Toast.LENGTH_LONG).show();

					return false;
				}
				break;
			}
			indexView += 3;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.amphiprion.myaccount.driver.file.OnTaskEndListener#taskEnded()
	 */
	@Override
	public void taskEnded(FileImportTask task) {
		try {
			List<Operation> operations = task.get();
			Toast.makeText(DefineImportParameter.this,
					getResources().getString(R.string.nb_imported_operation, operations.size()), Toast.LENGTH_LONG)
					.show();
			OperationDao.getInstance(this).createAll(account, operations);
			Intent i = new Intent();
			i.putExtra("ACCOUNT", account);
			setResult(RESULT_OK, i);
			finish();
		} catch (Throwable e) {
			Log.e(ApplicationConstants.PACKAGE, "", e);
		}
	}
}
