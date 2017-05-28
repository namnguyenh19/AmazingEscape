package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;

import java.util.HashMap;

/**
 * Created by NamNguyen1 on 27/5/17.
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
