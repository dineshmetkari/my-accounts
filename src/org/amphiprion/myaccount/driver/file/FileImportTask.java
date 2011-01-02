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

import java.util.List;

import org.amphiprion.myaccount.database.RuleDao;
import org.amphiprion.myaccount.database.entity.Category;
import org.amphiprion.myaccount.database.entity.Operation;
import org.amphiprion.myaccount.database.entity.Rule;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * @author amphiprion
 * 
 */
@SuppressWarnings("unchecked")
public class FileImportTask extends AsyncTask<List<Parameter>, Void, List<Operation>> {

	private ProgressDialog dialog;// = new ProgressDialog(this);
	private OnTaskEndListener taskEndListener;
	private FileDriver driver;
	private Context context;

	/**
	 * 
	 */
	public FileImportTask(Context context, FileDriver driver) {
		dialog = new ProgressDialog(context);
		this.driver = driver;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		// here you have place code which you want to show in UI thread like
		// progressbar or dialog or any layout . here i am displaying a
		// progressDialog with test please wait while loading......

		dialog.setMessage(" please wait while loading............");
		dialog.show();

	}

	@Override
	protected List<Operation> doInBackground(List<Parameter>... params) {
		List<Rule> rules = RuleDao.getInstance(context).getRules();
		List<Operation> operations = driver.parse(params[0]);

		for (Operation op : operations) {
			boolean finded = false;
			String categoryId = null;
			for (Rule rule : rules) {
				String desc = op.getDescription().toUpperCase();
				String[] filters = rule.getFilter().toUpperCase().split("\\%");
				int pos = -1;
				for (String filter : filters) {
					pos = desc.indexOf(filter, pos);
					if (pos == -1) {
						break;
					}
				}
				if (pos != -1) {
					if (categoryId == null) {
						categoryId = rule.getCategoryId();
						finded = true;
					} else if (!categoryId.equals(rule.getCategoryId())) {
						// another category match (several categories not
						// supported)
						categoryId = null;
						finded = false;
						break;
					}
				}
			}
			if (finded) {
				op.setCategory(new Category(categoryId));
			}
		}
		return operations;
	}

	@Override
	protected void onPostExecute(List<Operation> result) {
		dialog.dismiss();
		if (taskEndListener != null) {
			taskEndListener.taskEnded(this);
		}
	}

	/**
	 * @param onClickListener
	 */
	public void setOnTaskEnd(OnTaskEndListener taskEndListener) {
		this.taskEndListener = taskEndListener;
	}

}
