package Source;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.set.ListOrderedSet;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.EdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class ScannerGenerator {
	public static final char EPS = '\u03B5';// placeholder for graph edges char value when epsilon edge
	/**
	 * Generates DFATable from Specification File
	 * 
	 * @param specFile
	 * @return
	 * @throws Exception 
	 */
	public static DFATable generateDFA(String specFile) throws Exception {
//		FileInputStream in = new FileInputStream(specFile);
		System.out.println("Parsing specification file '"+specFile+"' to NFAs...");
		BufferedReader in = new BufferedReader( new FileReader(specFile) );
		
		// Read each line
		String line;
		// Parse Character Classes
		HashMap<String,HashSet<Character>> tokens = new HashMap<String,HashSet<Character>>();
		while( (line = in.readLine()) != null && !line.isEmpty() ) {
			if (isValidCharClass(line)) {
				System.out.println("Parsing Character Class: "+line);
				parseCharClass(line,tokens);
			}
		}
		for (Entry<String, HashSet<Character>> e : tokens.entrySet()) {
			System.out.println( e.getKey() + "("+e.getValue().size()+") : " + e.getValue().toString());
		}
		
		// For each line, generate an NFA
		// Parse Identifiers
		ArrayList<NFA> partialNFAs = new ArrayList<NFA>();
		
		while( (line = in.readLine()) != null && !line.isEmpty() ) {
			System.out.println("Parsing Identifier: "+line);
			partialNFAs.add( parseIdentifier(line,tokens) );
		}

		

		in.close();
		System.out.println("Finished parsing specification file.");
		
		// Merge NFA's into BigNFA
		System.out.println("Merging NFAs...");
		NFA bigNFA = NFA.mergeNFAs(partialNFAs);
		
		DirectedSparseMultigraph<State, String> dgraph = generateGraph(bigNFA);
		drawGraph(dgraph, "BIGNFA", "BIGNFA for '"+specFile+"'");
		System.out.println("Done merging.");

		// Convert BigNFA
		System.out.println("Converting NFA to DFA...");
		// TODO :: Convert BigNFA to DFA
		System.out.println("Done converting.");
		return new DFATable(bigNFA);
	}
	
	/**
	 * Checks whether string is valid regex (defined by project)
	 * @param line
	 * @return
	 */
	private static boolean isValidCharClass(String line) {
		String k = line.replaceAll("\\\\ ", "<SPACE>"); // replace '\ ' with '<SPACE>' so split doesn't affect it
		if ( !k.matches("(\\$([\\w-]+) [^\\s]+ IN \\$\\w+$)|(\\$([\\w-]+) [^\\s]+$)") ) return false;
		int n=k.split(" ").length;
		if ( n == 2 || n == 4 ) return true;
		return false;
	}
	
	
	public static void parseCharClass(String line, HashMap<String, HashSet<Character>> tokens) throws ParseError {
		line.replaceAll("\\ ", "<SPACE>"); // replace '\ ' with '<SPACE>' so split doesn't affect it
		String[] chunks = line.split(" ");
		for (int i=0;i<chunks.length;++i) chunks[i] = chunks[i].replaceAll("<SPACE>", "\\ "); // replace spaceholder with '\ ' again
		
		String token = chunks[0];
		HashSet<Character> validChars = new HashSet<Character>();
		if ( tokens.containsKey(token) ) System.out.println("Token repeat Error!");
		else if (chunks.length == 2) {
			// Simple X Y
			String data = chunks[1];
			parseRule(data, validChars);
			tokens.put(token, validChars);
		}
		else if (chunks.length == 4 && chunks[2].equals("IN") ) {
			// $X Y IN $Z
			String data = chunks[1];
			validChars = new HashSet<Character>( tokens.get( chunks[3] ) );
			parseNotRule(data,validChars);
			
		}
		else {
			System.out.println("Wierd Chunks (n="+chunks.length+")! : " + line);
			for (String s : chunks) System.out.println(":: "+ s);
			throw new ParseError("Bad Specificiation Line: "+line);
		}
		
		tokens.put(token, validChars);
	}
	private static void parseRule(String data, HashSet<Character> validChars) {		
		if (data.startsWith("[") && data.endsWith("]")) {
			data = data.substring(1,data.length()-1);
		}
		accumulateChars(data,validChars);
	}
	
	private static void parseNotRule(String data, HashSet<Character> validChars) {
		if (data.startsWith("[^") && data.endsWith("]")) {
			// Not
			data = data.substring(2,data.length()-1);
		}
		deccumulateChars(data, validChars);
	}
	
	private static void accumulateChars(String data, HashSet<Character> validChars) {
		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			if (c == '-') for (char j = (char) (data.charAt(i-1)+1); j < data.charAt(i+1); j++) validChars.add(j);
			else if (c == '\\') continue; 
			else validChars.add(c);
		}
	}
	private static void deccumulateChars(String data, HashSet<Character> validChars) {
		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			if (c == '-') for (char j = (char) (data.charAt(i-1)+1); j < data.charAt(i+1); j++) validChars.remove(j);
			else if (c == '\\') continue; 
			else validChars.remove(c);
			
		}
	}
	
	public static NFA parseIdentifier(String line, HashMap<String,HashSet<Character>> tokens) throws ParseError {
		line.replaceAll("\\ ", "<SPACE>"); // replace '\ ' with '<SPACE>' so split doesn't affect it
		String name = line.substring( 0, line.indexOf(' ') );;
		String val = line.substring(line.indexOf(' '), line.length()).replaceAll(" ", ""); // remove all spaces
		val = val.replaceAll("<SPACE>","\\ "); // replace '\ ' with '<SPACE>' so split doesn't affect it
//		System.out.println(name + "=" + val);
		
//		RecursiveParser rp = new RecursiveParser(val,tokens);
//		while (rp.peekToken() != null) {
//			System.out.println(rp.peekToken());
//			rp.matchAnyToken();
//		}
		
		System.out.println("  Trying to Recursively Parse '"+val+"' for NFA '"+name+"'...");
		RecursiveParser rp = new RecursiveParser(val,tokens);
		NFA partialNFA = rp.getNFA(name);
		DirectedSparseMultigraph<State, String> dgraph = generateGraph(partialNFA);
//		System.out.println(dgraph);
		drawGraph(dgraph, name, "Partial NFA '"+name+"', REGEX: "+val  );
		System.out.println("  Finished Recursive Parse. (Partial NFA image saved to 'Images/graph"+name+".png'");
		return partialNFA;
	}
	/**
	 * Draws graph to JFrame for partial NFA 'name' with title 'title'
	 * @param dgraph
	 * @param name
	 * @param title
	 */
	public static void drawGraph(DirectedSparseMultigraph<State, String> dgraph, String name, String title) {
		Layout<State, String> layout = new ISOMLayout<State, String>(dgraph);
		BasicVisualizationServer<State, String> viz = new BasicVisualizationServer<State, String>(layout);
		viz.setPreferredSize(new Dimension(1000,1000));
		
		// Vertex
		final Font vertexFont = new Font("Times New Roman", Font.BOLD, 20);
		Transformer<State, Font> vertexFontTransform = new Transformer<State, Font>() {
			@Override
			public Font transform(State arg0) {
				// TODO Auto-generated method stub
				return vertexFont;
			}	
		};
		viz.getRenderContext().setVertexFontTransformer(vertexFontTransform );
		
		final Font edgeFont = new Font(Font.MONOSPACED, Font.PLAIN , 20);
		Transformer<String, Font> edgeFontTransform = new Transformer<String, Font>() {
			@Override
			public Font transform(String s) {
				// TODO Auto-generated method stub
				return edgeFont;
			}
		};
		viz.getRenderContext().setEdgeFontTransformer(edgeFontTransform);
		
		Transformer<State,Shape> vertexSize = new Transformer<State,Shape>(){
            public Shape transform(State i){
            	Ellipse2D circle;
            	if (i.tokenName != null)
            		circle = new Ellipse2D.Double(-70, -30, 140, 60);
            	else
            		circle = new Ellipse2D.Double(-20, -20, 40, 40);
                // in this case, the vertex is twice as large
//                if(i == 2) return AffineTransform.getScaleInstance(2, 2).createTransformedShape(circle);
//                else return circle;
                return circle;
            }
        };
        viz.getRenderContext().setVertexShapeTransformer(vertexSize);
        
		
		viz.getRenderContext().setVertexLabelTransformer(new Transformer<State,String>() {
			public String transform(State s) {
				if (s.tokenName != null)
					return "S"+s.stateNum+":"+s.tokenName;
				return "S"+s.stateNum;
				} 
			});
		viz.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		Transformer<State,Paint> vertexPaint = new Transformer<State,Paint>() {
			public Paint transform(State s) {
				if (s.isStart) return Color.GREEN;
				return s.isFinal ? new Color(255, 120, 120) : Color.cyan; 
			}
		};
		viz.getRenderContext().setVertexFillPaintTransformer(vertexPaint); // Green state if final, blue otherwise
		
		// Edge
		viz.getRenderContext().setEdgeLabelTransformer(new Transformer<String,String>() {
				public String transform(String s) {
						return s.substring(0,s.indexOf('['));
					} 
			}); // Edge label
//		viz.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<State,String>()); // Straight line edges
//		viz.getRenderContext().setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(Color.blue, true)); // no rotate of edge labels
//		viz.getRenderContext().setLabelOffset(5);
//		drawImage(viz,title);
		saveImage(viz,name);
	}

	private static void drawImage(BasicVisualizationServer<State, String> viz,
			String title) {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(viz);
		frame.pack();
		frame.setVisible(true);
	}

	private static void saveImage(BasicVisualizationServer<State, String> viz,String name) {
		VisualizationImageServer<State, String> imageViz =
			    new VisualizationImageServer<State, String>(viz.getGraphLayout(),
			        viz.getGraphLayout().getSize());
		imageViz.setRenderContext(viz.getRenderContext());
		imageViz.setRenderer(viz.getRenderer());
		
		BufferedImage image = (BufferedImage) imageViz.getImage(
			    new Point2D.Double(viz.getGraphLayout().getSize().getWidth() / 2,
			    viz.getGraphLayout().getSize().getHeight() / 2),
			    new Dimension(viz.getGraphLayout().getSize()));
		
		File f = new File("Images");
		if (!f.exists()) f.mkdirs();
		
		File outputfile = new File("Images/graph"+name+".png");

		try {
		    ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
		    System.out.println("Could not write graph "+name+" to image.");
		}
	}

	/**
	 * Creates a visualizable Directed Sparse Multipgraph of the partial DFA
	 * @param partialNFA
	 * @return
	 */
	public static DirectedSparseMultigraph<State, String> generateGraph(
			NFA partialNFA) {
		DirectedSparseMultigraph<State, String> g = new DirectedSparseMultigraph<State, String>();
		Stack<State> v = new Stack<State>();
		ListOrderedSet<State> visited = new ListOrderedSet<State>();
		//HashSet()s
		v.add(partialNFA.entry);
		while (!v.isEmpty()) {
			State cState = v.pop();
			if (visited.contains(cState)) continue; // skip visited
			visited.add(cState);
			
			// Get new connected nodes
			ListOrderedSet<State> x = new ListOrderedSet<State>();
			x.addAll( cState.getCharEdges().values() );
			x.addAll( cState.getEpsEdges() );
			x.removeAll(visited); // disjoint
			System.out.println(cState + " : " + x );
			v.addAll( x ); // add new nodes
			
			HashMap<State,String> edges = new HashMap<State, String>(); // Merges Edges for some state pair together
			for ( Entry<Character, State> e : cState.getCharEdges().entrySet() ) {
				// if edges has state in already, set edges[state] = edges[state]+new_character
				State s = e.getValue();
				if (edges.containsKey(e.getValue()))
					edges.put(s,edges.remove(s) +e.getKey());
				else
					edges.put(s, Character.toString(e.getKey()));
			}
			for ( State other : cState.getEpsEdges() ) {
				// if edges has state in already, set edges[state] = edges[state]+new_character
				if ( edges.containsKey(other) )
					edges.put(other, edges.remove(other)+EPS);
				else
					edges.put(other, Character.toString(EPS) );
			}
			
			for ( Entry<State,String> e : edges.entrySet()) {
				String allChars = simplify(e.getValue());
				String edgeName = allChars+"[S"+cState.stateNum+"->S"+e.getKey().stateNum+"]";
				g.addEdge(edgeName, cState, e.getKey(), EdgeType.DIRECTED);
			}
//			for ( Entry<Character, State> e : cState.getCharEdges().entrySet() ) {
//				g.addEdge(e.getKey()+"("+cState.stateNum+"->"+e.getValue().stateNum+")", cState, e.getValue(), EdgeType.DIRECTED);
//			}
			
//			for ( State other : cState.getEpsEdges() ) { 
//				g.addEdge(EPS+"("+cState.stateNum+"->"+other.stateNum+")", cState, other, EdgeType.DIRECTED);
//			}
		}
		return g;
	}
	
	/**
	 * Cleans up a list of characters for an edge into something resembling sexy
	 * for example 'adbc\0' -> 'abcd<EPS>'
	 * and entire alphabet -> 'a-z' or 'A-Z'
	 * @param s
	 * @return
	 */
	private static String simplify(String s) {
		char[] chars = s.toCharArray();
		Arrays.sort(chars);
		s = new String(chars);
//		s = s.replaceAll(Character.toString(EPS), "<EPS>");
		s = s.replaceAll("abcdefghijklmnopqrstuvwxyz", "a-z");
		s = s.replaceAll("ABCDEFGHIJKLMNOPQRSTUVXYZ", "A-Z");
		s = s.replaceAll("0123456789", "0-9");
		return s;
	}
}
