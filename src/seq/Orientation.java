package seq;

public enum Orientation
	{
	FORWARD, REVERSE, NOTORIENTED;

	public static Orientation reverse(Orientation orientation)
		{
		if(orientation==FORWARD)
			return REVERSE;
		else if(orientation==REVERSE)
			return FORWARD;
		else if(orientation==NOTORIENTED)
			return NOTORIENTED;
		else
			return null;
		}

	}
