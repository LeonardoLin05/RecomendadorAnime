package es.upm.proyecto.jade.recomendador.agents;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.upm.proyecto.jade.recomendador.behaviours.RecommendBehaviour;
import es.upm.proyecto.jade.recomendador.models.AgentBase;
import es.upm.proyecto.jade.recomendador.models.AgentModel;
import es.upm.proyecto.jade.recomendador.models.Anime;
import jade.wrapper.StaleProxyException;

public class RecommendationAgent extends AgentBase {

	private static final long serialVersionUID = 6733107524071635301L;

	public static final String NICKNAME = "Recommender";
	
	private static final Logger logger = LoggerFactory.getLogger(RecommendationAgent.class);
	
	private List<Anime> animesRecomendados = new ArrayList<>();
	
	@Override
	protected void setup() {
		super.setup();
		this.type = AgentModel.RECOMMENDER;
		
		registerAgentDF();
		
		try {
			// Obtenemos las preferencias del usuario
			String[] preferencias = getParams();
			
			// Guardamos el género para parsarselo al AnimeDataAgent
			Object[] genreId = {preferencias[0]};
			logger.info("Creating AnimeDataAgent...");
			getContainerController()
				.createNewAgent("DataRecover", "es.upm.proyecto.jade.recomendador.agents.AnimeDataAgent", genreId)
				.start();
			
			addBehaviour(new RecommendBehaviour(this));	
		} catch (StaleProxyException e) {
			logger.error("Error processing request", e);
		}
	}
	
	@Override
	protected void takeDown() {
    	try {
    		// TODO: ordenar la lista y quedarse por ejemplo con los 5 mejores
    		// Además de esto, en vez de hacer lo que hay abajo, se puede crear
    		// un nuevo agente encargado de realizar el analisis de los comentarios
    		// pasandole los 5 mejores animes obtenidos
    		ObjectMapper mapper = new ObjectMapper();
    		
    		List<Anime> top5 = animesRecomendados.stream()
    		        .sorted(Comparator.comparingDouble(Anime::getHeuristicScore).reversed())
    		        .limit(5)
    		        .collect(Collectors.toList());
    		
    		Object[] params = {mapper.writeValueAsString(top5)};
			getContainerController()
			.createNewAgent("ReviewRecover", "es.upm.proyecto.jade.recomendador.agents.ReviewRecoverAgent", params)
			.start();
		} catch (StaleProxyException | JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	public void concatenarAnimesRecomendados(List<Anime> animesRecomendados) {
		this.animesRecomendados.addAll(animesRecomendados);
	}
	
}
