package edu.kit.student.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Graph;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.serialize.SerializedEdge;
import edu.kit.student.graphmodel.serialize.SerializedGraph;
import edu.kit.student.graphmodel.serialize.SerializedVertex;
import edu.kit.student.objectproperty.GAnsProperty;
import javafx.geometry.Bounds;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.util.Pair;

/**
 * The GraphViewGraphFactory generates the visual representation of a given
 * {@link Graph} and gives access to the set {@link Graph}.
 * 
 * @author Nicolas
 */
public class GraphViewGraphFactory {

	private Graph<Vertex,Edge<Vertex>> graph;
	private Map<VertexShape, Vertex> vertices;
	private Map<EdgeShape, Edge<Vertex>> edges;

	/**
	 * Constructor. Sets the graph and generates the vertices and edges for
	 * visualization.
	 * 
	 * @param graph
	 *            The graph data that will be shown.
	 */
	public GraphViewGraphFactory(Graph<Vertex,Edge<Vertex>> graph) {
		vertices = new HashMap<VertexShape, Vertex>();
		edges = new HashMap<EdgeShape, Edge<Vertex>>();
		this.graph = graph;
		
		createVertices();
		createEdges();
	}
	
	public Graph<Vertex,Edge<Vertex>> getGraph() {
		return this.graph;
	}

	/**
	 * Returns all graphical elements that have been generated by the factory.
	 * 
	 * @return All graphical elements generated by the factory.
	 */
	public List<GAnsGraphElement> getGraphicalElements() {
		List<GAnsGraphElement> elements = new LinkedList<GAnsGraphElement>();
		elements.addAll(vertices.keySet());
		elements.addAll(edges.keySet());
		
		return elements;
	}
	
	public Set<VertexShape> getVertexShapes() {
		return vertices.keySet();
	}

	/**
	 * Returns the vertex element from the graph model that is being represented
	 * by the shape. Can be null if an {@link EdgeShape} is passed.
	 * 
	 * @param shape
	 *            The shape that represents the vertex.
	 * @return The Vertex being represented by the passed shape.
	 */
	public Vertex getVertexFromShape(GAnsGraphElement shape) {
		return vertices.get(shape);
	}

	/**
	 * Returns the edge element from the graph model that is being represented
	 * by the shape. Can be null if an {@link VertexShape} is passed.
	 * 
	 * @param shape
	 *            The shape that represents the edge.
	 * @return The Edge being represented by the passed shape.
	 */
	public Edge<?> getEdgeFromShape(GAnsGraphElement shape) {
		return edges.get(shape);
	}
	
	/**
	 * Calculates and returns the size of a vertex with the given text.
	 * 
	 * @param text The text which size the vertex depends on.
	 * @return A Pair of width and height of the vertex.
	 */
	public static Pair<Double, Double> getSizeOfVertex(String text) {
		VertexShape shape = new VertexShape();
		shape.setText(text);
		Pair<Double, Double> pair = new Pair<Double, Double>(shape.getWidth(), shape.getHeight());
		return pair;
	}
	
	private void createVertices() {
		Set<Vertex> set = graph.getVertexSet();
		for(Vertex vertex : set) {
			VertexShape shape = new VertexShape(vertex);
			vertices.put(shape, vertex);
		}
	}
	
	private void createEdges() {
		Set<Edge<Vertex>> set = graph.getEdgeSet();
		for(Edge<Vertex> edge : set) {
			EdgeShape shape = new EdgeShape(edge);
			edges.put(shape, edge);
		}
	}
	
	public SerializedGraph serializeGraph() {
		return new SerializedGraph(new HashMap<String, String>(), new HashMap<String, String>(),
				serializeVertices(), serializeEdges());
	}
	
	private Set<SerializedVertex> serializeVertices() {
		Set<SerializedVertex> set = new HashSet<SerializedVertex>();
		for(VertexShape shape : vertices.keySet()) {
			Map<String,String> shapeProperties = new HashMap<String,String>();
			Bounds bounds = shape.getBoundsInParent();
			shapeProperties.put("label", shape.getText());
			shapeProperties.put("minX", Double.toString(bounds.getMinX()));
			shapeProperties.put("minY", Double.toString(bounds.getMinY()));
			shapeProperties.put("maxX", Double.toString(bounds.getMaxX()));
			shapeProperties.put("maxY", Double.toString(bounds.getMaxY()));
			shapeProperties.put("arcWidth", Double.toString(shape.getElementShape().getArcWidth()));
			shapeProperties.put("arcHeight", Double.toString(shape.getElementShape().getArcHeight()));
			
			Map<String,String> metaProperties = new HashMap<String,String>();
			Vertex vertex = vertices.get(shape);
			for(GAnsProperty<?> property : vertex.getProperties()) {
				metaProperties.put(property.getName(), property.getValueAsString());
			}
			SerializedVertex serialized = new SerializedVertex(shapeProperties, metaProperties);
			set.add(serialized);
		}
		
		return set;
	}
	
	private Set<SerializedEdge> serializeEdges() {
		Set<SerializedEdge> set = new HashSet<SerializedEdge>();
		for(EdgeShape shape : edges.keySet()) {
			Map<String,String> shapeProperties = new HashMap<String,String>();
			Path path = shape.getElementShape();
			shapeProperties.put("label", shape.getText());
			for(int i = 0; i < path.getElements().size(); i++) {
				PathElement element = path.getElements().get(i);
				if(LineTo.class.equals(element.getClass())) {
					LineTo line = (LineTo)element;
					shapeProperties.put(i + "x", Double.toString(line.getX()));
					shapeProperties.put(i + "y", Double.toString(line.getY()));
				} else {
					//TODO: throw exception, only straight lines allowed in path!
				}
			}
			
			Map<String,String> metaProperties = new HashMap<String,String>();
			Edge<?> edge = edges.get(shape);
			for(GAnsProperty<?> property : edge.getProperties()) {
				metaProperties.put(property.getName(), property.getValueAsString());
			}
			SerializedEdge serialized = new SerializedEdge(shapeProperties, metaProperties);
			set.add(serialized);
		}
		
		return set;
	}
}
