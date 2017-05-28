package mycontroller;

import java.util.ArrayList;
import java.util.List;

import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

/**
 * Factory class for creating a list of atomic moves, representing movements
 * such as a three-point turn.
 * 
 * Created by NamNguyen1 on 27/5/17.
 */
public class ManoeuvreFactory {
	/*
	 * Design flaw comment:
	 * 
	 * Previously in the class diagram, the parameter 'dest' was in threePointTurn,
	 * uTurn and reverseTurn methods. We have removed this, during our implementation,
	 * for a few reasons:
	 * 
	 * 1. The three manoeuvres we have to implementat are mainly used for dealing
	 * with dead-ends. Thus, the destination can be determined when we hit a wall during
	 * a U-turn, 3-point turn etc.
	 * 
	 * 2. No other class is determining the 'dest' parameter in our diagrams, as View class
	 * only checks if we can do these manoevure's.
	 * 
	 */
	
	
    public static List<Move> threePointTurn(MyAIController ctrl) {
    	List<Move> ret = new ArrayList<Move>();
    	
    	// MOVE 1:
    	Coordinate newCoordAdd = addCoords(toCoordinate(ctrl.getOrientation()),
    				toCoordinate(getClockwiseDirection(ctrl.getOrientation())));
    	
    	Coordinate newCoord = addCoords(new Coordinate(ctrl.getPosition()), toCoordinate(ctrl.getOrientation()));
    	newCoord = addCoords(newCoord, toCoordinate(getClockwiseDirection(ctrl.getOrientation())));

    	while (!ctrl.getView().get(newCoord).getName().equals("Wall")) {
    		newCoord = addCoords(newCoord, toCoordinate(getClockwiseDirection(ctrl.getOrientation())));
    	}
    	
    	float newAngle = toPrincipalAngle((float)Math.toDegrees(Math.atan2(newCoordAdd.x, newCoordAdd.y)));
    	Direction newOrient = getClockwiseDirection(ctrl.getOrientation());
    	
    	ret.add(new Move(newCoord, newOrient, newAngle, false));
    	
    	
    	// MOVE 2
    	ret.add(new Move(addCoords(new Coordinate(ctrl.getPosition()), toCoordinate(ctrl.getOrientation())),
    			newOrient, toPrincipalAngle(newAngle - 180), true));
    	
    	// HEAD TO DESTINATION
    	newOrient = getOppositeDir(ctrl.getOrientation());
    	newCoord = addCoords(newCoord, toCoordinate(newOrient));
    	ret.add(new Move(newCoord, newOrient, toAngle(newOrient), false));
    	
    	
        return ret;
    }

    public static List<Move> uTurn(MyAIController ctrl) {
    	List<Move> ret = new ArrayList<Move>();
    	
    	// assume we have a row of 3 tiles in the car's direction to make u-turn
    	// this needs to be checked by an external function, if not checked here
    	
    	// MOVE 1: /
    	Coordinate newDir = toCoordinate(ctrl.getOrientation());
    	Coordinate newCoord = addCoords(newDir, new Coordinate(ctrl.getPosition()));
    	float newAngle = toPrincipalAngle(toAngle(ctrl.getOrientation()) - 45);
    	ret.add(new Move(newCoord, ctrl.getOrientation(), newAngle, false));
    	
    	// MOVE 2: --
    	// TODO: This may not be needed, depending on the cars speed, and can be implied.
    	newDir = toCoordinate(getClockwiseDirection(ctrl.getOrientation()));
    	newAngle = toPrincipalAngle(newAngle - 45);
    	
    	while (!ctrl.getView().get(newCoord).getName().equals("Wall")) {
    		newCoord = addCoords(newCoord, newDir);
    		
    		if (ctrl.getView().get(newCoord).getName().equals("Wall")) {
    			break;
    		}
    		
        	ret.add(new Move(newCoord, getClockwiseDirection(ctrl.getOrientation()), newAngle, false));
    	}
    	
    	// MOVE 3: \
    	newCoord = addCoords(newCoord, newDir);
    	newAngle = toPrincipalAngle(newAngle - 45);
    	ret.add(new Move(newCoord, getClockwiseDirection(ctrl.getOrientation()), newAngle, false));
    	
    	// HEAD TO DESTINATION
    	newCoord = addCoords(newCoord, toCoordinate(getOppositeDir(ctrl.getOrientation())));
    	newAngle = toPrincipalAngle(newAngle - 45);
    	ret.add(new Move(newCoord, getOppositeDir(ctrl.getOrientation()), newAngle, false));
    	
        return ret;
    }

    public static List<Move> reverseTurn(MyAIController ctrl) {
    	List<Move> ret = new ArrayList<Move>();
        
        // TODO: Should we add a check if the opposite orientation makes sense with our new dest?
    	Direction newOrient = getOppositeDir(ctrl.getOrientation());
    	Coordinate dest = addCoords(new Coordinate(ctrl.getPosition()), toCoordinate(newOrient));
    	
        ret.add(new Move(dest, newOrient, toAngle(newOrient), true));
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
    
    public static List<Move> followWall(MyAIController ctrl) {
    	Direction antiDir = getAntiClockwiseDirection(ctrl.getOrientation());
    	List<Move> ret = new ArrayList<Move>();
    	
    	// TODO: added null coordinate to indicate we are not going to a certain coordinate but 
    	ret.add(new Move(null, antiDir, toAngle(antiDir), false));
    	
    	return ret;
    }
    
    private static float toPrincipalAngle(float angle) {
    	return (angle + 360) % 360;
    }
    
    private static Coordinate addCoords(Coordinate c1, Coordinate c2) {
    	return new Coordinate(c1.x + c2.x, c1.y + c2.y);
    }
    
    private static WorldSpatial.Direction getOppositeDir(WorldSpatial.Direction direction) {
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
    
    private static Coordinate toCoordinate(WorldSpatial.Direction direction) {
    	if (direction == Direction.NORTH) {
    		return new Coordinate(0, 1);
    	} else if (direction == Direction.SOUTH) {
    		return new Coordinate(0, -1);
    	} else if (direction == Direction.EAST) {
    		return new Coordinate(1, 0);
    	} else {
    		return new Coordinate(-1, 0);
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
    
    private static WorldSpatial.Direction getAntiClockwiseDirection(WorldSpatial.Direction direction) {
    	if (direction == Direction.NORTH) {
    		return Direction.WEST;
    	} else if (direction == Direction.EAST) {
    		return Direction.NORTH;
    	} else if (direction == Direction.SOUTH) {
    		return Direction.EAST;
    	} else {
    		return Direction.SOUTH;
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
