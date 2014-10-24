package com.suscipio_solutions.consecro_mud.Items.ShipTech;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.SpaceShip;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Technical;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;


public class GenCompEnviroSystem extends GenElecCompItem
{
	@Override public String ID(){	return "GenCompEnviroSystem";}

	protected final static int ENVIRO_TICKS=7;
	protected int tickDown=ENVIRO_TICKS;

	protected int airResource=RawMaterial.RESOURCE_AIR;

	public GenCompEnviroSystem()
	{
		super();
		setName("a generic environment system");
		setDisplayText("a generic environment system sits here.");
		setDescription("");
	}

	@Override public TechType getTechType() { return TechType.SHIP_ENVIRO_CONTROL; }

	@Override
	public void executeMsg(Environmental myHost, CMMsg msg)
	{
		super.executeMsg(myHost, msg);
		if(msg.amITarget(this))
		{
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_LOOK:
				if(CMLib.flags().canBeSeenBy(this, msg.source()))
					msg.source().tell(L("@x1 is currently @x2",name(),(activated()?"delivering power.\n\r":"deactivated/disconnected.\n\r")));
				return;
			case CMMsg.TYP_POWERCURRENT:
				if(activated())
				{
					if(--tickDown <=0)
					{
						tickDown=ENVIRO_TICKS;
						final SpaceObject obj=CMLib.map().getSpaceObject(this, true);
						if(obj instanceof SpaceShip)
						{
							final SpaceShip ship=(SpaceShip)obj;
							final Area A=ship.getShipArea();
							double pct= Math.min(super.getInstalledFactor(),1.0)
									  * Math.min(super.getFinalManufacturer().getReliabilityPct(),1.0);
							if(subjectToWearAndTear())
								pct=pct*CMath.div(usesRemaining(),100);
							final String code=Technical.TechCommand.AIRREFRESH.makeCommand(Double.valueOf(pct),Integer.valueOf(airResource));
							final CMMsg msg2=CMClass.getMsg(msg.source(), ship, me, CMMsg.NO_EFFECT, null, CMMsg.MSG_ACTIVATE|CMMsg.MASK_CNTRLMSG, code, CMMsg.NO_EFFECT,null);
							if(A.okMessage(msg2.source(), msg))
								A.executeMsg(msg2.source(), msg);
						}
					}
				}
				break;
			}
		}
	}
}
