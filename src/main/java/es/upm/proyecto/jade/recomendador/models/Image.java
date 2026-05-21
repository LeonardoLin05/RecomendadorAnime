package es.upm.proyecto.jade.recomendador.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Image implements Serializable {
	
	private static final long serialVersionUID = 8243570778973818472L;
	
	@JsonProperty("image_url")
	private String imageUrl;
	@JsonProperty("small_image_url")
	private String smallImageUrl;
	@JsonProperty("large_image_url")
	private String largeImageUrl;
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public String getSmallImageUrl() {
		return smallImageUrl;
	}
	
	public void setSmallImageUrl(String smallImageUrl) {
		this.smallImageUrl = smallImageUrl;
	}
	
	public String getLargeImageUrl() {
		return largeImageUrl;
	}
	
	public void setLargeImageUrl(String largeImageUrl) {
		this.largeImageUrl = largeImageUrl;
	}
}
