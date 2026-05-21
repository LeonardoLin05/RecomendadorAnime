package es.upm.proyecto.jade.recomendador.behaviours;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiFetchBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 3315139644095423750L;

	private static final Logger logger = LoggerFactory.getLogger(ApiFetchBehaviour.class);
	
	// Para realizar llamadas a la API
	private static final OkHttpClient client = new OkHttpClient();
	
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
		
        String url = "https://api.jikan.moe/v4/anime?genres=" + genreId + "&page=" + page;
        
        Request request = new Request.Builder()
        		.url(url)
        		.build();
        
    	int retry = 0;
    	
        while(retry < 5) {
	        logger.info("Retrieving page {} (attempt {})", page, retry + 1);
	        try(Response response = client.newCall(request).execute()) {
	        	if(response.isSuccessful()) {
	        		logger.info("Page {} retrieved successfully", page);
	        		String json = response.body().string();
	        		
	        		// Preparamos el contenido del mensaje a enviar
	        		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
	        		AID receiver = new AID();
	        		receiver.setName(receiverName);
	        		msg.addReceiver(receiver);
	            	msg.setContent(json);
	            	
	            	logger.info("Sending animes to {}", receiverName);
	            	myAgent.send(msg);
	            	return;
	        	}
	        	// Por si estamos haciendo demasidas peticiones seguidas
	        	else if (response.code() == 429) {
	        		retry++;
	                long waitTime = (long) Math.pow(2, retry) * 1000; // exponential backoff
	
	                logger.warn("429 received. Retrying in {} ms...", waitTime);
	
	                myAgent.doWait(waitTime);
	        	}
	        	else {
	        		logger.error("HTTP error {} {}", response.code(), response.message());
	                return;
	        	}
	        } catch (IOException e) {
	        	logger.error("Error calling API", e);
				return;
			}
		}
        logger.error("Max retries reached for page {}", page);
	}
}
