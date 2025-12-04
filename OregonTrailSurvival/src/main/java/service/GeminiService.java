package service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Servicio para integrar la API de Gemini y generar diálogos dinámicos
 */
public class GeminiService {

    private static final String API_KEY = "TU_API_KEY_AQUI"; // Reemplazar con tu API key
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    
    private final OkHttpClient client;
    private final Gson gson;

    public GeminiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    /**
     * Genera un diálogo contextual basado en la situación del juego
     */
    public String generarDialogo(String contexto, String tipoPersonaje) {
        try {
            String prompt = construirPrompt(contexto, tipoPersonaje);
            String respuesta = llamarGeminiAPI(prompt);
            return procesarRespuesta(respuesta);
        } catch (Exception e) {
            System.err.println("Error al generar diálogo con Gemini: " + e.getMessage());
            return obtenerDialogoFallback(tipoPersonaje);
        }
    }

    /**
     * Construye el prompt para Gemini según el contexto
     */
    private String construirPrompt(String contexto, String tipoPersonaje) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Eres un personaje en el juego Oregon Trail. ");
        
        switch (tipoPersonaje.toLowerCase()) {
            case "comerciante":
                prompt.append("Eres un comerciante amigable que vende suministros. ");
                break;
            case "viajero":
                prompt.append("Eres un viajero experimentado que da consejos. ");
                break;
            case "guia":
                prompt.append("Eres un guía del camino que conoce los peligros. ");
                break;
            default:
                prompt.append("Eres un personaje neutral. ");
        }
        
        prompt.append("Contexto actual: ").append(contexto).append(". ");
        prompt.append("Genera un diálogo corto (máximo 2 oraciones) apropiado para la situación. ");
        prompt.append("El diálogo debe ser inmersivo y estar ambientado en el viejo oeste americano de 1848.");
        
        return prompt.toString();
    }

    /**
     * Llama a la API de Gemini
     */
    private String llamarGeminiAPI(String prompt) throws IOException {
        // Construir el JSON del request
        JsonObject requestBody = new JsonObject();
        JsonObject contents = new JsonObject();
        JsonObject parts = new JsonObject();
        
        parts.addProperty("text", prompt);
        contents.add("parts", gson.toJsonTree(new JsonObject[]{gson.fromJson(parts.toString(), JsonObject.class)}));
        requestBody.add("contents", gson.toJsonTree(new JsonObject[]{gson.fromJson(contents.toString(), JsonObject.class)}));

        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(API_URL + "?key=" + API_KEY)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error en la API: " + response.code());
            }
            return response.body().string();
        }
    }

    /**
     * Procesa la respuesta de Gemini
     */
    private String procesarRespuesta(String jsonResponse) {
        try {
            JsonObject response = gson.fromJson(jsonResponse, JsonObject.class);
            return response.getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();
        } catch (Exception e) {
            System.err.println("Error procesando respuesta de Gemini: " + e.getMessage());
            return "...";
        }
    }

    /**
     * Diálogos de respaldo si falla la API
     */
    private String obtenerDialogoFallback(String tipoPersonaje) {
        switch (tipoPersonaje.toLowerCase()) {
            case "comerciante":
                return "¡Bienvenido viajero! Tengo los mejores suministros para tu viaje.";
            case "viajero":
                return "El camino es largo y peligroso. Asegúrate de llevar suficiente comida.";
            case "guia":
                return "Ten cuidado con los enemigos en el camino. Mantén tu arma lista.";
            default:
                return "Buena suerte en tu viaje hacia el oeste.";
        }
    }

    /**
     * Genera un diálogo para un evento específico del juego
     */
    public String generarDialogoEvento(String evento, String escenario, int salud, int recursos) {
        String contexto = String.format(
                "Evento: %s. Escenario: %s. Salud del jugador: %d. Recursos disponibles: %d",
                evento, escenario, salud, recursos
        );
        return generarDialogo(contexto, "guia");
    }

    /**
     * Genera un consejo basado en el estado del jugador
     */
    public String generarConsejo(int salud, int comida, int municion, String escenario) {
        String contexto = String.format(
                "El jugador está en %s con %d de salud, %d de comida y %d de munición",
                escenario, salud, comida, municion
        );
        
        if (salud < 2) {
            contexto += ". El jugador tiene poca salud";
        }
        if (comida < 5) {
            contexto += ". El jugador tiene poca comida";
        }
        if (municion < 10) {
            contexto += ". El jugador tiene poca munición";
        }
        
        return generarDialogo(contexto, "viajero");
    }
}
