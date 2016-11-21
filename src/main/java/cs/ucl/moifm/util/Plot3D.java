package cs.ucl.moifm.util;

import java.awt.Color;
import java.util.List;

import javax.swing.JFrame;

import com.orsoncharts.Chart3D;
import com.orsoncharts.Chart3DFactory;
import com.orsoncharts.data.xyz.XYZDataset;
import com.orsoncharts.data.xyz.XYZSeries;
import com.orsoncharts.data.xyz.XYZSeriesCollection;
import com.orsoncharts.graphics3d.Dimension3D;
import com.orsoncharts.graphics3d.ViewPoint3D;
import com.orsoncharts.plot.XYZPlot;
import com.orsoncharts.renderer.xyz.ScatterXYZRenderer;

import cs.ucl.moifm.model.Plan;

/**
 * Scatter plot demo chart configuration.
 */
@SuppressWarnings("serial")
public class Plot3D extends JFrame {
	private Front paretoFront;
	private List<Plan> dominated;
	
	public Plot3D(Front data, List<Plan> dom){
		this.paretoFront = data;
		this.dominated = dom;
	}
	
    public Chart3D createChart(XYZDataset dataset) {
        Chart3D chart = Chart3DFactory.createScatterChart("Scatter Plot showing Solutions", 
                null, dataset, "Expected Cost", "Expected Value", "Investment Risk");
        XYZPlot plot = (XYZPlot) chart.getPlot();
        ScatterXYZRenderer renderer = (ScatterXYZRenderer) plot.getRenderer();
        plot.setDimensions(new Dimension3D(10, 6, 10));
        renderer.setSize(0.08);
        renderer.setColors(new Color(255, 128, 128), new Color(0, 255, 0));
//        LogAxis3D yAxis = new LogAxis3D("Y (log scale)");
//        yAxis.setTickLabelOrientation(LabelOrientation.PERPENDICULAR);
//        yAxis.receive(new ChartStyler(chart.getStyle()));
//        plot.setYAxis(yAxis);
        chart.setViewPoint(ViewPoint3D.createAboveLeftViewPoint(40));
        
        return chart;
    }

    /**
     * Creates a sample dataset (hard-coded for the purpose of keeping the
     * demo self-contained - in practice you would normally read your data
     * from a file, database or other source).
     * 
     * @return A dataset.
     */
    public XYZDataset createDataset() {
        XYZSeries s1 = new XYZSeries("Non-dominated Solution");
        float x, y, z;
        for (int i = 0; i < paretoFront.members.size(); i++) {
        	x = (float)paretoFront.members.get(i).getExpectedCost();
			y = (float)paretoFront.members.get(i).getExpectedNPV();
			z = (float)paretoFront.members.get(i).getInvestmentRisk();
            s1.add(x,y,z);
        }
        XYZSeries s2 = new XYZSeries("Dominated Solution");
        for (int i = 0; i < dominated.size(); i++) {
        	x = (float)dominated.get(i).getExpectedCost();
			y = (float)dominated.get(i).getExpectedNPV();
			z = (float)dominated.get(i).getInvestmentRisk();
            s2.add(x,y,z);
        }
        XYZSeriesCollection dataset = new XYZSeriesCollection();
        dataset.add(s2);
        dataset.add(s1);
        return dataset;
    }
    
    public static void main(String[] args){
//    	Plot3D scatter = new Plot3D();
//    	XYZDataset data = scatter.createDataset();
//    	Chart3D chart = scatter.createChart(data);
//    	Chart3DPanel panel = new Chart3DPanel(chart);
//    	panel.setPreferredSize(new java.awt.Dimension(700, 400));
//    	scatter.add(panel);
//    	scatter.pack();
//    	scatter.setLocationRelativeTo(null);
//    	scatter.setVisible(true);
    	
    	
    }
       
}