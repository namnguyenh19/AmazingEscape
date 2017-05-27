package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import world.World;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NamNguyen1 on 27/5/17.
 */
public class Path {
    private HashMap<Coordinate, MapTile> view;
    private ArrayList<Coordinate> titles;
    private boolean hasTrap;

    public static final int GRASS_COST = 2;
    public static final int LAVA_COST = 100;
    public static final int MUD_COST = 10;

    public double calculatePathCost(){
        return 0;
    }

    public void validatePath(){

    }
}
