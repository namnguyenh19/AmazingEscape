package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.HashMap;

/**
 * TrapHandler for mud trap.
 * 
 * Created by Project Group 37.
 */
public class MudHandler extends TrapHandler {


    public MudHandler(boolean blocked, HashMap<Coordinate, MapTile> view){
        super(blocked,view);
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

}
