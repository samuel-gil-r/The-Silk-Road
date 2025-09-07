/**
 * Representa una posición lógica en la ruta de la seda.
 * Convierte un índice (0..length-1) a coordenadas (x,y) en una
 * espiral cuadrada para dibujar en el Canvas.
 */
public final class Location {
    private final int index;

    // Tamaño de celda y centro del tablero (ajústalos si quieres)
    private static final int CELL = 30;
    private static final int CX = 300, CY = 300;

    /**
     * Crea una ubicación con el índice dado.
     * @param index posición lógica en la ruta.
     */
    public Location(int index){ this.index = index; }

    /** @return índice lógico de la ubicación. */
    public int index(){ return index; }

    /**
     * Convierte el índice a coordenadas (x,y) en espiral cuadrada.
     * @return arreglo de tamaño 2: [x, y] en píxeles.
     */
    public int[] toXY(){
        if(index == 0) return new int[]{CX, CY};

        // Cálculo compacto de espiral cuadrada (anillo, lado, desplazamiento)
        int layer = (int)Math.ceil((Math.sqrt(index + 1) - 1) / 2.0);
        int side  = 2*layer;
        int max   = (2*layer + 1)*(2*layer + 1) - 1;
        int d     = max - index; // distancia desde el final del anillo

        int x, y;
        if (d < side) {                     // lado inferior: →
            x =  layer - d;  y = -layer;
        } else if ((d -= side) < side) {    // lado izquierdo: ↑
            x = -layer;       y = -layer + d;
        } else if ((d -= side) < side) {    // lado superior: ←
            x = -layer + d;   y =  layer;
        } else {                             // lado derecho: ↓
            d -= side;        x =  layer;    y =  layer - d;
        }
        return new int[]{CX + x*CELL, CY + y*CELL};
    }
}
