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
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;



@SuppressWarnings("rawtypes")
public class Chant_PlantBed extends Chant
{
	@Override public String ID() { return "Chant_PlantBed"; }
	private final static String localizedName = CMLib.lang().L("Plant Bed");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTGROWTH;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	protected Item peaPod=null;

	@Override
	public void unInvoke()
	{
		super.unInvoke();
		if(peaPod!=null)
		{
			final Room R=CMLib.map().roomLocation(peaPod);
			if(R!=null)
				R.showHappens(CMMsg.MSG_OK_VISUAL,L("A pea-pod shrivels up!"));
			final Rideable RI=(Rideable)peaPod;
			for(int r=RI.numRiders()-1;r>=0;r--)
				RI.fetchRider(r).setRiding(null);
			peaPod.destroy();
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((((mob.location().domainType()&Room.INDOORS)>0)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_DESERT)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_CITY)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_SPACEPORT)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_WATERSURFACE)
		   ||(mob.location().domainType()==Room.DOMAIN_OUTDOORS_AIR))
		&&(!auto))
		{
			mob.tell(L("This chant will not work here."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> chant(s) to the ground.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Item newItem=CMClass.getItem("GenBed");
				final Rideable newRide=(Rideable)newItem;
				newItem.setName(L("a plant bed"));
				newItem.setDisplayText(L("A enormously comfortable pea-pod looks ready to sleep in."));
				newItem.setDescription(L("The plant bed looks like a hollowed pea-pod with fern-like cushioning inside.  Looks like a nice place to take a nap in!"));
				newRide.setRideBasis(Rideable.RIDEABLE_SLEEP);
				newRide.setRiderCapacity(1);
				newItem.setMaterial(RawMaterial.RESOURCE_HEMP);
				newItem.basePhyStats().setWeight(1000);
				newItem.setBaseValue(0);
				CMLib.flags().setGettable(newItem,false);
				final Ability A=CMClass.getAbility("Prop_RideResister");
				A.setMiscText("disease poison");
				newItem.addNonUninvokableEffect(A);
				newItem.recoverPhyStats();
				newItem.setMiscText(newItem.text());
				peaPod=newItem;
				mob.location().addItem(newItem,ItemPossessor.Expire.Resource);
				mob.location().showHappens(CMMsg.MSG_OK_ACTION,L("A comfortable pea-pod bed grows nearby."));
				mob.location().recoverPhyStats();
				beneficialAffect(mob,newItem,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> chant(s) to the ground, but nothing happens."));

		// return whether it worked
		return success;
	}
}
