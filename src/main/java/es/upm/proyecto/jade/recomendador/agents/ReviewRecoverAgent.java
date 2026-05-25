package es.upm.proyecto.jade.recomendador.agents;

import es.upm.proyecto.jade.recomendador.behaviours.CheckForRequestReviewBehaviour;
import es.upm.proyecto.jade.recomendador.models.AgentBase;
import es.upm.proyecto.jade.recomendador.models.AgentModel;

public class ReviewRecoverAgent extends AgentBase {

	private static final long serialVersionUID = 1303606930404638077L;
	
	public static final String NICKNAME = "ReviewRecover";
	
	@Override
	protected void setup() {
		super.setup();
		this.type = AgentModel.REVIEWER;
		
		addBehaviour(new CheckForRequestReviewBehaviour(this));
		
		registerAgentDF();
	}
}
