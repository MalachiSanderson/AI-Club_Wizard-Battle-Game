package arena.core;

/**
 * The <code>Vector2</code> class represents a a two-dimensional coordinate of integers, such as <code>(x, y)</code>.
 * This is used in conjuction with {@link GameUtility}.
 * 
 * @author ERAU AI Club
 */
public final class Vector2
{
	private int x;
	private int y;
	
	/**
	 * Creates a <code>(0, 0)</code> vector.
	 */
	public Vector2()
	{
		this(0, 0);
	}
	
	/**
	 * Creates a vector from the specified x and y values.
	 * 
	 * @param x - the x-position
	 * @param y - the y-position
	 */
	public Vector2(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Returns the x-position of the vector.
	 * 
	 * @return the x-position
	 */
	public final int getX()
	{
		return x;
	}

	/**
	 * Sets the x-position to the specified value.
	 * 
	 * @param x - the new x-position
	 */
	public final void setX(int x)
	{
		this.x = x;
	}

	/**
	 * Returns the y-position of the vector.
	 * 
	 * @return the y-position
	 */
	public final int getY()
	{
		return y;
	}

	/**
	 * Sets the y-position to the specified value.
	 * 
	 * @param y - the new y-position
	 */
	public final void setY(int y)
	{
		this.y = y;
	}

	@Override
	public final String toString()
	{
		return "Position [x=" + x + ", y=" + y + "]";
	}
}