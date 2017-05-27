package mycontroller;

import java.util.ArrayList;
import java.util.List;

import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

/**
 * Created by NamNguyen1 on 27/5/17.
 */
public class ManoeuvreFactory {
    public static List<Move> threePointTurn(MyAIController ctrl, Coordinate dest) {
        return null;
    }

    public static List<Move> uTurn(MyAIController ctrl, Coordinate dest) {
        return null;
    }

    public static List<Move> reverseTurn(MyAIController ctrl, Coordinate dest) {
    	List<Move> ret = new ArrayList<Move>();
        
        // TODO: Should we add a check if the opposite orientation makes sense with our new dest?
        ret.add(new Move(dest, getOppositeOrientation(ctrl.getOrientation()), 0, true));
        return ret;
    }

    public static List<Move> followPath(MyAIController ctrl, Path path) {
    	List<Move> ret = new ArrayList<Move>();
    	// keep track of position based on traversing a path, starting at current
    	// car position
    	Coordinate currentPos = new Coordinate(ctrl.getPosition());
    	
    	for (Coordinate c : path.getTilesInPath()) {
    		float angle = (float)Math.toDegrees(Math.atan2(c.y - currentPos.y, c.x - currentPos.x));
    		Direction orientation;

    	    if (angle < 0){
    	        angle += 360;
    	    }
    	    
    	    // TODO: Do we even need to record this??
    	    if (angle <= WorldSpatial.NORTH_DEGREE) {
    	    	orientation = Direction.NORTH;
    	    } else if (angle <= WorldSpatial.WEST_DEGREE) {
    	    	orientation = Direction.WEST;
    	    } else if (angle <= WorldSpatial.SOUTH_DEGREE) {
    	    	orientation = Direction.SOUTH;
    	    } else {
    	    	orientation = Direction.EAST;
    	    }
    	    
    	    ret.add(new Move(c, orientation, angle, false));
    	    
    	    // moving to new point 'c' leads us to the car's new current position
    	    currentPos = c;
    	}
    	
        return ret;
    }
    
    private static WorldSpatial.Direction getOppositeOrientation(WorldSpatial.Direction direction) {
    	if (direction == Direction.NORTH) {
    		return Direction.SOUTH;
    	} else if (direction == Direction.SOUTH) {
    		return Direction.NORTH;
    	} else if (direction == Direction.EAST) {
    		return Direction.WEST;
    	} else {
    		return Direction.EAST;
    	}
    }
}
