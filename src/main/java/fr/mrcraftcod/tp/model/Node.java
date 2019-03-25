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
public class Node{
	private final int ID;
	private final HashMap<String, Object> attributes;
	
	public Node(int id){
		this.ID = id;
		this.attributes = new HashMap<>();
	}
	
	public void addAttribute(String name, Object object){
		this.attributes.put(name, object);
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(ID).toHashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		
		Node node = (Node) o;
		
		return new EqualsBuilder().append(ID, node.ID).isEquals();
	}
	
	@Override
	public String toString(){
		return new ToStringBuilder(this).append("ID", ID).append("attributes", attributes).toString();
	}
	
	public Optional<Object> getAttr(String name){
		return Optional.ofNullable(this.getAttributes().get(name));
	}
	
	public HashMap<String, Object> getAttributes(){
		return this.attributes;
	}
	
	public int getID(){
		return this.ID;
	}
}
