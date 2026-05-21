package es.upm.proyecto.jade.recomendador.behaviours;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.upm.proyecto.jade.recomendador.models.ApiFetch;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class ApiFetchBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 3315139644095423750L;

	private static final Logger logger = LoggerFactory.getLogger(ApiFetchBehaviour.class);
	
	private String genreId;
	private int page;
	
	private String receiverName;
	
	public ApiFetchBehaviour(Agent agent, String genreId, int page, String receiverName) {
		super(agent);
		this.genreId = genreId;
		this.page = page;
		this.receiverName = receiverName;
	}
	
	@Override
	public void action() {
		
        String url = "https://api.jikan.moe/v4/anime?+"
        		+ "genres=" + genreId 
        		+ "&page=" + page
        		+ "&order_by=score"
        		+ "&sort=desc";
        
        new ApiFetch<Void>(myAgent) {

			@Override
			public Void responseHandler(String json) {
        		// Preparamos el contenido del mensaje a enviar
        		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        		AID receiver = new AID();
        		receiver.setName(receiverName);
        		msg.addReceiver(receiver);
            	msg.setContent(json);
            	
            	logger.info("Sending animes to {}", receiverName);
            	myAgent.send(msg);
            	return null;
			}
        	
		}.fetch(url);
	}
}
