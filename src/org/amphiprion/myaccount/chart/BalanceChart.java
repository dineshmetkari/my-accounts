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

import java.util.List;

import net.droidsolutions.droidcharts.awt.Rectangle2D;
import net.droidsolutions.droidcharts.common.RectangleInsets;
import net.droidsolutions.droidcharts.core.ChartFactory;
import net.droidsolutions.droidcharts.core.JFreeChart;
import net.droidsolutions.droidcharts.core.axis.CategoryAxis;
import net.droidsolutions.droidcharts.core.axis.CategoryLabelPositions;
import net.droidsolutions.droidcharts.core.axis.NumberAxis;
import net.droidsolutions.droidcharts.core.data.CategoryDataset;
import net.droidsolutions.droidcharts.core.data.PieDataset;
import net.droidsolutions.droidcharts.core.plot.CategoryPlot;
import net.droidsolutions.droidcharts.core.plot.PiePlot;
import net.droidsolutions.droidcharts.core.plot.PlotOrientation;
import net.droidsolutions.droidcharts.core.renderer.category.LineAndShapeRenderer;

import org.amphiprion.myaccount.database.OperationDao;
import org.amphiprion.myaccount.database.entity.Report;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;

public class BalanceChart extends View {
	/** The view bounds. */
	private final Rect mRect = new Rect();
	/** The user interface thread handler. */
	private final Handler mHandler;
	private CategoryDataset dataset;
	private JFreeChart chart;

	/**
	 * Creates a new graphical view.
	 * 
	 * @param context
	 *            the context
	 * @param chart
	 *            the chart to be drawn
	 */
	public BalanceChart(Context context, Report report) {
		super(context);
		mHandler = new Handler();
		dataset = OperationDao.getInstance(context).getBalanceDataset(report);
		chart = createLineChart(dataset);

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
	private JFreeChart createLineChart(final CategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createLineChart(" ", "", "",// chart
				// title
				dataset, PlotOrientation.VERTICAL,// data
				false, // include legend
				false, false);

		Paint white = new Paint(Paint.ANTI_ALIAS_FLAG);
		white.setColor(Color.WHITE);
		Paint dkGray = new Paint(Paint.ANTI_ALIAS_FLAG);
		dkGray.setColor(Color.DKGRAY);
		Paint lightGray = new Paint(Paint.ANTI_ALIAS_FLAG);
		lightGray.setColor(Color.LTGRAY);
		lightGray.setStrokeWidth(10);

		final CategoryPlot plot = chart.getCategoryPlot();

		// plot.setBackgroundPaint(dkGray);
		plot.setDomainGridlinePaint(lightGray);
		plot.setRangeGridlinePaint(lightGray);
		plot.setDomainGridlinesVisible(true);

		chart.setBackgroundPaint(white);

		// set the stroke for each series...
		// plot.getRenderer().setSeriesStroke(0, 1f);
		// plot.getRenderer().setSeriesStroke(1, 2f);
		// plot.getRenderer().setSeriesStroke(2, 3f);

		Paint blue = new Paint(Paint.ANTI_ALIAS_FLAG);
		blue.setColor(Color.rgb(125, 138, 46));
		// blue.set
		blue.setAlpha(200);
		blue.setStrokeWidth(10);
		Paint green = new Paint(Paint.ANTI_ALIAS_FLAG);
		green.setColor(Color.rgb(255, 240, 165));
		green.setAlpha(200);

		Paint red = new Paint(Paint.ANTI_ALIAS_FLAG);
		red.setColor(Color.rgb(182, 73, 38));
		red.setAlpha(200);

		// plot.getRenderer().setSeriesPaint(0, blue);
		// plot.getRenderer().setSeriesPaint(1, green);
		// plot.getRenderer().setSeriesPaint(2, red);

		// customise the renderer...
		final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		// renderer.setItemLabelsVisible(true);
		renderer.setSeriesShapesVisible(0, true);
		renderer.setSeriesShapesVisible(1, true);
		renderer.setSeriesShapesVisible(2, true);

		// customise the range axis...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setAutoRangeIncludesZero(false);
		rangeAxis.setLowerMargin(0.10);
		rangeAxis.setLabelAngle(90);

		final CategoryAxis domainAxis = plot.getDomainAxis();
		// domainAxis.set
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
		domainAxis.setUpperMargin(0.10);
		domainAxis.setLowerMargin(0.10);

		plot.setInsets(new RectangleInsets(10, 0, 0, 10));
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