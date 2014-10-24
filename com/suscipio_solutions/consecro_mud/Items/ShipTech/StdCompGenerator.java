package com.suscipio_solutions.consecro_mud.Items.ShipTech;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Electronics;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class StdCompGenerator extends StdCompFuelConsumer implements Electronics.PowerGenerator
{
	@Override public String ID(){	return "StdCompGenerator";}
	public StdCompGenerator()
	{
		super();
		setName("a generator");
		setDisplayText("a generator sits here.");
		setDescription("If you put the right fuel in it, I'll bet it makes power.");

		material=RawMaterial.RESOURCE_STEEL;
		setPowerCapacity(1000);
		setPowerRemaining(0);
		baseGoldValue=0;
		recoverPhyStats();
	}

	protected int   generatedAmtPerTick = 1;

	@Override public int getGeneratedAmountPerTick() { return generatedAmtPerTick; }
	@Override
	public void setGenerationAmountPerTick(int amt)
	{
		generatedAmtPerTick=amt;
	}

	@Override public TechType getTechType() { return TechType.SHIP_GENERATOR; }

	@Override
	public void executeMsg(Environmental myHost, CMMsg msg)
	{
		super.executeMsg(myHost, msg);
		if(msg.amITarget(this))
		{
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_GET:
				clearFuelCache();
				break;
			case CMMsg.TYP_INSTALL:
				clearFuelCache();
				break;
			case CMMsg.TYP_ACTIVATE:
				if((msg.source().location()!=null)&&(!CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG)))
					msg.source().location().show(msg.source(), this, CMMsg.MSG_OK_VISUAL, L("<S-NAME> power(s) up <T-NAME>."));
				this.activate(true);
				break;
			case CMMsg.TYP_DEACTIVATE:
				if((msg.source().location()!=null)&&(!CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG)))
					msg.source().location().show(msg.source(), this, CMMsg.MSG_OK_VISUAL, L("<S-NAME> shut(s) down <T-NAME>."));
				this.activate(false);
				break;
			case CMMsg.TYP_LOOK:
				if(CMLib.flags().canBeSeenBy(this, msg.source()))
					msg.source().tell(L("@x1 is currently @x2",name(),(activated()?"delivering power.\n\r":"deactivated/shut down.\n\r")));
				return;
			case CMMsg.TYP_POWERCURRENT:
				if(msg.value()==0)
				{
					if((((powerCapacity() - powerRemaining()) >= getGeneratedAmountPerTick())
						||(powerRemaining() < getGeneratedAmountPerTick()))
					&&(Math.random()<getFinalManufacturer().getReliabilityPct()))
					{
						double generatedAmount = getGeneratedAmountPerTick();
						generatedAmount *= getFinalManufacturer().getEfficiencyPct();
						generatedAmount *= getInstalledFactor();
						if(subjectToWearAndTear() && (usesRemaining()<=200))
							generatedAmount *= CMath.div(usesRemaining(), 100.0);
						long newAmount=powerRemaining() + Math.round(generatedAmount);
						if(newAmount > powerCapacity())
							newAmount=powerCapacity();
						setPowerRemaining(newAmount);
					}
				}
				break;
			}
		}
	}
}
