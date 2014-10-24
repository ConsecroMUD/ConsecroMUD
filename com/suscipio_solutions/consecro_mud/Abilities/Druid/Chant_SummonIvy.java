package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;




public class Chant_SummonIvy extends Chant_SummonPlants
{
	@Override public String ID() { return "Chant_SummonIvy"; }
	private final static String localizedName = CMLib.lang().L("Summon Ivy");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTGROWTH;}
	@Override protected int canAffectCode(){return CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}

	public Item buildIvy(MOB mob, Room room)
	{
		final Item newItem=CMClass.getItem("GenItem");
		newItem.setMaterial(RawMaterial.RESOURCE_GREENS);
		switch(CMLib.dice().roll(1,5,0))
		{
		case 1:
		case 4:
			newItem.setName(L("poison ivy"));
			newItem.setDisplayText(L("a lovely trifoliate is growing here."));
			newItem.setDescription("");
			break;
		case 2:
			newItem.setName(L("poison sumac"));
			newItem.setDisplayText(L("a small pinnately leafletted tree grows here"));
			newItem.setDescription("");
			break;
		case 3:
		case 5:
			newItem.setName(L("poison oak"));
			newItem.setDisplayText(L("a lovely wrinkled plant grows here"));
			newItem.setDescription("");
			break;
		}
		final Chant_SummonIvy newChant=new Chant_SummonIvy();
		newItem.basePhyStats().setLevel(10+newChant.getX1Level(mob));
		newItem.basePhyStats().setWeight(1);
		newItem.setSecretIdentity(mob.Name());
		newItem.setMiscText(newItem.text());
		newItem.addNonUninvokableEffect(CMClass.getAbility("Disease_PoisonIvy"));
		room.addItem(newItem);
		newItem.setExpirationDate(0);
		room.showHappens(CMMsg.MSG_OK_ACTION,CMLib.lang().L("Suddenly, @x1 sprout(s) up here.",newItem.name()));
		newChant.PlantsLocation=room;
		newChant.littlePlants=newItem;
		if(CMLib.law().doesOwnThisProperty(mob,room))
		{
			newChant.setInvoker(mob);
			newChant.setMiscText(mob.Name());
			newItem.addNonUninvokableEffect(newChant);
		}
		else
			newChant.beneficialAffect(mob,newItem,0,(newChant.adjustedLevel(mob,0)*240)+450);
		room.recoverPhyStats();
		return newItem;
	}

	@Override
	protected Item buildMyPlant(MOB mob, Room room)
	{
		return buildIvy(mob,room);
	}
}
