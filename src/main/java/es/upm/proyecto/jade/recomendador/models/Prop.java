package es.upm.proyecto.jade.recomendador.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Prop implements Serializable {
	
	private static final long serialVersionUID = -2899423912981928073L;
	
	private Date from;

	public Date getFrom() {
		return from;
	}

	public void setYear(Date from) {
		this.from = from;
	}
}
