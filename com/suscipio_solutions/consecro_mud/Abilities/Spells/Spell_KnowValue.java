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
public class Spell_KnowValue extends Spell
{
	@Override public String ID() { return "Spell_KnowValue"; }
	private final static String localizedName = CMLib.lang().L("Know Value");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> weigh(s) the value of <T-NAMESELF> carefully.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				String str=null;
				if(target.value()<=0)
					str=L("@x1 isn't worth anything.",target.name(mob));
				else
				if(target.value()==0)
					str=L("@x1 is worth hardly anything at all",target.name(mob));
				else
					str=L("@x1 is worth @x2 ",target.name(mob),CMLib.beanCounter().nameCurrencyShort(mob,(double)target.value()));
				if(mob.isMonster())
					CMLib.commands().postSay(mob,null,str,false,false);
				else
					mob.tell(str);
			}

		}
		else
			beneficialVisualFizzle(mob,target,L("<S-NAME> weigh(s) the value of <T-NAMESELF>, looking more frustrated every second."));


		// return whether it worked
		return success;
	}
}
