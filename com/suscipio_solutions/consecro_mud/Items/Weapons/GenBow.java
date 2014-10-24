package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class GenBow extends StdBow
{
	@Override public String ID(){	return "GenBow";}
	protected String	readableText="";
	public GenBow()
	{
		super();
		setName("a generic short bow");
		setDisplayText("a generic short bow sits here.");
		setDescription("");
		setAmmunitionType("arrows");
		setAmmoCapacity(20);
		setAmmoRemaining(20);
		recoverPhyStats();
	}

	@Override public boolean isGeneric(){return true;}


	@Override
	public String text()
	{
		return CMLib.coffeeMaker().getPropertiesStr(this,false);
	}
	@Override public String readableText(){return readableText;}
	@Override public void setReadableText(String text){readableText=text;}

	@Override
	public void setMiscText(String newText)
	{
		miscText="";
		CMLib.coffeeMaker().setPropertiesStr(this,newText,false);
		recoverPhyStats();
	}
}

