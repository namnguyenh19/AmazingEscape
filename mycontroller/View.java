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
    // How many minimum units the wall is away from the player.
 	private int wallSensitivity = 2;

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
    
    private boolean checkFollowingWall(Coordinate pos) {
        switch(this.curDir){
            case EAST:
                return checkNorth(pos);
            case NORTH:
                return checkWest(pos);
            case SOUTH:
                return checkEast(pos);
            case WEST:
                return checkSouth(pos);
            default:
                return false;
        }

    }
    
    private boolean checkCornerAhead(Coordinate pos){

        switch (this.curDir){
            case EAST:
                return checkCornerEast(pos);
            case NORTH:
                return checkCornerNorth(pos);
            case SOUTH:
                return checkCornerSouth(pos);
            case WEST:
                return checkCornerWest(pos);
            default:
                return false;
        }
    }
    
    /**
     * Check if there is a corner ahead
     */
    public boolean checkCornerAhead() {
    	return checkCornerAhead(this.curPos);
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
    	ArrayList<Path> paths = new ArrayList<Path>();
    	
    	
    	ArrayList<Coordinate> initCoords = new ArrayList<Coordinate>();
    	initCoords.add(curPos);

    	Path initPath = new Path(curView, initCoords, curDir);
    	
    	recursive(paths, initPath, curDir, curDir);
    	
    	return paths;
    }
    
    private void recursive(ArrayList<Path> list, Path p, WorldSpatial.Direction prevOrient, WorldSpatial.Direction currOrient) {
    	Coordinate currentPos = p.getTilesInPath().get(p.getTilesInPath().size() - 1);
    	
    	ArrayList<WorldSpatial.Direction> possibleOrients = new ArrayList<WorldSpatial.Direction>();
    	
    	if (p.getTilesInPath().size() > 3) {
			p.getTilesInPath().remove(0); // TODO: fix
			list.add(p);
			return;
		}
    	
    	MapTile testTile = this.curView.get(ManoeuvreFactory.addCoords(currentPos, 
    			ManoeuvreFactory.toCoordinate(ManoeuvreFactory.getClockwiseDirection(currOrient))));
    	
    	if (!testTile.getName().equals("Wall")) {
    		
    		testTile = this.curView.get(ManoeuvreFactory.addCoords(currentPos, ManoeuvreFactory.toCoordinate(currOrient)));
    		
    		if (testTile.getName().equals("Wall")) {
    			possibleOrients.add(ManoeuvreFactory.getClockwiseDirection(currOrient));
    		} else {
    			possibleOrients.add(ManoeuvreFactory.getAntiClockwiseDirection(currOrient));
    		}
    		
    		
    		possibleOrients.add(currOrient);
    	} else {
    		possibleOrients.add(currOrient);
    		possibleOrients.add(ManoeuvreFactory.getClockwiseDirection(currOrient));
    	}
    	
    	int numNull = 0;
    	for (WorldSpatial.Direction d : possibleOrients) {
    		Coordinate coAdd = ManoeuvreFactory.toCoordinate(d);
    		Coordinate newPos = new Coordinate(coAdd.x + currentPos.x, coAdd.y + currentPos.y);
    		MapTile tile = this.curView.get(newPos);
    		
    		if (tile == null) {
    			numNull++;
    			continue;
    		}
    		
    		if (!tile.getName().equals("Wall")) {
    	    	ArrayList<Coordinate> newCoords = new ArrayList<Coordinate>(p.getTilesInPath());
    	    	newCoords.add(newPos);
    	    	
    	    	// for debugging purposes
    	    	System.out.print("NewPath " + checkFollowingWall(currentPos) + " " + currentPos + " " + currOrient + " " + d + ": " + newCoords.toString() + "\n");

    	    	Path newPath = new Path(this.curView, newCoords, curDir);
    	    	recursive(list, newPath, currOrient, d);
    		} else {
    			numNull++;
    		}
    	}
    	
    	if (numNull == possibleOrients.size()) {
    		if (p.getTilesInPath().size() > 0) {
    			p.getTilesInPath().remove(0); // TODO: fix
    			list.add(p);
    		}
    		
    		return;
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
    private boolean checkCornerEast(Coordinate pos){
        for(int i = 0; i < VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(pos.x + i, pos.y);
            // check East with specific coordinate of a Wall
            for(int j = 0; j < VIEW_SQUARE; j++){
                MapTile tile = this.curView.get(new Coordinate(coor.x, coor.y+j));
                
                if (tile == null) {
                	continue;
                }
                
                if(tile.getName().equals("Wall")){
                    MapTile nextTile = this.curView.get(new Coordinate(coor.x+1, coor.y+j));
                    if(nextTile != null && !nextTile.getName().equals("Wall")){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkCornerWest(Coordinate pos){
        for(int i = 0; i < VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(pos.x - i, pos.y);
            // check West with specific coordinate of a Wall
            for(int j = 0; j < VIEW_SQUARE; j++){
                MapTile tile = this.curView.get(new Coordinate(coor.x, coor.y-j));
                
                if (tile == null) {
                	continue;
                }
                
                if(tile.getName().equals("Wall")){
                    MapTile nextTile = this.curView.get(new Coordinate(coor.x-1, coor.y-j));
                    if(nextTile != null && !nextTile.getName().equals("Wall")){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkCornerNorth(Coordinate pos){
        for(int i = 0; i < VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(pos.x, pos.y + i);
            // check North with specific coordinate of a Wall
            for(int j = 0; j < VIEW_SQUARE; j++){
                MapTile tile = this.curView.get(new Coordinate(coor.x-j, coor.y));
                
                if (tile == null) {
                	continue;
                }
                
                if(tile.getName().equals("Wall")){
                    MapTile nextTile = this.curView.get(new Coordinate(coor.x-j, coor.y+1));
                    if(nextTile != null && !nextTile.getName().equals("Wall")){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkCornerSouth(Coordinate pos){
        for(int i = 0; i < VIEW_SQUARE; i++){
            Coordinate coor = new Coordinate(pos.x, pos.y - i);
            // check South with specific coordinate of a Wall
            for(int j = 0; j < VIEW_SQUARE; j++){
                MapTile tile = this.curView.get(new Coordinate(coor.x+j, coor.y));
                
                if (tile == null) {
                	continue;
                }
                
                if(tile.getName().equals("Wall")){
                    MapTile nextTile = this.curView.get(new Coordinate(coor.x+j, coor.y-1));
                    if(nextTile != null && !nextTile.getName().equals("Wall")){
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
        for(int i = 0; i <= wallSensitivity; i++){
            MapTile tile = this.curView.get(new Coordinate(coor.x+i, coor.y));
            if(tile != null && tile.getName().equals("Wall")){
                return true;
            }
        }
        return false;
    }

    private boolean checkWest(Coordinate coor){
        // Check tiles to my left
        for(int i = 0; i <= wallSensitivity; i++){
            MapTile tile = this.curView.get(new Coordinate(coor.x-i, coor.y));
            if(tile != null && tile.getName().equals("Wall")){
                return true;
            }
        }
        return false;
    }

    private boolean checkNorth(Coordinate coor){
        // Check tiles to towards the top
        for(int i = 0; i <= wallSensitivity; i++){
            MapTile tile = this.curView.get(new Coordinate(coor.x, coor.y+i));
            if(tile != null && tile.getName().equals("Wall")){
                return true;
            }
        }
        return false;
    }

    public boolean checkSouth(Coordinate coor){
        // Check tiles towards the bottom
        for(int i = 0; i <= wallSensitivity; i++){
            MapTile tile = this.curView.get(new Coordinate(coor.x, coor.y-i));
            if(tile != null && tile.getName().equals("Wall")){
                return true;
            }
        }
        return false;
    }
}