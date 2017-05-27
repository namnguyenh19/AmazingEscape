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
    private Coordinate currentPosition;
    private boolean canUTurn;
    private boolean canThreePoint;
    // How many minimum units the wall is away from the player.
    private int wallSensitivity = 2;


    public View(HashMap<Coordinate, MapTile> view, WorldSpatial.Direction dir){
        this.curDir = dir;
        this.curView = view;
    }

    /**
     * Check if you have a wall in front of you!
     * @param orientation the orientation we are in based on WorldSpatial
     * @param curView what the car can currently see
     * @return
     */
    private boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> curView){
        switch(orientation){
            case EAST:
                return checkEast();
            case NORTH:
                return checkNorth();
            case SOUTH:
                return checkSouth();
            case WEST:
                return checkWest();
            default:
                return false;

        }
    }

    /**
     * Check if the wall is on your left hand side given your orientation
     * @param orientation
     * @param curView
     * @return
     */
    private boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> curView) {

        switch(orientation){
            case EAST:
                return checkNorth();
            case NORTH:
                return checkWest();
            case SOUTH:
                return checkEast();
            case WEST:
                return checkSouth();
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

    /**
     * Method below just iterates through the list and check in the correct coordinates.
     * i.e. Given your current position is 10,10
     * checkEast will check up to wallSensitivity amount of tiles to the right.
     * checkWest will check up to wallSensitivity amount of tiles to the left.
     * checkNorth will check up to wallSensitivity amount of tiles to the top.
     * checkSouth will check up to wallSensitivity amount of tiles below.
     */
    public boolean checkEast(){
        // Check tiles to my right
        for(int i = 0; i <= wallSensitivity; i++){
            MapTile tile = this.curView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
            if(tile.getName().equals("Wall")){
                return true;
            }
        }
        return false;
    }

    public boolean checkWest(){
        // Check tiles to my left
        for(int i = 0; i <= wallSensitivity; i++){
            MapTile tile = this.curView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
            if(tile.getName().equals("Wall")){
                return true;
            }
        }
        return false;
    }

    public boolean checkNorth(){
        // Check tiles to towards the top
        for(int i = 0; i <= wallSensitivity; i++){
            MapTile tile = this.curView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
            if(tile.getName().equals("Wall")){
                return true;
            }
        }
        return false;
    }

    public boolean checkSouth(){
        // Check tiles towards the bottom
        for(int i = 0; i <= wallSensitivity; i++){
            MapTile tile = this.curView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
            if(tile.getName().equals("Wall")){
                return true;
            }
        }
        return false;
    }
}
