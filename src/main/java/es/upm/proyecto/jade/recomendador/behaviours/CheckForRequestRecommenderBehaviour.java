package es.upm.proyecto.jade.recomendador.behaviours;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.upm.proyecto.jade.recomendador.agents.RecommendationAgent;
import es.upm.proyecto.jade.recomendador.models.AgentBase;
import es.upm.proyecto.jade.recomendador.models.AgentModel;
import es.upm.proyecto.jade.recomendador.models.Anime;
import es.upm.proyecto.jade.recomendador.models.UserPreferences;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CheckForRequestRecommenderBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -5116251931435166766L;

	private static final Logger logger = LoggerFactory.getLogger(CheckForRequestRecommenderBehaviour.class);
	
	private static final MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
	
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	public CheckForRequestRecommenderBehaviour(Agent agent) {
		super(agent);
	}
	
	@Override
	public void action() {
		ACLMessage aclMessage = myAgent.receive(mt);
		if(aclMessage != null && aclMessage.getContent() != null) {
			try {
				// Obtenemos las preferencias del usuario
				String preferencias = aclMessage.getContent();
				((RecommendationAgent) myAgent).setPreferencias(preferencias);
				
				((RecommendationAgent) myAgent).getAnimesRecomendados().clear();
				
				UserPreferences prefs = OBJECT_MAPPER.readValue(preferencias, UserPreferences.class);

				logger.info("Sending REQUEST to AnimeDataAgent...");
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
	    		AID receiver = new AID();
	    		receiver.setName(((AgentBase) myAgent).getAgentsDF(AgentModel.DATA)[0].getName().getName());
	    		msg.addReceiver(receiver);
	        	msg.setContent(String.valueOf(prefs.getGenre()));
	        	myAgent.send(msg);
	        	
	        	SequentialBehaviour sequentialBehaviour = new SequentialBehaviour(myAgent);
	        	
	        	sequentialBehaviour.addSubBehaviour(new RecommendBehaviour(myAgent));
	        	sequentialBehaviour.addSubBehaviour(new OneShotBehaviour(myAgent) {
					
					@Override
					public void action() {
						try {
				    		
				    		List<Anime> top5 = ((RecommendationAgent) myAgent).getAnimesRecomendados().stream()
				    		        .sorted(Comparator.comparingDouble(Anime::getHeuristicScore).reversed())
				    		        .limit(5)
				    		        .collect(Collectors.toList());

							String[] data = {
									OBJECT_MAPPER.writeValueAsString(top5),
									preferencias
							};
							
							logger.info("Sending REQUEST to ReviewRecoverAgent...");
							ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				    		AID receiver = new AID();
				    		receiver.setName(((AgentBase) myAgent).getAgentsDF(AgentModel.REVIEWER)[0].getName().getName());
				    		msg.addReceiver(receiver);
				        	msg.setContentObject(data);
				        	myAgent.send(msg);
						} catch (IOException e) {
							logger.error("Error sending request", e);
						}
					}
				});
	        	
				myAgent.addBehaviour(sequentialBehaviour);
			} catch (JsonProcessingException e) {
				logger.error("Error processing Json", e);
			}
		}
		else {
			block();
		}
	}
}
