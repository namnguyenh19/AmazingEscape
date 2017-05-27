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
    	List<Move> ret = new ArrayList<Move>();
    	
        return null;
    }

    public static List<Move> uTurn(MyAIController ctrl, Coordinate dest) {
    	List<Move> ret = new ArrayList<Move>();
    	
    	// assume we have a row of 3 tiles in the car's direction to make u-turn
    	// this needs to be checked by an external function, if not checked here
    	
    	// MOVE 1: /
    	Coordinate newDir = getUnitCoordinate(ctrl.getOrientation());
    	Coordinate newCoord = addCoords(newDir, new Coordinate(ctrl.getPosition()));
    	float newAngle = toPrincipalAngle(toAngle(ctrl.getOrientation()) - 45);
    	ret.add(new Move(newCoord, ctrl.getOrientation(), newAngle, false));
    	
    	// MOVE 2: --
    	newDir = getUnitCoordinate(getClockwiseDirection(ctrl.getOrientation()));
    	newCoord = addCoords(newCoord, newDir);
    	newAngle = toPrincipalAngle(newAngle - 45);
    	ret.add(new Move(newCoord, getClockwiseDirection(ctrl.getOrientation()), newAngle, false));
    	
    	// MOVE 3: \
    	newCoord = addCoords(newCoord, newDir);
    	newAngle = toPrincipalAngle(newAngle - 45);
    	ret.add(new Move(newCoord, getClockwiseDirection(ctrl.getOrientation()), newAngle, false));
    	
    	// HEAD TO DESTINATION
    	newAngle = toPrincipalAngle(newAngle - 45);
    	ret.add(new Move(dest, ctrl.getOrientation(), newAngle, false));
    	
        return ret;
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
    		angle = toPrincipalAngle(angle);
    		Direction orientation;
    	    
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
    
    private static float toPrincipalAngle(float angle) {
    	return (angle + 360) % 360;
    }
    
    private static Coordinate addCoords(Coordinate c1, Coordinate c2) {
    	return new Coordinate(Integer.toString(c1.x + c2.x) + "," + Integer.toString(c1.y + c2.y));
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
    
    private static Coordinate getUnitCoordinate(WorldSpatial.Direction direction) {
    	if (direction == Direction.NORTH) {
    		return new Coordinate("0,1");
    	} else if (direction == Direction.SOUTH) {
    		return new Coordinate("0,-1");
    	} else if (direction == Direction.EAST) {
    		return new Coordinate("1,0");
    	} else {
    		return new Coordinate("-1,0");
    	}
    }
    
    private static WorldSpatial.Direction getClockwiseDirection(WorldSpatial.Direction direction) {
    	if (direction == Direction.NORTH) {
    		return Direction.EAST;
    	} else if (direction == Direction.EAST) {
    		return Direction.SOUTH;
    	} else if (direction == Direction.SOUTH) {
    		return Direction.WEST;
    	} else {
    		return Direction.NORTH;
    	}
    }
    
    private static int toAngle(WorldSpatial.Direction direction) {
    	if (direction == Direction.NORTH) {
    		return WorldSpatial.NORTH_DEGREE;
    	} else if (direction == Direction.EAST) {
    		return WorldSpatial.EAST_DEGREE_MIN;
    	} else if (direction == Direction.SOUTH) {
    		return WorldSpatial.SOUTH_DEGREE;
    	} else {
    		return WorldSpatial.WEST_DEGREE;
    	}
    }
}
