package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_ArcaneMark extends Spell
{
	@Override public String ID() { return "Spell_ArcaneMark"; }
	private final static String localizedName = CMLib.lang().L("Arcane Mark");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_ALTERATION;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<2)
		{
			mob.tell(L("You must specify what object you want the spell cast on, and the message you wish the object have marked upon it. "));
			return false;
		}
		final Physical target=mob.location().fetchFromMOBRoomFavorsItems(mob,null,((String)commands.elementAt(0)),Wearable.FILTER_UNWORNONLY);
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",((String)commands.elementAt(0))));
			return false;
		}
		if((!(target instanceof Item))||(!target.isGeneric()))
		{
			mob.tell(L("You can't can't cast this on @x1.",target.name(mob)));
			return false;
		}
		final String message=CMParms.combine(commands,1);

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L("^S<S-NAME> invoke(s) a spell upon <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(target.description().indexOf("Some markings on it say")>=0)
					target.setDescription(L("@x1 Some other markings say `@x2`.",target.description(),message));
				else
					target.setDescription(L("@x1 Some markings on it say `@x2`.",target.description(),message));
			}

		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to invoke a spell upon <T-NAMESELF>, but the spell fizzles."));


		// return whether it worked
		return success;
	}
}
