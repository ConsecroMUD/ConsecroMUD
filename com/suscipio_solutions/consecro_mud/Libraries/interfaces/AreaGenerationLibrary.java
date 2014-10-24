package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.XMLLibrary.XMLpiece;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.exceptions.CMException;
import com.suscipio_solutions.consecro_mud.core.interfaces.Modifiable;

public interface AreaGenerationLibrary extends CMLibrary
{
	public void buildDefinedIDSet(List<XMLpiece> xmlRoot, Map<String,Object> defined);
	public List<Item> findItems(XMLLibrary.XMLpiece piece, Map<String,Object> defined) throws CMException;
	public List<MOB> findMobs(XMLLibrary.XMLpiece piece, Map<String,Object> defined) throws CMException;
	public String findString(String tagName, XMLLibrary.XMLpiece piece, Map<String,Object> defined) throws CMException;
	public Room buildRoom(XMLLibrary.XMLpiece piece, Map<String,Object> defined, Exit[] exits, int direction) throws CMException;
	public void checkRequirements(XMLLibrary.XMLpiece piece, Map<String,Object> defined) throws CMException;
	public Map<String,String> getUnfilledRequirements(Map<String,Object> defined, XMLLibrary.XMLpiece piece);
	public Area findArea(XMLLibrary.XMLpiece piece, Map<String,Object> defined, int directions) throws CMException;
	public boolean fillInArea(XMLLibrary.XMLpiece piece, Map<String,Object> defined, Area A, int direction) throws CMException;
	public LayoutManager getLayoutManager(String named);
	public void postProcess(final Map<String,Object> defined) throws CMException;
	public void defineReward(Modifiable E, List<String> ignoreStats, String defPrefix, XMLLibrary.XMLpiece piece, String value, Map<String,Object> defined) throws CMException;
	public void preDefineReward(Modifiable E, List<String> ignoreStats, String defPrefix, XMLLibrary.XMLpiece piece, Map<String,Object> defined) throws CMException;
	public List<XMLpiece> getAllChoices(String tagName, XMLLibrary.XMLpiece piece, Map<String,Object> defined) throws CMException;

	public static interface LayoutManager
	{
		public String name();
		public List<LayoutNode> generate(int num, int dir);
	}

	public static interface LayoutNode
	{
		public void crossLink(LayoutNode to);
		public void delLink(LayoutNode linkNode);
		public LayoutNode getLink(int d);
		public Hashtable<Integer,LayoutNode> links();
		public Hashtable<LayoutTags,String> tags();
		public long[] coord();
		public boolean isStreetLike();
		public void deLink();
		public void flag(LayoutFlags flag);
		public void flagRun(LayoutRuns dirs);
		public boolean isFlagged(LayoutFlags flag);
		public LayoutRuns getFlagRuns();
		public LayoutTypes type();
		public void setExits(int[] dirs);
		public void reType(LayoutTypes type);
		public String getColorRepresentation(int line);
		public Room room();
		public void setRoom(Room room);
	}

	public enum LayoutTags { NODERUN, NODEFLAGS, NODETYPE, NODEEXITS}
	public enum LayoutTypes { surround, leaf, street, square, interior }
	public enum LayoutFlags { corner, gate, intersection, tee, offleaf }
	public enum LayoutRuns { ew,ns,ud,nesw,nwse }
}
