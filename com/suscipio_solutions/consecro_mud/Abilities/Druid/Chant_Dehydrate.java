package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;




@SuppressWarnings("rawtypes")
public class Chant_Dehydrate extends Chant
{
	@Override public String ID() { return "Chant_Dehydrate"; }
	private final static String localizedName = CMLib.lang().L("Dehydrate");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Dehydrate)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ENDURING;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_ANY);
		if(target==null) return false;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) to <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					if(target instanceof MOB)
					{
						((MOB)target).curState().adjThirst(-150 - ((mob.phyStats().level()+(2*super.getXLEVELLevel(mob))) * 100),((MOB)target).maxState().maxThirst(((MOB)target).baseWeight()));
						mob.location().show(((MOB)target),null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> feel(s) incredibly thirsty!"));
					}
					else
					if(target instanceof Item)
					{
						if(target instanceof Container)
						{
							final List<Item> V=((Container)target).getContents();
							for(int i=0;i<V.size();i++)
							{
								final Item I=V.get(i);
								if(I instanceof Drink)
								{
									if(((Drink)I).liquidRemaining()<10000)
										((Drink)I).setLiquidRemaining(0);
									if(I instanceof RawMaterial)
										I.destroy();
								}
							}
							if(target instanceof Drink)
							{
								if(((Drink)target).liquidRemaining()<10000)
									((Drink)target).setLiquidRemaining(0);
							}
							if(target instanceof RawMaterial)
								((Item)target).destroy();
						}
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> chant(s) to <T-NAMESELF>, but nothing happens."));
		// return whether it worked
		return success;
	}
}
