package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class GenFoodResource extends GenFood implements RawMaterial, Food
{
	@Override public String ID(){	return "GenFoodResource";}
	protected static Ability rot=null;

	public GenFoodResource()
	{
		super();
		setName("an edible resource");
		setDisplayText("a pile of edible resource sits here.");
		setDescription("");
		material=RawMaterial.RESOURCE_BERRIES;
		setNourishment(200);
		basePhyStats().setWeight(0);
		recoverPhyStats();
		decayTime=0;
	}

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

	@Override public boolean rebundle(){return false;}//CMLib.materials().rebundle(this);}
	@Override public void quickDestroy(){ CMLib.materials().quickDestroy(this);}

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
}
