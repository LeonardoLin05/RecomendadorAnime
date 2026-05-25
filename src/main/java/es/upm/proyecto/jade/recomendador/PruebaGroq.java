package es.upm.proyecto.jade.recomendador;

import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

public class PruebaGroq 
{
	public static void main(String[] args) 
	{
        String apiKey = Config.getGroqApiKey();
        String modelo = "llama-3.3-70b-versatile";

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        String instruccionesPrompt = cargarPromptSistema();
        String peticionUsuarioPrompt = "Quiero un anime de deportes, que contenga deportes de combate, en concreto boxeo, que sea alrededor de 2010, que el protagonista sea joven y tenga alrededor de 25 episodios.";
        		// "quiero ver una pelicula anime de soldados de la segudna guerra mundial que haya finalizado, que el protagonista sea un chico joven y sea toda una experencia dramataica, aparte que sea de antes de los 2000 y que los espisodios no duren mas de 30 minutos";
        JSONObject json = new JSONObject();
        json.put("model", modelo);
        json.put("temperature", 0.0);

        JSONArray messages = new JSONArray();

        JSONObject msgSystem = new JSONObject();
        msgSystem.put("role", "system");
        msgSystem.put("content", instruccionesPrompt);
        messages.put(msgSystem);

        JSONObject msgUser = new JSONObject();
        msgUser.put("role", "user");
        msgUser.put("content", peticionUsuarioPrompt);
        messages.put(msgUser);

        json.put("messages", messages);

        Request request = new Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .post(RequestBody.create(json.toString(), mediaType)) 
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) 
        {
            if (response.isSuccessful() && response.body() != null) 
            {
                String respuesta = response.body().string();
                System.out.println("Éxito. Respuesta de Groq:\n" + respuesta);
                
                JSONObject jsonRespuesta = new JSONObject(respuesta);
                String contenidoIA = jsonRespuesta.getJSONArray("choices")
                                                  .getJSONObject(0)
                                                  .getJSONObject("message")
                                                  .getString("content");
                
                System.out.println("\n El JSON que ha extraído la IA es:\n" + contenidoIA);
                
                System.out.println("Peticiones restantes hoy: " + response.header("x-ratelimit-remaining-requests"));
                System.out.println("Tokens restantes este minuto: " + response.header("x-ratelimit-remaining-tokens"));
            } 
            else 
            {
                System.out.println("Error: " + response.code());
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
	}
	
	public static String cargarPromptSistema() 
	{
        try 
        {
            InputStream is = PruebaGroq.class.getClassLoader().getResourceAsStream("instruccionesPrompt.txt");
            
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
            System.out.println("Error al leer el archivo de instrucciones:");
            e.printStackTrace();
            return "";
        }
    }
	
	public static class Config 
	{

	    public static String getGroqApiKey() 
	    {
	        try {
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
	            System.err.println("No se pudo leer el archivo .env");
	        }
	        return null; // Si no lo encuentra, devuelve null
	    }
	}
}
