/**
 * Robot que recorre la ruta. Acumula ganancias y se dibuja como un círculo rojo.
 */
public class Robot {
    private final int initialLocation;
    private int location;
    private int profit;
    private Circle view;

    /**
     * Crea un robot en la posición dada.
     * @param location posición inicial.
     */
    public Robot(int location){
        this.location = location;
        this.initialLocation = location;
    }

    /** @return posición actual del robot. */
    public int location(){ return location; }

    /** @return ganancia acumulada del robot. */
    public int profit(){ return profit; }

    /** Suma a la ganancia acumulada. */
    public void addProfit(int v){ profit += v; }

    /** Vuelve a la posición inicial y pone ganancias en cero. */
    public void reset(){ location = initialLocation; profit = 0; }

    /**
     * Cambia la posición del robot y actualiza el dibujo.
     * @param newLoc nueva ubicación lógica.
     */
    public void moveTo(int newLoc){
        location = newLoc;
        if(view != null){
            int[] xy = new Location(location).toXY();
            view.moveTo(xy[0] - 7, xy[1] - 7);
        }
    }

    /** Muestra el robot en pantalla (círculo rojo). */
    public void makeVisible(){
        if(view == null){
            view = new Circle();
            view.changeSize(14);
            view.changeColor("red");
        }
        int[] xy = new Location(location).toXY();
        view.moveTo(xy[0] - 7, xy[1] - 7);
        view.makeVisible();
    }

    /** Oculta el robot. */
    public void makeInvisible(){ if(view != null) view.makeInvisible(); }
}
