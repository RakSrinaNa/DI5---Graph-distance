package fr.mrcraftcod.tp.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-03-22.
 *
 * @author Thomas Couchoud
 * @since 2019-03-22
 */
public class Edge{
	private final int from;
	private final int to;
	private final HashMap<String, Object> attributes;
	
	public Edge(int from, int to){
		this.from = from;
		this.to = to;
		this.attributes = new HashMap<>();
	}
	
	public void addAttribute(String name, Object object){
		this.attributes.put(name, object);
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(from).append(to).toHashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		
		Edge edge = (Edge) o;
		
		return new EqualsBuilder().append(from, edge.from).append(to, edge.to).isEquals();
	}
	
	@Override
	public String toString(){
		return new ToStringBuilder(this).append("from", from).append("to", to).append("attributes", attributes).toString();
	}
	
	public Object getAttr(String name){
		return Optional.ofNullable(this.getAttributes().get(name));
	}
	
	private HashMap<String, Object> getAttributes(){
		return this.attributes;
	}
	
	public int getFrom(){
		return this.from;
	}
	
	public int getTo(){
		return this.to;
	}
}
