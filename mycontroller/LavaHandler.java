package mycontroller;

import tiles.MapTile;
import world.*;
import utilities.Coordinate;

import java.util.HashMap;

/**
 * Created by NamNguyen1 on 27/5/17.
 */
public class LavaHandler extends TrapHandler{

    private int numLava;
    private int curHP;

    public LavaHandler(Car car, String type, Coordinate dest, boolean blocked, HashMap<Coordinate, MapTile> view){
        super(car, type,dest,blocked,view);
    }

    private int predictHealth(float speed){
        return 0;
    }

    public Path handleTrap(){
        return null;
    }
}
