package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NamNguyen1 on 27/5/17.
 */
public class GrassHandler extends TrapHandler{

    private WorldSpatial.RelativeDirection[] POSSIBLEDIR = {WorldSpatial.RelativeDirection.LEFT, WorldSpatial.RelativeDirection.RIGHT};

    public GrassHandler(boolean blocked, HashMap<Coordinate, MapTile> view){
        super(blocked,view);
    }

    public boolean handleTrap(Path path){
        if (pathBlocked){
            return false;
        }
        return true;
    }
}
