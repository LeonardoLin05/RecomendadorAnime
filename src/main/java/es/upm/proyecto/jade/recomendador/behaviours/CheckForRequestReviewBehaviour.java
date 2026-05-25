package es.upm.proyecto.jade.recomendador.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import jade.lang.acl.UnreadableException;

public class CheckForRequestReviewBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -1497092242530182853L;
	
	private static final Logger logger = LoggerFactory.getLogger(CheckForRequestReviewBehaviour.class);
	
	private static final MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

	private List<Anime> animes;
	
	public CheckForRequestReviewBehaviour(Agent agent) {
		super(agent);
	}
	
	@Override
	public void action() {
		ACLMessage aclMessage = myAgent.receive(mt);
		if(aclMessage != null) {
			ObjectMapper mapper = new ObjectMapper();
			
			try {
				String[] data = (String[]) aclMessage.getContentObject();
				animes = mapper.readValue(data[0], new TypeReference<List<Anime>>() {});
		        
				List<String> personalizado = new ArrayList<>();
		        if (data.length > 1 && data[1] != null) {
		            UserPreferences prefs = mapper.readValue(data[1], UserPreferences.class);
		            if (prefs.getPersonalizado() != null) {
		                personalizado = prefs.getPersonalizado();
		            }
		        }
		        
		        final List<String> personalizadoFinal = personalizado;
		        
				SequentialBehaviour sequentialBehaviour = new SequentialBehaviour(myAgent);
				for (Anime anime : animes) {
					sequentialBehaviour.addSubBehaviour(new ApiFetchReviewBehaviour(myAgent, anime, personalizadoFinal));
				}
				sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
					
					@Override
					public void action() {
						logger.info("Finished retrieval of reviews");
						try {
							animes.sort(Comparator.comparingDouble(Anime::getAiScore).reversed());
							ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
							AID receiver = new AID();
							receiver.setName(((AgentBase) myAgent).getAgentsDF(AgentModel.INTERFACE)[0].getName().getName());
							msg.addReceiver(receiver);
							msg.setConversationId("anime-request-recommend");
							msg.setContentObject((Serializable) animes);
							myAgent.send(msg);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				myAgent.addBehaviour(sequentialBehaviour);
			} catch (JsonProcessingException | UnreadableException e) {
				logger.error("Error processing request");
			}
		}
		else {
			block();
		}
	}
}
