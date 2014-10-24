package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Spell_IronGrip extends Spell
{
	@Override public String ID() { return "Spell_IronGrip"; }
	private final static String localizedName = CMLib.lang().L("Iron Grip");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Iron Grip)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_TRANSMUTATION;}


	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
			if((mob.location()!=null)&&(!mob.amDead()))
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-YOUPOSS> weapon hand becomes flesh again."));

		super.unInvoke();

	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg)) return false;
		if((affected!=null)&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			if((msg.amITarget(mob))
			&&(msg.tool()!=null)
			&&(msg.tool().ID().toUpperCase().indexOf("DISARM")>=0))
			{
				mob.location().show(msg.source(),mob,CMMsg.MSG_OK_ACTION,L("<S-NAME> attempt(s) to disarm <T-NAME>, but the grip is too strong!"));
				return false;
			}
			else
			if((msg.amISource(mob))
			&&(msg.targetMinor()==CMMsg.TYP_REMOVE)
			&&(msg.target()!=null)
			&&(msg.target() instanceof Item)
			&&(mob.isMine(msg.target()))
			&&(((Item)msg.target()).amWearingAt(Wearable.WORN_WIELD)))
			{
				mob.location().show(mob,null,msg.target(),CMMsg.MSG_OK_ACTION,L("<S-NAME> attempt(s) to let go of <O-NAME>, but <S-HIS-HER> grip is too strong!"));
				if((!mob.isInCombat())&&(mob.isAttribute(MOB.Attrib.AUTODRAW)))
				{
					mob.tell(L("** Autodraw has been turned OFF. **"));
					mob.setAttribute(MOB.Attrib.AUTODRAW,false);
				}
				return false;
			}
			else
			if((msg.amISource(mob))
			&&((msg.targetMinor()==CMMsg.TYP_DROP)
				||(msg.targetMinor()==CMMsg.TYP_GET))
			&&(msg.target()!=null)
			&&(msg.target() instanceof Item)
			&&(mob.isMine(msg.target()))
			&&(((Item)msg.target()).amWearingAt(Wearable.WORN_WIELD)))
			{
				mob.location().show(mob,null,msg.target(),CMMsg.MSG_OK_ACTION,L("<S-NAME> attempt(s) to let go of <O-NAME>, but <S-HIS-HER> grip is too strong!"));
				return false;
			}
			else
			if((msg.amISource(mob))
			&&(msg.sourceMinor()==CMMsg.TYP_THROW)
			&&(msg.tool() instanceof Item)
			&&(!((Item)msg.tool()).amWearingAt(Wearable.IN_INVENTORY))
			&&(mob.isMine(msg.tool())))
			{
				mob.location().show(mob,null,msg.tool(),CMMsg.MSG_OK_ACTION,L("<S-NAME> attempt(s) to let go of <O-NAME>, but <S-HIS-HER> grip is too strong!"));
				return false;
			}
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> watch(es) <T-HIS-HER> weapon hand turn to iron!"):L("^S<S-NAME> invoke(s) a spell on <T-NAMESELF> and <T-HIS-HER> weapon hand turns into iron!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to invoke a spell, but fail(s)."));

		return success;
	}
}
