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
package org.amphiprion.myaccount.chart;

import java.util.Date;
import java.util.List;

import net.droidsolutions.droidcharts.awt.Font;
import net.droidsolutions.droidcharts.awt.Rectangle2D;
import net.droidsolutions.droidcharts.core.ChartFactory;
import net.droidsolutions.droidcharts.core.JFreeChart;
import net.droidsolutions.droidcharts.core.data.PieDataset;
import net.droidsolutions.droidcharts.core.plot.PiePlot;

import org.amphiprion.myaccount.database.OperationDao;
import org.amphiprion.myaccount.database.entity.Account;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.View;

public class PieChart extends View {
	/** The view bounds. */
	private final Rect mRect = new Rect();
	/** The user interface thread handler. */
	private final Handler mHandler;
	private PieDataset dataset;
	private JFreeChart chart;

	/**
	 * Creates a new graphical view.
	 * 
	 * @param context
	 *            the context
	 * @param chart
	 *            the chart to be drawn
	 */
	public PieChart(Context context, Account account, Date[] period) {
		super(context);
		mHandler = new Handler();

		dataset = OperationDao.getInstance(context).getPieDataset(account, period[0], period[1], false);
		chart = createChart(dataset);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.getClipBounds(mRect);

		chart.draw(canvas, new Rectangle2D.Double(0, 0, mRect.width(), mRect.height()));
		Paint p = new Paint();
		p.setColor(Color.RED);
	}

	/**
	 * Schedule a user interface repaint.
	 */
	public void repaint() {
		mHandler.post(new Runnable() {
			public void run() {
				invalidate();
			}
		});
	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            the dataset.
	 * 
	 * @return a chart.
	 */
	private JFreeChart createChart(final PieDataset dataset) {
		JFreeChart chart = ChartFactory.createPieChart("Pie Chart Demo 1", // chart
				// title
				dataset, // data
				false, // include legend
				true, false);

		PiePlot plot = (PiePlot) chart.getPlot();

		Paint white = new Paint(Paint.ANTI_ALIAS_FLAG);
		white.setColor(Color.WHITE);

		Paint dkGray = new Paint(Paint.ANTI_ALIAS_FLAG);
		dkGray.setColor(Color.DKGRAY);

		Paint lightGray = new Paint(Paint.ANTI_ALIAS_FLAG);
		lightGray.setColor(Color.LTGRAY);
		lightGray.setStrokeWidth(10);

		Paint black = new Paint(Paint.ANTI_ALIAS_FLAG);
		black.setColor(Color.BLACK);

		Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		borderPaint.setColor(Color.WHITE);
		borderPaint.setStrokeWidth(5);
		// chart.setBorderPaint(borderPaint);
		chart.setBackgroundPaint(dkGray);

		plot.setLabelFont(new Font("SansSerif", Typeface.BOLD, 12));
		plot.setNoDataMessage("No data available");
		plot.setCircular(true);
		plot.setLabelGap(0.02);
		plot.setLabelBackgroundPaint(lightGray);
		plot.setBackgroundPaint(dkGray);

		// Resources res = getResources();
		// int[] colors = new int[] { 2555, 125445, 15454, 15454, 2154, 2154,
		// 54, 87845 };
		//
		// int[] outlineColors = new int[] { res.getColor(R.color.white),
		// res.getColor(R.color.white),
		// res.getColor(R.color.white), res.getColor(R.color.white),
		// res.getColor(R.color.white),
		// res.getColor(R.color.white), res.getColor(R.color.white),
		// res.getColor(R.color.white) };
		// PieRenderer renderer = new PieRenderer(colors, outlineColors, 3f);
		// renderer.setColor(plot, dataset);
		return chart;
	}

	/*
	 * A simple renderer for setting custom colors for a pie chart.
	 */

	public static class PieRenderer {
		private int[] fillColor;
		private int[] outlineColor;
		private float outlineStroke;

		public PieRenderer(int[] color, int[] outlineColor, float outlineStroke) {
			fillColor = color;
			this.outlineColor = outlineColor;
			this.outlineStroke = outlineStroke;
		}

		@SuppressWarnings("unchecked")
		public void setColor(PiePlot plot, PieDataset dataset) {
			List<Comparable> keys = dataset.getKeys();
			int aInt;

			for (int i = 0; i < keys.size(); i++) {
				aInt = i % fillColor.length;

				Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				fillPaint.setColor(fillColor[aInt]);
				plot.setSectionPaint(keys.get(i), fillPaint);

				Paint outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				outlinePaint.setColor(outlineColor[aInt]);
				plot.setSectionOutlinePaint(keys.get(i), outlinePaint);

				plot.setSectionOutlineStroke(keys.get(i), outlineStroke);
			}
		}
	}
}