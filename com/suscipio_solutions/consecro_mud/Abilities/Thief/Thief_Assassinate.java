package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_Assassinate extends ThiefSkill
{
	@Override public String ID() { return "Thief_Assassinate"; }
	private final static String localizedName = CMLib.lang().L("Assassinate");
	@Override public String name() { return localizedName; }
	protected String displayText=L("(Tracking)");
	@Override public String displayText(){ return displayText;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_OTHERS;}
	private static final String[] triggerStrings =I(new String[] {"ASSASSINATE"});
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public long flags(){return Ability.FLAG_TRACKING;}
	@Override public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_DIRTYFIGHTING; }
	protected List<Room> theTrail=null;
	public int nextDirection=-2;
	protected MOB tracking=null;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if(tickID==Tickable.TICKID_MOB)
		{
			if(nextDirection==-999)
				return true;

			if((theTrail==null)
			||(affected == null)
			||(!(affected instanceof MOB)))
				return false;

			final MOB mob=(MOB)affected;
			if((mob.isInCombat())
			&&(mob.isMonster())
			&&(!CMLib.flags().isMobile(mob)))
				return true;

			final Room room=mob.location();
			if(room==null) return false;
			if(room.isInhabitant(tracking))
			{
				if(CMLib.flags().isHidden(mob))
				{
					final Ability A=mob.fetchAbility("Thief_BackStab");
					if(A!=null)
					{
						A.setAbilityCode(5);
						A.invoke(mob,tracking,false,0);
						A.setAbilityCode(0);
					}
				}
				else
					CMLib.combat().postAttack(mob,tracking,mob.fetchWieldedItem());
				if((!mob.isMonster())||(CMLib.flags().isMobile(mob)))
					return false;
				return true;
			}

			for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
			{
				final Room nextRoom=room.getRoomInDir(d);
				final Exit nextExit=room.getExitInDir(d);
				if((nextRoom!=null)
				   &&(nextExit!=null)
				   &&(nextExit.isOpen())
				   &&(nextRoom.isInhabitant(tracking)))
				{
					nextDirection=d; break;
				}
			}

			if(nextDirection==999)
			{
				mob.tell(L("The trail seems to pause here."));
				nextDirection=-2;
				unInvoke();
			}
			else
			if(nextDirection==-1)
			{
				mob.tell(L("The trail dries up here."));
				nextDirection=-999;
				unInvoke();
			}
			else
			if(nextDirection>=0)
			{
				mob.tell(L("The trail seems to continue @x1.",Directions.getDirectionName(nextDirection)));
				if(mob.isMonster())
				{
					final Room nextRoom=room.getRoomInDir(nextDirection);
					if((nextRoom!=null)&&(nextRoom.getArea()==room.getArea()))
					{
						if(!nextRoom.isInhabitant(tracking))
						{
							final Ability A=mob.fetchAbility("Thief_Sneak");
							if(A!=null)
							{
								final int dir=nextDirection;
								nextDirection=-2;
								final Vector V=new Vector();
								V.addElement(Directions.getDirectionName(dir));
								A.invoke(mob,V,null,false,0);
							}
							else
							{
								final int dir=nextDirection;
								nextDirection=-2;
								CMLib.tracking().walk(mob,dir,false,false);
							}
						}
						else
						{
							final int dir=nextDirection;
							nextDirection=-2;
							CMLib.tracking().walk(mob,dir,false,false);
						}
					}
					else
					{
						unInvoke();
					}
				}
				else
					nextDirection=-2;
			}

		}
		return true;
	}

	@Override
	public void affectPhyStats(Physical affectedEnv, PhyStats affectableStats)
	{
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_WORK);
		super.affectPhyStats(affectedEnv, affectableStats);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);

		if(!(affected instanceof MOB))
			return;

		final MOB mob=(MOB)affected;
		if((msg.amISource(mob))
		&&(msg.amITarget(mob.location()))
		&&(CMLib.flags().canBeSeenBy(mob.location(),mob))
		&&(msg.targetMinor()==CMMsg.TYP_LOOK))
			nextDirection=CMLib.tracking().trackNextDirectionFromHere(theTrail,mob.location(),true);
	}

	@Override
	public void unInvoke()
	{
		final MOB mob=(affected instanceof MOB)?(MOB)affected:null;
		super.unInvoke();
		if((mob!=null)
		&&(!mob.amDead())
		&&(mob.isMonster())
		&&(!CMLib.flags().isMobile(mob))
		&&(mob.getStartRoom()!=null)
		&&(mob.location()!=mob.getStartRoom()))
			CMLib.tracking().wanderAway(mob,false,true);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!CMLib.flags().aliveAwakeMobileUnbound(mob,false))
			return false;

		if((!auto)&&(!CMLib.flags().canBeSeenBy(mob.location(),mob)))
		{
			mob.tell(L("You can't see anything to track!"));
			return false;
		}

		final List<Ability> V=CMLib.flags().flaggedAffects(mob,Ability.FLAG_TRACKING);
		for(final Ability A : V) A.unInvoke();
		if(V.size()>0)
		{
			mob.tell(L("You stop tracking."));
			if(commands.size()==0) return true;
		}

		theTrail=null;
		nextDirection=-2;

		tracking=null;
		String mobName="";
		if((!mob.isMonster())&&(mob.fetchEffect("Thief_Mark")!=null))
		{
			final Thief_Mark A=(Thief_Mark)mob.fetchEffect("Thief_Mark");
			if(A!=null) tracking=A.mark;
			if(tracking==null)
			{
				mob.tell(L("You'll need to Mark someone first."));
				return false;
			}
		}
		else
		{
			if(givenTarget!=null)
				mobName=givenTarget.name();
			else
				mobName=CMParms.combine(commands,0);
			if(givenTarget instanceof MOB)
				tracking=(MOB)givenTarget;
			if(mobName.length()==0)
			{
				mob.tell(L("Assassinate whom?"));
				return false;
			}
			final MOB M=((givenTarget instanceof MOB)&&(((MOB)givenTarget).location()==mob.location()))?
					(MOB)givenTarget:
					mob.location().fetchInhabitant(mobName);
			if(M!=null)
			{
				CMLib.combat().postAttack(mob,M,mob.fetchWieldedItem());
				return false;
			}
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		final boolean success=proficiencyCheck(mob,0,auto);

		final Vector rooms=new Vector();
		if(tracking!=null)
		{
			final Room R=tracking.location();
			if((R!=null)&&(R.isInhabitant(tracking))&&(CMLib.flags().canAccess(mob,R)))
				rooms.addElement(R);
		}
		else
		if(mobName.length()>0)
		{
			try
			{
				final TrackingLibrary.TrackingFlags flags=new TrackingLibrary.TrackingFlags();
				if(givenTarget!=null&&auto&&mob.isMonster())
					flags.plus(TrackingLibrary.TrackingFlag.AREAONLY);
				flags.plus(TrackingLibrary.TrackingFlag.OPENONLY)
					 .plus(TrackingLibrary.TrackingFlag.NOEMPTYGRIDS)
					 .plus(TrackingLibrary.TrackingFlag.NOAIR)
					 .plus(TrackingLibrary.TrackingFlag.NOWATER);
				final List<Room> checkSet=CMLib.tracking().getRadiantRooms(mob.location(),flags,50+(2*getXLEVELLevel(mob)));
				for (final Room room : checkSet)
				{
					final Room R=CMLib.map().getRoom(room);
					if(R.fetchInhabitant(mobName)!=null)
						rooms.addElement(R);
				}
			}catch(final NoSuchElementException nse){}
		}

		final TrackingLibrary.TrackingFlags flags=new TrackingLibrary.TrackingFlags();
		flags.plus(TrackingLibrary.TrackingFlag.OPENONLY)
			 .plus(TrackingLibrary.TrackingFlag.NOEMPTYGRIDS)
			 .plus(TrackingLibrary.TrackingFlag.NOAIR)
			 .plus(TrackingLibrary.TrackingFlag.NOWATER);
		if(givenTarget!=null&&auto&&mob.isMonster())
			flags.plus(TrackingLibrary.TrackingFlag.AREAONLY);
		if(rooms.size()>0)
			theTrail=CMLib.tracking().findBastardTheBestWay(mob.location(),rooms,flags,50+(2*getXLEVELLevel(mob)));

		if((tracking==null)&&(theTrail!=null)&&(theTrail.size()>0))
			tracking=theTrail.get(0).fetchInhabitant(mobName);

		if((success)&&(theTrail!=null)&&(tracking!=null))
		{
			theTrail.add(mob.location());

			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,tracking,this,CMMsg.MSG_THIEF_ACT,mob.isMonster()?null:L("<S-NAME> begin(s) to track <T-NAMESELF> for assassination."),CMMsg.NO_EFFECT,null,CMMsg.NO_EFFECT,null);
			if((mob.location().okMessage(mob,msg))
			&&(tracking.okMessage(tracking,msg)))
			{
				mob.location().send(mob,msg);
				tracking.executeMsg(tracking,msg);
				invoker=mob;
				displayText=L("(tracking @x1)",tracking.name());
				final Thief_Assassinate newOne=(Thief_Assassinate)this.copyOf();
				if(mob.fetchEffect(newOne.ID())==null)
					mob.addEffect(newOne);
				mob.recoverPhyStats();
				newOne.nextDirection=CMLib.tracking().trackNextDirectionFromHere(theTrail,mob.location(),true);
			}
		}
		else
			return beneficialVisualFizzle(mob,tracking,L("<S-NAME> attempt(s) to track <T-NAMESELF> for assassination, but fail(s)."));


		// return whether it worked
		return success;
	}
}
