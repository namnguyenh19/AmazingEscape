package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NamNguyen1 on 27/5/17.
 */
public class GrassHandler extends TrapHandler{

    private WorldSpatial.RelativeDirection[] POSSIBLEDIR = {WorldSpatial.RelativeDirection.LEFT, WorldSpatial.RelativeDirection.RIGHT};

    public GrassHandler(boolean blocked, HashMap<Coordinate, MapTile> view){
        super(blocked,view);
    }

    public boolean handleTrap(Path path){
        Direction prevDir = path.getOrientation();
        
        for (int i = 0; i < path.getTilesInPath().size(); i++) {
        	Coordinate c = path.getTilesInPath().get(i);
        	Direction nextDir = null;
        	
        	if (path.getTileName().get(c).equals("Grass")) {
        		if (i < path.getTilesInPath().size()) {
        			Coordinate next_c = path.getTilesInPath().get(i+1);
        			nextDir = ManoeuvreFactory.toDirection((int)ManoeuvreFactory.getAngleTwoPts(c, next_c));
        			
        			// if we make a turn in the grass (considering previous orientation),
        			// then avoid making this path which has this grass
        			if ((prevDir == Direction.NORTH || prevDir == Direction.SOUTH) &&
        					(nextDir == Direction.EAST || nextDir == Direction.WEST)) {
        				return false;
        			} else if ((prevDir == Direction.EAST || prevDir == Direction.WEST) &&
        					(nextDir == Direction.NORTH || nextDir == Direction.SOUTH)) {
        				return false;
        			}
        		}
        	}
        	
        	prevDir = nextDir;
        }
        
        if (pathBlocked){
            return false;
        }
        
        return true;
    }
}
