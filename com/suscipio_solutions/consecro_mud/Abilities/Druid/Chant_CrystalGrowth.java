package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.ItemCraftor;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;




@SuppressWarnings("rawtypes")
public class Chant_CrystalGrowth extends Chant
{
	@Override public String ID() { return "Chant_CrystalGrowth"; }
	private final static String localizedName = CMLib.lang().L("Crystal Growth");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ROCKCONTROL;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int overrideMana(){return 50;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(mob.location().domainType()!=Room.DOMAIN_INDOORS_CAVE)
		{
			mob.tell(L("This magic will not work here."));
			return false;
		}
		final int material=RawMaterial.RESOURCE_CRYSTAL;
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> chant(s) to the cave walls.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);

				ItemCraftor A=null;
				switch(CMLib.dice().roll(1,10,0))
				{
				case 1:
				case 2:
				case 3:
				case 4:
					A=(ItemCraftor)CMClass.getAbility("Blacksmithing");
					break;
				case 5:
				case 6:
				case 7:
					A=(ItemCraftor)CMClass.getAbility("Armorsmithing");
					break;
				case 8:
				case 9:
				case 10:
					A=(ItemCraftor)CMClass.getAbility("Weaponsmithing");
					break;
				}
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
				final Ability A2=CMClass.getAbility("Chant_Brittle");
				if(A2!=null) building.addNonUninvokableEffect(A2);

				mob.location().showHappens(CMMsg.MSG_OK_ACTION,L("a tiny crystal fragment drops out of the stone, swells and grows, forming into @x1.",building.name()));
				mob.location().recoverPhyStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> chant(s) to the walls, but nothing happens."));

		// return whether it worked
		return success;
	}
}
