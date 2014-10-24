package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;

public class IndoorUnderWater extends StdRoom implements Drink
{
	@Override public String ID(){return "IndoorUnderWater";}
	public IndoorUnderWater()
	{
		super();
		basePhyStats.setWeight(3);
		name="the water";
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_SWIMMING);
		recoverPhyStats();
		atmosphere=RawMaterial.RESOURCE_FRESHWATER;
	}
	@Override public int domainType(){return Room.DOMAIN_INDOORS_UNDERWATER;}
	@Override protected int baseThirst(){return 0;}
	@Override public long decayTime(){return 0;}
	@Override public void setDecayTime(long time){}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected instanceof MOB)
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_SWIMMING);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		switch(UnderWater.isOkUnderWaterAffect(this,msg))
		{
		case -1: return false;
		case 1: return true;
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		UnderWater.sinkAffects(this,msg);
	}
	@Override public boolean disappearsAfterDrinking(){return false;}
	@Override public int thirstQuenched(){return 500;}
	@Override public int liquidHeld(){return Integer.MAX_VALUE-1000;}
	@Override public int liquidRemaining(){return Integer.MAX_VALUE-1000;}
	@Override public int liquidType(){return RawMaterial.RESOURCE_FRESHWATER;}
	@Override public void setLiquidType(int newLiquidType){}
	@Override public void setThirstQuenched(int amount){}
	@Override public void setLiquidHeld(int amount){}
	@Override public void setLiquidRemaining(int amount){}
	@Override public int amountTakenToFillMe(Drink theSource){return 0;}
	@Override public boolean containsDrink(){return true;}
	@Override public List<Integer> resourceChoices(){return UnderWater.roomResources;}
}
