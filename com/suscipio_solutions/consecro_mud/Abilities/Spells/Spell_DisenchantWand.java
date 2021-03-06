package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wand;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_DisenchantWand extends Spell
{
	@Override public String ID() { return "Spell_DisenchantWand"; }
	private final static String localizedName = CMLib.lang().L("Disenchant Wand");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_EVOCATION;	}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if((success)
		&&(target instanceof Wand)
		&&(((Wand)target).usesRemaining()>0)
		&&(((Wand)target).getSpell()!=null))
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> hold(s) <T-NAMESELF> and cast(s) a spell.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				((Wand)target).setSpell(null);
				((Wand)target).setUsesRemaining(0);
				mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("<T-NAME> fades and becomes dull!"));
				if((target.basePhyStats().disposition()&PhyStats.IS_BONUS)==PhyStats.IS_BONUS)
					target.basePhyStats().setDisposition(target.basePhyStats().disposition()-PhyStats.IS_BONUS);
				target.recoverPhyStats();
			}

		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> hold(s) <T-NAMESELF> and whisper(s), but nothing happens."));


		// return whether it worked
		return success;
	}
}
