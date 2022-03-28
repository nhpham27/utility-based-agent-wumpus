import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MCTS {
	Node root;
	int numIteration;
	int NUM_ACTION = 6;
	private double EXPLORATION_CONSTANT = 20000;
	boolean treeDebug = false;
	HashMap<Integer, String> actionNames;
	
	MCTS(WorldState state, int numIteration){
		this.root = new Node(state);
		this.numIteration = numIteration;
		
		this.actionNames = new HashMap<>();
		actionNames.put(Action.GO_FORWARD, "Forward");
		actionNames.put(Action.TURN_LEFT, "Left");
		actionNames.put(Action.TURN_RIGHT, "Right");
		actionNames.put(Action.GRAB, "Grab");
		actionNames.put(Action.SHOOT, "Shoot");
		actionNames.put(Action.NO_OP, "No-op");
	}
	
	// get the best of the agent by selecting the child node
	// that have the best value
	public int getBestAction() {
		double maxVal = -99999999;
		int action = 0;
		for(int i = 0; i < root.children.length; i++) {
			Node currChild = root.children[i];
			// update max, find node to select
			if(currChild.value > maxVal) {
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
	
	// Build the Monte Carlo tree search using the number of iteration
	public void buildSearchTree() {
		if(this.treeDebug)
			System.out.println("****Start building search tree****");
		// Add child nodes for the root node
		this.expansion(root);
		for(int i = 0; i < this.numIteration; i++) {
			if(this.treeDebug)
				System.out.println("Iteration: " + i);
			// select the next node to visit
			Node temp = this.selection();
			if(temp.numVisit == 0) {
				// if the node has not been visited, do simulation and back propagation
				this.backpropagation(temp, this.simulation(temp));
			}
			else {
				// if the node has been visited, expand the node and
				// do simulation, back propagation on the first child
				this.expansion(temp);
				Node firstChild = temp.children[0];
				this.backpropagation(firstChild, this.simulation(firstChild));
			}
		}
	}
	
	// The UCB1 formula to select the next node to explore
	private double UCB1(double exploitation, int parentVisit, int childVisit) {
		return exploitation + this.EXPLORATION_CONSTANT*Math.sqrt(Math.log(parentVisit)/(childVisit+1));
	}
	
	// Select the next node to explore
	private Node selection() {
		if(this.treeDebug)
			System.out.println("Selection");
		Node temp = this.root;
		double maxVal = -99999999;
		int maxChild = 0;
		// traverse the tree, select the child nodes that have
		// highest value from UCB1 formula
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
	
	// Expand the node by adding child nodes for each possible action
	// of the agent
	private void expansion(Node node) {
		if(this.treeDebug)
			System.out.println("Expansion");
		Integer[] temp = {Action.GRAB, Action.GO_FORWARD, Action.TURN_LEFT, 
				Action.TURN_RIGHT, Action.NO_OP, 
				Action.SHOOT};
		// shuffle the actions in the list so that
		// the first action from the list will be 
		// selected randomly
		List<Integer> actions = Arrays.asList(temp);
		//Collections.shuffle(actions);
		node.children = new Node[this.NUM_ACTION];
		// Add a child node for each action
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
			//child.value = this.simulation(child);
		}
	}
	
	// Simulate the node by evaluating the value of the
	// state that the agent is currently in
	private double simulation(Node node) {
		if(this.treeDebug)
			System.out.println("Simulation");

		return node.state.evaluationFunction(node.action);
	}
	
	// Doing back propagation to accumulate the value along
	// the way back to the root node
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
