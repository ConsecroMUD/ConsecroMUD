package com.suscipio_solutions.consecro_mud.Items.ShipTech;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ShipComponent;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;


public class StdShipEngine extends StdCompGenerator implements ShipComponent.ShipEngine
{
	@Override public String ID(){	return "StdShipEngine";}

	protected float 	installedFactor	= 1.0F;
	protected int		maxThrust		= 1000;
	protected int		thrust			= 0;
	protected long		specificImpulse	= SpaceObject.VELOCITY_SUBLIGHT;
	protected double	fuelEfficiency	= 0.33;

	public StdShipEngine()
	{
		super();
		setName("a ships engine");
		basePhyStats.setWeight(50000);
		setDisplayText("a ships engine sits here.");
		setDescription("");
		baseGoldValue=500000;
		basePhyStats().setLevel(1);
		recoverPhyStats();
		setMaterial(RawMaterial.RESOURCE_STEEL);
	}
	@Override
	public boolean sameAs(Environmental E)
	{
		if(!(E instanceof StdShipEngine)) return false;
		return super.sameAs(E);
	}
	@Override public double getFuelEfficiency() { return fuelEfficiency; }
	@Override public void setFuelEfficiency(double amt) { fuelEfficiency=amt; }
	@Override public float getInstalledFactor() { return installedFactor; }
	@Override public void setInstalledFactor(float pct) { if((pct>=0.0)&&(pct<=2.0)) installedFactor=pct; }
	@Override public int getMaxThrust(){return maxThrust;}
	@Override public void setMaxThrust(int max){maxThrust=max;}
	@Override public int getThrust(){return thrust;}
	@Override public void setThrust(int current){thrust=current;}
	@Override public long getSpecificImpulse() { return specificImpulse; }
	@Override public void setSpecificImpulse(long amt) { specificImpulse = amt; }
	@Override public TechType getTechType() { return TechType.SHIP_ENGINE; }

	@Override protected boolean willConsumeFuelIdle() { return getThrust()>0; }

	@Override
	public void executeMsg(Environmental myHost, CMMsg msg)
	{
		super.executeMsg(myHost, msg);
		StdShipThruster.executeThrusterMsg(this, myHost, circuitKey, msg);
	}
}
