/**
 * Tienda de la ruta. Tiene una ubicación y una cantidad de tenges.
 * Se dibuja como un rectángulo amarillo.
 */
public class Store {
    private final int location;
    private final int initialTenges;
    private int tenges;
    private Rectangle view;

    /**
     * Crea una tienda.
     * @param location ubicación lógica donde se coloca.
     * @param tenges dinero inicial.
     */
    public Store(int location, int tenges){
        this.location = location;
        this.tenges = tenges;
        this.initialTenges = tenges;
    }

    /** @return ubicación de la tienda. */
    public int location(){ return location; }

    /** @return dinero disponible. */
    public int tenges(){ return tenges; }

    /** Establece el dinero actual. */
    public void setTenges(int v){ tenges = v; }

    /** Restaura el dinero al valor inicial. */
    public void reset(){ tenges = initialTenges; }

    /**
     * Dibuja/actualiza la tienda en la pantalla.
     * Nota: como Rectangle no tiene moveTo, la posicionamos
     * moviéndola desde su valor por defecto (70,15).
     */
    public void makeVisible(){
        int[] p = new Location(location).toXY();
        int targetX = p[0] - 9; // centrar rectángulo 18x18
        int targetY = p[1] - 9;

        if(view == null){
            view = new Rectangle();
            view.changeColor("yellow");
            view.changeSize(18,18);
            view.makeVisible();
            // Rectangle por defecto arranca en (70,15)
            view.moveHorizontal(targetX - 70);
            view.moveVertical(targetY - 15);
        } else {
            view.makeVisible(); // ya está en su sitio
        }
    }

    /** Oculta la tienda si está visible. */
    public void makeInvisible(){ if(view != null) view.makeInvisible(); }
}
