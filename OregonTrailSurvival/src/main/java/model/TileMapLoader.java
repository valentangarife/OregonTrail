package model;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class TileMapLoader {

    public static TileMap load(String resourcePath) {
        try (InputStream is = TileMapLoader.class.getResourceAsStream(resourcePath)) {

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            java.util.List<String> lineas = new java.util.ArrayList<>();

            String line;
            while ((line = br.readLine()) != null)
                if (!line.trim().isEmpty())
                    lineas.add(line);

            int rows = lineas.size();
            int cols = lineas.get(0).length();

            TileMap map = new TileMap(rows, cols);

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    char ch = lineas.get(r).charAt(c);

                    // Si es un dígito, usar directamente
                    if (Character.isDigit(ch)) {
                        int value = ch - '0';
                        map.setTile(r, c, value);
                    } else {
                        // Convertir letras a números (compatibilidad)
                        switch (ch) {
                            case 'S': map.setTile(r, c, 2); break; // Arena
                            case 'I': map.setTile(r, c, 2); break; // Inicio (arena)
                            case 'G': map.setTile(r, c, 1); break; // Grass → Árbol
                            case 'T': map.setTile(r, c, 1); break; // Tree → Árbol
                            case 'B': map.setTile(r, c, 1); break; // Bush → Árbol
                            case 'W': map.setTile(r, c, 3); break; // Water → Agua
                            case 'P': map.setTile(r, c, 5); break; // Portal
                            case 'E': map.setTile(r, c, 1); break; // Enemy spawn → Árbol
                            default:  map.setTile(r, c, 1); break; // Por defecto árbol
                        }
                    }
                }
            }

            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
