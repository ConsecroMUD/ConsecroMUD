package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Thief_TurfWar extends ThiefSkill
{
	@Override public String ID() { return "Thief_TurfWar"; }
	private final static String localizedName = CMLib.lang().L("Turf War");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Turf War)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"TURFWAR"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STREETSMARTS;}
	public static Ability sparringRoomA=null;
	protected MOB defender=null;
	protected boolean defenderPKILLON=false;


	protected long timeToNextCast = 0;
	@Override protected int getTicksBetweenCasts() { return CMProps.getIntVar(CMProps.Int.TICKSPERMUDMONTH);}
	@Override protected long getTimeOfNextCast(){ return timeToNextCast; }
	@Override protected void setTimeOfNextCast(long absoluteTime) { timeToNextCast=absoluteTime;}

	public static synchronized Ability getSparringRoom()
	{
		if(sparringRoomA==null)
		{
			sparringRoomA=CMClass.getAbility("Prop_SparringRoom");
			if(sparringRoomA==null)
			{
				Log.errOut("Thief_TurfWar","Unable to load ability: Prop_SparringRoom");
				return null;
			}
		}
		return sparringRoomA;
	}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if(!super.okMessage(host,msg))
			return false;
		final Ability A=getSparringRoom();
		if(A==null) return true;
		if(!A.okMessage(host, msg))
			return false;
		return true;
	}

	public boolean isADefender(Room R, MOB M)
	{
		if(R==null) return false;
		final Ability A=R.fetchEffect("Thief_TagTurf");
		if(A==null) return false;
		final Pair<Clan,Integer> clanRole=M.getClanRole(A.text());
		return (A.text().equals(M.Name())
			||((clanRole!=null)&&(clanRole.second.intValue()>=clanRole.first.getGovernment().getAcceptPos())));
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		final Ability A=getSparringRoom();
		if(A!=null) A.executeMsg(host, msg);
		super.executeMsg(host,msg);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking, tickID))
			return false;
		if(!(affected instanceof Room))
			return false;
		final Room R=(Room)affected;
		if(R==null) return false;
		final MOB attacker=invoker();
		if(attacker==null)
			return false;
		if(attacker.location()!=R)
		{
			// failure in offensive
			unInvoke();
			return false;
		}
		if(defender==null)
		{
			for(int m=0;m<R.numInhabitants();m++)
			{
				final MOB M=R.fetchInhabitant(m);
				if((M!=null)&&(!M.isMonster())&&(M!=attacker)&&(isADefender(R,M)))
				{
					defender=M;
					R.showHappens(CMMsg.MSG_OK_ACTION,L("@x1 arrives to defend this turf! Let the war begin!",M.name()));
					defenderPKILLON=defender.isAttribute(MOB.Attrib.PLAYERKILL);
					defender.setAttribute(MOB.Attrib.PLAYERKILL,true);
					attacker.setVictim(defender);
					defender.setVictim(attacker);
					// this is a safe fight, so nothing matters but the blows.
				}
			}
		}
		else
		if(defender!=null)
		{
			if(defender.location()!=R)
			{
				unInvoke();
				return false;
				// failure in defense!
			}
			if(attacker.isInCombat() && defender.isInCombat() && (getTickDownRemaining() < 10))
				setTickDownRemaining(10);
		}
		return true;
	}

	@Override
	public void unInvoke()
	{
		final MOB attacker=invoker();
		if(attacker!=null)
			attacker.makePeace();
		if(defender!=null)
		{
			defender.makePeace();
			defender.setAttribute(MOB.Attrib.PLAYERKILL,this.defenderPKILLON);
		}

		if(affected instanceof Room)
		{
			final Room R=(Room)affected;
			if((attacker!=null)&&(attacker.location()==R)
			&&((defender==null)||(defender.location()!=R)))
			{
				R.showHappens(CMMsg.MSG_OK_ACTION,L("@x1 has won the turf war!",attacker.Name()));
				final Ability A=R.fetchEffect("Thief_TagTurf");
				if(A!=null) A.unInvoke();
			}
			else
			if((attacker!=null)&&(attacker.location()!=R)
			&&((defender!=null)&&defender.location()==R))
				R.showHappens(CMMsg.MSG_OK_ACTION,L("@x1 has won the turf war!",defender.Name()));
		}
		super.unInvoke();
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		Room target=mob.location();
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof Room))
			target=(Room)givenTarget;
		Ability A=target.fetchEffect(ID());
		if(A!=null)
		{
			mob.tell(L("A turf war is already underway here."));
			return false;
		}

		if(!mob.isAttribute(MOB.Attrib.PLAYERKILL))
		{
			mob.tell(L("You must turn on your playerkill flag first."));
			return false;
		}

		A=target.fetchEffect("Thief_TagTurf");
		Clan turfC=null;
		MOB turfM=null;
		if(A!=null)
		{
			final Pair<Clan,Integer> clanRole=mob.getClanRole(A.text());
			if(A.text().equals(mob.Name())
				||((clanRole!=null)&&(clanRole.second.intValue()>=clanRole.first.getGovernment().getAcceptPos())))
			{
				mob.tell(L("You can't declare war on your own turf!"));
				return true;
			}
			turfC=CMLib.clans().getClan(A.text());
			if(turfC==null)
				turfM=CMLib.players().getLoadPlayer(A.text());
			if(turfM==null)
			{
				A.unInvoke();
				mob.tell(L("This turf is untagged."));
				return true;
			}
		}
		else
		{
			mob.tell(L("This turf is not tagged by anyone."));
			return false;
		}

		final Room R=target;
		final boolean success=proficiencyCheck(mob,0,auto);

		final CMMsg msg=CMClass.getMsg(mob,target,this,auto?CMMsg.MASK_ALWAYS:CMMsg.MSG_DELICATE_HANDS_ACT,CMMsg.MSG_OK_VISUAL,CMMsg.MSG_OK_VISUAL,auto?"":L("<S-NAME> declare(s) a turf war!"));
		if(!success)
		{
			return beneficialVisualFizzle(mob,target,auto?"":L("<S-NAME> attempt(s) to declare a turf war, but can't get started."));
		}
		else
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			beneficialAffect(mob,target,asLevel,(CMProps.getIntVar(CMProps.Int.TICKSPERMUDMONTH)));
			if(target.fetchEffect(ID())!=null)
			{
				for(final Session S : CMLib.sessions().localOnlineIterable())
					if((S.mob()!=null)&&(S.mob()!=mob)&&(isADefender(R,S.mob())))
						S.mob().tell(L("@x1 has declared a turf war at '@x2'.  You must immediately go and defend it to keep your tag.",mob.name(mob),R.displayText(mob)));
				setTimeOfNextCast(mob);
			}
		}
		return success;
	}
}
