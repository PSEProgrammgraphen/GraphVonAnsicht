package edu.kit.student.graphmodel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.LayoutOption;

import java.util.Set;

/**
 * A {@link DefaultDirectedGraph} is a specific Graph which only contains
 * {@link DirectedEdge} as edges.
 */
public class DefaultDirectedGraph<V extends Vertex, E extends DirectedEdge<V>> implements DirectedGraph<V, E>, ViewableGraph<V, E> {

	private DirectedGraphLayoutRegister register;
	private GAnsProperty<String> name;
	private GAnsProperty<Integer> id;
	private FastGraphAccessor fga;
	private Set<V> vertexSet;
	private Set<E> edgeSet;

	
	/**
	 * Constructor
	 * 
	 * @param name
	 *     The name of the new graph
	 * @param id
	 *     The id of the new graph
	 */
	public DefaultDirectedGraph(String name, Integer id) {
	    //create Sets
	    this.vertexSet = new HashSet<V>();
	    this.edgeSet = new HashSet<E>();
	    this.name = new GAnsProperty<String>("graphName", name);
	    this.id = new GAnsProperty<Integer>("graphID", id);
	}
	
    @Override
    public String getName() {
        return name.getValue();
    }

    @Override
    public Integer getID() {
        return id.getValue();
    }
    
    /**
     * Adds an edge to the edgeSet
     * @param edge
     */
    public void addEdge(E edge) {
        this.edgeSet.add(edge);
    }
	
    /**
     * Adds an vertex to the vertexSet
     * @param vertex
     */
    public void addVertex(V vertex) {
        this.vertexSet.add(vertex);
    }
    
	/**
	 * Returns the source of a edge of the graph.
	 * 
	 * @param edge
	 *            A edge which is contained in the graph.
	 * @return The vertex which the edge is coming from.
	 */
	public V getSource(E edge) {
	    //TODO: is this method necessary? because caller already has the edge
	    if(this.edgeSet.contains(edge)){
	        return edge.getSource();
	    }
		return null;
	}
	

    @Override
    public Set<V> getVertexSet() {
        return this.vertexSet;
    }

    @Override
    public Set<E> getEdgeSet() {
        return this.edgeSet;
    }
	

	@Override
	public List<LayoutOption> getRegisteredLayouts() {
		return register.getLayoutOptions();
	}


	@Override
	public Integer outdegreeOf(V vertex) {
		Integer outdegree = 0;
		Iterator<E> itr = this.edgeSet.iterator();
		while(itr.hasNext()) {
		    if(itr.next().getSource() == vertex) {
		        outdegree++;
		    }
		}
		
		return outdegree;
	}

	@Override
	public Integer indegreeOf(V vertex) {
	      Integer indegree = 0;
	      Iterator<E> itr = this.edgeSet.iterator();
	      while(itr.hasNext()) {
	          if(itr.next().getTarget() == vertex) {
	              indegree++;
	          }
	      }
	      
	      return indegree;
	}

	@Override
	public Set<E> outgoingEdgesOf(V vertex) {
	      Set<E> outgoing = new HashSet<E>();
	      Iterator<E> itr = this.edgeSet.iterator();
	      while(itr.hasNext()) {
	          E next = itr.next();
	          if(next.getSource() == vertex) {
	              outgoing.add(next);
	          }
	      }
	        
	      return outgoing;
	}

	@Override
	public Set<E> incomingEdgesOf(V vertex) {
        Set<E> ingoing = new HashSet<E>();
        Iterator<E> itr = this.edgeSet.iterator();
        while(itr.hasNext()) {
            E next = itr.next();
            if(next.getTarget() == vertex) {
                ingoing.add(next);
            }
        }
          
        return ingoing;
	}


	@Override
	public Set<E> edgesOf(V vertex) {
		Set<E> result = this.incomingEdgesOf(vertex);
		result.addAll(this.outgoingEdgesOf(vertex));
        return result;
	}

	@Override
	public FastGraphAccessor getFastGraphAccessor() {
		return fga;
	}

	@Override
	public void addToFastGraphAccessor(FastGraphAccessor fga) {
		// TODO Auto-generated method stub

	}

	@Override
	public SerializedGraph serialize(List<Entry<String, String>> attributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompoundVertex collapse(Set<V> subset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<V> expand(CompoundVertex vertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCompound(Vertex vertex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LayoutOption getDefaultLayout() {
		// TODO Auto-generated method stub
		return null;
	}
}