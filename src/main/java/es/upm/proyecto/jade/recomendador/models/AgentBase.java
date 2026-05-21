package es.upm.proyecto.jade.recomendador.models;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public abstract class AgentBase extends Agent {
	
	private static final long serialVersionUID = 1055829481251936882L;
	
	private static final Logger logger = LoggerFactory.getLogger(AgentBase.class);

	// Con la clase AgentModel tenemos acceso a los tipos de agentes que utilizamos en el proyecto
	protected AgentModel type;
	
	// Si se pasan parámetros al agente en su creación, los capturamos
	protected String[] params;
	
	@Override
	protected void setup() {
		super.setup();
		 this.params = Arrays.asList(getArguments()).toArray(new String[getArguments().length]);
	}
	
	public DFAgentDescription[] getAgentsDF(AgentModel type) {
		// Indico las características el tipo de servicio que quiero encontrar
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription templateSd = new ServiceDescription();
		
		// Como define el tipo el agente coordinador también podríamos buscar por nombre
		templateSd.setType(type.getValue());
		template.addServices(templateSd);
		
		DFAgentDescription[] result = new DFAgentDescription[0];
		
		try {
			result = DFService.search(this, template);
		} catch (FIPAException e) {
			e.printStackTrace();
			logger.error("DFService.search don't work!!!");
		}
		return result;
	}
	
	public void registerAgentDF() {
		//El registro va a coincidir con el tipo que se define en la clase AgentModel (el nombre del agente)
		DFAgentDescription dfd = new DFAgentDescription();
		
		//Incluimos el identificador del agente en el descriptor de servicios
		dfd.setName(this.getAID());
		
		//Creamos un nuevo servicio
		ServiceDescription sd = new ServiceDescription();
		sd.setType(this.type.getValue());
		sd.setName(this.getLocalName());
		
		//Incluimos el nuevo servicio en el descriptor de servicios
		dfd.addServices(sd);
		
		try {
			DFAgentDescription[] results = DFService.search(this, dfd);
			//Realizamos el registro del descriptor de servicios en el DF
			if (results == null || results.length == 0)
				DFService.register(this, dfd);
		} catch (FIPAException e) {
			logger.info("Unable to register.");
			this.doDelete();
		}
	}
	
	public void deregisterAgentDF() {
		try {
			DFService.deregister(this);
		}
		catch (FIPAException e){
			logger.error("Unable to deregister.");
			this.doDelete();
		}
	}
	
	@Override
	public void doDelete() {
		super.doDelete();
		logger.error(": Exit!!!");
	}
	
	public String[] getParams(){
		return this.params;
	}
}
