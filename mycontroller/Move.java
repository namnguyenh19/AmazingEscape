package mycontroller;

import utilities.Coordinate;
import world.WorldSpatial;

/**
 * Created by NamNguyen1 on 27/5/17.
 */
public class Move {
    Coordinate dest;
    WorldSpatial.Direction orientation;
    float angle;
    boolean reverse;

    Move(Coordinate dest, WorldSpatial.Direction orient, float angle, boolean reverse){
        this.dest = dest;
        this.orientation = orient;
        this.angle = angle;
        this.reverse = reverse;
    }
}
