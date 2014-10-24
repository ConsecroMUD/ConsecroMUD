package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Scroll;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Erase extends Spell
{
	@Override public String ID() { return "Spell_Erase"; }
	private final static String localizedName = CMLib.lang().L("Erase Scroll");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int classificationCode() {	return Ability.ACODE_SPELL|Ability.DOMAIN_ALTERATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{

		if((commands.size()<1)&&(givenTarget==null))
		{
			mob.tell(L("Erase what?."));
			return false;
		}
		final Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null) return false;

		if(!(target instanceof Scroll)&&(!target.isReadable()))
		{
			mob.tell(L("You can't erase that."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("The words on <T-NAME> fade."):L("^S<S-NAME> whisper(s), and then rub(s) on <T-NAMESELF>, making the words fade.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(target instanceof Scroll)
					((Scroll)target).setSpellList("");
				else
					target.setReadableText("");
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> whisper(s), and then rub(s) on <T-NAMESELF>, but nothing happens."));


		// return whether it worked
		return success;
	}
}
