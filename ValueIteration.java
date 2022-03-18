import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class ValueIteration {
	int numIteration;
	int NUM_ACTION = 4;
	private double EXPLORATION_CONSTANT = 100;
	private int simulationTime;
	WorldState valueMatrix;
	double goldValue = 1000;
	double deadValue = -1000;
	boolean treeDebug = false;
	ValueIteration(WorldState state, int numIteration){
		this.numIteration = numIteration;
		this.valueMatrix = state.copyWorldState();
	}
	
	private void initMatrix() {
		if(AgentFunction.debugMode)
			System.out.println("Initialize matrix");
		int matrixSize = this.valueMatrix.getWorldSize();
		for(int i = 0;i < matrixSize; i++) {
			for(int j = 0;j < matrixSize; j++) {
				Square q = this.valueMatrix.state[i][j];
				if(q.isWumpus > 0.1 && q.isPit > 0.1) {
					q.value = (q.isWumpus + q.isPit)*this.deadValue;
				}
				else if(q.numVisit == 0) {
					q.value = 1;
				}
				else if(q.numVisit > 0){
					q.value = 0;
				}
				else if(i == 0 || i == 5 || j == 0 || j == 0) {
					q.value = -1000;
				}
			}
		}
	}
	
	public void valueIteration() {
		if(AgentFunction.debugMode)
			System.out.println("State value iteration");
		this.initMatrix();
		int matrixSize = this.valueMatrix.getWorldSize();
		int n = this.numIteration;
		while(n > 0) {
			for(int i = 1;i < matrixSize - 1; i++) {
				for(int j = 1;j < matrixSize - 1; j++) {
					Square q = this.valueMatrix.state[i][j];
					if(q.isWumpus < 0.1 && q.isPit < 0.1 && q.numVisit > 0) {
						updateValue(q);
					}
				}
			}
			n--;
		}
		
	}
	
	private void updateValue(Square q) {
		Integer[] temp = {Action.GO_FORWARD, Action.TURN_LEFT, Action.TURN_RIGHT, Action.NO_OP, Action.GRAB};
		int x = q.x;
		int y = q.y;
		// get the squares around the agent
		Square topSquare = this.valueMatrix.state[x][y+1];
		Square leftSquare = this.valueMatrix.state[x-1][y];
		Square bottomSquare = this.valueMatrix.state[x][y-1];
		Square rightSquare = this.valueMatrix.state[x+1][y];
		double maxVal = -999999;
		// move up
		maxVal = getValue(maxVal, q, topSquare);
		maxVal = getValue(maxVal, q, leftSquare);
		maxVal = getValue(maxVal, q, rightSquare);
		maxVal = getValue(maxVal, q, bottomSquare);
//		for(int i = 0; i < squares.length; i++) {
//			double currVal = q.value;
//			currVal = -1 + squares[i].value;
//			
//			if(currVal > maxVal) {
//				maxVal = currVal;
//			}
//		}
		q.value = maxVal;
	}
	
	private double getValue(double maxVal, Square currSquare, Square aroundSquare) {
		double currVal = 0;
		if(aroundSquare.x == 0 || aroundSquare.x == 5 
				|| aroundSquare.y == 0 || aroundSquare.y == 5 ) {
			currVal = currSquare.value;
		}
		else {
			currVal = aroundSquare.value;
		}
		if(currVal > maxVal)
			maxVal = currVal;
		
		return maxVal;
	}
	
	public void printMatrix() {
		int matrixSize = this.valueMatrix.getWorldSize();
		for(int i = matrixSize - 1;i >= 0; i--) {
			for(int j = 0;j < matrixSize; j++) {
				Square temp = this.valueMatrix.state[j][i];
				System.out.print("[" + temp.value + "]");
			}
			System.out.println("");
		}
		System.out.println("");
	}
}


//
//public class ForwardSearch {
//	int step;
//	ForwardSearch(int s){
//		this.step = s;
//	}
//	
//	public int search(WorldState state, int step) {
//		int[] actions = {Action.GO_FORWARD, Action.TURN_LEFT, Action.TURN_RIGHT, Action.NO_OP};
//		int maxAction = 0;
//		double maxVal = -99999999;
//		for(int a : actions) {
//			double temp =  searchHelper(state, a, step);
//			
//			if(a == Action.NO_OP) {
//				temp += 1;
//			}
//			
//			if(temp > maxVal) {
//				maxVal = temp;
//				maxAction = a;
//			}
//			if(AgentFunction.debugMode) {
//				System.out.print("action: " + a);
//				System.out.println(", value: " + temp);
//			}
//		}
//		
//		return maxAction;
//	}
//	
//	public double searchHelper(WorldState state, int action, int step) {
//		WorldState newState = state.copyWorldState();
//		newState.updateAgentPosition(action);
//		double value = newState.evaluationFunction();
////		System.out.println(step);
//		if(step < 0)
//			return newState.evaluationFunction();
//		int[] actions = {Action.GO_FORWARD, Action.TURN_LEFT, Action.TURN_RIGHT, Action.NO_OP};
//		int maxAction = 0;
//		double maxVal = -99999999;
//		double sum = 0;
//		for(int a : actions) {
//			int tempStep = step - 1;
//			double temp =  searchHelper(newState, a, tempStep);
//			sum += temp;
//			if(temp > maxVal) {
//				maxVal = temp;
//				maxAction = a;
//			}
//		}
//		
//		return sum;
//	}
//}
