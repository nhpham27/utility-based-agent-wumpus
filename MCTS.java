import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MCTS {
	Node root;
	int numIteration;
	int NUM_ACTION = 6;
	private double EXPLORATION_CONSTANT = 20;
	private int simulationTime;
	boolean treeDebug = false;
	HashMap<Integer, String> actionNames;
	
	MCTS(WorldState state, int numIteration, int simulTime){
		this.root = new Node(state);
		this.numIteration = numIteration;
		this.simulationTime = simulTime;
		
		this.actionNames = new HashMap<>();
		actionNames.put(Action.GO_FORWARD, "Forward");
		actionNames.put(Action.TURN_LEFT, "Left");
		actionNames.put(Action.TURN_RIGHT, "Right");
		actionNames.put(Action.GRAB, "Grab");
		actionNames.put(Action.SHOOT, "Shoot");
		actionNames.put(Action.NO_OP, "No-op");
	}
	
	public int getBestAction() {
		double maxVal = -99999999;
		int action = 0;
		for(int i = 0; i < root.children.length; i++) {
			Node currChild = root.children[i];
			// update max, find node to select
			if(currChild.value > maxVal && !currChild.isTerminal) {
				maxVal = currChild.value;
				action = currChild.action;
			}
			if(AgentFunction.debugMode) {
				System.out.print("action: " + this.actionNames.get(currChild.action));
				System.out.print(", value: " + currChild.value);
				if(currChild.isTerminal) {
					System.out.print("  This is terminal node");
				}
				System.out.println("");
			}
		}
		
		return action;
	}
	
	public void buildSearchTree() {
		if(this.treeDebug)
			System.out.println("****Start building search tree****");
		this.expansion(root);
		for(int i = 0; i < this.numIteration; i++) {
			if(this.treeDebug)
				System.out.println("Iteration: " + i);
			Node temp = this.selection();
			if(temp.numVisit == 0) {
				this.backpropagation(temp, this.simulation(temp));
			}
			else {
				if(temp.isTerminal == false) {
					this.expansion(temp);
					Node firstChild = temp.children[0];
					this.backpropagation(firstChild, this.simulation(firstChild));
				}
			}
		}
	}
	
	private double UCB1(double exploitation, int parentVisit, int childVisit) {
		return exploitation + this.EXPLORATION_CONSTANT*Math.sqrt(Math.log(parentVisit)/(childVisit+1));
	}
	
	private Node selection() {
		if(this.treeDebug)
			System.out.println("Selection");
		Node temp = this.root;
		double maxVal = -99999999;
		int maxChild = 0;
		while(temp.children != null) {
			for(int i = 0; i < temp.children.length; i++) {
				Node currChild = temp.children[i];
				double currVal = this.UCB1(currChild.value, temp.numVisit, currChild.numVisit);
				// update max, find node to select
				if(currVal > maxVal) {
					maxVal = currVal;
					maxChild = i;
				}
			}
			
			// update temp as the selected node
			temp = temp.children[maxChild];
		}
		
		return temp;
	}
	
	private void expansion(Node node) {
		if(this.treeDebug)
			System.out.println("Expansion");
		Integer[] temp = {Action.GO_FORWARD, Action.TURN_LEFT, 
				Action.TURN_RIGHT, Action.NO_OP, 
				Action.GRAB, Action.SHOOT};
		List<Integer> actions = Arrays.asList(temp);
		Collections.shuffle(actions);
		node.children = new Node[this.NUM_ACTION];
		for(int i = 0; i < actions.size(); i++) {
			// make a copy of current node's world state
			WorldState st = node.state.copyWorldState();
			// update the position of agent based on action
			st.updateAgentPosition(actions.get(i));
			
			// create a new node taking this new world state
			Node child = new Node(st);
			// set its parent as the current node
			child.parent = node;
			// set the action to lead to this child node from parent node
			child.action = actions.get(i);
			// add new node to children list
			node.children[i] = child;
//			child.value = st.evaluationFunction(child.action);
			if(st.getAgentLocation().isWumpus > 0 
					|| st.getAgentLocation().isPit > 0 ) {
				child.isTerminal = true;
			}
		}
	}
	
	private double simulation(Node node) {
		if(this.treeDebug)
			System.out.println("Simulation");
		WorldState tempWorld = node.state.copyWorldState();
		Integer[] actions = {Action.GO_FORWARD, Action.TURN_LEFT, 
							Action.TURN_RIGHT, Action.NO_OP, 
							Action.GRAB, Action.SHOOT};
		double totalValue = 0;

		boolean agentDead = false;
//		for(int i = 0; i < simulationTime; i++) {
//			// get a random action
//			List<Integer> actions = Arrays.asList(temp);
//			Collections.shuffle(actions);
//			
//			// make a move based on the action
//			tempWorld.updateAgentPosition(actions.get(0));
//			
//			// add the value of the state to the total
//			//double value = this.evaluate(tempWorld, actions.get(0));
//			double value = tempWorld.evaluationFunction(actions.get(0));
//			totalValue += value;
//		}

			double maxVal = -99999;
			for(int j = 0; j < actions.length; j++) {
				tempWorld.updateAgentPosition(actions[j]);
				double value = tempWorld.evaluationFunction(actions[j]);
				if(value > maxVal) {
					maxVal = value;
				}
			}
			totalValue += maxVal;

		return totalValue;
	}
	
	private int evaluate(WorldState state, int action) {
		Square agentLoc = state.getAgentLocation();
		HashMap<String,Square> squares = state.getAroundSquares(agentLoc);

		if(agentLoc.isWumpus > 0 || agentLoc.isPit > 0) {
			return -1000;
		}
		else if(agentLoc.hasGlitter && action == Action.GRAB) {
			return 1000;
		}
		else if(action == Action.SHOOT 
				&& squares.get("front").isWumpus > 0
				&& state.checkArrow() == true) {
			return 2;
		}
		else {
			return 10 - agentLoc.numVisit;
		}
	}
	
	private void backpropagation(Node node, double value) {
		if(this.treeDebug)
			System.out.println("Back Propagation");
		Node temp = node;
		while(temp != null) {
			temp.value += value;
			temp.numVisit += 1;
			temp = temp.parent;
		}
	}
}
