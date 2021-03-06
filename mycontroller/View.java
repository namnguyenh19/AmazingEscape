package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class provided containing methods to interpret a local view
 * of a car.
 * 
 * Created by Project Group 37.
 */
public class View {
    private HashMap<Coordinate, MapTile> curView;
    private WorldSpatial.Direction curDir;
    private Coordinate curPos;

    public boolean isCanUTurn() {
        return canUTurn;
    }

    public boolean isCanThreePoint() {
        return canThreePoint;
    }

    public boolean isTurnLeft() {
        return turnLeft;
    }

    public boolean isTurnRight() {
        return turnRight;
    }

    private boolean canUTurn;
    private boolean canThreePoint;
    private boolean turnLeft;
    private boolean turnRight;

    // distance to each direction the car can see
    public static final int VIEW_SQUARE = 3;

    public Coordinate getCurPos() {
        return curPos;
    }

    public View(HashMap<Coordinate, MapTile> view, WorldSpatial.Direction dir, Coordinate pos){
        this.curDir = dir;
        this.curView = view;
        this.curPos = pos;
    }

    /**
     * Check if you have a wall in front of you!
     */
    public boolean checkWallAhead(){
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
    public boolean checkFollowingWall() {

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
     * Method traverses through the car's view and get all available paths
     */
    public ArrayList<Path> getPaths() {
        if (checkDeadEnd()){
            return null;
        }

        ArrayList<Path> paths = new ArrayList<>();

        switch (this.curDir){
            case EAST:
                paths = getPathsEast();
                break;
            case WEST:
                paths = getPathsWest();
                break;
            case NORTH:
                paths = getPathsNorth();
                break;
            case SOUTH:
                paths = getPathsSouth();
                break;
        }

        return paths;
    }

    /**
     * Methods below get available paths according to the car's current orientation
     */

    private ArrayList<Path> getPathsEast(){
        ArrayList<Path> paths = new ArrayList<>();

        if (checkWallAhead()){
            int noLeft = 1;
            if(noPathLeft()){
                // do nothing
            }
            else{
                noLeft = -1;
            }

            //turn right
            Coordinate coor = curPos;
            ArrayList<Coordinate> tiles = new ArrayList<>();

            for(int i = 1; i <= VIEW_SQUARE; i++){
                coor = new Coordinate(curPos.x, curPos.y - i*noLeft);
                tiles.add(coor);
            }

            paths.add(new Path(this.curView, tiles, this.curDir));

            return paths;
        }

        if (checkCornerAhead()){
            Coordinate corner = curPos;
            boolean found = false;

            //find Wall
            for (int i = 1; i <= VIEW_SQUARE && !found; i++){
                for(int j = 1; j < VIEW_SQUARE; j++){
                    corner = new Coordinate(curPos.x + j, curPos.y + i);
                    MapTile tile = this.curView.get(corner);
                    if (tile.getName().equals("Wall")){
                        if (isCorner(corner, this.curDir)){
                            found = true;
                            break;
                        }
                    }

                }
            }

            int leftDistance = corner.y - curPos.y;
            int distance = corner.x - curPos.x;
            ArrayList<Coordinate> tiles = new ArrayList<>();

            for (int i = 1; i <= distance+1; i++){
                Coordinate coor = new Coordinate(curPos.x+i,curPos.y);
                tiles.add(coor);
            }
            for (int j = 1; j <= leftDistance; j++){
                Coordinate coor = new Coordinate(curPos.x + distance + 1, curPos.y + j);
                tiles.add(coor);
            }

            paths.add(new Path(this.curView, tiles, curDir));

            return paths;
        }
        else {

            ArrayList<Coordinate> tiles = new ArrayList<>();

            tiles.add(new Coordinate(curPos.x+1, curPos.y));

            paths.add(new Path(this.curView, tiles, this.curDir));

            return paths;

//            //car is going straight
//            int leftWalldistance = 0;
//            int rightWalldistance = 0;
//
//            ArrayList<Coordinate> leftTiles = new ArrayList<>();
//            ArrayList<Coordinate> rightTiles = new ArrayList<>();
//
//            //check where wall is on each side
//
//            //check left side of car first
//            for(int i = 1; i <= VIEW_SQUARE; i++){
//                Coordinate left = new Coordinate(curPos.x, curPos.y+i);
//
//                MapTile tile = curView.get(left);
//                if (tile.getName().equals("Wall")){
//                    leftWalldistance = i;
//                    break;
//                }
//
//            }
//
//            //adding paths to the left
//            for (int i = 1; i < leftWalldistance; i++){
//                for(int j = 1; j <= VIEW_SQUARE; j++){
//                    Coordinate coor = new Coordinate(curPos.x + j, curPos.y + i);
//                    leftTiles.add(coor);
//                }
//
//                Path newPath = new Path(this.curView, leftTiles, curDir);
//                paths.add(newPath);
//            }
//
//            //check right side of car
//            for(int i = 1; i <= VIEW_SQUARE; i++){
//                Coordinate right = new Coordinate(curPos.x, curPos.y-i);
//
//                MapTile tile = curView.get(right);
//                if (tile.getName().equals("Wall")){
//                    rightWalldistance = i;
//                    break;
//                }
//
//            }
//
//            //adding paths to the right and middle
//            for (int i = 0; i < rightWalldistance; i++){
//                for(int j = 1; j <= VIEW_SQUARE; j++){
//                    Coordinate coor = new Coordinate(curPos.x + j, curPos.y - i);
//                    rightTiles.add(coor);
//                }
//
//                Path newPath = new Path(this.curView, rightTiles, this.curDir);
//                paths.add(newPath);
//            }
//
//            return paths;
        }
    }

    private ArrayList<Path> getPathsWest(){
        ArrayList<Path> paths = new ArrayList<>();

        if (checkWallAhead()){
            int noLeft = 1;
            if(noPathLeft()){
                // do nothing
            }
            else{
                noLeft = -1;
            }

            //turn to correct direction
            Coordinate coor = curPos;
            ArrayList<Coordinate> tiles = new ArrayList<>();

            for(int i = 1; i <= VIEW_SQUARE; i++){
                coor = new Coordinate(curPos.x, curPos.y + noLeft*i);
                tiles.add(coor);
            }

            paths.add(new Path(this.curView, tiles, this.curDir));

            return paths;
        }

        if (checkCornerAhead()){
            Coordinate corner = curPos;
            boolean found = false;

            //find Wall
            for (int i = 1; i <= VIEW_SQUARE && !found; i++){
                for(int j = 1; j < VIEW_SQUARE; j++){
                    corner = new Coordinate(curPos.x - j, curPos.y - i);
                    MapTile tile = this.curView.get(corner);
                    if (tile.getName().equals("Wall")){
                        if (isCorner(corner, this.curDir)){
                            found = true;
                            break;
                        }
                    }

                }
            }

            int leftDistance = corner.y - curPos.y;
            int distance = corner.x - curPos.x;
            ArrayList<Coordinate> tiles = new ArrayList<>();

            for (int i = 1; i <= distance+1; i++){
                Coordinate coor = new Coordinate(curPos.x-i,curPos.y);
                tiles.add(coor);
            }
            for (int j = 1; j <= leftDistance; j++){
                Coordinate coor = new Coordinate(curPos.x - distance - 1, curPos.y - j);
                tiles.add(coor);
            }

            paths.add(new Path(this.curView, tiles, curDir));

            return paths;
        }
        else {

            ArrayList<Coordinate> tiles = new ArrayList<>();

            tiles.add(new Coordinate(curPos.x-1, curPos.y));

            paths.add(new Path(this.curView, tiles, this.curDir));

            return paths;

//            //car is going straight
//            int leftWalldistance = 0;
//            int rightWalldistance = 0;
//
//            ArrayList<Coordinate> leftTiles = new ArrayList<>();
//            ArrayList<Coordinate> rightTiles = new ArrayList<>();
//
//            //check where wall is on each side
//
//            //check left side of car first
//            for(int i = 1; i <= VIEW_SQUARE; i++){
//                Coordinate left = new Coordinate(curPos.x, curPos.y-i);
//
//                MapTile tile = curView.get(left);
//                if (tile.getName().equals("Wall")){
//                    leftWalldistance = i;
//                    break;
//                }
//
//            }
//
//            //adding paths to the left
//            for (int i = 1; i < leftWalldistance; i++){
//                for(int j = 1; j <= VIEW_SQUARE; j++){
//                    Coordinate coor = new Coordinate(curPos.x - j, curPos.y - i);
//                    leftTiles.add(coor);
//                }
//
//                Path newPath = new Path(this.curView, leftTiles, curDir);
//                paths.add(newPath);
//            }
//
//            //check right side of car
//            for(int i = 1; i <= VIEW_SQUARE; i++){
//                Coordinate right = new Coordinate(curPos.x, curPos.y+i);
//
//                MapTile tile = curView.get(right);
//                if (tile.getName().equals("Wall")){
//                    rightWalldistance = i;
//                    break;
//                }
//
//            }
//
//            //adding paths to the right and middle
//            for (int i = 0; i < rightWalldistance; i++){
//                for(int j = 1; j <= VIEW_SQUARE; j++){
//                    Coordinate coor = new Coordinate(curPos.x - j, curPos.y + i);
//                    rightTiles.add(coor);
//                }
//
//                Path newPath = new Path(this.curView, rightTiles, this.curDir);
//                paths.add(newPath);
//            }
//
//            return paths;
        }
    }

    private ArrayList<Path> getPathsNorth(){
        ArrayList<Path> paths = new ArrayList<>();

        if (checkWallAhead()){

            int noLeft = 1;
            if(noPathLeft()){
                // do nothing
            }
            else{
                noLeft = -1;
            }

            //turn
            Coordinate coor = curPos;
            ArrayList<Coordinate> tiles = new ArrayList<>();

            for(int i = 1; i <= VIEW_SQUARE; i++){
                coor = new Coordinate(curPos.x+i*noLeft, curPos.y);
                tiles.add(coor);
            }

            paths.add(new Path(this.curView, tiles, this.curDir));

            return paths;
        }

        if (checkCornerAhead()){
            Coordinate corner = curPos;
            boolean found = false;

            //find Wall
            for (int i = 1; i <= VIEW_SQUARE && !found; i++){
                for(int j = 1; j < VIEW_SQUARE; j++){
                    corner = new Coordinate(curPos.x - i, curPos.y + j);
                    MapTile tile = this.curView.get(corner);
                    if (tile.getName().equals("Wall")){
                        if (isCorner(corner, this.curDir)){
                            found = true;
                            break;
                        }
                    }

                }
            }

            int leftDistance = corner.y - curPos.y;
            int distance = corner.x - curPos.x;
            ArrayList<Coordinate> tiles = new ArrayList<>();

            for (int i = 1; i <= distance+1; i++){
                Coordinate coor = new Coordinate(curPos.x,curPos.y+i);
                tiles.add(coor);
            }
            for (int j = 1; j <= leftDistance; j++){
                Coordinate coor = new Coordinate(curPos.x - j, curPos.y + distance + 1);
                tiles.add(coor);
            }

            paths.add(new Path(this.curView, tiles, this.curDir));

            return paths;
        }
        else {

            ArrayList<Coordinate> tiles = new ArrayList<>();

            tiles.add(new Coordinate(curPos.x, curPos.y+1));

            paths.add(new Path(this.curView, tiles, this.curDir));

            return paths;

//            //car is going straight
//            int leftWalldistance = 0;
//            int rightWalldistance = 0;
//
//            ArrayList<Coordinate> leftTiles = new ArrayList<>();
//            ArrayList<Coordinate> rightTiles = new ArrayList<>();
//
//            //check where wall is on each side
//
//            //check left side of car first
//            for(int i = 1; i <= VIEW_SQUARE; i++){
//                Coordinate left = new Coordinate(curPos.x-i, curPos.y);
//
//                MapTile tile = curView.get(left);
//                if (tile.getName().equals("Wall")){
//                    leftWalldistance = i;
//                    break;
//                }
//
//            }
//
//            //adding paths to the left
//            for (int i = 1; i < leftWalldistance; i++){
//                for(int j = 1; j <= VIEW_SQUARE; j++){
//                    Coordinate coor = new Coordinate(curPos.x - i, curPos.y + j);
//                    leftTiles.add(coor);
//                }
//
//                Path newPath = new Path(this.curView, leftTiles, curDir);
//                paths.add(newPath);
//            }
//
//            //check right side of car
//            for(int i = 1; i <= VIEW_SQUARE; i++){
//                Coordinate right = new Coordinate(curPos.x+i, curPos.y);
//
//                MapTile tile = curView.get(right);
//                if (tile.getName().equals("Wall")){
//                    rightWalldistance = i;
//                    break;
//                }
//
//            }
//
//            //adding paths to the right and middle
//            for (int i = 0; i < rightWalldistance; i++){
//                for(int j = 1; j <= VIEW_SQUARE; j++){
//                    Coordinate coor = new Coordinate(curPos.x+i, curPos.y + j);
//                    rightTiles.add(coor);
//                }
//
//                Path newPath = new Path(this.curView, rightTiles, this.curDir);
//                paths.add(newPath);
//            }
//
//            return paths;
        }
    }

    private ArrayList<Path> getPathsSouth(){
        ArrayList<Path> paths = new ArrayList<>();

        if (checkWallAhead()){

            int noLeft = 1;
            if(noPathLeft()){
                // do nothing
            }
            else{
                noLeft = -1;
            }

            //turn right
            Coordinate coor = curPos;
            ArrayList<Coordinate> tiles = new ArrayList<>();

            for(int i = 1; i <= VIEW_SQUARE; i++){
                coor = new Coordinate(curPos.x-i*noLeft, curPos.y);
                tiles.add(coor);
            }

            paths.add(new Path(this.curView, tiles, this.curDir));

            return paths;
        }

        if (checkCornerAhead()){
            Coordinate corner = curPos;
            boolean found = false;

            //find Wall
            for (int i = 1; i <= VIEW_SQUARE && !found; i++){
                for(int j = 1; j < VIEW_SQUARE; j++){
                    corner = new Coordinate(curPos.x + i, curPos.y - j);
                    MapTile tile = this.curView.get(corner);
                    if (tile.getName().equals("Wall")){
                        if (isCorner(corner, this.curDir)){
                            found = true;
                            break;
                        }
                    }

                }
            }

            int leftDistance = corner.y - curPos.y;
            int distance = corner.x - curPos.x;
            ArrayList<Coordinate> tiles = new ArrayList<>();

            for (int i = 1; i <= distance+1; i++){
                Coordinate coor = new Coordinate(curPos.x,curPos.y-i);
                tiles.add(coor);
            }
            for (int j = 1; j <= leftDistance; j++){
                Coordinate coor = new Coordinate(curPos.x + j, curPos.y - distance - 1);
                tiles.add(coor);
            }

            paths.add(new Path(this.curView, tiles, this.curDir));

            return paths;
        }
        else {
            ArrayList<Coordinate> tiles = new ArrayList<>();

            tiles.add(new Coordinate(curPos.x, curPos.y-1));

            paths.add(new Path(this.curView, tiles, this.curDir));

            return paths;
//            //car is going straight
//            int leftWalldistance = 0;
//            int rightWalldistance = 0;
//
//            ArrayList<Coordinate> leftTiles = new ArrayList<>();
//            ArrayList<Coordinate> rightTiles = new ArrayList<>();
//
//            //check where wall is on each side
//
//            //check left side of car first
//            for(int i = 1; i <= VIEW_SQUARE; i++){
//                Coordinate left = new Coordinate(curPos.x+i, curPos.y);
//
//                MapTile tile = curView.get(left);
//                if (tile.getName().equals("Wall")){
//                    leftWalldistance = i;
//                    break;
//                }
//
//            }
//
//            //adding paths to the left
//            for (int i = 1; i < leftWalldistance; i++){
//                for(int j = 1; j <= VIEW_SQUARE; j++){
//                    Coordinate coor = new Coordinate(curPos.x + i, curPos.y - j);
//                    leftTiles.add(coor);
//                }
//
//                Path newPath = new Path(this.curView, leftTiles, this.curDir);
//                paths.add(newPath);
//            }
//
//            //check right side of car
//            for(int i = 1; i <= VIEW_SQUARE; i++){
//                Coordinate right = new Coordinate(curPos.x-i, curPos.y);
//
//                MapTile tile = curView.get(right);
//                if (tile.getName().equals("Wall")){
//                    rightWalldistance = i;
//                    break;
//                }
//
//            }
//
//            //adding paths to the right and middle
//            for (int i = 0; i < rightWalldistance; i++){
//                for(int j = 1; j <= VIEW_SQUARE; j++){
//                    Coordinate coor = new Coordinate(curPos.x-i, curPos.y - j);
//                    rightTiles.add(coor);
//                }
//
//                Path newPath = new Path(this.curView, rightTiles, this.curDir);
//                paths.add(newPath);
//            }
//
//            return paths;
        }
    }

    /**
     * @param coor coordinate of a Wall
     * @param orientation orientation of the car
     * @return is coor is a corner
     */
    private boolean isCorner(Coordinate coor, WorldSpatial.Direction orientation){
        Coordinate next;
        MapTile tile;
        switch (orientation){
            case EAST:
                next = new Coordinate(coor.x+1, coor.y);
                tile = this.curView.get(next);
                if (!tile.getName().equals("Wall")){
                    return true;
                }
                else {
                    return false;
                }
            case WEST:
                next = new Coordinate(coor.x-1, coor.y);
                tile = this.curView.get(next);
                if (!tile.getName().equals("Wall")){
                    return true;
                }
                else {
                    return false;
                }

            case SOUTH:
                next = new Coordinate(coor.x, coor.y-1);
                tile = this.curView.get(next);
                if (!tile.getName().equals("Wall")){
                    return true;
                }
                else {
                    return false;
                }

            case NORTH:
                next = new Coordinate(coor.x, coor.y+1);
                tile = this.curView.get(next);
                if (!tile.getName().equals("Wall")){
                    return true;
                }
                else {
                    return false;
                }

        }

        return false;
    }

    /**
     * Method below check for the space available in front of the car to do U Turn or ThreePoint Turn
     * given the current orientation of the car
     *
     * U Turn: checks for the following minimum space:    |space||space|car|
     * Three Point: checks for the following min space:   |space||car|
     */

    public void checkSpace(){
        int spaceLeft = 0;
        int spaceRight = 0;

        turnRight = false;
        turnLeft = false;
        canUTurn = false;
        canThreePoint = false;

        switch (this.curDir){
            case EAST:
                spaceLeft = sideSpace(1, false);
                spaceRight = sideSpace(-1, false);
                break;
            case WEST:
                spaceLeft = sideSpace(1, false);
                spaceRight = sideSpace(-1, false);
                break;
            case NORTH:
                spaceLeft = sideSpace(1, true);
                spaceRight = sideSpace(-1, true);
                break;
            case SOUTH:
                spaceLeft = sideSpace(1, true);
                spaceRight = sideSpace(-1, true);
                break;
        }

        if (spaceLeft >= 1){
            canThreePoint = true;
            turnLeft = true;
            if (spaceLeft >= 2){
                canUTurn = true;
            }
        }

        if (spaceRight >= 1){
            canThreePoint = true;
            turnRight = true;
            if (spaceRight >= 2){
                canUTurn = true;
            }
        }
    }

    /**
     * Check how much space to a side of the car
     * @param isLeft whether we're checking the left or right side of the car
     * @param vertical whether car is facing EAST/WEST or NORTH/EAST
     * @return number of empty tile(s)
     */
    private int sideSpace(int isLeft, boolean vertical){
        int count = 0;
        Coordinate coor;
        for(int i = 1; i <= VIEW_SQUARE; i++){
            //if car is in NORTH/SOUTH
            if (vertical){
                coor = new Coordinate(this.curPos.x - isLeft*i, this.curPos.y);
            }
            else {
                coor = new Coordinate(this.curPos.x, this.curPos.y + isLeft*i);
            }

            MapTile tile = this.curView.get(coor);
            if (!checkWall(tile)){
                count += 1;
            }
        }

        return count;
    }

    /**
     * Methods below checks for upcoming dead end given respective orientation of the car
     * It checks if there is wall ahead, if so check responding walls on two sides
     */

    private boolean checkEndEast(){
        for (int i = 1; i <= VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(this.curPos.x + i, this.curPos.y);
            MapTile tile = this.curView.get(coor);
            if(tile.getName().equals("Wall")){
                MapTile leftSide = this.curView.get(new Coordinate(coor.x-1, coor.y+1));
                MapTile rightSide = this.curView.get(new Coordinate(coor.x-1, coor.y-1));

                //get how wide the wall in front is
                for(int j = 0; j < VIEW_SQUARE; j++){
                    MapTile leftTile = this.curView.get(new Coordinate(coor.x, coor.y+j));
                    MapTile rightTile = this.curView.get(new Coordinate(coor.x, coor.y-j));

                    // can only be a dead end if tiles to both side of detected Wall are also Walls
                    if (checkTwoSide(leftTile, rightTile)){
                        //check the tile directly below leftTile and rightTile, they must not be a wall
                        MapTile leftBelow = this.curView.get(new Coordinate(coor.x-1, coor.y+j));
                        if (!checkWall(leftBelow)){
                            //get the diagonal tile
                            leftSide = this.curView.get(new Coordinate(coor.x-1, coor.y+j+1));
                        }

                        MapTile rightBelow = this.curView.get(new Coordinate(coor.x-1, coor.y-j));
                        if (!checkWall(rightBelow)){
                            //get diagonal tile
                            rightSide = this.curView.get(new Coordinate(coor.x-1, coor.y-j-1));
                        }

                        if (checkTwoSide(leftSide, rightSide)){
                            return true;
                        }

                        if (checkWall(leftBelow) && checkWall(rightBelow)){
                            break;
                        }

                    }

                }
            }
        }
        return false;
    }

    private boolean checkEndWest(){
        for (int i = 1; i <= VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(this.curPos.x - i, this.curPos.y);
            MapTile tile = this.curView.get(coor);
            if(tile.getName().equals("Wall")){
                MapTile leftSide = this.curView.get(new Coordinate(coor.x+1, coor.y-1));
                MapTile rightSide = this.curView.get(new Coordinate(coor.x+1, coor.y+1));

                //get how wide the wall in front is
                for(int j = 0; j < VIEW_SQUARE; j++){

                    MapTile leftTile = this.curView.get(new Coordinate(coor.x, coor.y-j));
                    MapTile rightTile = this.curView.get(new Coordinate(coor.x, coor.y+j));

                    if (checkTwoSide(leftTile, rightTile)){
                        //check the tile directly below leftMost and rightMost, they must not be a wall
                        MapTile leftBelow = this.curView.get(new Coordinate(coor.x+1, coor.y-j));
                        if (!checkWall(leftBelow)){
                            //get the diagonal tile
                            leftSide = this.curView.get(new Coordinate(coor.x+1, coor.y-j-1));
                        }

                        MapTile rightBelow = this.curView.get(new Coordinate(coor.x+1, coor.y+j));
                        if (!checkWall(rightBelow)){
                            //get diagonal tile
                            rightSide = this.curView.get(new Coordinate(coor.x+1, coor.y+j+1));
                        }

                        if (checkTwoSide(leftSide, rightSide)){
                            return true;
                        }

                        if (checkWall(leftBelow) && checkWall(rightBelow)){
                            break;
                        }
                    }

                }
            }
        }
        return false;
    }

    private boolean checkEndNorth(){
        for (int i = 1; i <= VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(this.curPos.x, this.curPos.y+i);
            MapTile tile = this.curView.get(coor);
            if(tile.getName().equals("Wall")){

                MapTile leftSide = this.curView.get(new Coordinate(coor.x-1, coor.y-1));
                MapTile rightSide = this.curView.get(new Coordinate(coor.x+1, coor.y-1));

                //get how wide the wall in front is
                for(int j = 0; j < VIEW_SQUARE; j++){
                    MapTile leftTile = this.curView.get(new Coordinate(coor.x-j, coor.y));
                    MapTile rightTile = this.curView.get(new Coordinate(coor.x+j, coor.y));

                    if (checkTwoSide(leftTile, rightTile)){
                        //check the tile directly below leftMost and rightMost, they must not be a wall
                        MapTile leftBelow = this.curView.get(new Coordinate(coor.x-j, coor.y-1));
                        if (!checkWall(leftBelow)){
                            //get the diagonal tile
                            leftSide = this.curView.get(new Coordinate(coor.x-j-1, coor.y-1));
                        }

                        MapTile rightBelow = this.curView.get(new Coordinate(coor.x+j, coor.y-1));
                        if (!checkWall(rightBelow)){
                            //get diagonal tile
                            rightSide = this.curView.get(new Coordinate(coor.x+j+1, coor.y-1));
                        }

                        if (checkTwoSide(leftSide, rightSide)){
                            return true;
                        }

                        if (checkWall(leftBelow) && checkWall(rightBelow)){
                            break;
                        }
                    }

                }
            }
        }
        return false;
    }

    private boolean checkEndSouth(){
        for (int i = 1; i <= VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(this.curPos.x, this.curPos.y-i);
            MapTile tile = this.curView.get(coor);
            if(tile.getName().equals("Wall")){

                MapTile leftSide = this.curView.get(new Coordinate(coor.x+1, coor.y+1));
                MapTile rightSide = this.curView.get(new Coordinate(coor.x-1, coor.y+1));

                //get how wide the wall in front is
                for(int j = 0; j < VIEW_SQUARE; j++){
                    MapTile leftTile = this.curView.get(new Coordinate(coor.x+j, coor.y));
                    MapTile rightTile = this.curView.get(new Coordinate(coor.x-j, coor.y));

                    if (checkTwoSide(leftTile, rightTile)){
                        //check the tile directly below leftMost and rightMost, they must not be a wall
                        MapTile leftBelow = this.curView.get(new Coordinate(coor.x+j, coor.y+1));
                        if (!checkWall(leftBelow)){
                            //get the diagonal tile
                            leftSide = this.curView.get(new Coordinate(coor.x+j+1, coor.y+1));
                        }

                        MapTile rightBelow = this.curView.get(new Coordinate(coor.x-j, coor.y+1));
                        if (!checkWall(rightBelow)){
                            //get diagonal tile
                            rightSide = this.curView.get(new Coordinate(coor.x-j-1, coor.y+1));
                        }

                        if (checkTwoSide(leftSide, rightSide)){
                            return true;
                        }

                        if (checkWall(leftBelow) && checkWall(rightBelow)){
                            break;
                        }
                    }

                }
            }
        }
        return false;
    }

    private boolean checkTwoSide(MapTile l, MapTile r){
        if(checkWall(l) && checkWall(r)){
            return true;
        }
        else {
            return false;
        }
    }

    private boolean checkWall(MapTile t){
        if(t.getName().equals("Wall")){
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
            // check East with specific coordinate of a Wall
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
            // check West with specific coordinate of a Wall
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
            // check South with specific coordinate of a Wall
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

    private boolean noPathLeft(){
        switch (this.curDir){
            case SOUTH:
                return checkEast(curPos);
            case NORTH:
                return checkWest(curPos);
            case WEST:
                return checkSouth(curPos);
            case EAST:
                return checkNorth(curPos);
            default:
                return false;
        }
    }
}