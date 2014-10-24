package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.TimeClock;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Disease_Lycanthropy extends Disease
{
	@Override public String ID() { return "Disease_Lycanthropy"; }
	private final static String localizedName = CMLib.lang().L("Lycanthropy");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Lycanthropy)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}

	@Override protected int DISEASE_TICKS(){return 9999999;}
	@Override protected int DISEASE_DELAY(){return 50;}
	@Override protected String DISEASE_DONE(){return "Your lycanthropy is cured.";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> feel(s) different.^?";}
	@Override protected String DISEASE_AFFECT(){return "";}
	protected boolean DISEASE_STD(){return false;}
	@Override public int difficultyLevel(){return 8;}

	protected boolean changed=false;
	@Override public int spreadBitmap(){return DiseaseAffect.SPREAD_CONSUMPTION|DiseaseAffect.SPREAD_DAMAGE;}
	protected List<Room> deathTrail=null;
	protected Race theRace=null;
	protected Race lycanRace()
	{
		if(!changed) return null;
		if(theRace==null) theRace=CMClass.getRace("WereWolf");
		return theRace;
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(!(affected instanceof MOB)) return;
		if(lycanRace()!=null)
		{
			if(affected.name().indexOf(' ')>0)
				affectableStats.setName(L("a @x1 called @x2",lycanRace().name(),affected.name()));
			else
				affectableStats.setName(L("@x1 the @x2",affected.name(),lycanRace().name()));
			lycanRace().setHeightWeight(affectableStats,'M');
		}
	}
	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(lycanRace()!=null)
		{
			affectableStats.setMyRace(lycanRace());
			if(affected.baseCharStats().getStat(CharStats.STAT_AGE)>0)
				affectableStats.setStat(CharStats.STAT_AGE,lycanRace().getAgingChart()[affected.baseCharStats().ageCategory()]);
		}
	}

	public MOB victimHere(Room room, MOB mob)
	{
		if(room==null) return null;
		if(mob==null) return null;
		for(int i=0;i<room.numInhabitants();i++)
		{
			final MOB M=room.fetchInhabitant(i);
			if((M!=null)
			&&(M!=mob)
			&&(!CMLib.flags().isEvil(M))
			&&(mob.mayIFight(M))
			&&(M.phyStats().level()<(mob.phyStats().level()+5)))
				return M;
		}
		return null;
	}

	protected boolean findVictim(MOB mob, Room room, Vector rooms, int depth)
	{
		if(depth>5)
			return false;
		if(victimHere(room,mob)!=null)
		{
			rooms.addElement(room);
			return true;
		}
		for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
		{
			final Room R=room.getRoomInDir(d);
			final Exit E=room.getExitInDir(d);
			if((R!=null)&&(E!=null)&&(E.isOpen()))
			{
				if(findVictim(mob,R,rooms,depth+1))
				{
					rooms.addElement(R);
					return true;
				}
			}
		}
		return false;
	}

	public void tickLycanthropically(MOB mob)
	{
		if(mob==null) return;
		if(mob.location()==null) return;
		if(mob.isInCombat()) return;

		if((CMLib.dice().rollPercentage()<15)
		&&((mob.location().domainType()&Room.INDOORS)>0))
			mob.location().show(mob,null,CMMsg.MSG_NOISE,L("<S-NAME> howl(s) at the moon! ARROOOOOOOO!!!!"));
		// time to tick lycanthropically
		final MOB M=victimHere(mob.location(),mob);
		if(M!=null)
		{
			deathTrail=null;
			CMLib.combat().postAttack(mob,M,mob.fetchWieldedItem());
			return;
		}
		if((deathTrail!=null)&&(!deathTrail.contains(mob.location())))
		   deathTrail=null;
		if(deathTrail==null)
		{
			final Vector rooms=new Vector();
			if((findVictim(mob,mob.location(),rooms,0))&&(rooms.size()>0))
			{
				TrackingLibrary.TrackingFlags flags;
				flags = new TrackingLibrary.TrackingFlags()
						.plus(TrackingLibrary.TrackingFlag.OPENONLY)
						.plus(TrackingLibrary.TrackingFlag.AREAONLY)
						.plus(TrackingLibrary.TrackingFlag.NOEMPTYGRIDS)
						.plus(TrackingLibrary.TrackingFlag.NOAIR)
						.plus(TrackingLibrary.TrackingFlag.NOWATER);
				deathTrail=CMLib.tracking().findBastardTheBestWay(mob.location(),rooms,flags,50);
				if(deathTrail!=null)
					deathTrail.add(mob.location());
			}
		}
		if(deathTrail!=null)
		{
			final int nextDirection=CMLib.tracking().trackNextDirectionFromHere(deathTrail,mob.location(),true);
			if((nextDirection==999)
			||(nextDirection==-1))
				deathTrail=null;
			else
			if(nextDirection>=0)
			{
				final Room nextRoom=mob.location().getRoomInDir(nextDirection);
				if((nextRoom!=null)
				&&((nextRoom.getArea()==mob.location().getArea()))||(!mob.isMonster()))
				{
					if(!CMLib.tracking().walk(mob,nextDirection,false,false))
						deathTrail=null;
					else
					if(CMLib.dice().rollPercentage()<15)
						mob.location().show(mob,null,CMMsg.MSG_NOISE,L("<S-NAME> sniff(s) at the air."));

				}
				else
					deathTrail=null;
			}
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))	return false;
		if(affected==null) return false;
		if(!(affected instanceof MOB)) return true;

		final MOB mob=(MOB)affected;
		if(mob.amDead()) return true;

		if(!changed)
		{
			if(mob.location()==null) return true;
			final Area A=mob.location().getArea();
			if(((A.getTimeObj().getTODCode()==TimeClock.TimeOfDay.DUSK)||(A.getTimeObj().getTODCode()==TimeClock.TimeOfDay.NIGHT))
			&&(A.getTimeObj().getMoonPhase()==TimeClock.MoonPhase.FULL))
			{
				changed=true;
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> turn(s) into a @x1!",lycanRace().name()));
				mob.recoverCharStats();
				mob.recoverPhyStats();
				mob.recoverMaxState();
				mob.location().recoverRoomStats();
			}
		}
		else
		{
			if(mob.location()==null) return true;
			final Area A=mob.location().getArea();
			if(((A.getTimeObj().getTODCode()!=TimeClock.TimeOfDay.DUSK)&&(A.getTimeObj().getTODCode()!=TimeClock.TimeOfDay.NIGHT))
			||(A.getTimeObj().getMoonPhase()!=TimeClock.MoonPhase.FULL))
			{
				changed=false;
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> revert(s) to normal."));
				mob.recoverCharStats();
				mob.recoverPhyStats();
				mob.recoverMaxState();
				mob.location().recoverRoomStats();
				return true;
			}
			tickLycanthropically(mob);
		}
		return true;
	}
}
