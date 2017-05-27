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
    public static final int PATHCOST = 10;

    public MudHandler(Car car, String type, Coordinate dest, boolean blocked, HashMap<Coordinate, MapTile> view){
        super(car,type,dest,blocked,view);
    }

    public Path handleTrap(){
        return null;
    }

    private float predictSpeed(){
        return 0;
    }
}
