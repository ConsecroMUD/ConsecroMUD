package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Skill_Subdue extends StdSkill
{
	@Override public String ID() { return "Skill_Subdue"; }
	private final static String localizedName = CMLib.lang().L("Subdue");
	@Override public String name() { return localizedName; }
	@Override public String displayText() { return L("(Subdueing "+whom+")"); }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"SUBDUE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_EVASIVE;}
	@Override public int usageType(){return USAGE_MOVEMENT;}
	protected MOB whom=null;
	protected int whomDamage=0;
	protected int asLevel=0;

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setArmor(affectableStats.attackAdjustment() - 10 + super.getXLEVELLevel(invoker()));
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(affected instanceof MOB)
		{
			final MOB M=(MOB)affected;
			if(canBeUninvoked()&&
			(M.amDead()||(!CMLib.flags().isInTheGame(M, false))||(!M.amActive())||M.amDestroyed()||(M.getVictim()!=whom)))
			{
				unInvoke();
				return true;
			}
			if((msg.targetMinor()==CMMsg.TYP_DAMAGE)
			&&(affected !=null)
			&&(msg.source()==affected)
			&&(msg.target()==whom))
			{
				whomDamage+=msg.value();
				msg.setValue(1);
			}
		}
		return true;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(affected instanceof MOB)
		{
			if((msg.source()==affected)
			&&(msg.target()==whom)
			&&(msg.targetMinor()==CMMsg.TYP_EXAMINE)
			&&(CMLib.flags().canBeSeenBy(whom, msg.source())))
			{
				final double actualHitPct = CMath.div(whom.curState().getHitPoints()-whomDamage,whom.baseState().getHitPoints());
				msg.source().tell(msg.source(),whom,null,L("<T-NAME> is @x1 health away from being overcome.",CMath.toPct(actualHitPct)));
			}

			if((msg.targetMinor()==CMMsg.TYP_DAMAGE)
			&&(affected !=null)
			&&(msg.source()==affected)
			&&(msg.target()==whom)
			&&(whom.curState().getHitPoints() - whomDamage)<=0)
			{
				final Ability sap=CMClass.getAbility("Skill_ArrestingSap");
				if(sap!=null) sap.invoke(whom,new XVector(new Object[]{"SAFELY",Integer.toString(adjustedLevel(msg.source(),asLevel))}),whom,true,0);
				whom.makePeace();
				msg.source().makePeace();
				unInvoke();
			}
		}
	}

	@Override
	public void unInvoke()
	{
		if((canBeUninvoked())&&(affected instanceof MOB))
			((MOB)affected).tell(L("You are no longer trying to subdue @x1",whom.name()));
		super.unInvoke();
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		return Ability.QUALITY_INDIFFERENT;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Ability A=mob.fetchEffect(ID());
		if(A!=null)
			A.unInvoke();
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),auto?"":L("^F^<FIGHT^><S-NAME> attempt(s) to subdue <T-NAMESELF>!^</FIGHT^>^?"));
			CMLib.color().fixSourceFightColor(msg);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,mob,asLevel,0);
				final Skill_Subdue SK=(Skill_Subdue)mob.fetchEffect(ID());
				if(SK!=null)
				{
					SK.whom=target;
					SK.asLevel=asLevel;
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> attempt(s) to subdue <T-NAMESELF>, but fails."));
		return success;
	}
}
