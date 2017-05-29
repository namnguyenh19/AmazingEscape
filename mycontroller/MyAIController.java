package mycontroller;

import controller.CarController;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial.Direction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
	// Car Speed to move at
	private final float CAR_SPEED = 2;
	
	private static final float ROTATE_EPSILON = 0.1f;

	private HashSet<Coordinate> visited;
	/** The tile we want to move to. */
	private List<Path> visitedPaths;

	public MyAIController(Car car) {
		super(car);
		
		actions = new ArrayList<Move>();
		visited = new HashSet<Coordinate>();
		visitedPaths = new ArrayList<Path>();
	}


	@Override
	public void update(float delta) {
		// Retrieve local surrounding of car, to be fed into View class to interpret it
		
		View currentView = new View(getView(), this.getOrientation(), getCurPos());

		System.out.println("Current position: " + getCurPos() + "\nCurrent orientation: " + getOrientation());
		
		visited.add(new Coordinate(getPosition()));

		if (actions.size() > 0) {
			this.applyMove(delta, currentView);
			return;
		}
		
		boolean nearDeadEnd = currentView.checkDeadEnd();
		List<Move> newMoves = null;
		
		if (nearDeadEnd) { 		// if we're at a dead-end
			// check if we can do 3-turn or u-turn
			currentView.checkSpace();
			
			// execute move based on space remaining
			if (currentView.isCanUTurn()) {
				newMoves = ManoeuvreFactory.uTurn(this);
			} else if (currentView.isCanThreePoint()) {
				newMoves = ManoeuvreFactory.threePointTurn(this);
			} else {
				newMoves = ManoeuvreFactory.reverseTurn(this);
			}

			actions.addAll(newMoves);
		} else {
			// find all potential paths, and move on the best path found
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
		boolean prepareTurn = false;


		float angleDiff = ManoeuvreFactory.toPrincipalAngle(this.getAngle() - move.angle);

		// do appropriate turn from the current Move, if angle isn't aligned
		if (Math.abs(angleDiff) > ROTATE_EPSILON) {
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
			}

			MAX_SPEED = CAR_SPEED * 0.5f;
		} else {
			// if there's a wall ahead, reduce speed
			if (view.checkWallAhead() ) {
				MAX_SPEED = CAR_SPEED * 0.3f;
				prepareTurn = true;
			} else {
				MAX_SPEED = CAR_SPEED;
			}
			
		}
		
		if (move.reverse && getVelocity() > -MAX_SPEED) {
			this.applyReverseAcceleration();
		} else if (prepareTurn && getVelocity() > MAX_SPEED) {
			this.applyReverseAcceleration();
		} else if (getVelocity() < MAX_SPEED) {
			this.applyForwardAcceleration();
		}
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
	
	
	private Coordinate getCurPos(){
		return new Coordinate(getPosition());
	}
}
