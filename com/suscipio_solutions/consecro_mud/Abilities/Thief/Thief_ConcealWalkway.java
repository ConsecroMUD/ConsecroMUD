package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_ConcealWalkway extends ThiefSkill
{
	@Override public String ID() { return "Thief_ConcealWalkway"; }
	private final static String localizedName = CMLib.lang().L("Conceal Walkway");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"WALKWAYCONCEAL","WALKCONCEAL","WCONCEAL","CONCEALWALKWAY"});
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALTHY;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	public int code=Integer.MIN_VALUE;

	@Override
	public int abilityCode()
	{
		if(code<0) code=CMath.s_int(text());
		return code;
	}
	@Override public void setAbilityCode(int newCode){code=newCode; super.miscText=""+newCode;}

	@Override
	public void affectPhyStats(Physical host, PhyStats stats)
	{
		super.affectPhyStats(host,stats);
		if(host instanceof Exit)
		{
			stats.setDisposition(stats.disposition()|PhyStats.IS_HIDDEN);
			stats.setLevel(stats.level()+abilityCode());
		}
	}

	@Override
	public void executeMsg(Environmental myHost, CMMsg msg)
	{
		super.executeMsg(myHost, msg);
		if(canBeUninvoked() && (invoker()!=null) && (!msg.source().isMonster()) && (msg.source()!=invoker()) && (msg.sourceMinor()==CMMsg.TYP_ENTER) &&(affected!=null))
		{
			if(!CMLib.flags().isInTheGame(invoker(), true))
			{
				unInvoke();
				if(affected!=null)
				{
					affected.delEffect(this);
					affected.recoverPhyStats();
				}
			}
			else
			{
				final Set<MOB> grp=invoker().getGroupMembers(new HashSet<MOB>());
				if(!grp.contains(msg.source()))
				{
					unInvoke();
					if(affected!=null)
					{
						affected.delEffect(this);
						affected.recoverPhyStats();
					}
				}
			}
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((commands.size()<1)&&(givenTarget==null))
		{
			mob.tell(L("Which way would you like to conceal?"));
			return false;
		}
		Environmental chkE=null;
		final String typed=CMParms.combine(commands,0);
		if(Directions.getGoodDirectionCode(typed)<0)
			chkE=mob.location().fetchFromMOBRoomItemExit(mob,null,typed,Wearable.FILTER_WORNONLY);
		else
			chkE=mob.location().getExitInDir(Directions.getGoodDirectionCode(typed));
		int direction=-1;
		for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
			if(mob.location().getExitInDir(d)==chkE)
				direction=d;
		if((!(chkE instanceof Exit))||(!CMLib.flags().canBeSeenBy(chkE,mob))||(direction<0))
		{
			mob.tell(L("You don't see any directions called '@x1' here.",typed));
			return false;
		}
		final Room R2=mob.location().getRoomInDir(direction);
		if((!CMath.bset(mob.location().domainType(),Room.INDOORS))
		&&(R2!=null)
		&&(!CMath.bset(R2.domainType(),Room.INDOORS)))
		{
			mob.tell(L("This only works on walkways into or within buildings."));
			return false;
		}
		final Exit X=(Exit)chkE;
		if((!auto)&&(X.phyStats().level()>(adjustedLevel(mob,asLevel)*2)))
		{
			mob.tell(L("You aren't good enough to conceal that direction."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,X,this,CMMsg.MSG_THIEF_ACT,L("<S-NAME> conceal(s) <T-NAME>."),CMMsg.MSG_THIEF_ACT,null,CMMsg.MSG_THIEF_ACT,null);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Ability A=(Ability)super.copyOf();
				A.setInvoker(mob);
				A.setAbilityCode((adjustedLevel(mob,asLevel)*2)-X.phyStats().level());
				final Room R=mob.location();
				if((CMLib.law().doesOwnThisProperty(mob,R))
				||((R2!=null)&&(CMLib.law().doesOwnThisProperty(mob,R2))))
				{
					X.addNonUninvokableEffect(A);
					CMLib.database().DBUpdateExits(mob.location());
				}
				else
					A.startTickDown(mob,X,15*(adjustedLevel(mob,asLevel)));
				X.recoverPhyStats();
			}
		}
		else
			beneficialVisualFizzle(mob,X,L("<S-NAME> attempt(s) to coneal <T-NAME>, but obviously fail(s)."));
		return success;
	}
}
