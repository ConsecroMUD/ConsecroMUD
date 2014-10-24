package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_FortifyFood extends Prayer
{
	@Override public String ID() { return "Prayer_FortifyFood"; }
	private final static String localizedName = CMLib.lang().L("Nourishing Food");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_BLESSING;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_ITEMS;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null) return false;


		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		if(!(target instanceof Food))
		{
			mob.tell(L("@x1 is not edible.",target.name(mob)));
			return false;
		}

		final boolean success=proficiencyCheck(mob,0,auto);

		if(((Food)target).nourishment()>1000)
		{
			mob.tell(L("@x1 is already well fortified.",target.name(mob)));
			return false;
		}

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> @x1 to fortify <T-NAMESELF>.^?",prayForWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(mob,target,CMMsg.MSG_OK_ACTION,L("<T-NAME> look(s) much more nutritious!"));
				((Food)target).setNourishment(((Food)target).nourishment()+1000);
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> @x1, but nothing happens.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
