package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_DivineGuidance extends Prayer
{
	@Override public String ID() { return "Prayer_DivineGuidance"; }
	private final static String localizedName = CMLib.lang().L("Divine Guidance");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Awaiting Divine Guidance)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_COMMUNING;}
	@Override public long flags(){return Ability.FLAG_HOLY;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(!(affected instanceof MOB)) return;
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_GOOD);
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+10+(2*getXLEVELLevel(invoker())));
		affectableStats.setDamage(affectableStats.damage()+5+getXLEVELLevel(invoker()));
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(affected instanceof MOB)
		{
			final MOB mob=(MOB)affected;
			if(canBeUninvoked())
				mob.tell(L("You have received your divine guidance."));
		}
		super.unInvoke();
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((invoker==null)||(!(affected instanceof MOB)))
			return;
		if(msg.amISource((MOB)affected)
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(!msg.amITarget(affected))
		&&(msg.tool() instanceof Weapon)
		&&(msg.value()>0))
		{
			unInvoke();
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,somanticCastCode(mob,target,auto),L(auto?"<T-NAME> await(s) divine guidance!":"^S<S-NAME> "+prayForWord(mob)+" to give <T-NAME> divine guidance.^?")+CMLib.protocol().msp("bless.wav",10));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
				target.recoverPhyStats();
				target.location().recoverRoomStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 for divine guidance, but <S-IS-ARE> not heard.",prayWord(mob)));
		// return whether it worked
		return success;
	}
}
