package mycontroller;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class MyAIController extends CarController{

	Queue<Move> actions;

	private boolean isFollowingWall = false; // This is initialized when the car sticks to a wall.
	private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false;
	private WorldSpatial.Direction previousState = null; // Keeps track of the previous state

	// Car Speed to move at
	private final float CAR_SPEED = 3;

	// Offset used to differentiate between 0 and 360 degrees
	private int EAST_THRESHOLD = 3;

	private ArrayList<Coordinate> visited;

	public MyAIController(Car car) {
		super(car);
	}


	@Override
	public void update(float delta) {

		//TODO replace with View and Path logic
//		// Gets what the car can see
//		HashMap<Coordinate, MapTile> currentView = getView();
//
//
//		checkStateChange();
//
//
//		// If you are not following a wall initially, find a wall to stick to!
//		if(!isFollowingWall){
//			if(getVelocity() < CAR_SPEED){
//				applyForwardAcceleration();
//			}
//			// Turn towards the north
//			if(!getOrientation().equals(WorldSpatial.Direction.NORTH)){
//				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
//				applyLeftTurn(getOrientation(),delta);
//			}
//			if(checkNorth(currentView)){
//				// Turn right until we go back to east!
//				if(!getOrientation().equals(WorldSpatial.Direction.EAST)){
//					lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
//					applyRightTurn(getOrientation(),delta);
//				}
//				else{
//					isFollowingWall = true;
//				}
//			}
//		}
//		// Once the car is already stuck to a wall, apply the following logic
//		else{
//
//			// Readjust the car if it is misaligned.
//			readjust(lastTurnDirection,delta);
//
//			if(isTurningRight){
//				applyRightTurn(getOrientation(),delta);
//			}
//			else if(isTurningLeft){
//				// Apply the left turn if you are not currently near a wall.
//				if(!checkFollowingWall(getOrientation(),currentView)){
//					applyLeftTurn(getOrientation(),delta);
//				}
//				else{
//					isTurningLeft = false;
//				}
//			}
//			// Try to determine whether or not the car is next to a wall.
//			else if(checkFollowingWall(getOrientation(),currentView)){
//				// Maintain some velocity
//				if(getVelocity() < CAR_SPEED){
//					applyForwardAcceleration();
//				}
//				// If there is wall ahead, turn right!
//				if(checkWallAhead(getOrientation(),currentView)){
//					lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
//					isTurningRight = true;
//
//				}
//
//			}
//			// This indicates that I can do a left turn if I am not turning right
//			else{
//				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
//				isTurningLeft = true;
//			}
//		}



	}

	/**
	 * Readjust the car to the orientation we are in.
	 * @param lastTurnDirection
	 * @param delta
	 */
	private void readjust(WorldSpatial.RelativeDirection lastTurnDirection, float delta) {
		if(lastTurnDirection != null){
			if(!isTurningRight && lastTurnDirection.equals(WorldSpatial.RelativeDirection.RIGHT)){
				adjustRight(getOrientation(),delta);
			}
			else if(!isTurningLeft && lastTurnDirection.equals(WorldSpatial.RelativeDirection.LEFT)){
				adjustLeft(getOrientation(),delta);
			}
		}

	}

	/**
	 * Try to orient myself to a degree that I was supposed to be at if I am
	 * misaligned.
	 */
	private void adjustLeft(WorldSpatial.Direction orientation, float delta) {

		switch(orientation){
			case EAST:
				if(getAngle() > WorldSpatial.EAST_DEGREE_MIN+EAST_THRESHOLD){
					turnRight(delta);
				}
				break;
			case NORTH:
				if(getAngle() > WorldSpatial.NORTH_DEGREE){
					turnRight(delta);
				}
				break;
			case SOUTH:
				if(getAngle() > WorldSpatial.SOUTH_DEGREE){
					turnRight(delta);
				}
				break;
			case WEST:
				if(getAngle() > WorldSpatial.WEST_DEGREE){
					turnRight(delta);
				}
				break;

			default:
				break;
		}

	}

	private void adjustRight(WorldSpatial.Direction orientation, float delta) {
		switch(orientation){
			case EAST:
				if(getAngle() > WorldSpatial.SOUTH_DEGREE && getAngle() < WorldSpatial.EAST_DEGREE_MAX){
					turnLeft(delta);
				}
				break;
			case NORTH:
				if(getAngle() < WorldSpatial.NORTH_DEGREE){
					turnLeft(delta);
				}
				break;
			case SOUTH:
				if(getAngle() < WorldSpatial.SOUTH_DEGREE){
					turnLeft(delta);
				}
				break;
			case WEST:
				if(getAngle() < WorldSpatial.WEST_DEGREE){
					turnLeft(delta);
				}
				break;

			default:
				break;
		}

	}

	/**
	 * Checks whether the car's state has changed or not, stops turning if it
	 *  already has.
	 */
	private void checkStateChange() {
		if(previousState == null){
			previousState = getOrientation();
		}
		else{
			if(previousState != getOrientation()){
				if(isTurningLeft){
					isTurningLeft = false;
				}
				if(isTurningRight){
					isTurningRight = false;
				}
				previousState = getOrientation();
			}
		}
	}

	/**
	 * Turn the car counter clock wise (think of a compass going counter clock-wise)
	 */
	private void applyLeftTurn(WorldSpatial.Direction orientation, float delta) {
		switch(orientation){
			case EAST:
				if(!getOrientation().equals(WorldSpatial.Direction.NORTH)){
					turnLeft(delta);
				}
				break;
			case NORTH:
				if(!getOrientation().equals(WorldSpatial.Direction.WEST)){
					turnLeft(delta);
				}
				break;
			case SOUTH:
				if(!getOrientation().equals(WorldSpatial.Direction.EAST)){
					turnLeft(delta);
				}
				break;
			case WEST:
				if(!getOrientation().equals(WorldSpatial.Direction.SOUTH)){
					turnLeft(delta);
				}
				break;
			default:
				break;

		}

	}

	/**
	 * Turn the car clock wise (think of a compass going clock-wise)
	 */
	private void applyRightTurn(WorldSpatial.Direction orientation, float delta) {
		switch(orientation){
			case EAST:
				if(!getOrientation().equals(WorldSpatial.Direction.SOUTH)){
					turnRight(delta);
				}
				break;
			case NORTH:
				if(!getOrientation().equals(WorldSpatial.Direction.EAST)){
					turnRight(delta);
				}
				break;
			case SOUTH:
				if(!getOrientation().equals(WorldSpatial.Direction.WEST)){
					turnRight(delta);
				}
				break;
			case WEST:
				if(!getOrientation().equals(WorldSpatial.Direction.NORTH)){
					turnRight(delta);
				}
				break;
			default:
				break;

		}

	}

}
