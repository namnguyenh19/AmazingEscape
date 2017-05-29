package mycontroller;

import controller.CarController;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Our AI auto-controller.
 *
 * Created by Project Group 37.
 *
 */
public class MyAIController extends CarController{
	/*
	 * Design flaw comment:
	 * 
	 * There are some deviations from our initial class diagram:
	 * 
	 * 1. applyMove(..) now has a delta parameter and no longer a move attribute.
	 * We have forgotten to consider that the turn methods needed a delta value.
	 * We encountered another problem of our controller requiring to remember the next destination
	 * once executing a Move.
	 * 
	 * To fix this, we hold a 'currentDest' which holds the desired destination, and we hold
	 * our current Move order until we reach currentDest, which then we can remove it from queue.
	 * 
	 * 
	 * 2. In the sequence diagram, we have forgotten to mention how the 'visited' attribute is
	 * updated; this is now included in our update(..) method.
	 * 
	 * 
	 */
	
	Queue<Move> actions;

	private boolean isTurningLeft = false;
	private boolean isTurningRight = false;
	private WorldSpatial.Direction previousState = null; // Keeps track of the previous state

	// Car Speed to move at
	private final float CAR_SPEED = 3;
	
	private static final float ROTATE_EPSILON = 3;

	// Offset used to differentiate between 0 and 360 degrees
	private int EAST_THRESHOLD = 3;

	private List<Coordinate> visited;
	/** The previous tile the car was on. */
	private Coordinate prevLocation;
	/** The tile we want to move to. */
	private Coordinate currentDest;

	public MyAIController(Car car) {
		super(car);
		
		actions = new LinkedList<Move>();
		visited = new ArrayList<Coordinate>();
		currentDest = new Coordinate(car.getPosition());
		prevLocation = currentDest;
	}


	@Override
	public void update(float delta) {
		// Retrieve local surrounding of car, to be fed into View class to interpret it
		
		View currentView = new View(getView(), this.getOrientation(), getCurPos());
		checkStateChange();
		
		System.out.println("Current position: " + getCurPos() + "\nCurrent orientation: " + getOrientation());
		
		if (!prevLocation.equals(new Coordinate(this.getPosition()))) {
			visited.add(prevLocation);
			prevLocation = new Coordinate(this.getPosition());
		}
		
		if (actions.size() > 0) {
			this.applyMove(delta);
			return;
		}
		
		boolean nearDeadEnd = currentView.checkDeadEnd();
		List<Move> newMoves = null;
		
		if (nearDeadEnd) {
							
			// TODO: for the dead end case. Do we need cornerAhead to be checked given we have paths?
			// TODO: make checkSpace() public
			currentView.checkSpace();
			
			// TODO: do we need to find the dest variable?
			if (currentView.isCanUTurn()) {
				newMoves = ManoeuvreFactory.uTurn(this);
			} else if (currentView.isCanThreePoint()) {
				newMoves = ManoeuvreFactory.threePointTurn(this);
			} else {
				newMoves = ManoeuvreFactory.reverseTurn(this);
			}
			
			System.out.println("near Deadend");
			for(Move m : newMoves){
				System.out.println(m.toString());
			}
			
			actions.addAll(newMoves);
		} else {
			
			//TODO CHECKCORNERAHDEAD not working
			if(currentView.checkCornerAhead()){
				System.out.println("Should turn left here");
				return;
			}
			
			for(Path p : currentView.getPaths()){
				System.out.println(p.toString());
			}
			
			Path bestPath = this.findBestPath(currentView.getPaths());
			
			
			System.out.println("Chosen path: " + bestPath.toString());
			
			newMoves = ManoeuvreFactory.followPath(this, bestPath);
			
			System.out.println("not near Deadend");
			for(Move m : newMoves){
				System.out.println(m.toString());
			}
			
			actions.addAll(newMoves);
		}

//		// If you are not following a wall initially, find a wall to stick to!
//		if(!currentView.checkFollowingWall()) {
//			
//			System.out.println("Following Wall");
//			
//			// Turn to a direction so that when we hit a wall, we can turn
//			// to left of the wall
//			List<Move> newMoves = ManoeuvreFactory.followWall(this);
//			
//			for(Move m : newMoves){
//				System.out.println(m.toString());
//			}
//			
//			actions.addAll(newMoves);
//			
//		}
//		// Once the car is already stuck to a wall, apply the following logic
//		else {
//						
//			boolean nearDeadEnd = currentView.checkDeadEnd();
//			List<Move> newMoves = null;
//			
//			if (nearDeadEnd) {
//								
//				// TODO: for the dead end case. Do we need cornerAhead to be checked given we have paths?
//				// TODO: make checkSpace() public
//				currentView.checkSpace();
//				
//				// TODO: do we need to find the dest variable?
//				if (currentView.isCanUTurn()) {
//					newMoves = ManoeuvreFactory.uTurn(this);
//				} else if (currentView.isCanThreePoint()) {
//					newMoves = ManoeuvreFactory.threePointTurn(this);
//				} else {
//					newMoves = ManoeuvreFactory.reverseTurn(this);
//				}
//				
//				System.out.println("near Deadend");
//				for(Move m : newMoves){
//					System.out.println(m.toString());
//				}
//				
//				actions.addAll(newMoves);
//			} else {
//				
//				Path bestPath = this.findBestPath(currentView.getPaths());
//				
//				newMoves = ManoeuvreFactory.followPath(this, bestPath);
//				
//				System.out.println("not near Deadend");
//				for(Move m : newMoves){
//					System.out.println(m.toString());
//				}
//				
//				actions.addAll(newMoves);
//			}
			
			/**

			// Readjust the car if it is misaligned.
			readjust(lastTurnDirection,delta);

			if(isTurningRight){
				applyRightTurn(getOrientation(),delta);
			}
			else if(isTurningLeft){
				// Apply the left turn if you are not currently near a wall.
				if(!currentView.checkFollowingWall()){
					applyLeftTurn(getOrientation(),delta);
				}
				else{
					isTurningLeft = false;
				}
			}
			// Try to determine whether or not the car is next to a wall.
			else if(currentView.checkFollowingWall()){
				// Maintain some velocity
				if(getVelocity() < CAR_SPEED){
					applyForwardAcceleration();
				}
				// If there is wall ahead, turn right!
				if(currentView.checkCornerAhead()){
					lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
					isTurningRight = true;

				}

			}
			// This indicates that I can do a left turn if I am not turning right
			else{
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				isTurningLeft = true;
			}
			
			**/
		
	}
	
	private void applyMove(float delta) {
		Move move = actions.peek();
		
		// if current Move has been done due to reaching dest
		if (move.dest != null && move.dest.equals(new Coordinate(this.getPosition()))) {
			actions.poll();
			move = actions.peek();
			
			if (move == null) {
				return;
			}
		}
		
		System.out.println(move);
		
		if (Math.abs(this.getAngle() - move.angle) > ROTATE_EPSILON) {
			// TODO: simple code, assumes the car is facing in the angle to do this
			// TODO: need to test for reverse case
			
			if (this.getOrientation() == Direction.EAST) {
				if (move.orientation == Direction.NORTH) {
					this.turnLeft(delta);
				} else if (move.orientation == Direction.SOUTH) {
					this.turnRight(delta);
				} else if (move.orientation == Direction.EAST) {
					if (this.getAngle() - move.angle > 0) {
						this.turnRight(delta);
					} else {
						this.turnLeft(delta);
					}
				}
			}
			
			if (this.getOrientation() == Direction.WEST) {
				if (move.orientation == Direction.NORTH) {
					this.turnRight(delta);
				} else if (move.orientation == Direction.SOUTH) {
					this.turnLeft(delta);
				}
			}
			
			if (this.getOrientation() == Direction.NORTH) {
				if (move.orientation == Direction.WEST) {
					this.turnLeft(delta);
				} else if (move.orientation == Direction.EAST) {
					this.turnRight(delta);
				}
			}
			
			if (this.getOrientation() == Direction.SOUTH) {
				if (move.orientation == Direction.WEST) {
					this.turnRight(delta);
				} else if (move.orientation == Direction.EAST) {
					this.turnLeft(delta);
				}
			}
		}
		
		if (move.reverse && getVelocity() > -CAR_SPEED) {
			this.applyReverseAcceleration();
		} else if (getVelocity() < CAR_SPEED) {
			this.applyForwardAcceleration();
		}
	}
	
	private Path findBestPath(List<Path> paths) {
		double pathCosts[] = new double[paths.size()];

		for(int i = 0; i < paths.size(); i++){
			pathCosts[i] = paths.get(i).calculatePathCost();
		}

		int minIndex = 0;
		double minVal = pathCosts[0];

		for(int i = 0; i < pathCosts.length; i++){
			if (pathCosts[i] < minVal){
				minVal = pathCosts[i];
				minIndex = i;
			}
		}

		return paths.get(minIndex);
	}
	
	public List<Coordinate> getVisitedTiles() {
		return visited;
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
	
	
	private Coordinate getCurPos(){
		return new Coordinate(getPosition());
	}
}
