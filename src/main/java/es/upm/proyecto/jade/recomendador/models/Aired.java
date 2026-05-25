package es.upm.proyecto.jade.recomendador.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Aired implements Serializable {
	
	private static final long serialVersionUID = 8393732683397481436L;
	
	private Prop prop;

	public Prop getProp() {
		return prop;
	}

	public void setProp(Prop prop) {
		this.prop = prop;
	}
}
