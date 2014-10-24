package com.suscipio_solutions.consecro_mud.Items.Weapons;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class GenLasso extends StdLasso
{
	@Override public String ID(){	return "GenLasso";}
	protected String	readableText="";
	public GenLasso()
	{
		super();

		setName("a generic lasso");
		setDisplayText("a generic lasso sits here.");
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

