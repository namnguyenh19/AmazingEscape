package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.HashMap;

/**
 * Created by NamNguyen1 on 27/5/17.
 */
public class LavaHandler extends TrapHandler{
    public static final int PATHCOST = 100;
    private int numLava;
    private int curHP;
    private MyAIController ctrl;

    public LavaHandler(String type, Coordinate dest, boolean blocked, HashMap<Coordinate, MapTile> view){
        super(type,dest,blocked,view);
    }

    private int predictHealth(float speed){
        return 0;
    }

    public Path handleTrap(){
        return null;
    }
}
