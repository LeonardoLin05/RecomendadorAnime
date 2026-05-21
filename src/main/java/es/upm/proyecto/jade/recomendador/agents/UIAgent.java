package es.upm.proyecto.jade.recomendador.agents;

import java.io.IOException;
import java.util.List;

import es.upm.proyecto.jade.recomendador.models.AgentBase;
import es.upm.proyecto.jade.recomendador.models.AgentModel;
import es.upm.proyecto.jade.recomendador.models.Anime;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.StaleProxyException;

public class UIAgent extends AgentBase {

	private static final long serialVersionUID = -8157277373975678421L;
	
	public static final String NICKNAME = "UserInterface";
	
	@Override
	protected void setup() {
		super.setup();
		this.type = AgentModel.INTERFACE;
		
		registerAgentDF();
		
		try {
			// TODO: esto debe ejecutarse al empezar la busqueda de animes recomendados después de rellenar los campos de preferencias
			// las preferencias meterlas en el array, por ahora esta puesta que el indice 0 sea el id del género, cambiar
			// si fuese necesario la posición
			Object[] preferences = {"2"};
			getContainerController()
			.createNewAgent("Recommender", "es.upm.proyecto.jade.recomendador.agents.RecommendationAgent", preferences)
			.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
		ACLMessage aclMessage = blockingReceive();
		try {
			List<Anime> animes = (List<Anime>) aclMessage.getContentObject();
			for(Anime anime : animes) {
				System.out.println(anime.getTitle());
			}
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
