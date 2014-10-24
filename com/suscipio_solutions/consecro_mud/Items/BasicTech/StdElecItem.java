package com.suscipio_solutions.consecro_mud.Items.BasicTech;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Manufacturer;
import com.suscipio_solutions.consecro_mud.Items.Basic.StdItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Electronics;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class StdElecItem extends StdItem implements Electronics
{
	@Override public String ID(){	return "StdElecItem";}

	protected long 			powerCapacity	= 100;
	protected long 			power			= 100;
	protected boolean 		activated		= false;
	protected String	 	manufacturer	= "RANDOM";
	protected Manufacturer  cachedManufact  = null;

	public StdElecItem()
	{
		super();
		setName("a piece of electronics");
		setDisplayText("a small piece of electronics sits here.");
		setDescription("You can't tell what it is by looking at it.");

		material=RawMaterial.RESOURCE_STEEL;
		baseGoldValue=0;
		recoverPhyStats();
	}

	protected static final boolean isThisPanelActivated(Electronics.ElecPanel E)
	{
		if(!E.activated())
			return false;
		if(E.container() instanceof Electronics.ElecPanel)
			return isThisPanelActivated((Electronics.ElecPanel)E.container());
		return true;
	}

	public static final boolean isAllWiringConnected(Electronics E)
	{
		if(E instanceof Electronics.ElecPanel)
			return isThisPanelActivated((Electronics.ElecPanel)E);
		if(E.container() instanceof Electronics.ElecPanel)
			return isThisPanelActivated((Electronics.ElecPanel)E.container());
		return true;
	}

	@Override public long powerCapacity(){return powerCapacity;}
	@Override public void setPowerCapacity(long capacity){powerCapacity=capacity;}
	@Override public long powerRemaining(){return power;}
	@Override public void setPowerRemaining(long remaining){power=remaining;}
	@Override public boolean activated(){ return activated; }
	@Override public void activate(boolean truefalse){activated=truefalse;}
	@Override public int powerNeeds(){return (int)Math.min(powerCapacity-power,Integer.MAX_VALUE);}
	@Override public int techLevel() { return phyStats().ability();}
	@Override public void setTechLevel(int lvl) { basePhyStats.setAbility(lvl); recoverPhyStats(); }
	@Override public String getManufacturerName() { return manufacturer; }
	@Override public TechType getTechType() { return TechType.GIZMO; }
	@Override public void setManufacturerName(String name) { cachedManufact=null; if(name!=null) manufacturer=name; }
	@Override
	public Manufacturer getFinalManufacturer()
	{
		if(cachedManufact==null)
		{
			cachedManufact=CMLib.tech().getManufacturerOf(this,manufacturer.toUpperCase().trim());
			if(cachedManufact==null)
				cachedManufact=CMLib.tech().getDefaultManufacturer();
		}
		return cachedManufact;
	}
}
