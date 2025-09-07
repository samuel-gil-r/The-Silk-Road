import java.util.ArrayList;

/**
 * Motor del simulador "The Silk Road" para el ciclo 1.
 * Administra la ruta, tiendas y robots; permite operaciones básicas,
 * consultas y reinicio del estado.
 */
public class SilkRoad {
    private final int length;
    private final ArrayList<Store> stores = new ArrayList<>();
    private final ArrayList<Robot> robots = new ArrayList<>();
    private boolean visible = false;
    private boolean ok = true;

    /**
     * Crea un simulador con la longitud indicada.
     * @param length número de posiciones válidas (>=1).
     */
    public SilkRoad(int length){
        if(length <= 0) throw new IllegalArgumentException("length > 0");
        this.length = length;
    }

    // -------------------- Operaciones sobre tiendas --------------------

    /**
     * Agrega una tienda en la ubicación dada con el dinero inicial.
     * @param location posición en la ruta.
     * @param tenges dinero inicial (>=0).
     */
    public void placeStore(int location, int tenges){
        if(!valid(location) || tenges < 0 || findStore(location) != null){ ok = false; return; }
        Store s = new Store(location, tenges);
        stores.add(s);
        if(visible) s.makeVisible();
        ok = true;
    }

    /**
     * Elimina la tienda de la ubicación dada (si existe).
     * @param location posición de la tienda a remover.
     */
    public void removeStore(int location){
        Store s = findStore(location);
        if(s == null){ ok = false; return; }
        s.makeInvisible();
        stores.remove(s);
        ok = true;
    }

    /** Reabastece todas las tiendas a su dinero inicial. */
    public void resupplyStores(){
        for(Store s : stores) s.reset();
        ok = true;
    }

    // -------------------- Operaciones sobre robots ---------------------

    /**
     * Coloca un robot en la ubicación dada (no se permite que dos robots
     * inicien en la misma ubicación).
     * @param location posición inicial del robot.
     */
    public void placeRobot(int location){
        if(!valid(location) || findRobotAt(location) != null){ ok = false; return; }
        Robot r = new Robot(location);
        robots.add(r);
        if(visible) r.makeVisible();
        ok = true;
    }

    /**
     * Elimina el primer robot encontrado en la ubicación dada.
     * @param location posición del robot a remover.
     */
    public void removeRobot(int location){
        Robot r = findRobotAt(location);
        if(r == null){ ok = false; return; }
        r.makeInvisible();
        robots.remove(r);
        ok = true;
    }

    /**
     * Mueve el primer robot ubicado en {@code location} una cantidad de pasos.
     * Si cae en una tienda con dinero, toma 1 tenge (regla simple de ganancia).
     * @param location ubicación actual del robot.
     * @param meters pasos a mover (positivo/negativo).
     */
    public void moveRobot(int location, int meters){
        Robot r = findRobotAt(location);
        if(r == null){ ok = false; return; }
        int nl = r.location() + meters;
        if(!valid(nl)){ ok = false; return; }
        r.moveTo(nl);
        Store s = findStore(nl);
        if(s != null && s.tenges() > 0){ s.setTenges(s.tenges() - 1); r.addProfit(1); }
        ok = true;
    }

    /** Devuelve todos los robots a su posición inicial y borra sus ganancias. */
    public void returnRobots(){
        for(Robot r : robots) r.reset();
        redraw();
        ok = true;
    }

    // -------------------- Estado global y consultas --------------------

    /**
     * Reinicia el simulador:
     * tiendas a dinero inicial, robots a posición inicial, ganancias a 0.
     */
    public void reboot(){
        for(Store s : stores) s.reset();
        for(Robot r : robots) r.reset();
        redraw();
        ok = true;
    }

    /** @return suma de las ganancias de todos los robots. */
    public int profit(){
        int p = 0;
        for(Robot r : robots) p += r.profit();
        return p;
    }

    /**
     * Información de tiendas ordenada por ubicación ascendente.
     * Cada fila = [location, tenges].
     */
    public int[][] stores(){
        sortStores();
        int[][] a = new int[stores.size()][2];
        for(int i = 0; i < stores.size(); i++){
            a[i][0] = stores.get(i).location();
            a[i][1] = stores.get(i).tenges();
        }
        return a;
    }

    /**
     * Información de robots ordenada por ubicación ascendente.
     * Cada fila = [location, profit].
     */
    public int[][] robots(){
        sortRobots();
        int[][] a = new int[robots.size()][2];
        for(int i = 0; i < robots.size(); i++){
            a[i][0] = robots.get(i).location();
            a[i][1] = robots.get(i).profit();
        }
        return a;
    }

    /** Muestra tiendas y robots en pantalla. */
    public void makeVisible(){ visible = true; redraw(); }

    /** Oculta todo en pantalla. */
    public void makeInvisible(){ visible = false; hideAll(); }

    /** Termina el simulador (oculta todo). */
    public void finish(){ makeInvisible(); }

    /** @return true si la última operación fue válida; false si falló. */
    public boolean ok(){ return ok; }

    // ------------------------- Auxiliares ------------------------------

    /** @return true si la ubicación está dentro de [0, length). */
    private boolean valid(int loc){ return loc >= 0 && loc < length; }

    /** Busca una tienda por ubicación. */
    private Store findStore(int loc){
        for(Store s : stores) if(s.location() == loc) return s;
        return null;
    }

    /** Busca el primer robot (por orden de inserción) en una ubicación. */
    private Robot findRobotAt(int loc){
        for(Robot r : robots) if(r.location() == loc) return r;
        return null;
    }

    /** Redibuja todos los elementos si el simulador está visible. */
    private void redraw(){
        if(!visible) return;
        for(Store s : stores) s.makeVisible();
        for(Robot r : robots) r.makeVisible();
    }

    /** Oculta todos los elementos. */
    private void hideAll(){
        for(Store s : stores) s.makeInvisible();
        for(Robot r : robots) r.makeInvisible();
    }

    /** Ordenamiento simple por location para tiendas. */
    private void sortStores(){
        for(int i=0;i<stores.size();i++)
            for(int j=i+1;j<stores.size();j++)
                if(stores.get(j).location() < stores.get(i).location()){
                    Store t = stores.get(i); stores.set(i, stores.get(j)); stores.set(j, t);
                }
    }

    /** Ordenamiento simple por location para robots. */
    private void sortRobots(){
        for(int i=0;i<robots.size();i++)
            for(int j=i+1;j<robots.size();j++)
                if(robots.get(j).location() < robots.get(i).location()){
                    Robot t = robots.get(i); robots.set(i, robots.get(j)); robots.set(j, t);
                }
    }
}
