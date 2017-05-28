package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;

import java.util.HashMap;

/**
 * Created by NamNguyen1 on 27/5/17.
 */
public abstract class TrapHandler {
    protected boolean pathBlocked;
    protected HashMap<Coordinate, MapTile> view;

    public TrapHandler(boolean blocked, HashMap<Coordinate, MapTile> view){
        this.pathBlocked = blocked;
        this.view = view;
    }

    public abstract boolean handleTrap(Path path);
}
