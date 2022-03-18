
public class Node{
	int numVisit; // number of visits of this node
	int action; // action that lead from parent node to this node
	double value;
	WorldState state;
	Node parent;
	Node[] children;
	boolean isTerminal;
	int depth;
	Node(WorldState state){
		this.numVisit = 0;
		this.value = 0;
		this.state = state.copyWorldState();
		this.parent = null;
		this.children = null;
		this.isTerminal = false;
		this.depth = 0;
	}
}
