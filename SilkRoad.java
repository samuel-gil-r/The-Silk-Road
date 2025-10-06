import java.util.*;

/** Versión compacta de SilkRoad que conserva la API del diagrama. */
public class SilkRoad {
    private final int length;
    private final ArrayList<Store> stores = new ArrayList<>();
    private final ArrayList<Robot> robots = new ArrayList<>();
    private final HashMap<Integer,Integer> emptiedTimes = new HashMap<>();
    private boolean visible = false, ok = true;
    private final ArrayList<Rectangle> roadTiles = new ArrayList<>();
    private final ArrayList<Circle> roadDots  = new ArrayList<>();
    private Rectangle barBack, barFill;
    private int profitMax = 1;
    /** Crea ruta de tamaño dado. */
    public SilkRoad(int length){
        if(length <= 0) throw new IllegalArgumentException("length>0");
        this.length = length;
        buildRoadViews();
        buildProfitBar();
        Canvas.getCanvas().eraseAll();
    }
    /** Crea tienda en location con dinero inicial. */
    public void placeStore(int location, int tenges){
        if(!valid(location) || tenges < 0 || findStore(location)!=null){ ok=false; return; }
        Store s = new Store(location, tenges);
        stores.add(s);
        if(visible) s.makeVisible();
        recalcProfitMax(); updateProfitBar(); ok=true;
    }
    /** Elimina la tienda exacta. */
    public void removeStore(int location){
        Store s = findStore(location); if(s==null){ ok=false; return; }
        s.makeInvisible(); stores.remove(s);
        recalcProfitMax(); updateProfitBar(); ok=true;
    }
    /** Crea robot en location. */
    public void placeRobot(int location){
        if(!valid(location) || findRobotAt(location)!=null){ ok=false; return; }
        Robot r = new Robot(location); robots.add(r);
        if(visible) r.makeVisible(); ok=true;
    }
    /** Elimina el robot exacto. */
    public void removeRobot(int location){
        Robot r = findRobotAt(location); if(r==null){ ok=false; return; }
        r.makeInvisible(); robots.remove(r); updateProfitBar(); ok=true;
    }
    /** Mueve un robot 'location' 'meters' pasos, cobra distancia y recoge si cae en tienda. */
    public void moveRobot(int location, int meters){
        Robot r=findRobotAt(location); if(r==null){ ok=false; return; }
        int nl=r.location()+meters; if(!valid(nl)){ ok=false; return; }
        r.addSteps(meters); r.moveTo(nl);
        Store s=findStore(nl);
        if(s!=null && s.tenges()>0){ r.addCollected(s.tenges()); s.setTenges(0); emptiedTimes.merge(nl,1,Integer::sum); }
        updateProfitBar(); ok=true;
    }
    /** Restaura el dinero de todas las tiendas. */
    public void resupplyStores(){ for(Store s:stores) s.reset(); recalcProfitMax(); updateProfitBar(); ok=true; }
    /** Regresa cada robot a su origen y borra su ganancia. */
    public void returnRobots(){ for(Robot r:robots) r.reset(); redraw(); updateProfitBar(); ok=true; }
    /** Resetea robots y tiendas (no cambia tamaño/espiral). */
    public void reboot(){ for(Store s:stores) s.reset(); for(Robot r:robots) r.reset(); redraw(); recalcProfitMax(); updateProfitBar(); ok=true; }

    /** Ganancia total de todos los robots. */
    public int profit(){ int p=0; for(Robot r:robots) p+=r.profit(); return p; }
    /** [[loc,tenges],…] ordenado. */
    public int[][] stores(){
        ArrayList<Store> ss=new ArrayList<>(stores);
        ss.sort((a,b)->a.location()-b.location());
        int[][] out=new int[ss.size()][2];
        for(int i=0;i<ss.size();i++){ out[i][0]=ss.get(i).location(); out[i][1]=ss.get(i).tenges(); }
        return out;
    }
    /** [[loc,profit],…] ordenado. */
    public int[][] robots(){
        ArrayList<Robot> rr=new ArrayList<>(robots);
        rr.sort((a,b)->a.location()-b.location());
        int[][] out=new int[rr.size()][2];
        for(int i=0;i<rr.size();i++){ out[i][0]=rr.get(i).location(); out[i][1]=rr.get(i).profit(); }
        return out;
    }
    /** Muestra visual. */
    public void makeVisible(){ visible=true; showRoad(true); showProfitBar(true); redraw(); }
    /** Oculta visual. */
    public void makeInvisible(){ visible=false; showRoad(false); showProfitBar(false); hideAll(); }
    /** Alias de ocultar. */
    public void finish(){ makeInvisible(); }
    /** Última operación válida. */
    public boolean ok(){ return ok; }
    /** Ejecuta días; devuelve ganancia máxima por día (no modifica posiciones). */
    public long[] simulateDaysGreedy(int[][] days){
        long[] out=new long[days.length];
        ArrayList<Integer> robotsPos=new ArrayList<>();
        ArrayList<int[]> storesCfg=new ArrayList<>();
        for(int d=0; d<days.length; d++){
            int[] e=days[d];
            if(e[0]==1){ placeRobot(e[1]); reboot(); }
            else{ placeStore(e[1], e[2]); }
            robotsPos.clear(); storesCfg.clear();
            for(int[] rp:robots()) robotsPos.add(rp[0]);
            for(int[] sp:stores()) if(sp[1]>0) storesCfg.add(new int[]{sp[0], sp[1]});
            out[d]=greedyDayWithLookAhead(robotsPos, storesCfg);
        }
        return out;
    }
    /** Estrategia greedy con vista de dos pasos para maximizar ganancia diaria.
     *  Usa en simulateDaysGreedy(). */
    private long greedyDayWithLookAhead(List<Integer> robotsPos, List<int[]> storesCfg){
        if(robotsPos.isEmpty()||storesCfg.isEmpty()) return 0L;
        ArrayList<Integer> ends=new ArrayList<>(robotsPos);
        ArrayList<int[]> free=new ArrayList<>(storesCfg);
        long total=0;

        long best=Long.MIN_VALUE; int br=-1, bs=-1;
        for(int r=0;r<ends.size();r++){
            int start=ends.get(r);
            for(int i=0;i<free.size();i++){
                int[] a=free.get(i); long g1=a[1]-Math.abs(start-a[0]);
                long g2=0; for(int j=0;j<free.size();j++){ if(j==i) continue;
                    int[] b=free.get(j); g2=Math.max(g2, b[1]-Math.abs(a[0]-b[0])); }
                long sc=g1+(g2>0?g2:0);
                if(sc>best){best=sc;br=r;bs=i;}
            }
        }
        if(bs!=-1){
            int[] pick=free.get(bs); long gain=pick[1]-Math.abs(ends.get(br)-pick[0]);
            if(gain>0){ total+=gain; ends.set(br,pick[0]); free.remove(bs); }
        }

        while(free.size()>=2){
            long bestPair=0; int rix=-1,i1=-1,i2=-1;
            for(int r=0;r<ends.size();r++){
                int cur=ends.get(r);
                for(int i=0;i<free.size();i++) for(int j=i+1;j<free.size();j++){
                    int[] a=free.get(i), b=free.get(j);
                    long g=a[1]-Math.abs(cur-a[0]) + b[1]-Math.abs(a[0]-b[0]);
                    if(g>bestPair){bestPair=g; rix=r; i1=i; i2=j;}
                }
            }
            if(bestPair<=0) break;
            total+=bestPair;
            int[] b=free.get(i2);
            ends.set(rix, b[0]);
            free.remove(i2); free.remove(i1);
        }

        while(!free.isEmpty()){
            long add=0; int rix=-1, ix=-1;
            for(int r=0;r<ends.size();r++){
                int cur=ends.get(r);
                for(int i=0;i<free.size();i++){
                    int[] s=free.get(i); long g=s[1]-Math.abs(cur-s[0]);
                    if(g>add){add=g; rix=r; ix=i;}
                }
            }
            if(add<=0) break;
            total+=add; int[] pick=free.remove(ix); ends.set(rix,pick[0]);
        }
        return total;
    }
      /** Verifica si la posición es válida en la carretera.
     *  Usa en moveRobot(), moveRobots(), greedyDayWithLookAhead(). */
    private boolean valid(int loc){ return loc>=0 && loc<length; }
     /** Busca una tienda por su posición.
     *  Usa en getStoreTengesAt(), moveRobot(), resupplyStores(). */
    private Store findStore(int loc){ for(Store s:stores) if(s.location()==loc) return s; return null; }
    /** Busca un robot por su posición.
     *  Usa en moveRobot(), removeRobot(), returnRobots(). */
    private Robot findRobotAt(int loc){ for(Robot r:robots) if(r.location()==loc) return r; return null; }
     /** Redibuja robots y tiendas visibles.
     *  Usa en reboot(), makeVisible(), moveRobot(). */
    private void redraw(){ if(!visible) return; for(Store s:stores) s.makeVisible(); for(Robot r:robots) r.makeVisible(); }
      /** Oculta todos los objetos visuales.
     *  Usa en reboot(), makeInvisible(), finish(). */
    private void hideAll(){ for(Store s:stores) s.makeInvisible(); for(Robot r:robots) r.makeInvisible(); }
    /** Crea la visual de la carretera.
     *  Usa en constructor SilkRoad(). */
    private void buildRoadViews(){
        final int TILE=20, DOT=6; Location.setSideForLength(length);
        for(int i=0;i<length;i++){
            int[] c=new Location(i).toXY();
            Rectangle t=new Rectangle(); t.changeColor("blue"); t.changeSize(TILE,TILE);
            t.moveHorizontal(c[0]-TILE/2-70); t.moveVertical(c[1]-TILE/2-15); roadTiles.add(t);
            Circle d=new Circle(); d.changeColor("white"); d.changeSize(DOT);
            d.moveTo(c[0]-DOT/2, c[1]-DOT/2); roadDots.add(d);
        }
    }
    /** Muestra u oculta la carretera.
     *  Usa en makeVisible(), makeInvisible(). */
    private void showRoad(boolean on){
        for(Rectangle r:roadTiles){ if(on) r.makeVisible(); else r.makeInvisible(); }
        for(Circle c:roadDots){ if(on) c.makeVisible(); else c.makeInvisible(); }
    }
     /** Construye la barra de ganancias.
     *  Usa en constructor SilkRoad(). */
    private void buildProfitBar(){
        barBack=new Rectangle(); barBack.changeColor("black"); barBack.changeSize(20,320);
        int x=240,y=560; barBack.moveHorizontal(x-70); barBack.moveVertical(y-15);
        barFill=new Rectangle(); barFill.changeColor("yellow"); barFill.changeSize(16,0);
        barFill.moveHorizontal((x+2)-70); barFill.moveVertical((y+2)-15);
    }
    /** Muestra u oculta la barra de ganancias.
     *  Usa en makeVisible(), makeInvisible(). */
    private void showProfitBar(boolean on){
        if(on){ barBack.makeVisible(); barFill.makeVisible(); }
        else { barBack.makeInvisible(); barFill.makeInvisible(); }
        updateProfitBar();
    }
    /** Recalcula el máximo posible de ganancias.
     *  Usa en resupplyStores(), reboot(). */
    private void recalcProfitMax(){ int sum=0; for(Store s:stores) sum+=s.initialTenges(); profitMax=Math.max(1,sum); }
     /** Actualiza el tamaño de la barra de ganancias.
     *  Usa en showProfitBar(), moveRobot(). */
    private void updateProfitBar(){
        int max=316, cur=profit(); int w=(int)Math.round(max*(cur/(double)profitMax));
        if(w<0) w=0; if(w>max) w=max; barFill.changeSize(16,w);
    }
    /** Devuelve el dinero actual de una tienda o -1 si no existe.
     *  Usa en pruebas y métodos públicos. */
    public int getStoreTengesAt(int location){
        Store s=findStore(location); return (s==null)?-1:s.tenges();
    }
}
