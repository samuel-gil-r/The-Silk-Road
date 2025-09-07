import java.util.ArrayList;

public class SilkRoad {
    private final int length;
    private final ArrayList<Store> stores = new ArrayList<>();
    private final ArrayList<Robot> robots = new ArrayList<>();
    private boolean visible = false;
    private boolean ok = true;

    public SilkRoad(int length){
        if(length<=0) throw new IllegalArgumentException("length>0");
        this.length = length;
    }

    // ----- tiendas -----
    public void placeStore(int location, int tenges){
        if(!valid(location) || tenges<0){ ok=false; return; }
        Store s = findStore(location);
        if(s!=null){ ok=false; return; }
        s = new Store(location, tenges);
        stores.add(s);
        if(visible) s.makeVisible();
        ok = true;
    }

    public void removeStore(int location){
        Store s = findStore(location);
        if(s==null){ ok=false; return; }
        s.makeInvisible();
        stores.remove(s);
        ok = true;
    }

    public void resupplyStores(){ for(Store s:stores) s.reset(); ok = true; }

    // ----- robots -----
    public void placeRobot(int location){
        if(!valid(location)){ ok=false; return; }
        if(findRobotAt(location)!=null){ ok=false; return; }
        Robot r = new Robot(location);
        robots.add(r);
        if(visible) r.makeVisible();
        ok = true;
    }

    public void removeRobot(int location){
        Robot r = findRobotAt(location);
        if(r==null){ ok=false; return; }
        r.makeInvisible();
        robots.remove(r);
        ok = true;
    }

    /** mueve el primer robot que esté en 'location' 'meters' posiciones (+/-) */
    public void moveRobot(int location, int meters){
        Robot r = findRobotAt(location);
        if(r==null){ ok=false; return; }
        int nl = r.location() + meters;
        if(!valid(nl)){ ok=false; return; }
        r.moveTo(nl);
        Store s = findStore(nl);
        if(s!=null && s.tenges()>0){ s.setTenges(s.tenges()-1); r.addProfit(1); }
        ok = true;
    }

    public void returnRobots(){ for(Robot r:robots) r.reset(); redraw(); ok = true; }

    // ----- estado global -----
    public void reboot(){
        for(Store s:stores) s.reset();
        for(Robot r:robots) r.reset();
        redraw();
        ok = true;
    }

    public int profit(){
        int p=0; for(Robot r:robots) p+=r.profit(); return p;
    }

    /** stores: [location, tenges] ordenadas por location ascendente (simple selection-sort) */
    public int[][] stores(){
        sortStores();
        int[][] a = new int[stores.size()][2];
        for(int i=0;i<stores.size();i++){ a[i][0]=stores.get(i).location(); a[i][1]=stores.get(i).tenges(); }
        return a;
    }

    /** robots: [location, profit] ordenados por location ascendente */
    public int[][] robots(){
        sortRobots();
        int[][] a = new int[robots.size()][2];
        for(int i=0;i<robots.size();i++){ a[i][0]=robots.get(i).location(); a[i][1]=robots.get(i).profit(); }
        return a;
    }

    public void makeVisible(){ visible = true; redraw(); }
    public void makeInvisible(){ visible = false; hideAll(); }
    public void finish(){ makeInvisible(); }
    public boolean ok(){ return ok; }

    // ----- helpers cortos -----
    private boolean valid(int loc){ return loc>=0 && loc<length; }

    private Store findStore(int loc){
        for(Store s:stores) if(s.location()==loc) return s; return null;
    }

    private Robot findRobotAt(int loc){
        for(Robot r:robots) if(r.location()==loc) return r; return null; // primero por inserción
    }

    private void redraw(){
        if(!visible) return;
        for(Store s:stores) s.makeVisible();
        for(Robot r:robots) r.makeVisible();
    }

    private void hideAll(){
        for(Store s:stores) s.makeInvisible();
        for(Robot r:robots) r.makeInvisible();
    }

    private void sortStores(){ // orden asc por location (simple y corto)
        for(int i=0;i<stores.size();i++)
            for(int j=i+1;j<stores.size();j++)
                if(stores.get(j).location()<stores.get(i).location()){
                    Store t=stores.get(i); stores.set(i,stores.get(j)); stores.set(j,t);
                }
    }

    private void sortRobots(){
        for(int i=0;i<robots.size();i++)
            for(int j=i+1;j<robots.size();j++)
                if(robots.get(j).location()<robots.get(i).location()){
                    Robot t=robots.get(i); robots.set(i,robots.get(j)); robots.set(j,t);
                }
    }
}
