package student;

import java.util.Arrays;




import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Set;

import game.GetOutState;
import game.Tile;
import game.FindState;
import game.SewerDiver;
import game.Node;
import game.NodeStatus;
import game.Edge;

public class DiverMax extends SewerDiver {
	HashSet<Long> visited = new HashSet<Long>(); // set of id's of visited nodes
	Stack<Long> breadcrumbs = new Stack<Long>(); // set of id's of nodes where
	// Max has been but not yet returned, used to determine how to walk backwards
	boolean found = false; // true iff the ring has been found

	

    /** Get to the ring in as few steps as possible. Once you get there, 
     * you must return from this function in order to pick
     * it up. If you continue to move after finding the ring rather 
     * than returning, it will not count.
     * If you return from this function while not standing on top of the ring, 
     * it will count as a failure.
     * 
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the ring in fewer steps.
     * 
     * At every step, you know only your current tile's ID and the ID of all 
     * open neighbor tiles, as well as the distance to the ring at each of these tiles
     * (ignoring walls and obstacles). 
     * 
     * In order to get information about the current state, use functions
     * currentLocation(), neighbors(), and distanceToRing() in FindState.
     * You know you are standing on the ring when distanceToRing() is 0.
     * 
     * Use function moveTo(long id) in FindState to move to a neighboring 
     * tile by its ID. Doing this will change state to reflect your new position.
     * 
     * A suggested first implementation that will always find the ring, but likely won't
     * receive a large bonus multiplier, is a depth-first walk. Some
     * modification is necessary to make the search better, in general.*/
    @Override public void findRing(FindState state) {
        //TODO : Find the ring and return.
        // DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
        // Instead, write your method elsewhere, with a good specification,
        // and call it from this one.
    	dfs(state);
    }
    
    /** A recursive, depth-first walk where Max utilizes the set visited and 
     * the stack breadcrumbs to determine where to move next and how to get back
     * to the starting node should he not find the ring, respectively. An 
     * ArrayList of non-visited neighbors is used to sort by distance to the 
     * ring in order to make the algorithm greedy.
     * 
     * @param state
     */
    public void dfs(FindState state) {
    	visited.add(state.currentLocation());
    	breadcrumbs.push(state.currentLocation());
    	 
    	Collection<NodeStatus> nbs = state.neighbors();
    	nbs.removeIf(nb -> visited.contains(nb.getId()));
    	
    	// NO UNVISITED NEIGHBORS, POP THE STACK AND RETURN
    	if(nbs.size() == 0) {
    		breadcrumbs.pop();
    		return;
    	}
    	
    	// MAKING LIST FOR GREEDY
    	List<NodeStatus> alist = new ArrayList<NodeStatus>();
    	for(NodeStatus nb : nbs) {
    		alist.add(nb);
    	}
    	
    	// SORTING LIST FOR GREEDY
    	alist.sort((nb1,nb2) -> nb1.getDistanceToTarget() - nb2.getDistanceToTarget());
    	
    	// MAIN LOOP
    	for (int i = 0; i < alist.size(); i++ ) {
    		NodeStatus nb = alist.get(i);
    		
    		// FOUND RING AS NEIGHBOR
    		if (nb.getDistanceToTarget() == 0) {
    			state.moveTo(nb.getId());
    			visited.add(nb.getId());
    			found = true;
    			return;
    		}
    		
    		// DIDN'T FIND RING AS NEIGHBOR, LOOK DEEPER
    		else {
    			long current_id = state.currentLocation();
    			state.moveTo(nb.getId());
    			dfs(state);
    			if (found) return;
    			walkback(state,current_id);
    			breadcrumbs.push(current_id);
    		}
    	}
    	
    	breadcrumbs.pop();
    	
    }
    
    /** Moves Max back along the path determined by the state of the stack
     * breadcrumbs until he/she arrives at id, where he/she ends his walk
     * after following the breadcrumbs.
     *
     * @param state
     * @param id
     */
    public void walkback(FindState state, long id) {
    	long a = breadcrumbs.pop();
    	while (breadcrumbs.size() > 0 && a != id) {
    		state.moveTo(a);
       		a = breadcrumbs.pop();
    	}
    	state.moveTo(a);
    	
    	
    }
     
    
    

    
    /** Get out of the sewer system before the steps are all used, trying to collect
     * as many coins as possible along the way. Your solution must ALWAYS get out
     * before the steps are all used, and this should be prioritized above
     * collecting coins.
     * 
     * You now have access to the entire underlying graph, which can be accessed
     * through GetOutState. currentNode() and getExit() will return Node objects
     * of interest, and getNodes() will return a collection of all nodes on the graph. 
     * 
     * You have to get out of the sewer system in the number of steps given by
     * getStepsRemaining(); for each move along an edge, this number is decremented
     * by the weight of the edge taken.
     * 
     * Use moveTo(n) to move to a node n that is adjacent to the current node.
     * When n is moved-to, coins on node n are automatically picked up.
     * 
     * You must return from this function while standing at the exit. Failing to
     * do so before steps run out or returning from the wrong node will be
     * considered a failed run.
     * 
     * Initially, there are enough steps to get from the starting point to the
     * exit using the shortest path, although this will not collect many coins.
     * For this reason, a good starting solution is to use the shortest path to
     * the exit. */
    @Override public void getOut(GetOutState state) {
        //TODO: Get out of the sewer system before the steps are used up.
        // DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
        //with a good specification, and call it from this one.
    	

    	getOutTest2(state);
    }
    
    /** A basic method that uses the shortest path algorithm and walks Max along
     * the shortest path from the ring to the end. It does not optimize to get 
     * a large amount of coins, but rather it prioritizes getting out as quickly as
     * possible.
     * 
     * @param state
     */
    public void getOutFast(GetOutState state) {
    	List<Node> shortest_path = Paths.shortestPath(state.currentNode(), state.getExit());
    	shortest_path.remove(0);
    	while (shortest_path.size() > 0) {
    		
    		
    		
    		state.moveTo(shortest_path.get(0));
    		shortest_path.remove(0);
    	}
    }
    
    /** A slightly optimized version of getOutFast. It uses a greedy philosophy
     * to temporarily deviate from the shortest path if a neighbor has coins. It 
     * then recurses and walks Max along the shortest path from the neighbor to the exit.
     * 
     * @param state
     */
    public void getOutMoney(GetOutState state) {
    	List<Node> shortest_path = Paths.shortestPath(state.currentNode(), state.getExit());
    	if (shortest_path.contains(state.currentNode())) shortest_path.remove(0);
    	if (shortest_path.size() == 0) return;
    	
    	Set<Edge> edges = state.currentNode().getExits();
    	List<Node> nbs = new ArrayList<Node>();
    	for (Edge edge : edges) {
    		nbs.add(edge.getOther(state.currentNode()));
    	}
    	
    	nbs.sort((nb1, nb2) -> nb2.getTile().coins() - nb1.getTile().coins());
    	
    	if (nbs.get(0).getTile().coins() > 0) {
    		state.moveTo(nbs.get(0));
    		getOutMoney(state);
    	}
    	
    	else {
    		state.moveTo(shortest_path.get(0));
    		getOutMoney(state);
    	}
    	
    }
    
    /** This recursive method calculates the most coin-laden route from Max's
     * current node to the exit, then moves him/her one step along the calculated path
     * before recursing.
     * 
     * @param state
     */
    public void getOutTest2(GetOutState state) {
    	if (state.currentNode() == state.getExit()) return;
    	Collection<Node> allnodes = state.allNodes();
    	List<Node> list_allnodes = new ArrayList<Node>();
    	
    	for (Node n : allnodes) {
    		list_allnodes.add(n);
    	}
    	
    	list_allnodes.removeIf(n1 -> n1.getTile().coins() == 0);
    	list_allnodes.sort((n1, n2) -> n2.getTile().coins() - n1.getTile().coins());
    	
    	int numsteps = state.stepsLeft();
    	int i = 0;
    	while ( i < list_allnodes.size() &&
    			Paths.pathDistance(Paths.shortestPath(state.currentNode(), list_allnodes.get(i))) +
    			Paths.pathDistance(Paths.shortestPath(list_allnodes.get(i), state.getExit())) + 20 >= numsteps){
    		i = i + 1;
    	}
    	
    	List<Node> newpath;
    	if (i == list_allnodes.size()) {
    		newpath = Paths.shortestPath(state.currentNode(), state.getExit());
    	}
    	else {
    		newpath = Paths.shortestPath(state.currentNode(), list_allnodes.get(i));
    	}
    	newpath.remove(0);
    	
    	if (newpath.size() == 0) {
    		Set<Edge> edges = state.currentNode().getExits();
    		List<Node> nbs = new LinkedList<Node>();
    		
    		for (Edge edge : edges) {
    			nbs.add(edge.getOther(state.currentNode()));
    		}
    		
    		nbs.sort((nb1,nb2) -> nb2.getTile().coins() - nb1.getTile().coins());
    		
    		Node cnode = state.currentNode();
    		state.moveTo(nbs.get(0));
    		state.moveTo(cnode);
    	}
    	
    	while (newpath.size() > 0) {
    		state.moveTo(newpath.get(0));
    		newpath.remove(0);
    	}
    	
    	getOutTest2(state);
    }
    
    /** When called with Max on a given node, visits each neighboring node
     * that has a non-zero coin value and returns Max to the original node.
     * 
     * @param state
     */
    public void getNbs(GetOutState state) {
    	Set<Edge> edges = state.currentNode().getExits();
    	List<Node> nbs = new ArrayList<Node>();
    	for (Edge edge : edges) {
    		nbs.add(edge.getOther(state.currentNode()));
    	}
    	nbs.removeIf(nb1 -> nb1.getTile().coins() == 0);
    	nbs.sort((nb1, nb2) -> nb2.getTile().coins() - nb1.getTile().coins());
    	
    	Node current = state.currentNode();
    	for (Node nb : nbs) {
    		state.moveTo(nb);
    		state.moveTo(current);
    	}
    	
    }

}
