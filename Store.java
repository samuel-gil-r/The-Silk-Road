import java.awt.*;

public class Store {
    private final int location;
    private final int initialTenges;
    private int tenges;
    private Rectangle view;

    public Store(int location, int tenges){
        this.location = location;
        this.tenges = tenges;
        this.initialTenges = tenges;
    }

    public int location(){ return location; }
    public int tenges(){ return tenges; }
    public void setTenges(int v){ tenges = v; }
    public void reset(){ tenges = initialTenges; }

    public void makeVisible(){
        if(view==null){
            view = new Rectangle();
            view.changeColor("yellow");
            view.changeSize(18,18);
            int[] p = new Location(location).toXY();
            view.makeVisible();
            view.moveHorizontal(p[0]-9 - getX(view));
            view.moveVertical(  p[1]-9 - getY(view));
        } else view.makeVisible();
    }

    public void makeInvisible(){ if(view!=null) view.makeInvisible(); }

    // utilidades mínimas para posicionar (no expuestas)
    private int getX(Rectangle r){ return 0; } // BlueJ no expone getters; movemos por delta
    private int getY(Rectangle r){ return 0; }
}
