package es.upm.proyecto.jade.recomendador.behaviours;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.upm.proyecto.jade.recomendador.models.Anime;
import es.upm.proyecto.jade.recomendador.models.AnimeByGenre;
import jade.core.behaviours.OneShotBehaviour;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiFetchBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 3315139644095423750L;

	private String genreId;
	
	@Override
	public void action() {
		// Para realizar llamadas a la API
		OkHttpClient client = new OkHttpClient();
		// Para transformar de JSON a objeto Java
        ObjectMapper mapper = new ObjectMapper();
		
        String url = "https://api.jikan.moe/v4/anime?genres=" + genreId;
        
        Request request = new Request.Builder()
        		.url(url)
        		.build();
        
        try(Response response = client.newCall(request).execute()) {
        	
        	String json = response.body().string();
        	
        	AnimeByGenre animes = mapper.readValue(json, AnimeByGenre.class);
        	
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
}
