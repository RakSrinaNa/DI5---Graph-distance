package fr.mrcraftcod.tp;

import fr.mrcraftcod.tp.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import static org.w3c.dom.Node.ELEMENT_NODE;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-03-22.
 *
 * @author Thomas Couchoud
 * @since 2019-03-22
 */
public class GXLParser{
	private static final Logger LOGGER = LoggerFactory.getLogger(GXLParser.class);
	
	public static GXL fromFile(Path path) throws ParserConfigurationException, IOException, SAXException{
		final var dbFactory = DocumentBuilderFactory.newInstance();
		final var dBuilder = dbFactory.newDocumentBuilder();
		final var doc = dBuilder.parse(path.toFile());
		final var gxlRoot = doc.getDocumentElement();
		gxlRoot.normalize();
		
		final var gxl = new GXL();
		gxl.addAllGraphs(parseGraphs(gxlRoot.getElementsByTagName("graph")));
		return gxl;
	}
	
	private static Collection<Graph> parseGraphs(NodeList graphs){
		final var c = new ArrayList<Graph>();
		for(var i = 0; i < graphs.getLength(); i++){
			if(graphs.item(i).getNodeType() == ELEMENT_NODE){
				c.add(parseGraph((Element) graphs.item(i)));
			}
		}
		return c;
	}
	
	private static Graph parseGraph(Element item){
		final var graph = new Graph(Integer.parseInt(item.getAttribute("id")));
		graph.setEdgeMode(EdgeMode.valueOf(item.getAttribute("edgemode").toUpperCase()));
		
		final var nodes = item.getElementsByTagName("node");
		for(var i = 0; i < nodes.getLength(); i++){
			if(nodes.item(i).getNodeType() == ELEMENT_NODE){
				graph.addNode(parseNode((Element) nodes.item(i)));
			}
		}
		
		final var edges = item.getElementsByTagName("edge");
		for(var i = 0; i < edges.getLength(); i++){
			if(edges.item(i).getNodeType() == ELEMENT_NODE){
				graph.addEdge(parseEdge((Element) edges.item(i)));
			}
		}
		return graph;
	}
	
	private static Node parseNode(Element item){
		final var node = new Node(Integer.parseInt(item.getAttribute("id")));
		
		final var attrs = item.getElementsByTagName("attr");
		for(var i = 0; i < attrs.getLength(); i++){
			if(attrs.item(i).getNodeType() == ELEMENT_NODE){
				final var attr = (Element) attrs.item(i);
				node.addAttribute(attr.getAttribute("name"), parseObject(attr.getChildNodes()));
			}
		}
		return node;
	}
	
	private static Edge parseEdge(Element item){
		final var edge = new Edge(Integer.parseInt(item.getAttribute("from")), Integer.parseInt(item.getAttribute("to")));
		
		final var attrs = item.getElementsByTagName("attr");
		for(var i = 0; i < attrs.getLength(); i++){
			if(attrs.item(i).getNodeType() == ELEMENT_NODE){
				final var attr = (Element) attrs.item(i);
				edge.addAttribute(attr.getAttribute("name"), parseObject(attr.getChildNodes()));
			}
		}
		return edge;
	}
	
	private static Object parseObject(NodeList items){
		for(int i = 0; i < items.getLength(); i++){
			if(items.item(i).getNodeType() == ELEMENT_NODE){
				final var item = (Element) items.item(i);
				switch(item.getTagName()){
					case "Double":
						return Double.parseDouble(item.getTextContent());
					case "Float":
						return Float.parseFloat(item.getTextContent());
					default:
						LOGGER.error("Unknown tag object type {}", item.getTagName());
				}
			}
		}
		LOGGER.error("No elemnt nodes found for object");
		return null;
	}
}
