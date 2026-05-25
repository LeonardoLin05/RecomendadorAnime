package es.upm.proyecto.jade.recomendador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;

public class App {
	
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	
	private static void loadBoot() {
		jade.wrapper.AgentContainer cc;
		
		//Creamos una instancia de entorno de ejecución java
		jade.core.Runtime rt = jade.core.Runtime.instance();
		rt.setCloseVM(true);
		logger.info("Runtime created");
		
		//ProfileImpl permite recuperar distintos parámetros de ejecución y de lanzamiento de JADE
		Profile profile = new ProfileImpl(null, 1200, null);
		logger.info("Profile created");
		
		//Creamos un contenedor JADE dentro de la instancia java
		logger.info("Launching a whole in-process platform... {}", profile);
		cc = rt.createMainContainer(profile);
		
		try {
			ProfileImpl pContainer = new ProfileImpl(null, 1200, null);
			rt.createAgentContainer(pContainer);
			logger.info("Containers created");
			logger.info("Launching the rma agent on the main container...");
			
			//Se crea el agente RMA
			cc.createNewAgent("rma","jade.tools.rma.rma", new Object[0]).start();
			cc.createNewAgent("UserInterface","es.upm.proyecto.jade.recomendador.agents.UIAgent", new Object[0]).start();
			cc.createNewAgent("Recommender","es.upm.proyecto.jade.recomendador.agents.RecommendationAgent", new Object[0]).start();
			cc.createNewAgent("DataRecover","es.upm.proyecto.jade.recomendador.agents.AnimeDataAgent", new Object[0]).start();
			cc.createNewAgent("ReviewRecover","es.upm.proyecto.jade.recomendador.agents.ReviewRecoverAgent", new Object[0]).start();
			//Se crea el nuevo agente
		} catch(StaleProxyException e) {
			logger.error("Error during boot!!!");
			e.printStackTrace();
			System.exit(1);
		}
	}
    public static void main(String[] args) {
    	logger.info("Starting...");
    	//Llamamos a JADE y a la creación de agentes
    	loadBoot();
    	logger.info("MAS loaded...");
    }
}
