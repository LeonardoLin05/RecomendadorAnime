package es.upm.proyecto.jade.recomendador.models;

import java.io.Serializable;

public class Images implements Serializable {
	
	private static final long serialVersionUID = 5525569700260868414L;
	
	private Image jpg;
	private Image webp;
	
	public Image getJpg() {
		return jpg;
	}
	
	public void setJpg(Image jpg) {
		this.jpg = jpg;
	}
	
	public Image getWebp() {
		return webp;
	}
	
	public void setWebp(Image webp) {
		this.webp = webp;
	}
}
