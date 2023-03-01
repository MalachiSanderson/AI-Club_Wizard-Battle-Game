package arena.core;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class AStar
{
	final static Node[] calculatePath(Node[][] map, Node startNode, Node endNode)
	{
		List<Node> openList = new ArrayList<>();
		List<Node> closedList = new ArrayList<>();
		
		openList.add(startNode);
		startNode.setParent(null);
		
		while(!openList.isEmpty())
		{
			// Find lowest 'f' in open list
			Node q = getLowestFInList(openList);
			openList.remove(q);
			closedList.add(q);
			
			// We reached the end point, lets trace back the path
			if(q.equals(endNode))
			{
				return tracePath(startNode, endNode);
			}
			
			int x = q.getX();
			int y = q.getY();
			
			// For each neighbor of the current position...
			for(int dy = -1; dy <= 1; dy++)
			{
				for(int dx = -1; dx <= 1; dx++)
				{
					// Skip center (our current position)
					if(dx == 0 && dy == 0)
						continue;
					
					// Skip corner neighbors to prevent diagonal path finding, so that we are limited to up/left/right/down neighbors 
					if(Math.abs(dx) + Math.abs(dy) > 1)
						continue;
					
					// Skip out of bounds neighbors
					if(isOutOfBounds(map, x+dx, y+dy))
						continue;
					
					Node successor = map[x+dx][y+dy];
					
					// Skip if neighbor has been already evaluated
					if(closedList.contains(successor))
						continue;
					
					// Skip if neighbor is not walkable
					if(!successor.isWalkable())
						continue;
					
					// A* calculations
					double newG = q.getG() + distance(successor, q);
					double newH = manhattan(endNode, successor);
					double newF = newG + newH;
					if(newF < successor.getF() || !openList.contains(successor))
					{
						successor.setG(newG);
						successor.setH(newH);
						successor.setF(newF);
						successor.setParent(q);
						
						if(!openList.contains(successor))
						{
							openList.add(successor);
						}
					}
				}
			}
		}
		
		return tracePath(startNode, endNode);
	}
	
	private static final Node[] tracePath(Node startNode, Node endNode)
	{
		List<Node> path = new ArrayList<>();
		Node currentNode = endNode;
		while(!currentNode.equals(startNode))
		{
			path.add(currentNode);
			currentNode = currentNode.getParent();
			
			if(currentNode == null) // No path found.
				return new Node[0];
		}
		Collections.reverse(path);
		return path.toArray(new Node[path.size()]);
	}
	
	private static final boolean isOutOfBounds(Node[][] map, int x, int y)
	{
		return (x < 0 || y < 0 || x >= map.length || y >= map[0].length);
	}
	
	private static final Node getLowestFInList(List<Node> list)
	{
		Node lowestNode = null;
		double lowestF = Double.MAX_VALUE;
		for(Node node : list)
		{
			if(lowestNode == null || node.getF() < lowestF)
			{
				lowestNode = node;
				lowestF = node.getF();
			}
		}
		return lowestNode;
	}
	
	private static final int manhattan(Node node1, Node node2)
	{
		int dx = node1.getX() - node2.getX();
		int dy = node1.getY() - node2.getY(); 
		return Math.abs(dx) + Math.abs(dy);
	}
	
	private static final double distance(Node node1, Node node2)
	{
		int dx = node1.getX() - node2.getX();
		int dy = node1.getY() - node2.getY();
		return Math.sqrt(dx*dx + dy*dy);
	}
}
