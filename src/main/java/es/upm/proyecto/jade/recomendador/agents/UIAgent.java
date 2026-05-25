package es.upm.proyecto.jade.recomendador.agents;

import java.util.List;

import es.upm.proyecto.jade.recomendador.models.AgentBase;
import es.upm.proyecto.jade.recomendador.models.AgentModel;
import es.upm.proyecto.jade.recomendador.models.Anime;
import es.upm.proyecto.jade.recomendador.behaviours.GroqService;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.CyclicBehaviour;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UIAgent extends AgentBase {

	private static final long serialVersionUID = -8157277373975678421L;
	
	public static final String NICKNAME = "UserInterface";
	private static final Logger logger = LoggerFactory.getLogger(UIAgent.class);
	
	private MainUI gui;
    private GroqService groqService = new GroqService();
	 
	@Override
	protected void setup() 
	{
		super.setup();
		this.type = AgentModel.INTERFACE;
		registerAgentDF();
		
		SwingUtilities.invokeLater(() -> {
            gui = new MainUI(this);
        });
		
		addBehaviour(new CyclicBehaviour() 
		{
            @Override
            public void action() 
            {
                MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),MessageTemplate.MatchConversationId("anime-request-recommend"));

                ACLMessage msg = receive(mt);
                if (msg != null) 
                {
                    try 
                    {
                        List<Anime> animes = (List<Anime>) msg.getContentObject();
                        logger.info("Lista de animes recibida");
                        
                        SwingUtilities.invokeLater(() -> gui.mostrarResultados(animes));

                    } 
                    catch (Exception e) 
                    {
                        logger.error("Error al recibir animes", e);
                    }
                } 
                else 
                {
                    block();
                }
            }
        });
	}
	
	public void lanzarBusqueda(String textoPref) 
	{
        new Thread(() -> {
            String promptSistema = groqService.cargarPromptSistema("instruccionesPrompt.txt");
			String respuesta = groqService.llamarAGroq(promptSistema, textoPref);
			logger.info(respuesta);

			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			AID receiver = new AID();
			receiver.setName(getAgentsDF(AgentModel.RECOMMENDER)[0].getName().getName());
			msg.addReceiver(receiver);
			msg.setContent(respuesta);
			send(msg);
        }).start();
    }
}
