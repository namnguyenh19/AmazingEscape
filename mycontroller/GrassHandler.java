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

    public GrassHandler(Car car, String type, Coordinate dest, boolean blocked, HashMap<Coordinate, MapTile> view){
        super(car, type,dest,blocked,view);
    }

    public Path handleTrap(){
        return null;
    }
}
