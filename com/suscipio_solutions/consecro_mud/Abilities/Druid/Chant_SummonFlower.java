package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;




public class Chant_SummonFlower extends Chant_SummonPlants
{
	@Override public String ID() { return "Chant_SummonFlower"; }
	private final static String localizedName = CMLib.lang().L("Summon Flower");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTGROWTH;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	protected boolean processing=false;

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((msg.amITarget(littlePlants))
		&&(!processing)
		&&((msg.targetMinor()==CMMsg.TYP_GET)||(msg.targetMinor()==CMMsg.TYP_PUSH)||(msg.targetMinor()==CMMsg.TYP_PULL)))
		{
			processing=true;
			final Ability A=littlePlants.fetchEffect(ID());
			if(A!=null)
			{
				CMLib.threads().deleteTick(A,-1);
				littlePlants.delEffect(A);
				littlePlants.setSecretIdentity("");
			}
			if(littlePlants.fetchBehavior("Decay")==null)
			{
				final Behavior B=CMClass.getBehavior("Decay");
				B.setParms("min="+CMProps.getIntVar(CMProps.Int.TICKSPERMUDMONTH)+" max="+CMProps.getIntVar(CMProps.Int.TICKSPERMUDMONTH)+" chance=100");
				littlePlants.addBehavior(B);
				B.executeMsg(myHost,msg);
			}
			processing=false;
		}
	}
	public Item buildFlower(MOB mob, Room room)
	{
		final Item newItem=CMClass.getItem("GenItem");
		newItem.setMaterial(RawMaterial.RESOURCE_GREENS);
		switch(CMLib.dice().roll(1,5,0))
		{
		case 1:
			newItem.setName(L("a red rose"));
			newItem.setDisplayText(L("a red rose is growing here."));
			newItem.setDescription("");
			break;
		case 2:
			newItem.setName(L("a nice daisy"));
			newItem.setDisplayText(L("a nice daisy is growing here."));
			newItem.setDescription("");
			break;
		case 3:
			newItem.setName(L("a white carnation"));
			newItem.setDisplayText(L("a beautiful white carnation is growing here"));
			newItem.setDescription("");
			break;
		case 4:
			newItem.setName(L("a happy sunflower"));
			newItem.setDisplayText(L("a happy sunflower is growing here."));
			newItem.setDescription(L("Happy flowers have little yellow blooms."));
			break;
		case 5:
			newItem.setName(L("a lovely gladiola"));
			newItem.setDisplayText(L("a lovely gladiola is growing here."));
			newItem.setDescription("");
			break;
		}
		newItem.setSecretIdentity(mob.Name());
		newItem.setMiscText(newItem.text());
		room.addItem(newItem);
		final Chant_SummonFlower newChant=new Chant_SummonFlower();
		newItem.basePhyStats().setLevel(10+newChant.getX1Level(mob));
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
		return buildFlower(mob,room);
	}
}
