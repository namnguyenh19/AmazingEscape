package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NamNguyen1 on 27/5/17.
 */
public class View {
    private HashMap<Coordinate, MapTile> curView;
    private WorldSpatial.Direction curDir;
    private boolean canUTurn;
    private boolean canThreePoint;


    public View(HashMap<Coordinate, MapTile> view, WorldSpatial.Direction dir){
        this.curDir = dir;
        this.curView = view;
    }

    /**
     * Check if you have a wall in front of you!
     * @param orientation the orientation we are in based on WorldSpatial
     * @param currentView what the car can currently see
     * @return
     */
    private boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
        switch(orientation){
            case EAST:
                return checkEast(currentView);
            case NORTH:
                return checkNorth(currentView);
            case SOUTH:
                return checkSouth(currentView);
            case WEST:
                return checkWest(currentView);
            default:
                return false;

        }
    }

    /**
     * Check if the wall is on your left hand side given your orientation
     * @param orientation
     * @param currentView
     * @return
     */
    private boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {

        switch(orientation){
            case EAST:
                return checkNorth(currentView);
            case NORTH:
                return checkWest(currentView);
            case SOUTH:
                return checkEast(currentView);
            case WEST:
                return checkSouth(currentView);
            default:
                return false;
        }

    }

    /*
     *
     */
    public boolean checkCornerAhead(){
        return false;
    }

    /*
     *
     */
    public boolean checkDeadEnd(){
        return false;
    }

    /*
     *
     */
    public void checkSpace(){
        this.canThreePoint = false;
        this.canUTurn = false;
    }

    public ArrayList<Path> getPaths(){
        return null;
    }


}
