public class Store {
    private static final int SZ = 16;

    private final int location;
    private final int initialTenges;
    private int tenges;

    private final Rectangle rectView;
    private final Triangle  triView;
    private boolean visible = false;
    private boolean positioned = false;

    public Store(int location, int tenges){
        this.location = location;
        this.initialTenges = tenges;
        this.tenges = Math.max(0, tenges);

        rectView = new Rectangle();
        rectView.changeColor("yellow");
        rectView.changeSize(SZ, SZ);

        triView = new Triangle();
        triView.changeColor("green");
        triView.changeSize(SZ, SZ);
    }

    public int location(){ return location; }
    public int tenges(){ return tenges; }
    public int initialTenges(){ return initialTenges; }

    public void setTenges(int v){
        this.tenges = Math.max(0, v);
        if(visible) refreshView();
    }

    public void reset(){
        tenges = initialTenges;
        if(visible) refreshView();
    }

    public void makeVisible(){
        if(!positioned){
            int[] c = new Location(location).toXY();
            int rx = c[0] - SZ/2, ry = c[1] - SZ/2;
            rectView.moveHorizontal(rx - 70);
            rectView.moveVertical  (ry - 15);
            int ax = c[0], ay = c[1] - SZ/2;
            triView.moveHorizontal(ax - 140);
            triView.moveVertical  (ay - 15);
            positioned = true;
        }
        visible = true;
        refreshView();
    }

    public void makeInvisible(){
        if(!visible) return;
        rectView.makeInvisible();
        triView.makeInvisible();
        visible = false;
    }

    private void refreshView(){
        if(tenges > 0){
            triView.makeInvisible();
            rectView.makeVisible();
        }else{
            rectView.makeInvisible();
            triView.makeVisible();
        }
    }
}
