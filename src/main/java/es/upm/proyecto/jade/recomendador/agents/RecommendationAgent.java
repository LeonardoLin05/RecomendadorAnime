package es.upm.proyecto.jade.recomendador.agents;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.upm.proyecto.jade.recomendador.behaviours.RecommendBehaviour;
import es.upm.proyecto.jade.recomendador.models.AgentBase;
import es.upm.proyecto.jade.recomendador.models.AgentModel;
import es.upm.proyecto.jade.recomendador.models.Anime;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
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
    		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
    		AID receiver = new AID();
    		receiver.setName(getAgentsDF(AgentModel.INTERFACE)[0].getName().getName());
    		msg.addReceiver(receiver);
    		msg.setConversationId("anime-request-recommend");
			msg.setContentObject((Serializable) animesRecomendados);
			send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void concatenarAnimesRecomendados(List<Anime> animesRecomendados) {
		this.animesRecomendados.addAll(animesRecomendados);
	}
	
}
