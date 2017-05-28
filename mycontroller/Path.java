package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import world.World;
import world.WorldSpatial;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by NamNguyen1 on 27/5/17.
 */
public class Path {
    private HashMap<Coordinate, MapTile> view;
    private List<Coordinate> tiles;
    private boolean hasTrap;

    public static final int ROAD_COST = 1;
    public static final int GRASS_COST = 10;
    public static final int LAVA_COST = 100;
    public static final int MUD_COST = 50;

    public double calculatePathCost(){
        double totalCost = 0;

        for (Coordinate c : tiles){
            MapTile curTile = view.get(c);

            switch (curTile.getName()){
                case "Lava":
                    totalCost += LAVA_COST;
                    break;
                case "Grass":
                    totalCost += GRASS_COST;
                    break;
                case "Mud":
                    totalCost += MUD_COST;
                    break;
                default:
                    totalCost += ROAD_COST;
            }
        }

        return totalCost;
    }

    public void validatePath(){

    }
    
    public List<Coordinate> getTilesInPath() {
    	return Collections.unmodifiableList(tiles);
    }
}
