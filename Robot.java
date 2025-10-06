public class Robot {
    private final int initialLocation;
    private int location;
    private int moved;
    private int collected;

    private Circle view;
    private boolean visible = false;

    public Robot(int location){
        this.initialLocation = location;
        this.location = location;
    }

    public int location(){ return location; }
    public int profit(){ return collected - moved; }

    public void addSteps(int meters){ moved += Math.abs(meters); }
    public void addCollected(int amount){ if(amount > 0) collected += amount; }

    public void reset(){
        location = initialLocation;
        moved = 0;
        collected = 0;
        if(view != null){
            int[] c = new Location(location).toXY();
            view.moveTo(c[0]-6, c[1]-6);
        }
    }

    public void moveTo(int newLoc){
        location = newLoc;
        if(view != null){
            int[] c = new Location(location).toXY();
            view.moveTo(c[0]-6, c[1]-6);
        }
    }

    public void makeVisible(){
        if(view == null){
            view = new Circle();
            view.changeSize(12);
            view.changeColor("red");
        }
        int[] c = new Location(location).toXY();
        view.moveTo(c[0]-6, c[1]-6);
        view.makeVisible();
        visible = true;
    }

    public void makeInvisible(){
        if(view != null && visible) view.makeInvisible();
        visible = false;
    }
}
