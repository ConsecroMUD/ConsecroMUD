package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.SpellHolder;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wand;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_Disenchant extends Prayer
{
	@Override public String ID() { return "Prayer_Disenchant"; }
	private final static String localizedName = CMLib.lang().L("Neutralize Item");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_NEUTRALIZATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final int levelBonus=mob.phyStats().level()+(2*super.getXLEVELLevel(mob));
		int levelDiff=levelBonus-target.phyStats().level();
		levelDiff=levelDiff*5;
		final boolean success=proficiencyCheck(mob,5+levelDiff,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> appear(s) neutralized!"):L("^S<S-NAME> @x1 to neutralize <T-NAMESELF>.^?",prayForWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
				target.basePhyStats().setAbility(0);
				target.delAllEffects(true);
				if(target instanceof Wand)
				{
					((Wand)target).setSpell(null);
					((Wand)target).setUsesRemaining(0);
				}
				else
				if(target instanceof SpellHolder)
					((SpellHolder)target).setSpellList("");
				target.recoverPhyStats();
				mob.location().recoverRoomStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 to neutralize <T-NAMESELF>, but nothing happens.",prayForWord(mob)));
		// return whether it worked
		return success;
	}
}
