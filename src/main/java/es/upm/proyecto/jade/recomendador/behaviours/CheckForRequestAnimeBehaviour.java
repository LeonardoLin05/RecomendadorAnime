package es.upm.proyecto.jade.recomendador.behaviours;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.upm.proyecto.jade.recomendador.models.AgentBase;
import es.upm.proyecto.jade.recomendador.models.AgentModel;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CheckForRequestAnimeBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -4900816702572315394L;
	
	private static final Logger logger = LoggerFactory.getLogger(CheckForRequestAnimeBehaviour.class);
	
	private static final MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

	public CheckForRequestAnimeBehaviour(Agent agent) {
		super(agent);
	}
	
	@Override
	public void action() {
		ACLMessage aclMessage = myAgent.receive(mt);
		if(aclMessage != null) {
			String receiverName = ((AgentBase) myAgent).getAgentsDF(AgentModel.RECOMMENDER)[0].getName().getName();
			String genreId = aclMessage.getContent();
			
			SequentialBehaviour sequentialBehaviour = new SequentialBehaviour(myAgent);
			
			for(int i = 1; i <= 8; i++) {
				sequentialBehaviour.addSubBehaviour(new ApiFetchBehaviour(myAgent, genreId, i, receiverName));
			}
			
			logger.info("Starting retrieval of animes...");
			myAgent.addBehaviour(sequentialBehaviour);
		}
		else {
			block();
		}
	}
}
