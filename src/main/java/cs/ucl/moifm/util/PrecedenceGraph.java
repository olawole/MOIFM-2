/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
/* ----------------------
 * JGraphAdapterDemo.java
 * ----------------------
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 * 03-Aug-2003 : Initial revision (BN);
 * 07-Nov-2003 : Adaptation to JGraph 3.0 (BN);
 *
 */
package cs.ucl.moifm.util;

import java.awt.*;
import java.awt.geom.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import org.jgraph.*;
import org.jgraph.graph.*;
import org.jgrapht.*;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;
// resolve ambiguity
import org.jgrapht.graph.DefaultEdge;

import cs.ucl.moifm.model.MMF;


/**
 * A demo applet that shows how to use JGraph to visualize JGraphT graphs.
 *
 * @author Barak Naveh
 * @since Aug 3, 2003
 */
public class PrecedenceGraph
    extends JApplet
{
    private static final long serialVersionUID = 3256444702936019250L;
    private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
    private static final Dimension DEFAULT_SIZE = new Dimension(700, 400);
    private List<MMF> features;

    //
    private JGraphModelAdapter<String, DefaultEdge> jgAdapter;

    public PrecedenceGraph(List<MMF> mmfs){
    	features = mmfs;
    }
    
    /**
     * An alternative starting point for this demo, to also allow running this
     * applet as an application.
     *
     * @param args ignored.
     */
    public static void main(String [] args)
    {
//        PrecedenceGraph applet = new PrecedenceGraph();
//        applet.init();
//
//        JFrame frame = new JFrame();
//        frame.getContentPane().add(applet);
//        frame.setTitle("Precedence graph for the project");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
    }

    /**
     * {@inheritDoc}
     */
    public void init()
    {
        // create a JGraphT graph
        ListenableGraph<String, DefaultEdge> g =
            new ListenableDirectedMultigraph<String, DefaultEdge>(
                DefaultEdge.class);

        // create a visualization using JGraph, via an adapter
        jgAdapter = new JGraphModelAdapter<String, DefaultEdge>(g);

        JGraph jgraph = new JGraph(jgAdapter);
        
        adjustDisplaySettings(jgraph);
        getContentPane().add(jgraph);
        resize(DEFAULT_SIZE);
        HashMap<String, String> mapping = new HashMap<String, String>();
        for (MMF mmf : features){
        	String value = "";
        	for (MMF prec : mmf.getPrecursors()){
        		if (value == ""){
        			value += prec.getId();
        		}
        		else
        			value += "," + prec.getId();
        	}
        	mapping.put(mmf.getId(), value);
        }
        String[] vertices = new String[features.size()];
        int i = 0;
        for (String entry : mapping.keySet()){
        	vertices[i] = entry;
        	g.addVertex(vertices[i]);
        	i++;
        }
        for (i = 0; i < vertices.length; i++){
        	if (mapping.get(vertices[i]) != ""){
        		for (String s : mapping.get(vertices[i]).split(",")){
        			g.addEdge(vertices[i], s);
        		}
        		
        	}
        }
        
        for (i = 0; i < vertices.length; i++){
        	positionVertexAt(vertices[i], (int)(Math.random() * DEFAULT_SIZE.width), (int)(Math.random() * DEFAULT_SIZE.height));
        }
    }

    private void adjustDisplaySettings(JGraph jg)
    {
        jg.setPreferredSize(DEFAULT_SIZE);

        Color c = DEFAULT_BG_COLOR;
        String colorStr = null;

        try {
            colorStr = getParameter("bgcolor");
        } catch (Exception e) {
        }

        if (colorStr != null) {
            c = Color.decode(colorStr);
        }

        jg.setBackground(c);
    }

    @SuppressWarnings("unchecked") // FIXME hb 28-nov-05: See FIXME below
    private void positionVertexAt(Object vertex, int x, int y)
    {
        DefaultGraphCell cell = jgAdapter.getVertexCell(vertex);
        AttributeMap attr = cell.getAttributes();
        
        Rectangle2D bounds = GraphConstants.getBounds(attr);

        Rectangle2D newBounds =
            new Rectangle2D.Double(
                x,
                y,
                bounds.getWidth(),
                bounds.getHeight());

        GraphConstants.setBounds(attr, newBounds);

        // TODO: Clean up generics once JGraph goes generic
        AttributeMap cellAttr = new AttributeMap();
        cellAttr.put(cell, attr);
        jgAdapter.edit(cellAttr, null, null, null);
    }

    /**
     * a listenable directed multigraph that allows loops and parallel edges.
     */
    private static class ListenableDirectedMultigraph<V, E>
        extends DefaultListenableGraph<V, E>
        implements DirectedGraph<V, E>
    {
        private static final long serialVersionUID = 1L;

        ListenableDirectedMultigraph(Class<E> edgeClass)
        {
            super(new DirectedMultigraph<V, E>(edgeClass));
        }
    }
}

// End JGraphAdapterDemo.java
