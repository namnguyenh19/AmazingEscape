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
    private boolean canUTurn;
    private boolean canThreePoint;


    public View(HashMap<Coordinate, MapTile> view, WorldSpatial.Direction dir){
        this.curDir = dir;
        this.curView = view;
    }

    /*
     * Adapted from checkFollowingWall from AIController
     */
    public boolean checkFollowingWall(){
        return false;
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
}
