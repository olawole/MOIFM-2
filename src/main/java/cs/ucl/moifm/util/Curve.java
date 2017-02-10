package cs.ucl.moifm.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

@SuppressWarnings("serial")
public class Curve extends ApplicationFrame {
	
	Double[] xData;
	int[] yData;

	public Curve(String title, int[] xdata, Double[] ydata,String legend) throws Exception {
		super(title);
		if (xdata.length != ydata.length){
			throw new Exception("Number of data in both Axes must be same");
		}
		// TODO Auto-generated constructor stub
		final XYSeries series = new XYSeries(legend);
		series.add(0, 0);
		for (int i = 0; i < xdata.length; i++){
			series.add(xdata[i], ydata[i]);
		}
        final XYSeriesCollection data = new XYSeriesCollection(series);
        
        final JFreeChart chart = ChartFactory.createXYLineChart(
        		"Cash Flow Analysis",
            "Period", 
            "NPV", 
            data,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
       
   //     Number minimum = DatasetUtilities.findMinimumRangeValue(data);
   //     ValueMarker min = new ValueMarker(minimum.floatValue());
   //     min.setPaint(Color.blue);
   //     min.setLabel("Self-funding status");
   //     min.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
        final ChartPanel chartPanel = new ChartPanel(chart);
   //     chart.getXYPlot().addRangeMarker(min);
        chart.getXYPlot().setRangeZeroBaselineVisible(true);
        chart.getXYPlot().setDomainZeroBaselineVisible(true);
        chart.getXYPlot().setRenderer(new XYSplineRenderer());
        chart.setBackgroundPaint(Color.WHITE);
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 400));
//        chartPanel.addChartMouseListener(new ChartMouseListener() {
//			
//			@Override
//			public void chartMouseMoved(ChartMouseEvent arg0) {
//				
//				
//			}
//			
//			@Override
//			public void chartMouseClicked(ChartMouseEvent arg0) {
//				System.out.println(arg0.getEntity().getClass());
//				
//			}
//		});
        setContentPane(chartPanel);
	}
	public Curve(String title, HashMap<String, Double[][]> data, int period, int features) throws Exception {
		super(title);
		final XYSeriesCollection datap = new XYSeriesCollection();
		for (Map.Entry<String, Double[][]> entry: data.entrySet()){
			int[] xdata = IntStream.rangeClosed(1, period).toArray();
			Double[] ydata = entry.getValue()[features+1];
			if (xdata.length != ydata.length){
				throw new Exception("Number of data in both Axes must be same");
			}
			final XYSeries series = new XYSeries(entry.getKey());
			series.add(0, 0);
			for (int i = 0; i < xdata.length; i++){
				series.add(xdata[i], ydata[i]);
			}
	        datap.addSeries(series);
	        
		}
		
		// TODO Auto-generated constructor stub
		
        
        final JFreeChart chart = ChartFactory.createXYLineChart(
        		"Cash Flow Analysis",
            "Period", 
            "NPV", 
            datap,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
       
        Number minimum = DatasetUtilities.findMinimumRangeValue(datap);
        ValueMarker min = new ValueMarker(minimum.floatValue());
        min.setPaint(Color.blue);
        min.setLabel("Self-funding status");
        min.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chart.getXYPlot().addRangeMarker(min);
        chart.getXYPlot().setRangeZeroBaselineVisible(true);
        chart.getXYPlot().setDomainZeroBaselineVisible(true);
        chart.getXYPlot().setRenderer(new XYSplineRenderer());
        chart.setBackgroundPaint(Color.WHITE);
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 400));
//        chartPanel.addChartMouseListener(new ChartMouseListener() {
//			
//			@Override
//			public void chartMouseMoved(ChartMouseEvent arg0) {
//				
//				
//			}
//			
//			@Override
//			public void chartMouseClicked(ChartMouseEvent arg0) {
//				System.out.println(arg0.getEntity().getClass());
//				
//			}
//		});
        setContentPane(chartPanel);
	}
	
}
