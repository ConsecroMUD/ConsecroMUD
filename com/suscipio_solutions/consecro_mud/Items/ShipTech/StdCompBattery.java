package com.suscipio_solutions.consecro_mud.Items.ShipTech;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Electronics;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class StdCompBattery extends StdElecCompItem implements Electronics.PowerSource
{
	@Override public String ID(){	return "StdBattery";}

	public StdCompBattery()
	{
		super();
		setName("a battery");
		basePhyStats.setWeight(2);
		setDisplayText("a battery sits here.");
		setDescription("");
		baseGoldValue=5;
		basePhyStats().setLevel(1);
		recoverPhyStats();
		setMaterial(RawMaterial.RESOURCE_STEEL);
		super.activate(true);
		super.setPowerCapacity(1000);
		super.setPowerRemaining(1000);
	}

	@Override public TechType getTechType() { return TechType.SHIP_POWER; }

	@Override
	public void setMiscText(String newText)
	{
		if(CMath.isInteger(newText))
			this.setPowerCapacity(CMath.s_int(newText));
		super.setMiscText(newText);
	}

	@Override
	public boolean sameAs(Environmental E)
	{
		if(!(E instanceof GenCompBattery)) return false;
		return super.sameAs(E);
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		if(msg.amITarget(this))
		{
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_LOOK:
				super.executeMsg(host, msg);
				if(CMLib.flags().canBeSeenBy(this, msg.source()))
					msg.source().tell(L("@x1 is currently @x2",name(),(activated()?"delivering power.\n\r":"deactivated/disconnected.\n\r")));
				return;
			case CMMsg.TYP_POWERCURRENT:
				if(activated()
				&& ((Math.random() > super.getInstalledFactor())
					||(Math.random() > super.getFinalManufacturer().getReliabilityPct())
					||((subjectToWearAndTear())&&(usesRemaining()<=100)&&(Math.random()>CMath.div(usesRemaining(), 100))))
				&& (Math.random() > 0.9))
				{
					final Room R=CMLib.map().roomLocation(this);
					if(R!=null)
					{
						// malfunction!
						final CMMsg msg2=CMClass.getMsg(msg.source(), this, null, CMMsg.NO_EFFECT, null, CMMsg.MSG_DEACTIVATE|CMMsg.MASK_CNTRLMSG, "", CMMsg.NO_EFFECT,null);
						if(R.okMessage(msg.source(), msg2))
							R.send(msg.source(), msg2);
					}
					else
						activate(false);
				}
				break;
			}
		}
		super.executeMsg(host, msg);
	}
}
