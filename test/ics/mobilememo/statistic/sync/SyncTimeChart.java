package ics.mobilememo.statistic.sync;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * Draw a chart to show the "diff" values in time synchronization.
 * 
 * @author hengxin
 * @date Jul 5, 2014
 */
public class SyncTimeChart extends ApplicationFrame implements ActionListener
{
	private static final long serialVersionUID = 8280532831067487144L;

	public static final String CHART_TITLE = "Sync. Time Chart";
	
	private static final String TIME_AXIS_LABEL = "Time Points";
	private static final String VALUE_AXIS_LABEL = "Sync. Diff (ms)";
	
	private static final String IMPORT_DATA_SOURCE = "Import ...";
	private static final String DEFAULT_DATA_SOURCE_DIRECTORY = "C:\\Users\\ics-ant\\Desktop\\executions\\SyncTime";
	private static final String REMOVE_DATA_SOURCE = "Remove ...";
	
	private static final int THE_FIRST_LINE = 1;	// line no: starting from 1
	private static final int THE_SECOND_PART = 1;	// array index: starting from 0
	private static final int EVERY_THREE_LINE = 3;
	
	private static final int MAX_SERIES_NUMBER = 10;
	
	/** The plot. */
	private XYPlot plot;

	/** The index of the last dataset added. */
	private int datasetIndex = 0;

	/**
	 * Constructs a new demonstration application.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public SyncTimeChart(final String title)
	{
		super(title);
		
		final XYSeriesCollection sync_dataset = new XYSeriesCollection();
		final JFreeChart chart = ChartFactory.createXYLineChart(
				SyncTimeChart.CHART_TITLE, SyncTimeChart.TIME_AXIS_LABEL, SyncTimeChart.VALUE_AXIS_LABEL, sync_dataset);
		chart.setBackgroundPaint(Color.white);

		this.plot = chart.getXYPlot();
		this.plot.setBackgroundPaint(Color.lightGray);
		this.plot.setDomainGridlinePaint(Color.white);
		this.plot.setRangeGridlinePaint(Color.white);
		// this.plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 4, 4, 4, 4));
		final ValueAxis axis = this.plot.getDomainAxis();
		axis.setAutoRange(true);

		final NumberAxis rangeAxis2 = new NumberAxis("Range Axis 2");
		rangeAxis2.setAutoRangeIncludesZero(false);

		final JPanel content = new JPanel(new BorderLayout());

		final ChartPanel chartPanel = new ChartPanel(chart);
		content.add(chartPanel);

		final JButton btn_import = new JButton(SyncTimeChart.IMPORT_DATA_SOURCE);
		btn_import.setActionCommand(SyncTimeChart.IMPORT_DATA_SOURCE);
		btn_import.addActionListener(this);

		final JButton btn_remove = new JButton(SyncTimeChart.REMOVE_DATA_SOURCE);
		btn_remove.setActionCommand(SyncTimeChart.REMOVE_DATA_SOURCE);
		btn_remove.addActionListener(this);

		final JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(btn_import);
		buttonPanel.add(btn_remove);

		content.add(buttonPanel, BorderLayout.SOUTH);
		chartPanel.setPreferredSize(new java.awt.Dimension(1000, 618));
		setContentPane(content);

	}

	/**
	 * Creates a random dataset.
	 * 
	 * @param name
	 *            the series name.
	 * 
	 * @return The dataset.
	 */
	private XYSeriesCollection createDataset()
	{
		XYSeriesCollection data_series_collection = new XYSeriesCollection();
		
		File data_source_file = this.addSyncDataSourceFromFileChooser();
		if (data_source_file != null)
		{	
			XYSeries data_series =  this.createXYseries(data_source_file);
			data_series_collection.addSeries(data_series);
		}

		return data_series_collection;
	}

	/**
	 * Add a data source file from {@link JFileChooser}.
	 * @return {@link File} chosen as data source 
	 * (it will be null if the file chooser dialog is closed without 
	 * a data source file is chosen)
	 */
	private File addSyncDataSourceFromFileChooser()
	{
	    JFileChooser chooser = new JFileChooser(SyncTimeChart.DEFAULT_DATA_SOURCE_DIRECTORY);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Sync. Data File", "txt");
	    chooser.setFileFilter(filter);
	    
	    int returnVal = chooser.showOpenDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) 
	       return chooser.getSelectedFile();
	    
	    return null;
	}
	
	/**
	 * create an {@link XYSeries} from a data source file
	 * @param data_file file containing the sync. time data
	 * @return {@link XYSeries} containing a serial of sync. time data
	 */
	private XYSeries createXYseries(File data_file)
	{
		/**
		 * set the title of the {@link XYSeries}
		 */
		final String series_title = data_file.getParentFile().getName();
		final XYSeries series = new XYSeries(series_title);
		
		/**
		 * Parse the @param data_file containing the sync. time data.
		 * Each sync. time item consists of three lines:
		 * diff -381
		 * pc_time 1404440194045
		 * android_time 1404440193664
		 * 
		 * Only the "diff" value in the first line is relevant.
		 */
		BufferedReader br = null;
		String sync_data_line = null;
		String[] sync_data_line_parts;
		int line_no = 0;
		
		try
		{
			br = new BufferedReader(new FileReader(data_file));
			while((sync_data_line = br.readLine()) != null)
			{
				line_no++;
				
				// only the "diff" value in the first line is relevant
				if (line_no % SyncTimeChart.EVERY_THREE_LINE == SyncTimeChart.THE_FIRST_LINE)
				{
					// parse the "diff" value
					sync_data_line_parts = sync_data_line.split("\\s+");
					series.add(line_no / SyncTimeChart.EVERY_THREE_LINE + 1, Double.parseDouble(sync_data_line_parts[SyncTimeChart.THE_SECOND_PART]));
				}
			}				
		} catch (FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		} finally
		{
			try
			{
				br.close();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}

		return series;
	}
	
	// ****************************************************************************
	// * JFREECHART DEVELOPER GUIDE *
	// * The JFreeChart Developer Guide, written by David Gilbert, is available
	// *
	// * to purchase from Object Refinery Limited: *
	// * *
	// * http://www.object-refinery.com/jfreechart/guide.html *
	// * *
	// * Sales are used to provide funding for the JFreeChart project - please *
	// * support us so that we can continue developing free software. *
	// ****************************************************************************

	/**
	 * Handles a click on the button by adding new (random) data.
	 * 
	 * @param e
	 *            the action event.
	 */
	public void actionPerformed(final ActionEvent e)
	{

		if (e.getActionCommand().equals(SyncTimeChart.IMPORT_DATA_SOURCE))
		{
			if (this.datasetIndex < SyncTimeChart.MAX_SERIES_NUMBER)
			{
				this.datasetIndex++;
				this.plot.setDataset(this.datasetIndex, createDataset());
				this.plot.setRenderer(this.datasetIndex,
						new StandardXYItemRenderer());
			}
		} else if (e.getActionCommand().equals(SyncTimeChart.REMOVE_DATA_SOURCE))
		{
			if (this.datasetIndex >= 1)
			{
				this.plot.setDataset(this.datasetIndex, null);
				this.plot.setRenderer(this.datasetIndex, null);
				this.datasetIndex--;
			}
		}

	}
	
	public static void main(String[] args)
	{
        final SyncTimeChart sync_time_chart = new SyncTimeChart(SyncTimeChart.CHART_TITLE);
        sync_time_chart.pack();
        RefineryUtilities.centerFrameOnScreen(sync_time_chart);
        sync_time_chart.setVisible(true);
	}
}
