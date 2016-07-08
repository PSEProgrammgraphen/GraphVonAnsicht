package edu.kit.student.gui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Graph;
import edu.kit.student.graphmodel.GraphModel;
import edu.kit.student.graphmodel.Vertex;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * The StructureView regulates the access and representation of the elements in
 * the StructureView of GAns.
 * 
 * @author Nicolas
 */
public class StructureView extends TreeView<String> {

	private HashMap<TreeItem<String>, Integer> itemMap;

	/**
	 * Constructor.
	 */
	public StructureView() {
		itemMap = new HashMap<TreeItem<String>, Integer>();
		setShowRoot(false);
	}

	/**
	 * Creates a tree like representation from a given graph and its subgraphs.
	 * Should be called, before calling other methods, because there could be a
	 * dummy root-node in the View.
	 * 
	 * @param graph
	 *            The graph which should be represented.
	 */
	public void showGraphModel(GraphModel graphModel) {
		TreeItem<String> root = new TreeItem<String>();
		addGraphsToItem(graphModel.getRootGraphs(), root);
		setRoot(root);
	}
	
	private void addGraphsToItem(List<Graph<? extends Vertex, ? extends Edge<? extends Vertex>>> graphs, TreeItem<String> item) {
		List<TreeItem<String>> items = new LinkedList<TreeItem<String>>();
		for(Graph<? extends Vertex, ? extends Edge<? extends Vertex>> graph : graphs) {
			TreeItem<String> graphItem = new TreeItem<String>(graph.getName());
			itemMap.put(graphItem, graph.getID());
			items.add(graphItem);
			addGraphsToItem(graph.getChildGraphs(), graphItem);
		}
		item.getChildren().addAll(items);
	}
	
	/**
	 * Returns the id of the selected graph.
	 * @return The id of the selected graph.
	 */
	public Integer getIdOfSelectedItem() {
		return itemMap.get(getSelectionModel().getSelectedItem());
	}
}
