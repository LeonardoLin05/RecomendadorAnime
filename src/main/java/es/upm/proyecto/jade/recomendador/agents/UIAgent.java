package es.upm.proyecto.jade.recomendador.agents;

import java.io.IOException;
import java.util.List;

import es.upm.proyecto.jade.recomendador.models.AgentBase;
import es.upm.proyecto.jade.recomendador.models.AgentModel;
import es.upm.proyecto.jade.recomendador.models.Anime;
import es.upm.proyecto.jade.recomendador.behaviours.GroqService;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.StaleProxyException;
import jade.core.behaviours.CyclicBehaviour;
import javax.swing.SwingUtilities;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class UIAgent extends AgentBase {

	private static final long serialVersionUID = -8157277373975678421L;
	
	public static final String NICKNAME = "UserInterface";
	private static final Logger logger = LoggerFactory.getLogger(UIAgent.class);
	
	private MainUI gui;
    private GroqService groqService = new GroqService();
    private ObjectMapper mapper = new ObjectMapper();
	 
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
                MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),MessageTemplate.MatchConversationId("anime-request-recommend"));

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
		/*
		try {
		// Jsons de prueba con campos a rellenar para probar la heurística
			Object[] preferences = {
					"{" +
							"\"genre\": 1," +
							"\"status\": \"complete\"," +
							"\"type\": \"tv\"," +
							"\"year_min\": 2010," +
							"\"year_max\": 2015," +
							"\"episodes_min\": 12," +
							"\"episodes_max\": 50," +
							"\"duration_min\": 20," +
							"\"duration_max\": 30," +
							"\"themes\": 0," +
							"\"keywords\": [\"sword\", \"fantasy\"]," +
							"\"personalizado\": []" +
							"}"
			};
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
	*/
	
	public void lanzarBusqueda(String textoPref) 
	{
        new Thread(() -> {
            try 
            {
                String promptSistema = groqService.cargarPromptSistema("instruccionesPrompt.txt");
                String respuesta = groqService.llamarAGroq(promptSistema, textoPref);
                Object[] agentParams  = { respuesta }; 
                /*
                JSONObject params = new JSONObject(respuesta);
                
                String genreId = params.isNull("genre") ? null : String.valueOf(params.getInt("genre"));
                String status = params.isNull("status") ? null : params.getString("status");
                String type = params.isNull("type") ? null : params.getString("type");
                Integer yearMin = params.isNull("year_min") ? null : params.getInt("year_min");
                Integer yearMax = params.isNull("year_max") ? null : params.getInt("year_max");
                Integer epsMin = params.isNull("episodes_min") ? null : params.getInt("episodes_min");
                Integer epsMax = params.isNull("episodes_max") ? null : params.getInt("episodes_max");
                Integer durationMin = params.isNull("duration_min") ? null : params.getInt("duration_min");
                Integer durationMax = params.isNull("duration_max") ? null : params.getInt("duration_max");
                String themeId = params.isNull("theme") ? null : String.valueOf(params.getInt("genre"));
                JSONArray keywords = params.isNull("keywords") ? new JSONArray() : params.getJSONArray("keywords");
                JSONArray personalizado = params.isNull("personalizado") ? new JSONArray() : params.getJSONArray("personalizado");

                Object[] agentParams = 
                {
                    genreId,
                    status,
                    type,
                    yearMin != null ? String.valueOf(yearMin) : null,
                    yearMax != null ? String.valueOf(yearMax) : null,
                    epsMin  != null ? String.valueOf(epsMin)  : null,
                    epsMax  != null ? String.valueOf(epsMax)  : null,
                    durationMin  != null ? String.valueOf(epsMin)  : null,
                    durationMax  != null ? String.valueOf(epsMax)  : null,
                    themeId,
                    keywords.toString(),
                    personalizado.toString()
                };
                 */
                getContainerController().createNewAgent("Recommender","es.upm.proyecto.jade.recomendador.agents.RecommendationAgent",agentParams).start();

            } 
            catch (StaleProxyException e) 
            {
                logger.error("Error al crear RecommendationAgent", e);
            }
        }).start();
    }
}
