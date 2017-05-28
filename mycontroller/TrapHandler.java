package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.HashMap;

/**
 * Abstract class for handling traps in a path.
 * 
 * Created by Project Group 37.
 */
public abstract class TrapHandler {
    protected boolean pathBlocked;
    protected HashMap<Coordinate, MapTile> view;

    public TrapHandler(boolean blocked, HashMap<Coordinate, MapTile> view){
        this.pathBlocked = blocked;
        this.view = view;
    }

    /** Handles the trap given a path. Returns true if we should consider going
     * to this path.
     * 
     * Return false if not to go down this path due to trap.
     */
    public abstract boolean handleTrap(Path path);
}