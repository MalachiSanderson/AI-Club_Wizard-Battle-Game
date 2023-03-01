package arena.core;

public class OutOfBoundsException extends RuntimeException
{
	private static final long serialVersionUID = 1135804682254376126L;

	public OutOfBoundsException(String message)
	{
		super(message);
	}
	
	public OutOfBoundsException(int x, int y)
	{
		this("The position (" + x + ", " + y + ") is out of bounds.");
	}
}
