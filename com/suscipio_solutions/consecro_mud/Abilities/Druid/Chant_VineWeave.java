package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.ItemCraftor;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_VineWeave extends Chant
{
	@Override public String ID() { return "Chant_VineWeave"; }
	private final static String localizedName = CMLib.lang().L("Vine Weave");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int overrideMana(){return 50;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTGROWTH;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(mob.location().resourceChoices()==null)
		{
			mob.tell(L("This magic will not work here."));
			return false;
		}
		if(((mob.location().myResource()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_WOODEN)
		&&((mob.location().myResource()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_VEGETATION)
		&&(!mob.location().resourceChoices().contains(Integer.valueOf(RawMaterial.RESOURCE_COTTON)))
		&&(!mob.location().resourceChoices().contains(Integer.valueOf(RawMaterial.RESOURCE_SILK)))
		&&(!mob.location().resourceChoices().contains(Integer.valueOf(RawMaterial.RESOURCE_HEMP)))
		&&(!mob.location().resourceChoices().contains(Integer.valueOf(RawMaterial.RESOURCE_VINE)))
		&&(!mob.location().resourceChoices().contains(Integer.valueOf(RawMaterial.RESOURCE_WHEAT)))
		&&(!mob.location().resourceChoices().contains(Integer.valueOf(RawMaterial.RESOURCE_SEAWEED))))
		{
			mob.tell(L("This magic will not work here."));
			return false;
		}
		int material=RawMaterial.RESOURCE_VINE;
		if(mob.location().resourceChoices().contains(Integer.valueOf(RawMaterial.RESOURCE_VINE)))
			material=RawMaterial.RESOURCE_VINE;
		else
		if(mob.location().resourceChoices().contains(Integer.valueOf(RawMaterial.RESOURCE_SILK)))
			material=RawMaterial.RESOURCE_SILK;
		else
		if(mob.location().resourceChoices().contains(Integer.valueOf(RawMaterial.RESOURCE_HEMP)))
			material=RawMaterial.RESOURCE_HEMP;
		else
		if(mob.location().resourceChoices().contains(Integer.valueOf(RawMaterial.RESOURCE_WHEAT)))
			material=RawMaterial.RESOURCE_WHEAT;
		else
		if(mob.location().resourceChoices().contains(Integer.valueOf(RawMaterial.RESOURCE_SEAWEED)))
			material=RawMaterial.RESOURCE_SEAWEED;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> chant(s) to the plants.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final ItemCraftor A=(ItemCraftor)CMClass.getAbility("Weaving");
				ItemCraftor.ItemKeyPair pair=null;
				if(A!=null) pair=A.craftAnyItem(material);
				if(pair==null)
				{
					mob.tell(L("The chant failed for some reason..."));
					return false;
				}
				final Item building=pair.item;
				final Item key=pair.key;
				mob.location().addItem(building,ItemPossessor.Expire.Resource);
				if(key!=null) mob.location().addItem(key,ItemPossessor.Expire.Resource);
				mob.location().showHappens(CMMsg.MSG_OK_ACTION,L("@x1 twists out of some vines and grows still.",building.name()));
				mob.location().recoverPhyStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> chant(s) to the plants, but nothing happens."));

		// return whether it worked
		return success;
	}
}
