package es.upm.proyecto.jade.recomendador.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnimeByGenre {
	
	private List<Anime> data;

	public List<Anime> getData() {
		return data;
	}

	public void setData(List<Anime> data) {
		this.data = data;
	}
}
