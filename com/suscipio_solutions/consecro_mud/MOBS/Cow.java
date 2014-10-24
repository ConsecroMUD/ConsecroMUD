package com.suscipio_solutions.consecro_mud.MOBS;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;


public class Cow extends StdMOB implements Drink
{
	@Override public String ID(){return "Cow";}
	public Cow()
	{
		super();
		username="a cow";
		setDescription("A large lumbering beast that looks too slow to get out of your way.");
		setDisplayText("A fat happy cow wanders around here.");
		CMLib.factions().setAlignment(this,Faction.Align.NEUTRAL);
		setMoney(0);
		setWimpHitPoint(0);

		basePhyStats().setDamage(1);
		basePhyStats().setSpeed(1.0);
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(2);
		basePhyStats().setArmor(90);
		baseCharStats().setStat(CharStats.STAT_GENDER, 'F');
		baseCharStats().setMyRace(CMClass.getRace("Cow"));
		baseCharStats().getMyRace().startRacing(this,false);

		baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(),20,basePhyStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverPhyStats();
		recoverCharStats();
	}
	@Override public long decayTime(){return 0;}
	@Override public void setDecayTime(long time){}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(this)&&(msg.targetMinor()==CMMsg.TYP_DRINK))
			return true;
		return super.okMessage(myHost,msg);
	}
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(msg.amITarget(this)&&(msg.targetMinor()==CMMsg.TYP_DRINK))
		{
			final MOB mob=msg.source();
			final boolean thirsty=mob.curState().getThirst()<=0;
			final boolean full=!mob.curState().adjThirst(thirstQuenched(),mob.maxState().maxThirst(mob.baseWeight()));
			if(thirsty)
				mob.tell(L("You are no longer thirsty."));
			else
			if(full)
				mob.tell(L("You have drunk all you can."));
		}
		else
		if((msg.tool()==this)
		&&(msg.targetMinor()==CMMsg.TYP_FILL)
		&&(msg.target()!=null)
		&&(msg.target() instanceof Container)
		&&(((Container)msg.target()).capacity()>0))
		{
			final Container container=(Container)msg.target();
			final Item I=CMClass.getItem("GenLiquidResource");
			I.setName(L("some milk"));
			I.setDisplayText(L("some milk has been left here."));
			I.setDescription(L("It looks like milk"));
			I.setMaterial(RawMaterial.RESOURCE_MILK);
			I.setBaseValue(RawMaterial.CODES.VALUE(RawMaterial.RESOURCE_MILK));
			I.basePhyStats().setWeight(1);
			CMLib.materials().addEffectsToResource(I);
			I.recoverPhyStats();
			I.setContainer(container);
			if(container.owner()!=null)
				if(container.owner() instanceof MOB)
					((MOB)container.owner()).addItem(I);
				else
				if(container.owner() instanceof Room)
					((Room)container.owner()).addItem(I,ItemPossessor.Expire.Resource);
		}
	}
	@Override public int thirstQuenched(){return 100;}
	@Override public int liquidHeld(){return Integer.MAX_VALUE-1000;}
	@Override public int liquidRemaining(){return Integer.MAX_VALUE-1000;}
	@Override public int liquidType(){return RawMaterial.RESOURCE_MILK;}
	@Override public boolean disappearsAfterDrinking(){return false;}
	@Override public void setLiquidType(int newLiquidType){}
	@Override public void setThirstQuenched(int amount){}
	@Override public void setLiquidHeld(int amount){}
	@Override public void setLiquidRemaining(int amount){}
	@Override public boolean containsDrink(){return true;}
	@Override public int amountTakenToFillMe(Drink theSource){return 0;}
}
