package cs.ucl.moifm.util;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.tooltips.CoordinateTooltipRenderer;
import org.jzy3d.plot3d.rendering.tooltips.ITooltipRenderer;
import org.jzy3d.plot3d.rendering.view.AWTView;

import cs.ucl.moifm.model.Plan;

public class Plot extends AbstractAnalysis {
	private Front paretoFront;
	private List<Plan> dominated;
	
	public Plot(Front data, List<Plan> dom){
		this.paretoFront = data;
		this.dominated = dom;
	}
	
	public void init(){
		int size = paretoFront.members.size();
		int sizeN = dominated.size();
		float x, y, z;
		
		Coord3d[] points = new Coord3d[size];
		Coord3d[] domPoints = new Coord3d[sizeN];
		
		for (int i = 0; i < size; i++){
			x = (float)paretoFront.members.get(i).getExpectedCost();
			y = (float)paretoFront.members.get(i).getExpectedNPV();
			z = (float)paretoFront.members.get(i).getInvestmentRisk();
			points[i] = new Coord3d(x, y, z);
			
		}
		List<ITooltipRenderer> tooltips = new ArrayList<ITooltipRenderer>();
		for (int i = 0; i < sizeN; i++){
			x = (float)dominated.get(i).getExpectedCost();
			y = (float)dominated.get(i).getExpectedNPV();
			z = (float)dominated.get(i).getInvestmentRisk();
			domPoints[i] = new Coord3d(x, y, z);
			IntegerCoord2d screen = new IntegerCoord2d((int)x, (int)y);
			ITooltipRenderer r = new CoordinateTooltipRenderer("x", "y", "z", screen,domPoints[i],true);
//			
			tooltips.add(r);
			
		}
		Scatter scatter = new Scatter(points, Color.RED);
		
//		chart = AWTChartComponentFactory.chart(Quality.Advanced, getCanvasType());
//		chart.getScene().add(scatter);
		
//		AWTScatterMultiColor scatter = new AWTScatterMultiColor( points, new ColorMapper( new ColorMapRainbow(), -0.5f, 0.5f ) );
		Scatter scatter2 = new Scatter(domPoints,Color.BLACK);
        chart = AWTChartComponentFactory.chart(Quality.Nicest, getCanvasType());
        chart = AWTChartComponentFactory.chart(Quality.Nicest, getCanvasType());
        chart.getAxeLayout().setMainColor(Color.BLACK);
        chart.getAxeLayout().setXAxeLabel("Expected Investment Cost");
        chart.getAxeLayout().setYAxeLabel("Expected NPV");
        chart.getAxeLayout().setZAxeLabel("Investment Risk");
        chart.getView().setBackgroundColor(Color.WHITE);
        chart.getScene().add(scatter2);
        chart.getScene().add(scatter);
        ((AWTView)chart.getView()).setTooltips(tooltips);
        
        
        scatter2.setWidth(2);
        scatter.setWidth(2);
//        scatter.setLegend( new AWTColorbarLegend(scatter, chart.getView().getAxe().getLayout(), Color.WHITE, null) );
//        scatter.setLegendDisplayed(true);
        
	}

	/**
	 * @return the paretoFront
	 */
	public Front getParetoFront() {
		return paretoFront;
	}

	/**
	 * @param paretoFront the paretoFront to set
	 */
	public void setParetoFront(Front paretoFront) {
		this.paretoFront = paretoFront;
	}
}
