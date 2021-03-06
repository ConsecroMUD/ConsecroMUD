package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Reinforce extends Spell
{
	@Override public String ID() { return "Spell_Reinforce"; }
	private final static String localizedName = CMLib.lang().L("Reinforce");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_ALTERATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item target=getTarget(mob,null,givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null) return false;
		if(!target.subjectToWearAndTear())
		{	mob.tell(L("@x1 cannot be reinforced.",target.name(mob))); return false;}
		else
		if(target.usesRemaining()<100)
		{	mob.tell(L("@x1 must be repaired before it can be reinforced.",target.name(mob))); return false;}

		if(!super.invoke(mob,commands, givenTarget, auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,(((mob.phyStats().level()+(2*getXLEVELLevel(mob)))-target.phyStats().level())*5),auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),
									(auto?"<T-NAME> begins to shimmer!"
										 :"^S<S-NAME> incant(s) at <T-NAMESELF>!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(target.usesRemaining()>=150)
					mob.tell(L("@x1 cannot be reinforced further.",target.name(mob)));
				else
				{
					mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("<T-NAME> begin(s) to glow and harden!"));
					target.setUsesRemaining(target.usesRemaining()+50);
					target.recoverPhyStats();
					mob.location().recoverRoomStats();
				}
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> incant(s) at <T-NAMESELF>, but nothing happens."));

		// return whether it worked
		return success;
	}
}
