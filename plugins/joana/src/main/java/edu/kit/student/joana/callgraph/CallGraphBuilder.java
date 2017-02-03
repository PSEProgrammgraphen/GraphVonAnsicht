package edu.kit.student.joana.callgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.student.graphmodel.builder.GraphBuilderException;
import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IVertexBuilder;
import edu.kit.student.joana.CallGraphVertex;
import edu.kit.student.joana.InterproceduralVertex;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaEdge.EdgeKind;
import edu.kit.student.joana.JoanaEdgeBuilder;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.joana.methodgraph.MethodGraphBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The CallGraphBuilder implements an {@link IGraphBuilder} and builds 
 * one {@link CallGraph}.
 */
public class CallGraphBuilder implements IGraphBuilder {

    private final Logger logger = LoggerFactory.getLogger(CallGraphBuilder.class);

    private Map<String, String> data = new HashMap<>();
    private Set<MethodGraphBuilder> methodGraphBuilders = new HashSet<>();
    private Set<MethodGraph> methodGraphs = new HashSet<>();
    private Set<JoanaEdgeBuilder> callEdgeBuilders = new HashSet<>();
    private String name;
    
    public CallGraphBuilder(String name) {
        this.name = name;
    }
    
    @Override
    public IEdgeBuilder getEdgeBuilder(String sourceId, String targetId) {
        for (MethodGraphBuilder builder : methodGraphBuilders) {
            if (builder.containsVertexWithId(sourceId) && builder.containsVertexWithId(targetId)) {
                return builder.getEdgeBuilder(sourceId, targetId);
            }
        }
        // Found edge between two method graphs
        JoanaEdgeBuilder eBuilder = new JoanaEdgeBuilder(sourceId, targetId);
        callEdgeBuilders.add(eBuilder);
        return eBuilder;
    }

    @Override
    public IVertexBuilder getVertexBuilder(String vertexId) {
        //TODO: throw exception
        return null;
    }

    @Override
    public IGraphBuilder getGraphBuilder(String graphId) {
        MethodGraphBuilder builder = new MethodGraphBuilder(graphId);
        methodGraphBuilders.add(builder);
        return builder;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public void addData(String keyname, String value) throws IllegalArgumentException {
        this.data.put(keyname, value);
    }

    /**
     * Builds a new {@link CallGraph} with the given information, added before this call.
     * @return the {@link CallGraph}
     * @throws GraphBuilderException if the {@link CallGraph} could not be build.
     */
    public CallGraph build() throws GraphBuilderException {
        HashMap<String, JoanaVertex> vertexPool = new HashMap<>();

        long startTime = System.currentTimeMillis();

        for (MethodGraphBuilder b : methodGraphBuilders) {
            methodGraphs.add(b.build());
            vertexPool.putAll(b.getVertexPool());
        }
        long stopTime = System.currentTimeMillis();
        logger.info("Building MethodGraphs took " + (stopTime - startTime));
        startTime = stopTime;

        HashMap<Integer, CallGraphVertex> vertices = new HashMap<>();
        HashMap<CallGraphVertex, Set<CallGraphVertex>> connections = new HashMap<>();
        Set<JoanaEdge> edges = new HashSet<>();

        // Generate Callgraph
        // Generate CallGraphVertices for every method.
        for (MethodGraph methodGraph : methodGraphs) {
            vertices.put(methodGraph.getID(), new CallGraphVertex(methodGraph.getName(), methodGraph.getName(), methodGraph));
            connections.put(vertices.get(methodGraph.getID()), new HashSet<>());
        }
        stopTime = System.currentTimeMillis();
        logger.info("Building CallGraphVertices took " + (stopTime - startTime));
        startTime = stopTime;

        // Build the calledges in the method graphs.
        // This should be temporary. Better would be if they would really get built in the method graph builder.
        Set<JoanaEdge> callEdges = new HashSet<>();
        for (JoanaEdgeBuilder builder : callEdgeBuilders) {
            callEdges.add(builder.build(vertexPool));
        }

        stopTime = System.currentTimeMillis();
        logger.info("Building CallEdges in MethodGraphs took " + (stopTime - startTime));
        startTime = stopTime;

        //edges between two MethodGraphs. All interprocEdges can be found in callEdges, these are also edges not from the same MethodGraph
        //search in callEdges for interproceduralEdges, create a new InterproceduralVertex for both MethodGraphs the edge connects and add them to a set
        //later split this set of InterproceduralVertices and add every MethodGraph its own InterproceduralVertices from this set
        HashMap<Integer,Set<InterproceduralVertex>> mgIdToIVSet = new HashMap<>();
        for(JoanaEdge callEdge : callEdges){
        	boolean containsSource, containsTarget;
        	int mgGraphId;
        	for(MethodGraph mg : methodGraphs){
        		containsSource = mg.getVertexSet().contains(callEdge.getSource());
        		containsTarget = mg.getVertexSet().contains(callEdge.getTarget());
        		mgGraphId = mg.getID();
        		if(containsSource ^ containsTarget){
        			JoanaVertex normalVertex,dummy;
        			if(containsSource){
        				normalVertex = callEdge.getSource();
        				dummy = callEdge.getTarget();
        			}else{
        				normalVertex = callEdge.getTarget();
        				dummy = callEdge.getSource();
        			}
        			int dummyGraphId = -1; //should not stay at -1
        			String dummyGraphName = "";
        			//search for the graphid of the MethodGraph that contains the dummy vertex
        			for(MethodGraph mg2 : methodGraphs){
        				if(mg2.getVertexSet().contains(dummy)){
        					dummyGraphId = mg2.getID();
        					dummyGraphName = mg2.getName();
        					break;
        				}
        			}
        			assert(dummyGraphId != -1);
        			//add new InterproceduralVertex to mapping
        			if(!mgIdToIVSet.containsKey(mgGraphId)){
        				mgIdToIVSet.put(mgGraphId, new HashSet<>());
        			}
        			mgIdToIVSet.get(mgGraphId).add(new InterproceduralVertex(dummy.getName(), dummy.getLabel(),dummy,normalVertex,dummyGraphId,dummyGraphName,containsSource,callEdge.getEdgeKind()));
        		}
        	}
        }
        stopTime = System.currentTimeMillis();
        logger.info("Building enterprocedural edges took " + (stopTime - startTime));
        startTime = stopTime;
        //set the interprocedural vertices of a method graph
        for(MethodGraph mg : methodGraphs){
        	if(mgIdToIVSet.containsKey(mg.getID())){
        		mg.setInterprocVertices(mgIdToIVSet.get(mg.getID()));
        	}
        }
        stopTime = System.currentTimeMillis();
        logger.info("Setting interprocedural vertices took " + (stopTime - startTime));
        startTime = stopTime;

        //TODO: some checks if all mg's contain the right amount of IV's and the correct ones!
        
        //search for call loops
        //maybe there is a better/faster solution without searching through all edges
        for (MethodGraph methodGraph : methodGraphs) {
            for (JoanaEdge edge : methodGraph.getEdgeSet()) {
                if (edge.getEdgeKind() == JoanaEdge.EdgeKind.CL) {
                    callEdges.add(edge);
                }
            }
        }
        stopTime = System.currentTimeMillis();
        logger.info("Searching calloops vertices took " + (stopTime - startTime));
        startTime = stopTime;

        for (JoanaEdge callEdge : callEdges) {
            if (callEdge.getEdgeKind() != EdgeKind.CL)
                continue;
            int sourceID = 0;
            int targetID = 0;
            // Find which methodgraph contains the target and the source vertex for the callEdge
            for (MethodGraph methodGraph : methodGraphs) {
                if (methodGraph.getVertexSet().contains(callEdge.getSource())) {
                    sourceID = methodGraph.getID();
                }
                if (methodGraph.getVertexSet().contains(callEdge.getTarget())) {
                    targetID = methodGraph.getID();
                }
            }
            if (connections.get(vertices.get(sourceID)).contains(vertices.get(targetID))) {
                // Second call from this function. Skip.
                continue;
            }
            edges.add(new JoanaEdge(callEdge.getName(), callEdge.getLabel(), vertices.get(sourceID), vertices.get(targetID), EdgeKind.CL));
            connections.get(vertices.get(sourceID)).add(vertices.get(targetID));
        }
        stopTime = System.currentTimeMillis();
        logger.info("Building joanaedges for calledges took " + (stopTime - startTime));

        return new CallGraph(this.name, new HashSet<>(vertices.values()), edges);

    }
}
