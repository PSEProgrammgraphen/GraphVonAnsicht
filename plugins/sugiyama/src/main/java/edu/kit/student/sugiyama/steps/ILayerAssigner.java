package edu.kit.student.sugiyama.steps;

import edu.kit.student.sugiyama.AbsoluteLayerConstraint;
import edu.kit.student.sugiyama.LayerContainsOnlyConstraint;
import edu.kit.student.sugiyama.RelativeLayerConstraint;
import edu.kit.student.sugiyama.graph.ILayerAssignerGraph;

import java.util.Set;

/**
 * This interface represents a class that takes a directed graph and assigns every vertex in it a layer.
 */
public interface ILayerAssigner {

	/**
	 * Assigns every vertex in the graph parameter e relative height.
	 * @param graph input graph 
	 */ 
	public void assignLayers(ILayerAssignerGraph graph);
	
	/**
	 * Defines a set of constraints which should be considered by the algorithm.
	 * @param constraints relative layer constraints the algorithm should consider
	 */
	public void addRelativeConstraints(Set<RelativeLayerConstraint> constraints);
	
	/**
	 * Defines a set of constraints which should be considered by the algorithm
	 * @param constraints absolute layer constraints the algorithm should consider
	 */
	public void addAbsoluteConstraints(Set<AbsoluteLayerConstraint> constraints);
	
	/**
	 * Reassigns the layer of vertices whose layer is greater than the height parameter.
	 * @param height maximum height for vertices
	 */
	public void setMaxHeight(int height);
	
	/**
	 * Reassigns the layer of vertices in case there are more than the width parameter in one layer.
	 * @param width maximum amount of vertices in one layer
	 */
	public void setMaxWidth(int width);

	void addLayerContainsOnlyConstraints(Set<LayerContainsOnlyConstraint> constraints);
}
