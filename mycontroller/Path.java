package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import world.World;
import world.WorldSpatial;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Created by NamNguyen1 on 27/5/17.
 */
public class Path {
    private HashMap<Coordinate, MapTile> view;
    private HashMap<Coordinate, String> tileName;
    private ArrayList<Coordinate> tiles;
    private boolean hasTrap;

    public static final int ROAD_COST = 1;
    public static final int GRASS_COST = 10;
    public static final int LAVA_COST = 100;
    public static final int MUD_COST = 50;
    public static final int EXIT_COST = -50;

    public Path(HashMap<Coordinate, MapTile> view, ArrayList<Coordinate> tiles){
        this.view = view;
        this.tiles = tiles;
        translateTileName();
    }

    public double calculatePathCost(){
        double totalCost = 0;

        for (Coordinate c : tiles){
            String name = tileName.get(c);

            switch (name){
                case "Lava":
                    totalCost += LAVA_COST;
                    break;
                case "Grass":
                    totalCost += GRASS_COST;
                    break;
                case "Mud":
                    totalCost += MUD_COST;
                    break;
                case "Road":
                    totalCost += ROAD_COST;
                    break;
                case "Exit":
                    totalCost += EXIT_COST;
                default:
                    totalCost += ROAD_COST;
            }
        }

        return totalCost;
    }

    public void validatePath(){

    }

    public HashMap<Coordinate, String> getTileName(){
        return tileName;
    }

    private void translateTileName(){
        for (Coordinate c : view.keySet()){
            MapTile curTile = view.get(c);

            if (curTile.getName().equals("Trap")){
                switch (curTile.getClass().getName()){
                    case "tiles.LavaTrap":
                        tileName.put(c, "Lava");
                        break;
                    case "tiles.GrassTrap":
                        tileName.put(c, "Grass");
                        break;
                    case "tiles.MudTrap":
                        tileName.put(c, "Mud");
                        break;
                }
            }
            else{
                if (curTile.getName().equals("Utility")){
                    tileName.put(c, "Exit");
                }
                else {
                    tileName.put(c, "Road");
                }
            }
        }
    }
    
    public ArrayList<Coordinate> getTilesInPath() {
    	return tiles;
    }
}
