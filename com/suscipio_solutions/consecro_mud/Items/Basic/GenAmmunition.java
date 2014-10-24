package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Ammunition;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class GenAmmunition extends StdItem implements Ammunition
{
	@Override public String ID(){	return "GenAmmunition";}
	protected String	readableText="";
	public GenAmmunition()
	{
		super();

		setName("a batch of arrows");
		setDisplayText("a generic batch of arrows sits here.");
		setUsesRemaining(100);
		setAmmunitionType("arrows");
		setDescription("");
		recoverPhyStats();
	}

	@Override public boolean subjectToWearAndTear() { return false; }
	
	@Override public boolean isGeneric(){return true;}

	@Override
	public String text()
	{
		return CMLib.coffeeMaker().getPropertiesStr(this,false);
	}
	@Override public String readableText(){return readableText;}
	@Override
	public void setReadableText(String text)
	{
		if(isReadable()) CMLib.flags().setReadable(this,false);
		readableText=text;
	}
	@Override public String ammunitionType(){return readableText;}
	@Override public void setAmmunitionType(String text){readableText=text;}

	@Override
	public void setMiscText(String newText)
	{
		miscText="";
		CMLib.coffeeMaker().setPropertiesStr(this,newText,false);
		recoverPhyStats();
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		final MOB mob=msg.source();
		if(!msg.amITarget(this))
			return super.okMessage(myHost,msg);
		else
		if(msg.targetMinor()==CMMsg.NO_EFFECT)
			return super.okMessage(myHost,msg);
		else
		switch(msg.targetMinor())
		{
		case CMMsg.TYP_HOLD:
			mob.tell(L("You can't hold @x1.",name()));
			return false;
		case CMMsg.TYP_WEAR:
			mob.tell(L("You can't wear @x1.",name()));
			return false;
		case CMMsg.TYP_WIELD:
			mob.tell(L("You can't wield @x1 as a weapon.",name()));
			return false;
		}
		return super.okMessage(myHost,msg);
	}
}
