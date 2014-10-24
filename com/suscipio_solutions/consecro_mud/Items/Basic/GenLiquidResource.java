package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class GenLiquidResource extends GenDrink implements RawMaterial, Drink
{
	@Override public String ID(){	return "GenLiquidResource";}
	public GenLiquidResource()
	{
		super();
		setName("a puddle of resource thing");
		setDisplayText("a puddle of resource sits here.");
		setDescription("");
		setMaterial(RawMaterial.RESOURCE_FRESHWATER);
		disappearsAfterDrinking=false;
		basePhyStats().setWeight(0);
		setCapacity(0);
		recoverPhyStats();
	}
	protected static Ability rot=null;

	@Override
	public void setMaterial(int newValue)
	{
		super.setMaterial(newValue);
		decayTime=0;
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		super.executeMsg(host,msg);
		if(rot==null)
		{
			rot=CMClass.getAbility("Prayer_Rot");
			if(rot==null) return;
			rot.setAffectedOne(null);
		}
		rot.executeMsg(this,msg);
	}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if(rot==null)
		{
			rot=CMClass.getAbility("Prayer_Rot");
			if(rot==null) return true;
			rot.setAffectedOne(null);
		}
		if(!rot.okMessage(this,msg))
			return false;
		return super.okMessage(host,msg);
	}

	protected String domainSource=null;
	@Override public String domainSource(){return domainSource;}
	@Override public void setDomainSource(String src){domainSource=src;}
	@Override public boolean rebundle(){return false;}//CMLib.materials().rebundle(this);}
	@Override public void quickDestroy(){ CMLib.materials().quickDestroy(this);}
}
