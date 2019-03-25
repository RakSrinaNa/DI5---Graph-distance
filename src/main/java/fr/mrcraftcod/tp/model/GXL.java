package fr.mrcraftcod.tp.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-03-22.
 *
 * @author Thomas Couchoud
 * @since 2019-03-22
 */
public class GXL{
	private final ArrayList<Graph> graphs;
	
	public GXL(){
		this.graphs = new ArrayList<>();
	}
	
	public void addAllGraphs(Collection<Graph> graphs){
		this.graphs.addAll(graphs);
	}
	
	@Override
	public String toString(){
		return new ToStringBuilder(this).append("graphs", graphs).toString();
	}
}
