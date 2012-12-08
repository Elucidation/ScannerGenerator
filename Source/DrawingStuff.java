package Source;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class DrawingStuff {
	static int i = 0;
	/**
	 * Given an Abstract Syntax Tree
	 * It generates a visual representation and saves it to filename
	 * @param ast
	 */
	public static void drawAST(Node ast, String filename) {
		DelegateTree<Node, Integer> tree = new DelegateTree<Node,Integer>(new DirectedOrderedSparseMultigraph<Node, Integer>());
		tree.setRoot(ast);
		buildGraph(tree,ast);
		TreeLayout<Node, Integer> layout = new TreeLayout<Node, Integer>(tree,50);
		
		 VisualizationViewer<Node, Integer> vv = new VisualizationViewer<Node,Integer>(layout, new Dimension(600,600));
		 
		 setVisuals(vv);
		 
		 saveImage(vv,filename);
	}
	
	private static void setVisuals(VisualizationViewer<Node, Integer> vv) {
		// Background color white
		vv.setBackground(Color.white);
		// Straight edge lines
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
		// How the vertex label is made
		vv.getRenderContext().setVertexLabelTransformer(new Transformer<Node,String>() {
			public String transform(Node node) {
				if (node.data == null)
					return node.name;
				else {
					return node.name + "=" + node.data;
				}
			}});
		
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		vv.getRenderContext().setVertexShapeTransformer(new Transformer<Node,Shape>(){
            public Shape transform(Node node){
            	int pl = (node.name.length())*7; 
            	if (node.data != null)
            		pl += (node.data.toString().length() + 1)*6;
            	return new Rectangle2D.Double(-pl/2, -10, pl, 20);
            	
            }
        });
		
		// Vertex Font
		final Font vertexFont = new Font(Font.SERIF, Font.PLAIN , 10);
		Transformer<Node, Font> vertexFontTransformer = new Transformer<Node, Font>() {
			@Override
			public Font transform(Node node) {
				// TODO Auto-generated method stub
				return vertexFont;
			}
		};
		vv.getRenderContext().setVertexFontTransformer(vertexFontTransformer);
		
		// Vertex Color
		Transformer<Node,Paint> vertexPaint = new Transformer<Node,Paint>() {
			public Paint transform(Node n) {
				if (n.name.equals("MINIRE"))
					return new Color(250, 230, 250); // light red
				else if (n.children.size() == 0)
					return new Color(230, 250, 230); // light green
				else
					return new Color(230, 230, 250); // light blue
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		
	}

	private static void buildGraph(DelegateTree<Node, Integer> tree,
			Node node) {
//		System.out.println("VERTEX:"+node.name);
		for (Node child : node.children) {
			if (child != null) {
//				System.out.println("CHILD:"+child.name);
				tree.addChild(i++, node, child);
				buildGraph(tree, child); // Recurse
			} else {
				tree.addChild(i++, node, new Node("<epsilon>"));
			}
		}
	}

	private static void saveImage(VisualizationViewer<Node, Integer> viz, String filename) {
		VisualizationImageServer<Node, Integer> imageViz =
			    new VisualizationImageServer<Node, Integer>(viz.getGraphLayout(),
			        viz.getGraphLayout().getSize());
		imageViz.setRenderContext(viz.getRenderContext());
		imageViz.setRenderer(viz.getRenderer());
		
		BufferedImage image = (BufferedImage) imageViz.getImage(
			    new Point2D.Double(viz.getGraphLayout().getSize().getWidth() / 2,
			    viz.getGraphLayout().getSize().getHeight() / 2),
			    new Dimension(viz.getGraphLayout().getSize()));
		
		File f = new File("Images");
		if (!f.exists()) f.mkdirs();
		
		File outputfile = new File(filename);

		try {
		    ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
		    System.out.println("Could not write graph "+filename+" to image.");
		}
	}

}
