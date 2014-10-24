package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Thief_Flank extends ThiefSkill
{
	@Override public String ID() { return "Thief_Flank"; }
	private final static String localizedName = CMLib.lang().L("Flank");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Flanking)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_OTHERS;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_DIRTYFIGHTING;}
	private static final String[] triggerStrings =I(new String[] {"FLANK"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT;}
	protected MOB target=null;

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		final int xlvl=super.getXLEVELLevel(invoker());
		affectableStats.setDamage(affectableStats.damage()+5+xlvl);
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+50+(10*xlvl));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected==null)||(!(affected instanceof MOB))||(target==null))
			return false;
		if(!super.tick(ticking,tickID))
			return false;

		final MOB mob=(MOB)affected;
		if(mob.location()!=target.location())
			unInvoke();
		if(mob.getVictim()!=target)
			unInvoke();
		if(mob.rangeToTarget()>0)
			unInvoke();
		if(target.getVictim()==mob)
			unInvoke();
		return true;
	}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected==null)||(!(affected instanceof MOB))||(target==null))
			return true;

		final MOB mob=(MOB)affected;
		if(mob.location()!=target.location())
			unInvoke();
		if(mob.getVictim()!=target)
			unInvoke();
		if(mob.rangeToTarget()>0)
			unInvoke();
		if(target.getVictim()==mob)
			unInvoke();
		return super.okMessage(myHost,msg);
	}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if((canBeUninvoked())&&(mob!=null)&&(target!=null)&&(!mob.amDead()))
			mob.tell(L("You are no longer flanking @x1.",target.name(mob)));
		super.unInvoke();
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!mob.isInCombat())
		{
			mob.tell(L("You must be in combat to flank!"));
			return false;
		}
		MOB target=mob.getVictim();
		if(target.getVictim()==mob)
		{
			mob.tell(L("You can't flank someone who is attacking you!"));
			return false;
		}

		if(CMLib.flags().isSitting(mob))
		{
			mob.tell(L("You need to stand up!"));
			return false;
		}
		if(!CMLib.flags().aliveAwakeMobile(mob,false))
			return false;

		if(mob.rangeToTarget()>0)
		{
			mob.tell(L("You are too far away to flank @x1!",mob.getVictim().name()));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_MALICIOUS|CMMsg.MSG_THIEF_ACT,auto?"":L("<S-NAME> flank(s) <T-NAMESELF>!"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				target=mob.getVictim();
				beneficialAffect(mob,mob,asLevel,0);
			}
		}
		else
			return beneficialVisualFizzle(mob,target,L("<S-NAME> attempt(s) to flank <T-NAMESELF>, but flub(s) it."));
		return success;
	}
}
