package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;




@SuppressWarnings("rawtypes")
public class Chant_CaveFishing extends Chant
{
	@Override public String ID() { return "Chant_CaveFishing"; }
	private final static String localizedName = CMLib.lang().L("Cave Fishing");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ANIMALAFFINITY;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_ROOMS;}
	protected int previousResource=-1;

	@Override
	public void unInvoke()
	{
		if((affected instanceof Room)
		&&(this.canBeUninvoked()))
		{
			((Room)affected).showHappens(CMMsg.MSG_OK_VISUAL,L("The fish start to disappear!"));
			((Room)affected).setResource(previousResource);
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Room target=mob.location();
		if(target==null) return false;

		Environmental waterSrc=null;
		if((target.domainType()==Room.DOMAIN_INDOORS_WATERSURFACE)
		||(target.domainType()==Room.DOMAIN_INDOORS_UNDERWATER))
			waterSrc=target;
		else
		if(target.domainType()==Room.DOMAIN_INDOORS_CAVE)
		{
			for(int i=0;i<target.numItems();i++)
			{
				final Item I=target.getItem(i);
				if((I instanceof Drink)
				&&(I.container()==null)
				&&(((Drink)I).liquidType()==RawMaterial.RESOURCE_FRESHWATER)
				&&(!CMLib.flags().isGettable(I)))
					waterSrc=I;
			}
			if(waterSrc==null)
			{
				mob.tell(L("There is no water source here to fish in."));
				return false;
			}
		}
		else
		{
			mob.tell(L("This chant cannot be used outdoors."));
			return false;
		}


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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) to <T-NAME>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("Fish start swimming around in @x1!",target.name()));
					beneficialAffect(mob, target, asLevel,0);
					final Chant_CaveFishing A=(Chant_CaveFishing)target.fetchEffect(ID());
					if(A!=null)
					{
						mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("Fish start swimming around in @x1!",target.name()));
						A.previousResource=target.myResource();
						target.setResource(RawMaterial.CODES.FISHES()[CMLib.dice().roll(1,RawMaterial.CODES.FISHES().length,-1)]);
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> chant(s) to <T-NAME>, but the magic fades."));
		// return whether it worked
		return success;
	}
}
