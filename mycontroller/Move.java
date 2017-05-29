package mycontroller;

import utilities.Coordinate;
import world.WorldSpatial;

/**
 * Atomic class representing a move order.
 * 
 * Created by Project Group 37.
 */
public class Move {
    Coordinate dest;
    WorldSpatial.Direction orientation;
    /** Desired turn angle. */
    float angle;
    boolean reverse;

    Move(Coordinate dest, WorldSpatial.Direction orient, float angle, boolean reverse){
        this.dest = dest;
        this.orientation = orient;
        this.angle = angle;
        this.reverse = reverse;
    }
    
    public String toString(){
    	return "Going to " + dest + " with orientation " + this.orientation + " and angle " + this.angle;
    }
}
