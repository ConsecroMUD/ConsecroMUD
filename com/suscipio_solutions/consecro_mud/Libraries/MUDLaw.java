package com.suscipio_solutions.consecro_mud.Libraries;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Law;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.LegalLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Deity;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.interfaces.LandTitle;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class MUDLaw extends StdLibrary implements LegalLibrary
{
	@Override public String ID(){return "MUDLaw";}

	@Override
	public Law getTheLaw(Room R, MOB mob)
	{
		final LegalBehavior B=getLegalBehavior(R);
		if(B!=null)
		{
			final Area A2=getLegalObject(R.getArea());
			return B.legalInfo(A2);
		}
		return null;
	}

	@Override
	public LegalBehavior getLegalBehavior(Area A)
	{
		if(A==null) return null;
		final List<Behavior> V=CMLib.flags().flaggedBehaviors(A,Behavior.FLAG_LEGALBEHAVIOR);
		if(V.size()>0) return (LegalBehavior)V.get(0);
		LegalBehavior B=null;
		for(final Enumeration<Area> e=A.getParents();e.hasMoreElements();)
		{
			B=getLegalBehavior(e.nextElement());
			if(B!=null) break;
		}
		return B;
	}
	@Override
	public LegalBehavior getLegalBehavior(Room R)
	{
		if(R==null) return null;
		final List<Behavior> V=CMLib.flags().flaggedBehaviors(R,Behavior.FLAG_LEGALBEHAVIOR);
		if(V.size()>0) return (LegalBehavior)V.get(0);
		return getLegalBehavior(R.getArea());
	}
	@Override
	public Area getLegalObject(Area A)
	{
		if(A==null) return null;
		final List<Behavior> V=CMLib.flags().flaggedBehaviors(A,Behavior.FLAG_LEGALBEHAVIOR);
		if(V.size()>0) return A;
		Area A2=null;
		Area A3=null;
		for(final Enumeration<Area> e=A.getParents();e.hasMoreElements();)
		{
			A2=e.nextElement();
			A3=getLegalObject(A2);
			if(A3!=null) return A3;
		}
		return A3;
	}
	@Override
	public Area getLegalObject(Room R)
	{
		if(R==null) return null;
		return getLegalObject(R.getArea());
	}

	@Override
	public boolean isACity(Area A)
	{
		int other=0;
		int streets=0;
		int buildings=0;
		Room R=null;
		for(final Enumeration<Room> e=A.getCompleteMap();e.hasMoreElements();)
		{
			R=e.nextElement();
			if((R==null)||(R.roomID()==null)||(R.roomID().length()==0)) continue;
			if(R.domainType()==Room.DOMAIN_OUTDOORS_CITY)
				streets++;
			else
			if((R.domainType()==Room.DOMAIN_INDOORS_METAL)
			||(R.domainType()==Room.DOMAIN_INDOORS_STONE)
			||(R.domainType()==Room.DOMAIN_INDOORS_WOOD))
				buildings++;
			else
				other++;
		}
		if((streets<(other/2))||((streets+buildings)<other))
			return false;
		return true;
	}

	@Override
	public List<LandTitle> getAllUniqueTitles(Enumeration<Room> e, String owner, boolean includeRentals)
	{
		final Vector<LandTitle> V=new Vector<LandTitle>();
		final HashSet<Room> roomsDone=new HashSet<Room>();
		Room R=null;
		for(;e.hasMoreElements();)
		{
			R=e.nextElement();
			final LandTitle T=getLandTitle(R);
			if((T!=null)
			&&(!V.contains(T))
			&&(includeRentals||(!T.rentalProperty()))
			&&((owner==null)
				||(owner.length()==0)
				||(owner.equals("*")&&(T.getOwnerName().length()>0))
				||(T.getOwnerName().equals(owner))))
			{
				final List<Room> V2=T.getAllTitledRooms();
				boolean proceed=true;
				for(int v=0;v<V2.size();v++)
				{
					final Room R2=V2.get(v);
					if(!roomsDone.contains(R2))
						roomsDone.add(R2);
					else
						proceed=false;
				}
				if(proceed)
					V.addElement(T);

			}
		}
		return V;
	}

	@Override
	public LandTitle getLandTitle(Area area)
	{
		if(area==null) return null;
		for(final Enumeration<Ability> a=area.effects();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A!=null)&&(A instanceof LandTitle))
				return (LandTitle)A;
		}
		return null;
	}
	@Override
	public LandTitle getLandTitle(Room room)
	{
		if(room==null) return null;
		final LandTitle title=getLandTitle(room.getArea());
		if(title!=null) return title;
		for(final Enumeration<Ability> a=room.effects();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A!=null)&&(A instanceof LandTitle))
				return (LandTitle)A;
		}
		return null;
	}

	@Override
	public boolean isHomeRoomUpstairs(Room room)
	{
		if(isHomePeerRoom(room.getRoomInDir(Directions.DOWN)))
			return true;
		final Set<Room> peerRooms=getHomePeersOnThisFloor(room,new HashSet<Room>());
		for(final Room R : peerRooms)
		{
			if(isHomePeerRoom(R.getRoomInDir(Directions.DOWN)))
				return true;
		}
		return false;
	}

	public boolean isHomePeerRoom(Room R)
	{
		return ifHomePeerLandTitle(R)!=null;
	}

	public LandTitle ifHomePeerLandTitle(Room R)
	{
		if((R!=null)
		&&(R.ID().length()>0)
		&&(CMath.bset(R.domainType(),Room.INDOORS)))
			return CMLib.law().getLandTitle(R);
		return null;
	}

	public LandTitle ifLandTitle(Room R)
	{
		if((R!=null)
		&&(R.ID().length()>0))
			return CMLib.law().getLandTitle(R);
		return null;
	}

	@Override
	public boolean isRoomSimilarlyTitled(LandTitle title, Room R)
	{
		final LandTitle ptitle = ifLandTitle(R);
		if(ptitle ==null) return false;
		if(ptitle.getOwnerName().length()==0)
		{
			for(int d=0;d<Directions.NUM_DIRECTIONS();d++)
			{
				final Room sideRoom=R.getRoomInDir(d);
				final LandTitle psTitle=ifLandTitle(sideRoom);
				if(psTitle.getOwnerName().equals(title.getOwnerName()))
					return true;
			}
			return false;
		}
		else
			return ptitle.getOwnerName().equals(title.getOwnerName());
	}

	@Override
	public Set<Room> getHomePeersOnThisFloor(Room room, Set<Room> doneRooms)
	{
		for(int d=0;d<Directions.NUM_DIRECTIONS();d++)
		{
			if((d!=Directions.UP)&&(d!=Directions.DOWN)&&(d!=Directions.GATE))
			{
				final Room sideRoom=room.getRoomInDir(d);
				if(isHomePeerRoom(sideRoom)  &&(!doneRooms.contains(sideRoom)))
				{
					doneRooms.add(sideRoom);
					doneRooms.addAll(getHomePeersOnThisFloor(sideRoom, doneRooms));
				}
			}
		}
		return doneRooms;
	}

	@Override
	public boolean isHomeRoomDownstairs(Room room)
	{
		if(isHomePeerRoom(room.getRoomInDir(Directions.UP)))
			return true;
		final Set<Room> peerRooms=getHomePeersOnThisFloor(room,new HashSet<Room>());
		for(final Room R : peerRooms)
		{
			if(isHomePeerRoom(R.getRoomInDir(Directions.UP)))
				return true;
		}
		return false;
	}

	@Override
	public boolean doesHavePriviledgesInThisDirection(MOB mob, Room room, Exit exit)
	{
		final int dirCode=CMLib.map().getExitDir(room,exit);
		if(dirCode<0) return false;
		final Room otherRoom=room.getRoomInDir(dirCode);
		if(otherRoom==null) return false;
		return doesHavePriviledgesHere(mob,otherRoom);
	}

	@Override
	public boolean doesHavePriviledgesHere(MOB mob, Room room)
	{
		final LandTitle title=getLandTitle(room);
		if(title==null) return false;
		if(title.getOwnerName()==null) return false;
		if(title.getOwnerName().length()==0) return false;
		if(title.getOwnerName().equals(mob.Name())) return true;
		if((title.getOwnerName().equals(mob.getLiegeID())&&(mob.isMarriedToLiege())))
			return true;
		final Pair<Clan,Integer> clanRole=mob.getClanRole(title.getOwnerName());
		if((clanRole!=null)&&(clanRole.first.getAuthority(clanRole.second.intValue(), Clan.Function.HOME_PRIVS)!=Clan.Authority.CAN_NOT_DO))
			return true;
		if(mob.amFollowing()!=null)
			return doesHavePriviledgesHere(mob.amFollowing(),room);
		return false;
	}

	@Override
	public boolean doesAnyoneHavePrivilegesHere(MOB mob, String overrideID, Room R)
	{
		if((CMLib.law().doesHavePriviledgesHere(mob,R))||((overrideID.length()>0)&&(mob.Name().equals(overrideID))))
			return true;
		if(overrideID.length()>0)
		{
			final Pair<Clan,Integer> clanPair=mob.getClanRole(overrideID);
			if((clanPair!=null)&&(clanPair.first.getAuthority(clanPair.second.intValue(), Clan.Function.HOME_PRIVS)!=Clan.Authority.CAN_NOT_DO))
				return true;
		}
		for(int i=0;i<R.numInhabitants();i++)
		{
			final MOB M=R.fetchInhabitant(i);
			if(CMLib.law().doesHavePriviledgesHere(M,R))
				return true;
			if(overrideID.length()>0)
			{
				if(M.Name().equals(overrideID))
					return true;
				final Pair<Clan,Integer> clanPair=M.getClanRole(overrideID);
				if((clanPair!=null)&&(clanPair.first.getAuthority(clanPair.second.intValue(), Clan.Function.HOME_PRIVS)!=Clan.Authority.CAN_NOT_DO))
					return true;
			}
		}
		return false;
	}

	@Override
	public String getLandOwnerName(Room room)
	{
		final LandTitle title=getLandTitle(room);
		if(title==null) return "";
		if(title.getOwnerName()==null) return "";
		return title.getOwnerName();
	}

	@Override
	public boolean doesOwnThisProperty(String name, Room room)
	{
		final LandTitle title=getLandTitle(room);
		if(title==null) return false;
		if(title.getOwnerName()==null) return false;
		if(title.getOwnerName().length()==0) return false;
		if(title.getOwnerName().equals(name)) return true;
		return false;
	}

	@Override
	public Ability getClericInfusion(Physical room)
	{
		if(room==null) return null;
		for(final Enumeration<Ability> a=room.effects();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A!=null)&&(A.ID().startsWith("Prayer_Infuse")))
				return A;
		}
		return null;
	}
	@Override
	public Deity getClericInfused(Room room)
	{
		final Ability A=getClericInfusion(room);
		if(A==null) return null;
		return CMLib.map().getDeity(A.text());
	}

	@Override
	public boolean doesOwnThisProperty(MOB mob, Room room)
	{
		final LandTitle title=getLandTitle(room);
		if(title==null) return false;
		if(title.getOwnerName()==null) return false;
		if(title.getOwnerName().length()==0) return false;
		if(title.getOwnerName().equals(mob.Name())) return true;
		if((title.getOwnerName().equals(mob.getLiegeID())&&(mob.isMarriedToLiege())))
			return true;
		final Pair<Clan,Integer> clanRole=mob.getClanRole(title.getOwnerName());
		if((clanRole!=null)&&(clanRole.first.getAuthority(clanRole.second.intValue(),Clan.Function.PROPERTY_OWNER)!=Clan.Authority.CAN_NOT_DO))
			return true;
		if(mob.amFollowing()!=null)
			return doesOwnThisProperty(mob.amFollowing(),room);
		return false;
	}

	@Override
	public boolean isLegalOfficerHere(MOB mob)
	{
		if((mob==null)||(mob.location()==null)) return false;
		final Area A=this.getLegalObject(mob.location());
		if(A==null) return false;
		final LegalBehavior B=this.getLegalBehavior(A);
		if(B==null) return false;
		return B.isAnyOfficer(A, mob);
	}
	@Override
	public boolean isLegalJudgeHere(MOB mob)
	{
		if((mob==null)||(mob.location()==null)) return false;
		final Area A=this.getLegalObject(mob.location());
		if(A==null) return false;
		final LegalBehavior B=this.getLegalBehavior(A);
		if(B==null) return false;
		return B.isJudge(A, mob);
	}
	@Override public boolean isLegalOfficialHere(MOB mob){ return isLegalOfficerHere(mob)||isLegalJudgeHere(mob);}
}
