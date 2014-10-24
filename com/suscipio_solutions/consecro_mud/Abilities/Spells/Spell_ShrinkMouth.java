package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Spell_ShrinkMouth extends Spell
{
	@Override public String ID() { return "Spell_ShrinkMouth"; }
	private final static String localizedName = CMLib.lang().L("Shrink Mouth");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Shrunken Mouth)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_TRANSMUTATION;}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if((canBeUninvoked())&&(affected!=null))
		{
			if(affected instanceof MOB)
			{
				final MOB mob=(MOB)affected;
				if((mob.location()!=null)&&(!mob.amDead()))
					mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-YOUPOSS> mouth returns to its normal size."));
			}
		}
		super.unInvoke();
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost, msg))
			return false;
		if((msg.targetMinor()==CMMsg.TYP_EAT)
		&&(affected instanceof MOB)
		&&(msg.amISource((MOB)affected)))
		{
			msg.source().tell(L("Your mouth is too tiny to eat!"));
			return false;
		}
		return true;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> cast(s) a puckering spell on <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Ability A=target.fetchEffect("Spell_BigMouth");
				boolean isJustUnInvoking=false;
				if((A!=null)&&(A.canBeUninvoked()))
				{
					A.unInvoke();
					isJustUnInvoking=true;
				}
				if((!isJustUnInvoking)&&(msg.value()<=0))
				{
					beneficialAffect(mob,target,asLevel,0);
					if((!auto)&&(target.location()!=null))
						target.location().show(mob, target, CMMsg.MSG_OK_VISUAL, L("<T-YOUPOSS> mouth shrinks!"));
					CMLib.utensils().confirmWearability(target);
				}
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to cast a puckering spell, but fail(s)."));

		return success;
	}
}
