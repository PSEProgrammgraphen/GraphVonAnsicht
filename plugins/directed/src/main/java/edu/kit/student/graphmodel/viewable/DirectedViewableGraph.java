package edu.kit.student.graphmodel.viewable;

import java.util.*;

import edu.kit.student.graphmodel.*;
import edu.kit.student.graphmodel.action.EdgeAction;
import edu.kit.student.graphmodel.action.SubGraphAction;
import edu.kit.student.graphmodel.action.VertexAction;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedGraphLayoutOption;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.VertexFilter;
import edu.kit.student.util.IdGenerator;

/**
 * Represents a generic directed graph.
 * @author Lucas Steinmann
 */
public class DirectedViewableGraph implements DirectedGraph, ViewableGraph  {
	
	private final DefaultDirectedGraph<ViewableVertex, DirectedEdge> graph;
	public final String name;
	private List<DirectedViewableGraph> subGraphs;
	public final Integer id;
	
	public DirectedViewableGraph() {
		this(new DefaultDirectedGraph<>(), "DirectedGraph");
	}

	public DirectedViewableGraph(Set<ViewableVertex> vertices, Set<DirectedEdge> edges, String name) {
		this(new DefaultDirectedGraph<>(vertices, edges), name);
	}
	
	public DirectedViewableGraph(DefaultDirectedGraph<ViewableVertex, DirectedEdge> graph, String name) {
		this.graph = graph;
		this.name = name;
		this.id = IdGenerator.getInstance().createId();
	}

	@Override
	public Set<DirectedEdge> edgesOf(Vertex vertex) {
		return this.graph.edgesOf(vertex);
	}

	@Override
	public FastGraphAccessor getFastGraphAccessor() {
		return null;
	}

	@Override
	public void addToFastGraphAccessor(FastGraphAccessor fga) {
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Integer getID() {
		return this.id;
	}

	@Override
	public List<GAnsProperty<?>> getStatistics() {
		// TODO: implement
		return new LinkedList<>();
	}

	@Override
	public List<SubGraphAction> getSubGraphActions(Set<ViewableVertex> vertices) {
		return new LinkedList<>();
	}

	@Override
	public List<VertexAction> getVertexActions(Vertex vertex) {
		return new LinkedList<>();
	}

	@Override
	public List<EdgeAction> getEdgeActions(Edge edge) {
	    return new LinkedList<>();
	}

	@Override
	public void addVertexFilter(VertexFilter filter) {
	}

	@Override
	public void setVertexFilter(List<VertexFilter> filter) {
		
	}

	@Override
	public List<VertexFilter> getActiveVertexFilter() {
		return new LinkedList<>();
	}

	@Override
	public void addEdgeFilter(EdgeFilter filter) {
	}

	@Override
	public void setEdgeFilter(List<EdgeFilter> filter) {
	}

	@Override
	public List<EdgeFilter> getActiveEdgeFilter() {
		return new LinkedList<>();
	}

	@Override
	public void removeVertexFilter(VertexFilter filter) {
	}

	@Override
	public void removeEdgeFilter(EdgeFilter filter) {
	}

	@Override
	public Set<? extends InlineSubGraph> getInlineSubGraphs() {
		return new HashSet<>();
	}

	@Override
	public List<LayoutOption> getRegisteredLayouts() {
		return new LinkedList<>(Collections.singletonList(this.getDefaultLayout()));
	}

	@Override
	public LayoutOption getDefaultLayout() {
		if (!GenericGraphPlugin.directedGraphLayoutOptions.getLayoutOptions().isEmpty()) {
			DirectedGraphLayoutOption option = GenericGraphPlugin.directedGraphLayoutOptions.getLayoutOptions().get(0);
			option.setGraph(this);
			return option;
		} 
		return null;
	}

	@Override
	public Integer outdegreeOf(Vertex vertex) {
		return graph.outdegreeOf(vertex);
	}

	@Override
	public Integer indegreeOf(Vertex vertex) {
		return graph.indegreeOf(vertex);
	}

	@Override
	public Integer selfLoopNumberOf(Vertex vertex) {
		return graph.selfLoopNumberOf(vertex);
	}

	@Override
	public Set<? extends DirectedEdge> outgoingEdgesOf(Vertex vertex) {
		return graph.outgoingEdgesOf(vertex);
	}

	@Override
	public Set<? extends DirectedEdge> incomingEdgesOf(Vertex vertex) {
		return graph.incomingEdgesOf(vertex);
	}

	@Override
	public Set<? extends DirectedEdge> selfLoopsOf(Vertex vertex) {
		return graph.selfLoopsOf(vertex);
	}

	@Override
	public Set<? extends ViewableVertex> getVertexSet() {
		return graph.getVertexSet();
	}

	@Override
	public Set<? extends DirectedEdge> getEdgeSet() {
		return graph.getEdgeSet();
	}

	public String toString(){
		StringBuilder out = new StringBuilder("Vertices: {");
		for(Vertex v : this.getVertexSet()){
			out.append(v.getName()).append(", ");
		}
		out = new StringBuilder(out.substring(0, out.length() - 2));
		out.append("}");
		out.append('\n');
		out.append("Edges:{");
		out.append('\n');
		for(DirectedEdge e : this.getEdgeSet()){
			out.append(e.getName()).append("[").append(e.getSource().getName()).append("->").append(e.getTarget().getName()).append("] ");
			out.append(",");
			out.append('\n');
		}
		out = new StringBuilder(out.substring(0, out.length() - 2));
		return out + "}";
	}
}
