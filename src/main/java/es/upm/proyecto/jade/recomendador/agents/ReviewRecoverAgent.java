package es.upm.proyecto.jade.recomendador.agents;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import es.upm.proyecto.jade.recomendador.models.UserPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.upm.proyecto.jade.recomendador.behaviours.ApiFetchReviewBehaviour;
import es.upm.proyecto.jade.recomendador.models.AgentBase;
import es.upm.proyecto.jade.recomendador.models.AgentModel;
import es.upm.proyecto.jade.recomendador.models.Anime;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;

public class ReviewRecoverAgent extends AgentBase {

	private static final long serialVersionUID = 1303606930404638077L;
	
	private static final Logger logger = LoggerFactory.getLogger(ReviewRecoverAgent.class);

	private List<Anime> animes;
	
	@Override
	protected void setup() {
		super.setup();
		this.type = AgentModel.RECOMMENDER;
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			animes = mapper.readValue(getParams()[0], new TypeReference<List<Anime>>() {});
	        
			List<String> personalizado = new ArrayList<>();
	        if (getParams().length > 1 && getParams()[1] != null) 
	        {
	            UserPreferences prefs = mapper.readValue(getParams()[1], UserPreferences.class);
	            if (prefs.getPersonalizado() != null) 
	            {
	                personalizado = prefs.getPersonalizado();
	            }
	        }
	        
	        final List<String> personalizadoFinal = personalizado;
	        
			SequentialBehaviour sequentialBehaviour = new SequentialBehaviour(this);
			for (Anime anime : animes) {
				sequentialBehaviour.addSubBehaviour(new ApiFetchReviewBehaviour(this, anime, personalizadoFinal));
			}
			sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
				
				@Override
				public void action() {
					logger.info("Finished retrieval of reviews");
					try {
						animes.sort(Comparator.comparingDouble(Anime::getAiScore).reversed());
						ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
						AID receiver = new AID();
						receiver.setName(getAgentsDF(AgentModel.INTERFACE)[0].getName().getName());
						msg.addReceiver(receiver);
						msg.setConversationId("anime-request-recommend");
						msg.setContentObject((Serializable) animes);
						send(msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public int onEnd() {
					doDelete();
					return super.onEnd();
				}
			});
			addBehaviour(sequentialBehaviour);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
