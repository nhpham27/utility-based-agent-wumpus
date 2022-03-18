/*
 * Class that defines the agent function.
 * 
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
 * 
 * Last modified 2/19/07 
 * 
 * DISCLAIMER:
 * Elements of this application were borrowed from
 * the client-server implementation of the Wumpus
 * World Simulator written by Kruti Mehta at
 * The University of Texas at Arlington.
 * 
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class AgentFunction {
	
	// string to store the agent's name
	// do not remove this variable
	private String agentName = "green-frog";
	
	// all of these variables are created and used
	// for illustration purposes; you may delete them
	// when implementing your own intelligent agent
	private int[] actionTable;
	private boolean bump;
	private boolean glitter;
	private boolean breeze;
	private boolean stench;
	private boolean scream;
	private Random rand;
	private WorldState state;
	// 
	private int lastAction = -1;
	private int step;
	static boolean debugMode = false;
	static int trial = 5000;
	public AgentFunction()
	{
		// for illustration purposes; you may delete all code
		// inside this constructor when implementing your 
		// own intelligent agent

		// this integer array will store the agent actions
//		actionTable = new int[6];
//				  
//		actionTable[0] = Action.NO_OP;
//		actionTable[1] = Action.GO_FORWARD;
//		actionTable[2] = Action.TURN_RIGHT;
//		actionTable[3] = Action.TURN_LEFT;
//		actionTable[4] = Action.GRAB;
//		actionTable[5] = Action.SHOOT;
//		
		// new random number generator, for
		// randomly picking actions to execute
		rand = new Random();
		
		state = new WorldState();
		if(debugMode == true)
			state.printState();
		this.step = 51;
	}
	
	public int process(TransferPercept tp)
	{
		// To build your own intelligent agent, replace
		// all code below this comment block. You have
		// access to all percepts through the object
		// 'tp' as illustrated here:
		
		// read in the current percepts
		bump = tp.getBump();
		glitter = tp.getGlitter();
		breeze = tp.getBreeze();
		stench = tp.getStench();
		scream = tp.getScream();
		
		// update the state based on current percept
		// and the most recent action
		boolean[] percepts = {bump, glitter, breeze, stench, scream};
		this.state.updateState(lastAction, percepts);
		if(debugMode == true)
			this.state.printState();
		// return action to be performed
		//this.lastAction = actionTable[rand.nextInt(4)];
		MCTS mcts = new MCTS(this.state, 100, this.step);
		this.step--;
		mcts.buildSearchTree();
		
		lastAction = mcts.getBestAction();
		return lastAction;
	}
	
	// public method to return the agent's name
	// do not remove this method
	public String getAgentName() {
		return agentName;
	}
	
	
}