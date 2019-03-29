package fr.mrcraftcod.tp.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static fr.mrcraftcod.tp.model.EdgeMode.UNDIRECTED;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-03-22.
 *
 * @author Thomas Couchoud
 * @since 2019-03-22
 */
public class Graph{
	private final int ID;
	private final ArrayList<Node> nodes;
	private final ArrayList<Edge> edges;
	private EdgeMode edgeMode;
	private Path sourcePath;
	
	public Graph(int id){
		this.ID = id;
		this.edgeMode = UNDIRECTED;
		this.nodes = new ArrayList<>();
		this.edges = new ArrayList<>();
	}
	
	public void addNode(Node node){
		this.nodes.add(node);
	}
	
	public void addEdge(Edge edge){
		this.edges.add(edge);
	}
	
	@Override
	public String toString(){
		return new ToStringBuilder(this).append("ID", ID).append("sourcePath", sourcePath).append("nodes", nodes).append("edges", edges).append("edgeMode", edgeMode).toString();
	}
	
	public Optional<Node> getNodeByID(int id){
		return this.getNodes().stream().filter(n -> Objects.equals(n.getID(), id)).findAny();
	}
	
	public void addEdgeEditionCost(Pair<Double, int[][]> result, Graph graph){
		for(final var edge : this.getEdges()){
			var index1 = this.getNodeIndex(edge.getFrom()).orElseThrow();
			var index2 = this.getNodeIndex(edge.getTo()).orElseThrow();
			
			if(result.getRight()[index1][index2] == 1){
			
			}
		}
	}
	
	private Optional<Integer> getNodeIndex(int id){
		return IntStream.range(0, this.getNodeCount()).mapToObj(i -> ImmutablePair.of(i, this.getNodeAt(i))).filter(n -> n.getRight().isPresent()).filter(n -> Objects.equals(n.getRight().get().getID(), id)).map(ImmutablePair::getLeft).findAny();
	}
	
	public String getInstanceName(){
		final var fileName = this.getSourcePath().getFileName().toString();
		return fileName.substring(0, fileName.lastIndexOf("."));
	}
	
	public int getID(){
		return this.ID;
	}
	
	public Path getSourcePath(){
		return sourcePath;
	}
	
	public void setSourcePath(Path path){
		this.sourcePath = path;
	}
	
	public ArrayList<Node> getNodes(){
		return this.nodes;
	}
	
	public Optional<Edge> getEdge(int from, int to){
		return this.getEdges().stream().filter(e -> (Objects.equals(e.getFrom(), from) || Objects.equals(e.getTo(), to)) || (Objects.equals(this.getEdgeMode(), UNDIRECTED) && (Objects.equals(e.getFrom(), to) || Objects.equals(e.getTo(), from)))).findAny();
	}
	
	public ArrayList<Edge> getEdges(){
		return this.edges;
	}
	
	private EdgeMode getEdgeMode(){
		return this.edgeMode;
	}
	
	public void setEdgeMode(EdgeMode edgemode){
		this.edgeMode = edgemode;
	}
	
	public Optional<Edge> getEdgeAt(int pos){
		if(pos < 0 || pos >= this.getEdgeCount()){
			return Optional.empty();
		}
		return Optional.of(this.getEdges().get(pos));
	}
	
	private int getEdgeCount(){
		return this.getEdges().size();
	}
	
	public Matrix2D getEdgeCostMatrix(final Graph graph){
		final var edgeCost = 1D; //This is a predefined param
		final var alpha = 0.5D;
		
		final var matrix = new Matrix2D(this.getEdgeCount() + 1, graph.getEdgeCount() + 1);
		
		for(int i = 0; i < this.getEdgeCount() + 1; i++){
			for(int j = 0; j < graph.getEdgeCount() + 1; j++){
				var cost = 0D;
				
				if(!Objects.equals(i, this.getEdgeCount()) || !Objects.equals(j, graph.getEdgeCount())){
					if(Objects.equals(i, this.getEdgeCount()) || Objects.equals(j, graph.getEdgeCount())){
						cost = (1 - alpha) * edgeCost;
					}
				}
				
				matrix.put(i, j, cost);
			}
		}
		
		return matrix;
	}
	
	public Matrix2D getNodeCostMatrix(final Graph graph){
		final var nodeCost = 100000D; //This is a predefined param
		final var alpha = 0.5D;
		final var matrix = new Matrix2D(this.getNodeCount() + 1, graph.getNodeCount() + 1);
		
		for(int i = 0; i < this.getNodeCount() + 1; i++){
			for(int j = 0; j < graph.getNodeCount() + 1; j++){
				double cost = 0D;
				
				if(!Objects.equals(i, this.getNodeCount()) || !Objects.equals(j, graph.getNodeCount())){
					if(Objects.equals(i, this.getNodeCount()) || Objects.equals(j, graph.getNodeCount())){
						cost = alpha * nodeCost;
					}
					else{
						final var ni = this.getNodeAt(i).orElseThrow(IndexOutOfBoundsException::new);
						final var nj = graph.getNodeAt(j).orElseThrow(IndexOutOfBoundsException::new);
						
						cost = alpha * ni.getAttributes().entrySet().stream().filter(e -> !Objects.equals("x", e.getKey()) && !Objects.equals("y", e.getKey())).mapToDouble(e -> nj.getAttr(e.getKey()).map(v -> computeDistance(v, e.getValue())).orElse(0D)).sum();
					}
				}
				matrix.put(i, j, cost);
			}
		}
		
		return matrix;
	}
	
	public Matrix2D getBipartiteCostMatrix(final Graph graph){
		final var matrix = new Matrix2D(this.getNodeCount() + graph.getNodeCount(), this.getNodeCount() + graph.getNodeCount());
		final var nodeCosts = this.getNodeCostMatrix(graph);
		final var edgeCosts = this.getEdgeCostMatrix(graph);
		
		for(int i = 0; i < this.getNodeCount(); i++){
			for(int j = 0; j < graph.getNodeCount(); j++){
				final var theta = getTheta(graph, edgeCosts, i, j);
				matrix.put(i, j, nodeCosts.get(i, j) + theta);
			}
		}
		
		for(int i = 0; i < this.getNodeCount(); i++){
			for(int j = 0; j < graph.getNodeCount(); j++){
				matrix.put(i, graph.getNodeCount() + j, Double.MAX_VALUE);
			}
			final var theta = getTheta(graph, edgeCosts, i, graph.getNodeCount());
			matrix.put(i, graph.getNodeCount() + i, nodeCosts.get(i, graph.getNodeCount()) + theta);
		}
		
		for(int j = 0; j < graph.getNodeCount(); j++){
			for(int i = 0; i < this.getNodeCount(); i++){
				matrix.put(this.getNodeCount() + i, j, Double.MAX_VALUE);
			}
			final var theta = getTheta(graph, edgeCosts, this.getNodeCount(), j);
			matrix.put(this.getNodeCount() + j, j, nodeCosts.get(this.getNodeCount(), j) + theta);
		}
		
		return matrix;
	}
	
	private double getTheta(Graph graph, Matrix2D edgeCosts, int i, int j){
		//List<Edge> g1g2 = this.getEdgesAt(i);
		//List<Edge> g2g1 = graph.getEdgesAt(j);
		
		//final var size = Math.max(g1g2.size(), g2g1.size());
		//final var matrix = new Matrix2D(size, size);
		//TODO Fill matrix
		//return HungarianAlgorithm.hgAlgorithm(matrix.getAsArray(), "min").getLeft();
		return 0;
	}
	
	private List<Edge> getEdgesAt(int i){
		final var node = this.getNodeAt(i).orElseThrow();
		return this.getEdges().stream().filter(e -> Objects.equals(e.getFrom(), node.getID()) || Objects.equals(e.getTo(), node.getID())).collect(Collectors.toList());
	}
	
	private int getNodeCount(){
		return this.getNodes().size();
	}
	
	public Optional<Node> getNodeAt(int pos){
		if(pos < 0 || pos >= this.getNodeCount()){
			return Optional.empty();
		}
		return Optional.of(this.getNodes().get(pos));
	}
	
	private double computeDistance(Object o1, Object o2){
		return Math.abs((Double) o1 - (Double) o2);
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		
		Graph graph = (Graph) o;
		
		return new EqualsBuilder().append(ID, graph.ID).append(sourcePath, graph.sourcePath).isEquals();
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(ID).append(sourcePath).toHashCode();
	}
}
