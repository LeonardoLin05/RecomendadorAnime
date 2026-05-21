package es.upm.proyecto.jade.recomendador.behaviours;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.upm.proyecto.jade.recomendador.models.Anime;
import es.upm.proyecto.jade.recomendador.models.ApiFetch;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class ApiFetchReviewBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 4714051838842691642L;
	
	private static final Logger logger = LoggerFactory.getLogger(ApiFetchReviewBehaviour.class);

	// Para transformar de JSON a objeto Java
    private ObjectMapper mapper = new ObjectMapper();
	
	private Anime anime;
	
	public ApiFetchReviewBehaviour(Agent agent, Anime anime) {
		super(agent);
		this.anime = anime;
	}
	
	@Override
	public void action() {
		String url = "https://api.jikan.moe/v4/anime/" + anime.getId() + "/reviews";
		
		String resultado = new ApiFetch<String>(myAgent) {

			@Override
			public String responseHandler(String json) {
				String resultado = null;
				try {
					JsonNode data = mapper.readTree(json).path("data");
					
	        		StringBuilder stringBuilder = new StringBuilder();
	        		
	        		for(JsonNode review : data) {
	        			stringBuilder.append(review.path("review").asText());
	        		}
	        		resultado = stringBuilder.toString();
	        		logger.info("Reviews for {} retrieved successfully", anime.getTitle());
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return resultado;
			}
		}.fetch(url);
		
		// TODO: hacer resumen de las reviews
		
		anime.setReviewSummary(resultado);
	}
}
