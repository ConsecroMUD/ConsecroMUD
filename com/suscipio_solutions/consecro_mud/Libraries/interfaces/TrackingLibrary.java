package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;

public interface TrackingLibrary extends CMLibrary
{
	public List<Room> findBastardTheBestWay(Room location, Room destRoom, TrackingFlags flags, int maxRadius);
	public List<Room> findBastardTheBestWay(Room location, List<Room> destRooms, TrackingFlags flags, int maxRadius);
	public int trackNextDirectionFromHere(List<Room> theTrail, Room location, boolean openOnly);
	public void stopTracking(MOB mob);
	public boolean makeFall(Physical P, Room room, int avg);
	public int radiatesFromDir(Room room, List<Room> rooms);
	public void getRadiantRooms(Room room, List<Room> rooms, TrackingFlags flags, Room radiateTo, int maxDepth, Set<Room> ignoreRooms);
	public List<Room> getRadiantRooms(final Room room, final RFilters filters, final int maxDepth);
	public void getRadiantRooms(final Room room, List<Room> rooms, final RFilters filters, final Room radiateTo, final int maxDepth, final Set<Room> ignoreRooms);
	public List<Room> getRadiantRooms(Room room, TrackingFlags flags, int maxDepth);
	public boolean beMobile(MOB mob, boolean dooropen, boolean wander, boolean roomprefer, boolean roomobject, int[] status, List<Room> rooms);
	public void wanderAway(MOB M, boolean mindPCs, boolean andGoHome);
	public void wanderFromTo(MOB M, Room toHere, boolean mindPCs);
	public void wanderIn(MOB M, Room toHere);
	public boolean walk(MOB mob, int directionCode, boolean flee, boolean nolook);
	public boolean walk(MOB mob, int directionCode, boolean flee, boolean nolook, boolean noriders);
	public boolean run(MOB mob, int directionCode, boolean flee, boolean nolook, boolean noriders);
	public boolean walk(MOB mob, int directionCode, boolean flee, boolean nolook, boolean noriders, boolean always);
	public boolean run(MOB mob, int directionCode, boolean flee, boolean nolook, boolean noriders, boolean always);
	public boolean walk(Item I, int directionCode);
	public int findExitDir(MOB mob, Room R, String desc);
	public int findRoomDir(MOB mob, Room R);
	public boolean isAnAdminHere(Room R, boolean sysMsgsOnly);
	public void markToWanderHomeLater(MOB M);
	public List<Integer> getShortestTrail(final List<List<Integer>> finalSets);
	public List<List<Integer>> findAllTrails(Room from, Room to, List<Room> radiantTrail);
	public List<List<Integer>> findAllTrails(Room from, List<Room> tos, List<Room> radiantTrail);
	public String getTrailToDescription(Room R1, List<Room> set, String where, boolean areaNames, boolean confirm, int radius, Set<Room> ignoreRooms, int maxMins);
	public Rideable findALadder(MOB mob, Room room);
	public void postMountLadder(MOB mob, Rideable ladder);

	public static abstract class RFilter
	{
		public abstract boolean isFilteredOut(final Room R, final Exit E, final int dir);
	}

	public static class RFilterNode
	{
		private RFilterNode next=null;
		private final RFilter filter;
		public RFilterNode(RFilter fil){ this.filter=fil;}

	}
	public static class RFilters
	{
		private RFilterNode head=null;
		public boolean isFilteredOut(final Room R, final Exit E, final int dir)
		{
			RFilterNode me=head;
			while(me!=null)
			{
				if(me.filter.isFilteredOut(R,E,dir))
					return true;
				me=me.next;
			}
			return false;
		}
		public RFilters plus(RFilter filter)
		{
			RFilterNode me=head;
			if(me==null)
				head=new RFilterNode(filter);
			else
			{
				while(me.next!=null)
					me=me.next;
				me.next=new RFilterNode(filter);
			}
			return this;
		}
	}

	public static enum TrackingFlag
	{
		NOHOMES(new RFilter(){ 
			@Override
			public boolean isFilteredOut(final Room R, final Exit E, final int dir)
			{
				return CMLib.law().getLandTitle(R)!=null;
			}
		}),
		OPENONLY(new RFilter(){ 
			@Override
			public boolean isFilteredOut(final Room R, final Exit E, final int dir)
			{
				return !E.isOpen();
			}
		}),
		UNLOCKEDONLY(new RFilter(){ 
			@Override
			public boolean isFilteredOut(final Room R, final Exit E, final int dir)
			{
				return !E.hasALock();
			}
		}),
		AREAONLY(new RFilter(){ 
			@Override
			public boolean isFilteredOut(final Room R, final Exit E, final int dir)
			{
				return CMLib.law().getLandTitle(R)!=null;
			}
		}),
		NOEMPTYGRIDS(new RFilter(){ 
			@Override
			public boolean isFilteredOut(final Room R, final Exit E, final int dir)
			{
				return (R.getGridParent()!=null)&&(R.getGridParent().roomID().length()==0);
			}
		}),
		NOAIR(new RFilter(){ 
			@Override
			public boolean isFilteredOut(final Room R, final Exit E, final int dir)
			{
				return (R.domainType()==Room.DOMAIN_INDOORS_AIR) ||(R.domainType()==Room.DOMAIN_OUTDOORS_AIR);
			}
		}),
		NOWATER(new RFilter(){
			@Override
			public boolean isFilteredOut(final Room R, final Exit E, final int dir)
			{
				return (R.domainType()==Room.DOMAIN_INDOORS_WATERSURFACE)
					   ||(R.domainType()==Room.DOMAIN_INDOORS_UNDERWATER)
					   ||(R.domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER)
					   ||(R.domainType()==Room.DOMAIN_OUTDOORS_WATERSURFACE);
			}
		}),
		OUTDOORONLY(new RFilter(){ 
			@Override
			public boolean isFilteredOut(final Room R, final Exit E, final int dir)
			{
				return (R.domainType()&Room.INDOORS) != 0;
			}
		});
		public RFilter myFilter;
		private TrackingFlag(RFilter filter)
		{
			this.myFilter=filter;
		}
	}

	public static class TrackingFlags extends HashSet<TrackingFlag>
	{
		private static final long serialVersionUID = -6914706649617909073L;
		private int hashCode=(int)serialVersionUID;
		public TrackingFlags plus(TrackingFlag flag)
		{
			add(flag);
			hashCode^=flag.hashCode();
			return this;
		}
		@Override
		public int hashCode()
		{
			return hashCode;
		}
	}
}
