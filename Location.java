/**
 * Convierte un índice lógico (0..N-1) a coordenadas (x,y) en píxeles
 * siguiendo una ESPIRAL que inicia en la ESQUINA SUPERIOR-IZQUIERDA.
 * SilkRoad debe llamar a setSideForLength(length) antes de dibujar.
 */
public class Location {
    public static final int CELL = 30;   
    public static final int LEFT = 30;   
    public static final int TOP  = 30;   
    private static int SIDE = 1;

    private final int index;

    public Location(int index){ this.index = index; }

    /** Ajusta SIDE a un impar que cubre 'length' celdas. */
    public static void setSideForLength(int length){
        int s = (int)Math.ceil(Math.sqrt(length));
        if (s % 2 == 0) s++;           
        SIDE = Math.max(1, s);
    }

    /** Centro (x,y) en píxeles de la celda para este índice. */
    public int[] toXY(){
        int[] rc = spiralRC(index);           
        int cx = LEFT + rc[1]*CELL + CELL/2;  
        int cy = TOP  + rc[0]*CELL + CELL/2;  
        return new int[]{cx, cy};
    }

    
    private int[] spiralRC(int k){
        int top=0, left=0, bottom=SIDE-1, right=SIDE-1;
        int i=0, r=0, c=0;

        while(top<=bottom && left<=right){
            
            for(c=left, r=top; c<=right; c++, i++) if(i==k) return new int[]{r,c};
            top++;

            for(r=top, c=right; r<=bottom; r++, i++) if(i==k) return new int[]{r,c};
            right--;

            if(top>bottom || left>right) break;

            
            for(c=right, r=bottom; c>=left; c--, i++) if(i==k) return new int[]{r,c};
            bottom--;

            
            for(r=bottom, c=left; r>=top; r--, i++) if(i==k) return new int[]{r,c};
            left++;
        }
        
        return new int[]{Math.min(SIDE-1, Math.max(0,r)), Math.min(SIDE-1, Math.max(0,c))};
    }
}
