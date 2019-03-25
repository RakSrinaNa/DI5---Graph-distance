package fr.mrcraftcod.tp;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import fr.mrcraftcod.tp.model.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Main{
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	public static void main(final String[] args) throws IOException, SAXException, ParserConfigurationException{
		LOGGER.info("Starting program version {}", Main.getProgramVersion());
		final CLIParameters parameters = new CLIParameters();
		try{
			JCommander.newBuilder().addObject(parameters).build().parse(args);
		}
		catch(final ParameterException e){
			LOGGER.error("Failed to parse arguments", e);
			e.usage();
			System.exit(1);
		}
		
		final var graphs = new ArrayList<Graph>();
		for(File f : Objects.requireNonNull(new File("torename_GXL").listFiles())){
			graphs.addAll(GXLParser.fromFile(Paths.get(f.toURI())).getGraphs());
		}
		graphs.sort(Comparator.comparing(Graph::getSourcePath));
		
		for(var g1 : graphs){
			for(var g2 : graphs){
				if(!Objects.equals(g1, g2)){
					final var bipartite = g1.getBipartiteCostMatrix(g2);
					final var result = HungarianAlgorithm.hgAlgorithm(bipartite.getAsArray(), "min");
					LOGGER.info("{}~{} vs {}~{} ==> Distance: {}", g1.getSourcePath().getFileName(), g1.getID(), g2.getSourcePath().getFileName(), g2.getID(), result.getLeft());
				}
			}
		}
	}
	
	public static String getProgramVersion(){
		final Properties properties = new Properties();
		try{
			properties.load(Main.class.getResource("/version.properties").openStream());
		}
		catch(final IOException e){
			LOGGER.warn("Error reading version jsonConfigFile", e);
		}
		return properties.getProperty("simulator.version", "Unknown");
	}
}
