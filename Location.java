public final class Location {
    private final int index;

    // tamaño de celda y centro en pantalla (ajústalos si quieres)
    private static final int CELL = 30;
    private static final int CX = 300, CY = 300;

    public Location(int index){ this.index = index; }
    public int index(){ return index; }

    /** Índice → (x,y) en espiral cuadrada. Algoritmo corto y directo. */
    public int[] toXY(){
        if(index==0) return new int[]{CX, CY};
        int layer = (int)Math.ceil((Math.sqrt(index+1)-1)/2.0);
        int side  = 2*layer;
        int max   = (2*layer+1)*(2*layer+1)-1;
        int d     = max - index;          // distancia desde el final del anillo
        int x=0,y=0;
        if(d < side){            // lado abajo: → 
            x =  layer - d; y = -layer;
        } else if((d-=side) < side){ // lado izq: ↑
            x = -layer; y = -layer + d;
        } else if((d-=side) < side){ // lado arriba: ←
            x = -layer + d; y =  layer;
        } else {                     // lado der: ↓
            d -= side; x =  layer; y =  layer - d;
        }
        return new int[]{CX + x*CELL, CY + y*CELL};
    }
}
