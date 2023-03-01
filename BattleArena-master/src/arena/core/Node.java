package arena.core;

final class Node
{
	private boolean walkable;
	private int x, y;
	private Node parent;
	private double f, g, h;
	
	Node(int x, int y, boolean walkable)
	{
		f = 0;
		g = 0;
		h = 0;
		parent = null;
		this.x = x;
		this.y = y;
		this.walkable = walkable;
	}
	
	final boolean isWalkable()
	{
		return walkable;
	}

	final void setWalkable(boolean walkable)
	{
		this.walkable = walkable;
	}

	final Node getParent()
	{
		return parent;
	}

	final void setParent(Node parent)
	{
		this.parent = parent;
	}

	final double getF()
	{
		return f;
	}
	
	final void updateF()
	{
		f = g + h;
	}
	
	final void setF(double f)
	{
		this.f = f;
	}
	
	final double getG()
	{
		return g;
	}
	
	final void setG(double g)
	{
		this.g = g;
	}
	
	final double getH()
	{
		return h;
	}
	
	final void setH(double h)
	{
		this.h = h;
	}
	
	final int getX()
	{
		return x;
	}

	final void setX(int x)
	{
		this.x = x;
	}

	final int getY()
	{
		return y;
	}

	final void setY(int y)
	{
		this.y = y;
	}

	@Override
	public String toString()
	{
		return "Node [walkable=" + walkable + ", x=" + x + ", y=" + y + ", f=" + f + "]";
	}
}