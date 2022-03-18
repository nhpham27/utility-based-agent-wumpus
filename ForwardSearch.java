import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ForwardSearch {
	Node root;
	int numIteration;
	int NUM_ACTION = 5;
	boolean treeDebug = false;
	private int treeDepth;
	
	ForwardSearch(WorldState state, int numIteration, int depth){
		this.root = new Node(state);
		this.root.depth = 1;
		this.numIteration = numIteration;
		this.treeDepth = depth;
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
				System.out.print("action: " + currChild.action);
				System.out.println(", value: " + currChild.value);
			}
			if(currChild.isTerminal) {
				System.out.print("This is terminal");
			}
		}
		
		return action;
	}
	
	public void buildSearchTree() {
		if(this.treeDebug)
			System.out.println("****Start building search tree****");
		Queue<Node> queue = new LinkedList<>();
		queue.add(root);
		
		while(!queue.isEmpty()) {
			Node currNode = queue.remove();
			this.backpropagation(currNode, this.simulation(currNode, NUM_ACTION));
			if(currNode.depth < this.treeDepth) {
				this.expandNode(currNode);
				for(int i = 0; i < currNode.children.length; i++) {
					queue.add(currNode.children[i]);
				}
			}
		}
	}
	
	public void updateValues() {
		if(this.treeDebug)
			System.out.println("****Start updating values****");
		Queue<Node> queue = new LinkedList<>();
		queue.add(root);
		
		while(!queue.isEmpty()) {
			Node currNode = queue.remove();
		}
	}
	
	private void expandNode(Node node) {
		if(this.treeDebug)
			System.out.println("Expansion");
		Integer[] actions = {Action.GO_FORWARD, Action.TURN_LEFT, Action.TURN_RIGHT, Action.NO_OP, Action.GRAB};
		node.children = new Node[this.NUM_ACTION];
		double value, maxValue = -99999;
		for(int i = 0; i < actions.length; i++) {
			// make a copy of current node's world state
			WorldState st = node.state.copyWorldState();
			// create a new node taking this new world state
			Node child = new Node(st);
			// update the position of agent based on action
			st.updateAgentPosition(actions[i]);
			// set its parent as the current node
			child.parent = node;
			// set the action to lead to this child node from parent node
			child.action = actions[i];
			child.depth = node.depth + 1;
			// add new node to children list
			node.children[i] = child;
		}
		
		
	}
	
	private double simulation(Node node, int action) {
		WorldState tempState = node.state.copyWorldState();
		double value = 0;
		
//		tempState.updateAgentPosition(action);
//		Square agentLoc = tempState.getAgentLocation();
//		if(agentLoc.isWumpus > 0 || agentLoc.isPit > 0) {
//			value = -100;
//		}
//		else if(agentLoc.numVisit == 1) {
//			value = 20;
//		}
//		else if(agentLoc.hasGlitter && action == Action.GRAB) {
//			value = 100;
//		}
//		else if(action == Action.TURN_LEFT) {
//			HashMap<String,Square> squares = tempState.getAroundSquares(agentLoc);
//			if(squares.get("front").numVisit == 0) {
//				value = 10 - squares.get("front").numVisit;
//			}
//		}
//		else if(action == Action.TURN_LEFT) {
//			HashMap<String,Square> squares = tempState.getAroundSquares(agentLoc);
//			if(squares.get("front").numVisit == 0) {
//				value = 10 - squares.get("front").numVisit;
//				
//			}
//		}
		
		int simulationCount = 50;
		Integer[] temp = {Action.GO_FORWARD, Action.TURN_LEFT, Action.TURN_RIGHT, Action.NO_OP, Action.GRAB};
		double tempValue = this.evaluate(tempState, action);
		
//		tempState.updateAgentPosition(action);
//		value += tempValue;
//		if(tempValue == -1 || tempValue == 10) {
//			return tempValue;
//		}
		while(simulationCount > 0) {
			List<Integer> actions = Arrays.asList(temp);
			Collections.shuffle(actions);
			tempState.updateAgentPosition(actions.get(0));
			tempValue = this.evaluate(tempState, action);
			value += tempValue;
			if(tempValue == -1 || tempValue == 10 || tempValue == 2) {
				break;
			}
//			if(tempValue == -1) {
//				value = tempValue;
//				break;
//			}
//			else if(tempValue == 10) {
//				break;
//			}
			simulationCount--;
		}
		
		return value;
	}
	
	private double evaluate(WorldState state, int action) {
		Square agentLoc = state.getAgentLocation();
		if(agentLoc.isWumpus > 0 || agentLoc.isPit > 0) {
			return -1;
		}
		else if(agentLoc.numVisit == 1) {
			return 2;
		}
		else if(agentLoc.hasGlitter && action == Action.GRAB) {
			return 10;
		}
		
		return 0;
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
