package model;

public class TileMap {

    // Códigos de tiles (consistentes con los mapas)
    public static final int ARBOL = 1;     // Árbol / NO caminable
    public static final int ARENA = 2;     // Arena / CAMINABLE
    public static final int AGUA = 3;      // Agua / NO caminable
    public static final int PORTAL = 4;    // Portal tipo 1
    public static final int PORTAL2 = 5;   // Portal tipo 2
    
    // Alias para compatibilidad
    public static final int MURO = 1;      // Alias de ARBOL
    public static final int SUELO = 2;     // Alias de ARENA
    public static final int INICIO = 2;    // Inicio también es arena

    private int[][] tiles;
    private int rows;
    private int cols;

    public TileMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        tiles = new int[rows][cols];
    }

    public int getTile(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) return MURO;
        return tiles[r][c];
    }

    public void setTile(int r, int c, int value) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) return;
        tiles[r][c] = value;
    }

    public boolean esCaminable(int r, int c) {
        int t = getTile(r, c);
        // Solo arena (2) y portales (4, 5) son caminables
        return (t == ARENA || t == PORTAL || t == PORTAL2);
    }

    public boolean esPortal(int r, int c) {
        int t = getTile(r, c);
        return (t == PORTAL || t == PORTAL2);
    }

    public boolean esInicio(int r, int c) {
        return getTile(r, c) == ARENA; // Inicio es arena
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
}
