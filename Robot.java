public class Robot {
    private final int initialLocation;
    private int location;
    private int profit;
    private Circle view;

    public Robot(int location){
        this.location = location;
        this.initialLocation = location;
    }

    public int location(){ return location; }
    public int profit(){ return profit; }
    public void addProfit(int v){ profit += v; }

    public void reset(){ location = initialLocation; profit = 0; }

    public void moveTo(int newLoc){
        location = newLoc;
        if(view!=null){
            int[] p = new Location(location).toXY();
            view.moveTo(p[0]-7, p[1]-7);
        }
    }

    public void makeVisible(){
        if(view==null){
            view = new Circle();
            view.changeSize(14);
            view.changeColor("red");
        }
        int[] p = new Location(location).toXY();
        view.moveTo(p[0]-7, p[1]-7);
        view.makeVisible();
    }

    public void makeInvisible(){ if(view!=null) view.makeInvisible(); }
}
