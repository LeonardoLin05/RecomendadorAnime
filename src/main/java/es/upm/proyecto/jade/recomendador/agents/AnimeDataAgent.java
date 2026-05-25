package es.upm.proyecto.jade.recomendador.agents;

import es.upm.proyecto.jade.recomendador.behaviours.CheckForRequestAnimeBehaviour;
import es.upm.proyecto.jade.recomendador.models.AgentBase;
import es.upm.proyecto.jade.recomendador.models.AgentModel;

public class AnimeDataAgent extends AgentBase {

	private static final long serialVersionUID = 8407526462177863350L;

	public static final String NICKNAME = "DataRecover";
	
	@Override
	protected void setup() {
		super.setup();
		this.type = AgentModel.DATA;
		
		addBehaviour(new CheckForRequestAnimeBehaviour(this));
		
		registerAgentDF();
	}
}
