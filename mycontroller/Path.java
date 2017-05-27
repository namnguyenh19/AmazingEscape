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

    public double calculatePathCost(){
        return 0;
    }

    public void validatePath(){

    }
}
