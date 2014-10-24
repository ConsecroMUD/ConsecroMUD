package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class GenNet extends StdNet
{
	@Override public String ID(){	return "GenNet";}
	protected String	readableText="";
	public GenNet()
	{
		super();

		setName("a generic net");
		setDisplayText("a generic net sits here.");
		setDescription("");
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
