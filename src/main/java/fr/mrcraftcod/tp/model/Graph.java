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
import java.util.stream.Stream;
import static fr.mrcraftcod.tp.model.EdgeMode.UNDIRECTED;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-03-22.
 *
 * @author Thomas Couchoud
 * @since 2019-03-22
 */
public class Graph{
	private static final Double SCORE_EDGE_MAPPED = 0D;
	private static final Double SCORE_EDGE_DELETED = 0D;
	private static final Double SCORE_EDGE_CREATED = 0D;
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
	
	public double addEdgeEditionCost(Pair<Double, int[][]> result, Graph graph, Matrix2D edgeCosts){
		var realScore = result.getLeft();
		for(final var edge : this.getEdges()){
			final var index11 = this.getNodeIndex(edge.getFrom()).orElseThrow();
			final var index12 = this.getNodeIndex(edge.getTo()).orElseThrow();
			
			final var assign1 = result.getRight()[index11];
			final var assign2 = result.getRight()[index12];
			
			if(assign1[1] < graph.getNodeCount() && assign2[1] < graph.getNodeCount()){
				final var node21 = graph.getNodeAt(assign1[1]).orElseThrow();
				final var node22 = graph.getNodeAt(assign2[1]).orElseThrow();
				
				final var otherEdge = graph.getEdge(node21, node22);
				if(otherEdge.isPresent()){
					realScore += edgeCosts.get(this.getEdgeIndex(edge).orElseThrow(), graph.getEdgeIndex(otherEdge.get()).orElseThrow());
				}
				else{
					realScore += edgeCosts.get(this.getEdgeIndex(edge).orElseThrow(), graph.getNodeCount());
				}
			}
		}
		for(final var edge : graph.getEdges()){
			final var index21 = graph.getNodeIndex(edge.getFrom()).orElseThrow();
			final var index22 = graph.getNodeIndex(edge.getTo()).orElseThrow();
			
			final var assign1 = Stream.of(result.getRight()).filter(a -> a[1] == index21).findAny();
			final var assign2 = Stream.of(result.getRight()).filter(a -> a[1] == index22).findAny();
			
			final var edgeIndex = graph.getEdgeIndex(edge).orElseThrow();
			
			if(assign1.isPresent() && assign2.isPresent() && assign1.get()[1] < this.getNodeCount() && assign2.get()[1] < this.getNodeCount()){
				final var node11 = this.getNodeAt(assign1.get()[1]).orElseThrow();
				final var node12 = this.getNodeAt(assign2.get()[1]).orElseThrow();
				
				if(graph.getEdge(node11, node12).isEmpty()){
					realScore += edgeCosts.get(this.getEdgeCount(), edgeIndex);
				}
			}
			else{
				realScore += edgeCosts.get(this.getEdgeCount(), edgeIndex);
			}
		}
		return realScore;
	}
	
	private Optional<Integer> getEdgeIndex(Edge edge){
		return IntStream.range(0, this.getEdgeCount()).mapToObj(i -> ImmutablePair.of(i, this.getEdgeAt(i))).filter(n -> n.getRight().isPresent()).filter(n -> Objects.equals(n.getRight().get().getFrom(), edge.getFrom()) && Objects.equals(n.getRight().get().getTo(), edge.getTo())).map(ImmutablePair::getLeft).findAny();
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
	
	public Optional<Edge> getEdge(Node from, Node to){
		return this.getEdges().stream().filter(e -> (Objects.equals(e.getFrom(), from.getID()) || Objects.equals(e.getTo(), to.getID())) || (Objects.equals(this.getEdgeMode(), UNDIRECTED) && (Objects.equals(e.getFrom(), to.getID()) || Objects.equals(e.getTo(), from.getID())))).findAny();
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
