package es.upm.proyecto.jade.recomendador.models;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jade.core.Agent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class ApiFetch<T> {
	
	private static final Logger logger = LoggerFactory.getLogger(ApiFetch.class);
	
	// Para realizar llamadas a la API
	private static final OkHttpClient client = new OkHttpClient();
	
	private Agent myAgent;
	
	protected ApiFetch(Agent myAgent) {
		this.myAgent = myAgent;
	}
	
	public T fetch(String url) {
		
        Request request = new Request.Builder()
        		.url(url)
        		.build();
        
    	int retry = 0;
    	
        while(retry < 5) {
        	logger.info("Starting call...");
	        try(Response response = client.newCall(request).execute()) {
	        	if(response.isSuccessful()) {
	        		logger.info("Successfull call");
	        		return responseHandler(response.body().string());
	        	}
	        	// Por si estamos haciendo demasidas peticiones seguidas
	        	else if (response.code() == 429) {
	        		retry++;
	                long waitTime = (long) Math.pow(2, retry) * 1000; // exponential backoff
	
	                logger.warn("{} received. Retrying in {} ms...", response.code(), waitTime);
	
	                myAgent.doWait(waitTime);
	        	}
	        	else {
	        		logger.error("HTTP error {} {}", response.code(), response.message());
	                return null;
	        	}
	        } catch (IOException e) {
	        	logger.error("Error calling API", e);
				return null;
			}
		}
        logger.error("Max retries reached");
        return null;
	}
	
	public abstract T responseHandler(String json);
}
