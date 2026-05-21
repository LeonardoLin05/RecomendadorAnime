package es.upm.proyecto.jade.recomendador.agents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.upm.proyecto.jade.recomendador.behaviours.ApiFetchBehaviour;
import es.upm.proyecto.jade.recomendador.models.AgentBase;
import es.upm.proyecto.jade.recomendador.models.AgentModel;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;

public class AnimeDataAgent extends AgentBase {

	private static final long serialVersionUID = 8407526462177863350L;
	
	private static final Logger logger = LoggerFactory.getLogger(AnimeDataAgent.class);

	public static final String NICKNAME = "DataRecover";
	
	@Override
	protected void setup() {
		super.setup();
		this.type = AgentModel.DATA;
		
		logger.info("AnimeDataAgent created successfully");
		
		String receiverName = getAgentsDF(AgentModel.RECOMMENDER)[0].getName().getName();
		String genreId = getParams()[0];
		
		SequentialBehaviour sequentialBehaviour = new SequentialBehaviour(this);
		
		for(int i = 1; i <= 4; i++) {
			sequentialBehaviour.addSubBehaviour(new ApiFetchBehaviour(this, genreId, i, receiverName));
		}
		
		sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
			
			@Override
			public void action() {
				logger.info("Finished retrieval of animes");
			}
			
			@Override
			public int onEnd() {
				doDelete();
				return super.onEnd();
			}
		});
		
		logger.info("Starting retrieval of animes...");
		addBehaviour(sequentialBehaviour);
	}
}
