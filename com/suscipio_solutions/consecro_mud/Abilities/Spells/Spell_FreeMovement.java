package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_FreeMovement extends Spell
{
	@Override public String ID() { return "Spell_FreeMovement"; }
	private final static String localizedName = CMLib.lang().L("Free Movement");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Free Movement)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_ABJURATION;}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
			mob.tell(L("Your uninhibiting protection dissipates."));

		super.unInvoke();

	}


	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;
		if((msg.amITarget(mob))
		&&(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Ability)
		&&(!mob.amDead()))
		{
			final Ability A=(Ability)msg.tool();
			if(CMath.bset(A.flags(),Ability.FLAG_PARALYZING))
			{
				msg.addTrailerMsg(CMClass.getMsg(mob,null,CMMsg.MSG_OK_VISUAL,L("The uninhibiting barrier around <S-NAME> repels the @x1.",A.name())));
				return false;
			}
			final MOB newMOB=CMClass.getFactoryMOB();
			final CMMsg msg2=CMClass.getMsg(newMOB,null,null,CMMsg.MSG_SIT,null);
			newMOB.recoverPhyStats();
			try
			{
				A.affectPhyStats(newMOB,newMOB.phyStats());
				if((!CMLib.flags().aliveAwakeMobileUnbound(newMOB,true))
				   ||(CMath.bset(A.flags(),Ability.FLAG_PARALYZING))
				   ||(!A.okMessage(newMOB,msg2)))
				{
					msg.addTrailerMsg(CMClass.getMsg(mob,null,CMMsg.MSG_OK_VISUAL,L("The uninhibiting barrier around <S-NAME> repels the @x1.",A.name())));
					newMOB.destroy();
					return false;
				}
			}
			catch(final Exception e)
			{}
			newMOB.destroy();
		}
		return true;
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> feel(s) freely protected."):L("^S<S-NAME> invoke(s) an uninhibiting barrier of protection around <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to invoke an uninhibiting barrier, but fail(s)."));

		return success;
	}
}
