package es.upm.proyecto.jade.recomendador.agents;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

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
			SequentialBehaviour sequentialBehaviour = new SequentialBehaviour(this);
			for(Anime anime : animes) {
				sequentialBehaviour.addSubBehaviour(new ApiFetchReviewBehaviour(this, anime));
			}
			sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
				
				@Override
				public void action() {
					logger.info("Finished retrieval of reviews");
					try {
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
