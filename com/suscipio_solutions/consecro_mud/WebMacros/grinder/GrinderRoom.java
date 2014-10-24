package com.suscipio_solutions.consecro_mud.WebMacros.grinder;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;



public class GrinderRoom
{
	public int z=0;
	public int[] xy=null;
	public String roomID="";
	private Room roomCache=null;
	public Room room()
	{
		if((roomID.length()>0)
		&&((roomCache==null)||(roomCache.amDestroyed())))
		{
			roomCache=CMLib.map().getRoom(roomID);
			if(roomCache!=null)
				fixExits(roomCache);
		}
		return roomCache;
	}
	public boolean isRoomGood(){return ((roomCache!=null)&&(!roomCache.amDestroyed()));}
	public GrinderDir[] doors=new GrinderDir[Directions.NUM_DIRECTIONS()];
	public GrinderRoom(String newRoomID)
	{
		roomCache=null;
		roomID=newRoomID;
	}

	public void fixExits(Room R)
	{
		for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
		{
			final GrinderDir D=new GrinderDir();
			final Room R2=R.rawDoors()[d];
			if(R2!=null)
			{
				D.room=R2.roomID();
				final Exit E2=R.getRawExit(d);
				if(E2!=null)
					D.exit=E2;
			}
			doors[d]=D;
		}
	}
	public GrinderRoom(Room R)
	{
		roomCache=null;
		if(!R.amDestroyed())
		{
			roomCache=R;
			fixExits(R);
		}
		roomID=R.roomID();
	}
}
