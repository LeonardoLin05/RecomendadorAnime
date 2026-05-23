package es.upm.proyecto.jade.recomendador.behaviours;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class GroqService 
{
	private static final Logger logger = LoggerFactory.getLogger(GroqService.class);
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.3-70b-versatile";

    private final OkHttpClient client = new OkHttpClient();
    
    private String getApiKey() 
    {
        try 
        {
            List<String> lineas = Files.readAllLines(Paths.get(".env"));
            for (String linea : lineas) 
            {
                if (linea.startsWith("GROQ_API_KEY=")) 
                {
                    return linea.split("=")[1].trim();
                }
            }
        } 
        catch (Exception e)
        {
            logger.error("No se pudo leer el archivo .env", e);
        }
        return null;
    }
    
    public String cargarPromptSistema(String nombreFichero) 
    {
        try 
        {
            InputStream is = GroqService.class.getClassLoader().getResourceAsStream(nombreFichero);
            
            if (is == null) 
            {
                throw new IllegalArgumentException("No se encontró el archivo");
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) 
            {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } 
        catch (Exception e) 
        {
            logger.error("Error al leer el prompt del sistema", e);
            return "";
        }
    }
    
    public String llamarAGroq(String promptSistema, String mensajeUsuario) 
    {
        JSONObject json = new JSONObject();
        json.put("model", MODEL);
        json.put("temperature", 0.0);

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", promptSistema));
        messages.put(new JSONObject().put("role", "user").put("content", mensajeUsuario));
        json.put("messages", messages);

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
            .url(GROQ_URL)
            .post(RequestBody.create(json.toString(), mediaType))
            .addHeader("Authorization", "Bearer " + getApiKey())
            .addHeader("Content-Type", "application/json")
            .build();

        try (Response response = client.newCall(request).execute()) 
        {
            if (response.isSuccessful() && response.body() != null) 
            {
                String respuesta = response.body().string();
                return new JSONObject(respuesta)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            } 
            else 
            {
                logger.error("Error en llamada a Groq: {}", response.code());
            }
        } 
        catch (IOException e) 
        {
            logger.error("Error de conexión con Groq", e);
        }
        return null;
    }
    
    public String generarResumenYNota(String reviews, List<String> personalizado) 
    {
        String promptSistema = cargarPromptSistema("instruccionesResumen.txt");
        String reviewsLimitadas = reviews.length() > 3000 ? reviews.substring(0, 3000) : reviews;
        String mensajeUsuario = String.format("Preferencias del usuario: %s\n\nReviews del anime:\n%s",String.join(", ", personalizado),reviewsLimitadas);
        return llamarAGroq(promptSistema, mensajeUsuario);
    }
}
