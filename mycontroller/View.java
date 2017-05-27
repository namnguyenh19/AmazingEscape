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
    private Coordinate curPos;
    private boolean canUTurn;
    private boolean canThreePoint;

    // distance to each direction the car can see
    public static final int VIEW_SQUARE = 3;


    public View(HashMap<Coordinate, MapTile> view, WorldSpatial.Direction dir){
        this.curDir = dir;
        this.curView = view;
    }

    /**
     * Check if you have a wall in front of you!
     */
    private boolean checkWallAhead(){
        switch(this.curDir){
            case EAST:
                return checkEast(curPos);
            case NORTH:
                return checkNorth(curPos);
            case SOUTH:
                return checkSouth(curPos);
            case WEST:
                return checkWest(curPos);
            default:
                return false;

        }
    }

    /**
     * Check if the wall is on your left hand side given your orientation
     */
    private boolean checkFollowingWall() {

        switch(this.curDir){
            case EAST:
                return checkNorth(curPos);
            case NORTH:
                return checkWest(curPos);
            case SOUTH:
                return checkEast(curPos);
            case WEST:
                return checkSouth(curPos);
            default:
                return false;
        }

    }

    /**
     * Check if there is a corner ahead
     */
    public boolean checkCornerAhead(){

        switch (this.curDir){
            case EAST:
                return checkCornerEast();
            case NORTH:
                return checkCornerNorth();
            case SOUTH:
                return checkCornerSouth();
            case WEST:
                return checkCornerWest();
            default:
                return false;
        }
    }

    /**
     * Check if there is a dead end ahead
     */
    public boolean checkDeadEnd(){
        switch (this.curDir){
            case EAST:
                return checkEndEast();
            case NORTH:
                return checkEndNorth();
            case SOUTH:
                return checkEndSouth();
            case WEST:
                return checkEndWest();
            default:
                return false;
        }
    }

    /**
     * Check if there is enough space to do U Turn and/or Three Point
     */
    public void checkSpace(){

    }

    public ArrayList<Path> getPaths(){
        return null;
    }

    /**
     * Method below checks for upcoming dead end given respective orientation of the car
     * It checks if there is wall ahead, if so check responding walls on two sides
     */

    private boolean checkEndEast(){
        for (int i = 1; i <= VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(this.curPos.x + i, this.curPos.y);
            MapTile tile = this.curView.get(coor);
            if(tile.getName().equals("Wall")){
                MapTile leftTile = this.curView.get(new Coordinate(coor.x-1, coor.y+1));
                MapTile rightTile = this.curView.get(new Coordinate(coor.x-1, coor.y -1));
                checkTwoSide(leftTile, rightTile);
            }
        }
        return false;
    }

    private boolean checkEndWest(){
        for (int i = 1; i <= VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(this.curPos.x - i, this.curPos.y);
            MapTile tile = this.curView.get(coor);
            if(tile.getName().equals("Wall")){
                MapTile leftTile = this.curView.get(new Coordinate(coor.x+1, coor.y-1));
                MapTile rightTile = this.curView.get(new Coordinate(coor.x+1, coor.y+1));
                checkTwoSide(leftTile, rightTile);
            }
        }
        return false;
    }

    private boolean checkEndNorth(){
        for (int i = 1; i <= VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(this.curPos.x, this.curPos.y+i);
            MapTile tile = this.curView.get(coor);
            if(tile.getName().equals("Wall")){
                MapTile leftTile = this.curView.get(new Coordinate(coor.x-1, coor.y-1));
                MapTile rightTile = this.curView.get(new Coordinate(coor.x+1, coor.y-1));
                checkTwoSide(leftTile, rightTile);
            }
        }
        return false;
    }

    private boolean checkEndSouth(){
        for (int i = 1; i <= VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(this.curPos.x, this.curPos.y-i);
            MapTile tile = this.curView.get(coor);
            if(tile.getName().equals("Wall")){
                MapTile leftTile = this.curView.get(new Coordinate(coor.x+1, coor.y+1));
                MapTile rightTile = this.curView.get(new Coordinate(coor.x-1, coor.y+1));
                checkTwoSide(leftTile, rightTile);
            }
        }
        return false;
    }

    private boolean checkTwoSide(MapTile l, MapTile r){
        if(l.getName().equals("Wall") && r.getName().equals("Wall")){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Method below checks for upcoming corner given respective orientation of the car
     *  It checks if there is wall to the left, if so, check the tile to the wall's right
     *  if that's not a wall, then there is a corner
     */
    private boolean checkCornerEast(){
        for(int i = 0; i < VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(this.curPos.x + i, this.curPos.y);
            // check North with specific coordinate of a Wall
            for(int j = 0; j < VIEW_SQUARE; j++){
                MapTile tile = this.curView.get(new Coordinate(coor.x, coor.y+j));
                if(tile.getName().equals("Wall")){
                    MapTile nextTile = this.curView.get(new Coordinate(coor.x+1, coor.y+j));
                    if(!nextTile.getName().equals("Wall")){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkCornerWest(){
        for(int i = 0; i < VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(this.curPos.x - i, this.curPos.y);
            // check North with specific coordinate of a Wall
            for(int j = 0; j < VIEW_SQUARE; j++){
                MapTile tile = this.curView.get(new Coordinate(coor.x, coor.y-j));
                if(tile.getName().equals("Wall")){
                    MapTile nextTile = this.curView.get(new Coordinate(coor.x-1, coor.y-j));
                    if(!nextTile.getName().equals("Wall")){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkCornerNorth(){
        for(int i = 0; i < VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(this.curPos.x, this.curPos.y + i);
            // check North with specific coordinate of a Wall
            for(int j = 0; j < VIEW_SQUARE; j++){
                MapTile tile = this.curView.get(new Coordinate(coor.x-j, coor.y));
                if(tile.getName().equals("Wall")){
                    MapTile nextTile = this.curView.get(new Coordinate(coor.x-j, coor.y+1));
                    if(!nextTile.getName().equals("Wall")){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkCornerSouth(){
        for(int i = 0; i < VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(this.curPos.x, this.curPos.y - i);
            // check North with specific coordinate of a Wall
            for(int j = 0; j < VIEW_SQUARE; j++){
                MapTile tile = this.curView.get(new Coordinate(coor.x+j, coor.y));
                if(tile.getName().equals("Wall")){
                    MapTile nextTile = this.curView.get(new Coordinate(coor.x+j, coor.y-1));
                    if(!nextTile.getName().equals("Wall")){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Method below just iterates through the list and check in the correct coordinates.
     * i.e. Given your current position is 10,10
     * checkEast will check up to VIEW_SQUARE -1 amount of tiles to the right.
     * checkWest will check up to VIEW_SQUARE -1 amount of tiles to the left.
     * checkNorth will check up to VIEW_SQUARE -1 amount of tiles to the top.
     * checkSouth will check up to VIEW_SQUARE -1 amount of tiles below.
     */
    private boolean checkEast(Coordinate coor){
        // Check tiles to my right
        for(int i = 0; i < VIEW_SQUARE; i++){
            MapTile tile = this.curView.get(new Coordinate(coor.x+i, coor.y));
            if(tile.getName().equals("Wall")){
                return true;
            }
        }
        return false;
    }

    private boolean checkWest(Coordinate coor){
        // Check tiles to my left
        for(int i = 0; i < VIEW_SQUARE; i++){
            MapTile tile = this.curView.get(new Coordinate(coor.x-i, coor.y));
            if(tile.getName().equals("Wall")){
                return true;
            }
        }
        return false;
    }

    private boolean checkNorth(Coordinate coor){
        // Check tiles to towards the top
        for(int i = 0; i < VIEW_SQUARE; i++){
            MapTile tile = this.curView.get(new Coordinate(coor.x, coor.y+i));
            if(tile.getName().equals("Wall")){
                return true;
            }
        }
        return false;
    }

    private boolean checkSouth(Coordinate coor){
        // Check tiles towards the bottom
        for(int i = 0; i < VIEW_SQUARE; i++){
            MapTile tile = this.curView.get(new Coordinate(coor.x, coor.y-i));
            if(tile.getName().equals("Wall")){
                return true;
            }
        }
        return false;
    }
}
