package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class GenResource extends GenItem implements RawMaterial
{
	@Override public String ID(){	return "GenResource";}
	public GenResource()
	{
		super();
		setName("a pile of resource thing");
		setDisplayText("a pile of resource sits here.");
		setDescription("");
		setMaterial(RawMaterial.RESOURCE_IRON);
		basePhyStats().setWeight(0);
		recoverPhyStats();
	}

	protected String domainSource=null;
	@Override public String domainSource(){return domainSource;}
	@Override public void setDomainSource(String src){domainSource=src;}
	@Override public boolean rebundle(){return CMLib.materials().rebundle(this);}
	@Override public void quickDestroy(){ CMLib.materials().quickDestroy(this);}
}
