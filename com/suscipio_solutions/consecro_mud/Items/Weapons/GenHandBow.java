package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class GenHandBow extends StdBow
{
	@Override public String ID(){ return "GenHandBow";}
	protected String	readableText="";
	public GenHandBow()
	{
		super();
		setName("a generic hand crossbow");
		setDisplayText("a generic hand crossbow sits here.");
		setDescription("");
		setAmmunitionType("bolts");
		setAmmoCapacity(1);
		setAmmoRemaining(1);
		minRange=1;
		maxRange=2;
		setRawLogicalAnd(false);
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
