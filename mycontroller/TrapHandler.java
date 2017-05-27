package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;

import java.util.HashMap;

/**
 * Created by NamNguyen1 on 27/5/17.
 */
public abstract class TrapHandler {
    protected String trapType;
    protected Coordinate dest;
    protected boolean pathBlocked;
    protected HashMap<Coordinate, MapTile> view;
    protected Car car;

    public TrapHandler(Car car, String type, Coordinate dest, boolean blocked, HashMap<Coordinate, MapTile> view){
        this.trapType = type;
        this.dest = dest;
        this.pathBlocked = blocked;
        this.view = view;
        this.car = car;
    }

    public abstract Path handleTrap();
}
