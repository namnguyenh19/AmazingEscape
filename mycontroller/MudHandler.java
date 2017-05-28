package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;

import java.util.HashMap;

/**
 * Created by NamNguyen1 on 27/5/17.
 */
public class MudHandler extends TrapHandler {
    public static final float SPEED_REDUCE = 0.6f;
    private int numMuds;

    public MudHandler(Car car, String type, Coordinate dest, boolean blocked, HashMap<Coordinate, MapTile> view){
        super(car,type,dest,blocked,view);
    }

    public boolean handleTrap(Path path) {
        int count = 0;
        for (Coordinate c : path.getTilesInPath()) {
            if (path.getTileName().get(c).equals("Mud"))
                count++;
            if (count > 2)
                return false;
        }
        return true;
    }

    private float predictSpeed(){
        return 0;
    }
}
