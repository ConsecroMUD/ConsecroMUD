package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Fighter_CircleTrip extends FighterSkill
{
	boolean doneTicking=false;
	@Override public String ID() { return "Fighter_CircleTrip"; }
	private final static String localizedName = CMLib.lang().L("Circle Trip");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Tripped)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"CIRCLETRIP","CTRIP"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_DIRTYFIGHTING;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(!doneTicking)
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_SITTING);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;
		if((doneTicking)&&(msg.amISource(mob)))
			unInvoke();
		else
		if(msg.amISource(mob)&&(msg.sourceMinor()==CMMsg.TYP_STAND))
			return false;
		return true;
	}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
			doneTicking=true;
		super.unInvoke();
		if(canBeUninvoked())
		{
			if((mob.location()!=null)&&(!mob.amDead()))
			{
				final CMMsg msg=CMClass.getMsg(mob,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> regain(s) <S-HIS-HER> feet."));
				if(mob.location().okMessage(mob,msg))
				{
					mob.location().send(mob,msg);
					CMLib.commands().postStand(mob,true);
				}
			}
			else
				mob.tell(L("You regain your feet."));
		}
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if((mob!=null)&&(target!=null))
		{
			if(CMLib.flags().isSitting(mob))
				return Ability.QUALITY_INDIFFERENT;
			if(!CMLib.flags().aliveAwakeMobileUnbound(mob,false))
				return Ability.QUALITY_INDIFFERENT;
			if(mob.isInCombat()&&(mob.rangeToTarget()>0))
				return Ability.QUALITY_INDIFFERENT;
			if(mob.charStats().getBodyPart(Race.BODY_LEG)<=1)
				return Ability.QUALITY_INDIFFERENT;
			if(target.fetchEffect(ID())!=null)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(CMLib.flags().isSitting(mob))
		{
			mob.tell(L("You need to stand up!"));
			return false;
		}
		if(!CMLib.flags().aliveAwakeMobileUnbound(mob,false))
			return false;
		if(mob.isInCombat()&&(mob.rangeToTarget()>0))
		{
			mob.tell(L("You are too far away to circle trip!"));
			return false;
		}
		if(mob.charStats().getBodyPart(Race.BODY_LEG)<=1)
		{
			mob.tell(L("You need at least two legs to do this."));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if(h==null)
		{
			mob.tell(L("There doesn't appear to be anyone here worth tripping."));
			return false;
		}

		boolean success=true;
		CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),auto?"":L("^F^<FIGHT^><S-NAME> slide(s) into a circle trip!^<FIGHT^>^?"));
		CMLib.color().fixSourceFightColor(msg);
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			for (final Object element : h)
			{
				final MOB target=(MOB)element;

				if((CMLib.flags().isSitting(target)||CMLib.flags().isSleeping(target)))
				{
					mob.tell(L("@x1 is already on the floor!",target.name(mob)));
					return false;
				}

				if(target.riding()!=null)
				{
					mob.tell(L("You can't trip someone @x1 @x2!",target.riding().stateString(target),target.riding().name()));
					return false;
				}
				if(CMLib.flags().isInFlight(target))
				{
					mob.tell(L("@x1 is flying and can't be tripped!",target.name(mob)));
					return false;
				}

				int levelDiff=target.phyStats().level()-(((2*getXLEVELLevel(mob))+mob.phyStats().level()));
				if(levelDiff>0)
					levelDiff=levelDiff*5;
				else
					levelDiff=0;
				final int adjustment = ( -levelDiff ) +
								 ( -( 35 + ((int)Math.round( ( (target.charStats().getStat( CharStats.STAT_DEXTERITY ) ) - 9.0 ) * 3.0 ) ) ) );
				success=proficiencyCheck(mob,adjustment,auto);
				success=success&&(target.charStats().getBodyPart(Race.BODY_LEG)>0);
				if(success)
				{
					msg=CMClass.getMsg(mob,target,this,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),auto?L("<T-NAME> trip(s)!"):L("^F^<FIGHT^><S-NAME> trip(s) <T-NAMESELF>!^</FIGHT^>^?"));
					CMLib.color().fixSourceFightColor(msg);
					if(mob.location().okMessage(mob,msg))
					{
						mob.location().send(mob,msg);
						maliciousAffect(mob,target,asLevel,2,-1);
						target.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> hit(s) the floor!"));
					}
				}
				else
					return maliciousFizzle(mob,target,L("<S-NAME> attempt(s) to circle trip <T-NAMESELF>, but fail(s)."));
			}
		}
		else
			success=false;
		return success;
	}
}
