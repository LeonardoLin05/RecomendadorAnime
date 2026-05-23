package es.upm.proyecto.jade.recomendador.behaviours;

import java.util.List;

import es.upm.proyecto.jade.recomendador.models.UserPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.upm.proyecto.jade.recomendador.agents.RecommendationAgent;
import es.upm.proyecto.jade.recomendador.models.AgentBase;
import es.upm.proyecto.jade.recomendador.models.Anime;
import es.upm.proyecto.jade.recomendador.models.AnimeByGenre;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RecommendBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = -4105677757141840016L;
	
	private static final Logger logger = LoggerFactory.getLogger(RecommendBehaviour.class);
	
	private static final MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
	
	// Para transformar de JSON a objeto Java
    private ObjectMapper mapper = new ObjectMapper();
    
    private String[] preferencias = ((AgentBase) myAgent).getParams();

	private HeuristicCalculator heuristicCalculator;
	
    private int batch = 1;
    
	public RecommendBehaviour(Agent agent) {

		super(agent);
		try{
			UserPreferences preferences = new ObjectMapper().readValue(preferencias[0], UserPreferences.class);
			this.heuristicCalculator = new HeuristicCalculator(preferences);
		} catch (JsonProcessingException e){
			logger.error("Error passing UserPreferences in ReccomendBehaviour", e);
		}
	}
	
	@Override
	public void action() {
		ACLMessage aclMessage = myAgent.receive(mt);
		if(aclMessage != null) {
			logger.info("INFORM from AnimeDataAgent received");
			try {
				logger.info("Retrieving sended data...");
				List<Anime> animes = mapper.readValue(aclMessage.getContent(), AnimeByGenre.class).getData();
				logger.info("Sended data retrieved successfully");
				
				logger.info("Starting heuristic calculations...");
				heuristicCalculator.calcularScores(animes);
				logger.info("Finished heuristic calculations");
				
				((RecommendationAgent) myAgent).concatenarAnimesRecomendados(animes);
				
				batch++;
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		else {
			logger.info("Waiting for INFORM from AnimeDataAgent..");
			this.block();
		}
	}
	
	@Override
	public boolean done() {
		return batch > 4;
	}
	
	@Override
	public int onEnd() {
		myAgent.doDelete();
		return super.onEnd();
	}
}
