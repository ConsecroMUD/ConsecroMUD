package com.suscipio_solutions.consecro_mud.Locales.interfaces;
import java.util.Iterator;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.GridZones;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.WorldMap;
import com.suscipio_solutions.consecro_mud.core.collections.Converter;



public interface GridLocale extends Room, GridZones
{
	public String getGridChildLocaleID();
	public Room prepareGridLocale(Room fromRoom, Room toRoom, int direction);
	public void buildGrid();
	public void clearGrid(Room bringBackHere);
	public List<Room> getAllRooms();
	public Iterator<Room> getExistingRooms();
	public Iterator<WorldMap.CrossExit> outerExits();
	public void addOuterExit(WorldMap.CrossExit x);
	public void delOuterExit(WorldMap.CrossExit x);
	public static class ThinGridEntry
	{
		public Room room;
		public XYVector xy;
		public ThinGridEntry(Room R, int x, int y)
		{ room=R; xy=new XYVector(x,y);}
	}
	public static class ThinGridEntryConverter implements Converter<ThinGridEntry,Room>
	{
		public static ThinGridEntryConverter INSTANCE = new ThinGridEntryConverter();
		@Override public Room convert(ThinGridEntry obj) { return obj.room;}
	}
}
