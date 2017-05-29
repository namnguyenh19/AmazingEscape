package mycontroller;

import controller.CarController;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

import java.util.ArrayList;
import java.util.HashSet;
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
	
	List<Move> actions;

	private boolean isTurningLeft = false;
	private boolean isTurningRight = false;
	private WorldSpatial.Direction previousState = null; // Keeps track of the previous state

	// Car Speed to move at
	private final float CAR_SPEED = 3;
	
	private static final float ROTATE_EPSILON = 1;

	// Offset used to differentiate between 0 and 360 degrees
	private int EAST_THRESHOLD = 3;

	private HashSet<Coordinate> visited;
	/** The tile we want to move to. */
	private List<Path> visitedPaths;

	public MyAIController(Car car) {
		super(car);
		
		actions = new ArrayList<Move>();
		visited = new HashSet<Coordinate>();
		visitedPaths = new ArrayList<Path>();
		
		previousState = this.getOrientation();
	}


	@Override
	public void update(float delta) {
		// Retrieve local surrounding of car, to be fed into View class to interpret it
		
		View currentView = new View(getView(), this.getOrientation(), getCurPos());
		//checkStateChange();
		
		System.out.println("Current position: " + getCurPos() + "\nCurrent orientation: " + getOrientation());
		
		visited.add(new Coordinate(getPosition()));

		if (actions.size() > 0) {
			this.applyMove(delta, currentView);
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
			
			List<Path> allPaths = currentView.getPaths();
			for(Path p : allPaths){
				System.out.println(p.toString());
			}
			System.out.println("Number of paths found: " + allPaths.size());
			
			Path bestPath = this.findBestPath(allPaths);
			this.visitedPaths.add(bestPath);
			
			System.out.println("Chosen path: " + bestPath.toString());
			
			newMoves = ManoeuvreFactory.followPath(this, bestPath);
			
			System.out.println("not near Deadend");
			for(Move m : newMoves){
				System.out.println(m.toString());
			}
			
			actions.addAll(newMoves);
		}
	}
	
	private void applyMove(float delta, View view) {
		Move move = actions.get(0);
		
		if (move == null) {
			return;
		}
		
		System.out.println("Num actions left: " + actions.size() + " " + this.getOrientation());
		
		// if current Move has been done due to reaching dest
		if (move.dest != null && move.dest.equals(new Coordinate(this.getPosition()))) {
			actions.remove(0);
			
			if (actions.size() > 0) {
				move = actions.get(0);
			} else {
				return;
			}
		}
		
		float MAX_SPEED = 2f;
		boolean isRotating = true;
		Move move2 = actions.size() > 1 ? actions.get(1) : null;

		if (Math.abs(this.getAngle() - move.angle) > ROTATE_EPSILON) {
			// TODO: need to test for reverse case
			
			if (move.orientation == ManoeuvreFactory.getAntiClockwiseDirection(this.getOrientation())) {
				this.turnLeft(delta);
			} else if (move.orientation == ManoeuvreFactory.getClockwiseDirection(this.getOrientation())) {
				this.turnRight(delta);
			} else if (move.orientation == this.getOrientation()) {
				if ((move.orientation == Direction.EAST && this.getAngle() - move.angle < 0) || this.getAngle() - move.angle > 0) {
					this.turnRight(delta);
				} else {
					this.turnLeft(delta);
				}
			} else {
				isRotating = false;
			}
			
			
			MAX_SPEED = Math.abs(this.getAngle() - move.angle)/move.angle * CAR_SPEED * 0.2f;
		} else {
			if (move2 != null && move2.orientation != move.orientation) {
				System.out.println("INCOMING");
				MAX_SPEED = 0.2f * CAR_SPEED;
			} else {
				isRotating = false;
				MAX_SPEED = CAR_SPEED;
			}
			
			
		}
		
		if (move.reverse && getVelocity() > -MAX_SPEED) {
			this.applyReverseAcceleration();
		} else if (isRotating && getVelocity() > MAX_SPEED) {
			this.applyReverseAcceleration();
		} else if (getVelocity() < MAX_SPEED) {
			this.applyForwardAcceleration();
		}
		
		System.out.println(this.getVelocity());
	}
	
	
	private Path findBestPath(List<Path> paths) {		
		double minVal = paths.get(0).calculatePathCost();
		Path bestPath = paths.get(0);
		
		for (Path p : paths) {
			if (visitedPaths.contains(p)) {
				continue;
			}
			
			if (p.validatePath()) {
				double cost = p.calculatePathCost();
				
				if (cost < minVal) {
					minVal = cost;
					bestPath = p;
				}
			}
		}

		return bestPath;
	}
	
	public HashSet<Coordinate> getVisitedTiles() {
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
