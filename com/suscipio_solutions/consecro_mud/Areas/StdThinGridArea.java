package com.suscipio_solutions.consecro_mud.Areas;
import java.util.Enumeration;
import java.util.Iterator;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.RoomnumberSet;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.WorldMap;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.collections.IteratorEnumeration;
import com.suscipio_solutions.consecro_mud.core.collections.MultiEnumeration;


public class StdThinGridArea extends StdGridArea
{
	@Override public String ID(){	return "StdThinGridArea";}
	@Override public long flags(){return Area.FLAG_THIN;}
	public RoomnumberSet myRoomSet=null;

	@Override
	public void addProperRoom(Room R)
	{
		if(R!=null) R.setExpirationDate(WorldMap.ROOM_EXPIRATION_MILLIS);
		super.addProperRoom(R);
	}
	@Override
	public Room getRoom(String roomID)
	{
		if(!isRoom(roomID)) return null;
		Room R=super.getRoom(roomID);
		if(((R==null)||(R.amDestroyed()))&&(roomID!=null))
		{
			if(roomID.toUpperCase().startsWith(Name().toUpperCase()+"#"))
				roomID=Name()+roomID.substring(Name().length()); // for case sensitive situations
			R=CMLib.database().DBReadRoomObject(roomID,false);
			if(R!=null)
			{
				R.setArea(this);
				addProperRoom(R);
				CMLib.database().DBReadRoomExits(roomID,R,false);
				CMLib.database().DBReadContent(roomID,R,true);
				fillInAreaRoom(R);
				R.setExpirationDate(System.currentTimeMillis()+WorldMap.ROOM_EXPIRATION_MILLIS);
			}
		}
		return R;
	}

	@Override public int getPercentRoomsCached()
	{
		final double totalRooms=getProperRoomnumbers().roomCountAllAreas();
		if(totalRooms==0.0)
			return 100;
		final double currentRooms=getCachedRoomnumbers().roomCountAllAreas();
		return (int)Math.round((currentRooms/totalRooms)*100.0);
	}

	@Override protected int[] buildAreaIStats()
	{
		if(!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
			return emptyStats;
		final int totalRooms=getProperRoomnumbers().roomCountAllAreas();
		final int percent=getPercentRoomsCached();
		if((totalRooms>15)&&(percent<90))
			return emptyStats;
		if((totalRooms>5)&&(percent<50))
			return emptyStats;
		if(percent<10)
			return emptyStats;
		return super.buildAreaIStats();
	}

	public boolean isRoom(String roomID)
	{
		return getProperRoomnumbers().contains(roomID);
	}
	@Override
	public boolean isRoom(Room R)
	{
		if(R==null) return false;
		if(super.isRoom(R)) return true;
		if(R.roomID().length()==0) return false;
		return isRoom(R.roomID());
	}
	@Override public Enumeration<Room> getProperMap(){return new IteratorEnumeration<Room>(properRooms.values().iterator());}
	@Override
	public Enumeration<Room> getMetroMap()
	{
		final int minimum=getProperRoomnumbers().roomCountAllAreas()/10;
		if(getCachedRoomnumbers().roomCountAllAreas()<minimum)
		{
			for(int r=0;r<minimum;r++)
				getRandomProperRoom();
		}
		final MultiEnumeration<Room> multiEnumerator = new MultiEnumeration<Room>(new RoomIDEnumerator(this));
		for(final Iterator<Area> a=getChildrenReverseIterator();a.hasNext();)
			multiEnumerator.addEnumeration(a.next().getMetroMap());
		return new CompleteRoomEnumerator(multiEnumerator);
	}
	@Override public Enumeration<Room> getCompleteMap(){return new CompleteRoomEnumerator(new RoomIDEnumerator(this));}
}
