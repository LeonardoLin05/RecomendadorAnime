package es.upm.proyecto.jade.recomendador.agents;

import java.util.ArrayList;
import java.util.List;

import es.upm.proyecto.jade.recomendador.behaviours.CheckForRequestRecommenderBehaviour;
import es.upm.proyecto.jade.recomendador.models.AgentBase;
import es.upm.proyecto.jade.recomendador.models.AgentModel;
import es.upm.proyecto.jade.recomendador.models.Anime;

public class RecommendationAgent extends AgentBase {

	private static final long serialVersionUID = 6733107524071635301L;

	public static final String NICKNAME = "Recommender";
	
	private List<Anime> animesRecomendados;
	
	private String preferencias;
	
	@Override
	protected void setup() {
		super.setup();
		this.type = AgentModel.RECOMMENDER;
		
		animesRecomendados = new ArrayList<>();
		
		addBehaviour(new CheckForRequestRecommenderBehaviour(this));
		
		registerAgentDF();
	}
	
	public void concatenarAnimesRecomendados(List<Anime> animesRecomendados) {
		this.animesRecomendados.addAll(animesRecomendados);
	}
	
	public List<Anime> getAnimesRecomendados() {
		return animesRecomendados;
	}
	
	public void setPreferencias(String preferencias) {
		this.preferencias = preferencias;
	}
	
	public String getPreferencias() {
		return preferencias;
	}
	
}
