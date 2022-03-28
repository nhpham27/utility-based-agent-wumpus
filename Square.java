
public class Square {
	double isWumpus; // the chance of having Wumpus in this square
	// value range 0-9 (0-90%)
	double isPit; // the chance of having pit in this square
		// value range 0-9 (0-90%)
	int numVisit; // # of visits (# time steps the agent being in this square)
	boolean isSafe; // is this square safe?
	boolean isWall; // is this square a wall?
	char agentDirection; // direction of agent if it presents in this square
				// (A, >, <, v => North, East, West, South)
				// space character ' ' if agent is not in this square
	boolean hasStench; // the agent senses stench in this square
	boolean hasBreeze; // the agent senses breeeze in this square
	boolean hasGlitter;
	double value;
	int x,y; // coordinate of this square in the world state
	
	Square(){
	this.isWumpus = 0;
	this.isPit = 0;
	this.numVisit = 0;
	this.isWall = false;
	this.isSafe = false;
	this.hasStench = false;
	this.hasBreeze = false;
	this.hasGlitter = false;
	this.agentDirection = ' ';
	this.value = 0;
	x = y = -1;
	}
	
	// return max value in all the probabilities
	public double getMaxProb() {
		double[] arr = {this.isPit, this.isWumpus};
		double max = this.isPit;
		for(double val : arr) {
			if(max < val)
				max = val;
		}
		return max;
	}
	
	// return a copy of the current square
	public Square copySquare() {
		Square square = new Square();
		square.isWumpus = this.isWumpus;
		square.isPit = this.isPit;
		square.numVisit = this.numVisit;
		square.isWall = this.isWall;
		square.isSafe = this.isSafe;
		square.hasStench = this.hasStench;
		square.hasBreeze = this.hasBreeze;
		square.hasGlitter = this.hasGlitter;
		square.agentDirection =this.agentDirection;
		square.x = this.x;
		square.y = this.y;
		
		return square;
	}
}
