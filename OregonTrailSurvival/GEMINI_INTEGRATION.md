# 🤖 Integración de Gemini API - Oregon Trail

## Descripción
Este proyecto integra la API de Google Gemini para generar diálogos dinámicos e inteligentes con NPCs (bots pasivos) en el juego Oregon Trail.

## Características
- ✅ Diálogos generados dinámicamente con IA
- ✅ NPCs contextuales (Comerciante, Viajero, Guía)
- ✅ Consejos personalizados según el estado del jugador
- ✅ Reacciones a eventos del juego
- ✅ Interfaz de diálogo visual

## Configuración

### 1. Obtener API Key de Gemini
1. Ve a [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Inicia sesión con tu cuenta de Google
3. Crea una nueva API key
4. Copia la API key generada

### 2. Configurar la API Key
Edita el archivo `src/main/resources/config/gemini.properties`:
```properties
gemini.api.key=TU_API_KEY_AQUI
```

O edita directamente en `src/main/java/service/GeminiService.java`:
```java
private static final String API_KEY = "TU_API_KEY_AQUI";
```

### 3. Instalar Dependencias
```bash
mvn clean install
```

## Uso en el Juego

### Crear un NPC
```java
NPC comerciante = new NPC(
    "npc_1",
    "Juan el Comerciante",
    "comerciante",
    10, 10
);
```

### Generar Diálogos
```java
// Saludo inicial
String saludo = comerciante.saludar();

// Diálogo contextual
String dialogo = comerciante.hablar("El jugador necesita suministros");

// Consejo personalizado
String consejo = comerciante.darConsejo(
    jugador.getSalud(),
    jugador.getComida(),
    jugador.getMunicion(),
    "Llanuras"
);
```

### Abrir Ventana de Diálogo
```java
// En GameViewController o cualquier controlador
FXMLLoader loader = new FXMLLoader(
    getClass().getResource("/ui/dialogo-view.fxml")
);
Parent root = loader.load();

DialogoViewController controller = loader.getController();
controller.setNPC(comerciante);
controller.setJugador(jugador);

Stage dialogStage = new Stage();
dialogStage.setTitle("Conversación con " + comerciante.getNombre());
dialogStage.setScene(new Scene(root));
dialogStage.show();
```

## Tipos de NPCs

### Comerciante
- Vende suministros
- Da información sobre precios
- Ofrece ofertas especiales

### Viajero
- Comparte experiencias del camino
- Da consejos de supervivencia
- Advierte sobre peligros

### Guía
- Conoce el terreno
- Informa sobre rutas
- Alerta sobre enemigos

## Ejemplos de Diálogos Generados

### Comerciante
> "¡Bienvenido, viajero! Veo que tu salud está baja. Tengo medicina fresca que podría salvarte la vida. ¿Qué te parece un trato?"

### Viajero
> "He cruzado estas montañas muchas veces. Con tan poca comida, no llegarás lejos. Te recomiendo cazar antes de continuar."

### Guía
> "Las Montañas Rocosas son traicioneras. He visto a muchos perder todo aquí. Mantén tu arma lista y no bajes la guardia."

## Limitaciones
- Requiere conexión a Internet
- Límite de requests según tu plan de Google Cloud
- Tiempo de respuesta: 1-3 segundos

## Troubleshooting

### Error: "API Key inválida"
- Verifica que copiaste correctamente la API key
- Asegúrate de que la API key esté activa en Google Cloud Console

### Error: "Timeout"
- Verifica tu conexión a Internet
- Aumenta el timeout en `GeminiService.java`

### Diálogos genéricos
- Si la API falla, se usan diálogos de respaldo predefinidos
- Revisa los logs para ver errores específicos

## Costos
- Gemini API tiene un tier gratuito generoso
- Consulta [precios actuales](https://ai.google.dev/pricing)

## Recursos
- [Documentación de Gemini API](https://ai.google.dev/docs)
- [Google AI Studio](https://makersuite.google.com/)
- [Ejemplos de código](https://github.com/google/generative-ai-docs)
