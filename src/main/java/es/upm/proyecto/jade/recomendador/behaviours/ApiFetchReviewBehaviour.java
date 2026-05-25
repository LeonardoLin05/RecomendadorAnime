package es.upm.proyecto.jade.recomendador.behaviours;

import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.upm.proyecto.jade.recomendador.models.Anime;
import es.upm.proyecto.jade.recomendador.models.ApiFetch;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class ApiFetchReviewBehaviour extends OneShotBehaviour 
{

	private static final long serialVersionUID = 4714051838842691642L;
	
	private static final Logger logger = LoggerFactory.getLogger(ApiFetchReviewBehaviour.class);

	// Para transformar de JSON a objeto Java
    private ObjectMapper mapper = new ObjectMapper();
	
	private Anime anime;
	
	 private List<String> personalizado;
	 private GroqService groqService = new GroqService();
	
	public ApiFetchReviewBehaviour(Agent agent, Anime anime, List<String> personalizado) {
		super(agent);
		this.anime = anime;
		this.personalizado = personalizado;
	}
	
	@Override
	public void action() 
	{
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
	        		myAgent.doWait(3000);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return resultado;
			}
		}.fetch(url);
		
		if (resultado != null && !resultado.isEmpty()) 
		{
            logger.info("Llamando a Groq para generar resumen de {}", anime.getTitle());
            String respuestaGroq = groqService.generarResumenYNota(resultado, personalizado);

            if (respuestaGroq != null) 
            {
            	JSONObject resultadoGroq = new JSONObject(respuestaGroq);
            	anime.setAiScore(resultadoGroq.getDouble("nota_ia"));
            	anime.setAiSummary(resultadoGroq.getString("resumen"));
            	logger.info("Resumen y nota IA generados para {}: {}",anime.getTitle(), anime.getAiScore());
            }
		
		}	
	}
}
