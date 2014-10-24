package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_WearResister extends Prop_HaveResister
{
	@Override public String ID() { return "Prop_WearResister"; }
	@Override public String name(){ return "Resistance due to worn";}

	@Override
	public String accountForYourself()
	{ return "The wearer gains resistances: "+describeResistance(text());}

	public boolean checked=false;
	public boolean disabled=false;
	public boolean layered=false;

	@Override
	public int triggerMask()
	{
		return TriggeredAffect.TRIGGER_WEAR_WIELD;
	}

	public void check(MOB mob, Armor A)
	{
		if(!layered){ checked=true; disabled=false;}
		if(A.amWearingAt(Wearable.IN_INVENTORY))
		{
			checked=false;
			return;
		}
		if(checked) return;
		Item I=null;
		disabled=false;
		for(int i=0;i<mob.numItems();i++)
		{
			I=mob.getItem(i);
			if((I instanceof Armor)
			&&(!I.amWearingAt(Wearable.IN_INVENTORY))
			&&((I.rawWornCode()&A.rawWornCode())>0)
			&&(I!=A))
			{
				disabled=A.getClothingLayer()<=((Armor)I).getClothingLayer();
				if(disabled)
				{
					break;
				}
			}
		}
		checked=true;
	}

	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		layered=CMParms.parseSemicolons(newText.toUpperCase(),true).indexOf("LAYERED")>=0;
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		if((affected instanceof Armor)&&(msg.source()==((Armor)affected).owner()))
		{
			if((msg.targetMinor()==CMMsg.TYP_REMOVE)
			||(msg.sourceMinor()==CMMsg.TYP_WEAR)
			||(msg.sourceMinor()==CMMsg.TYP_WIELD)
			||(msg.sourceMinor()==CMMsg.TYP_HOLD)
			||(msg.sourceMinor()==CMMsg.TYP_DROP))
				checked=false;
			else
			{
				check(msg.source(),(Armor)affected);
				super.executeMsg(host,msg);
			}
		}
		else
			super.executeMsg(host,msg);
	}

	@Override
	public boolean canResist(Environmental E)
	{
		if(!super.canResist(E))
			return false;
		if(disabled&&checked) return false;
		if((!((Item)affected).amWearingAt(Wearable.IN_INVENTORY))
		&&((!((Item)affected).amWearingAt(Wearable.WORN_FLOATING_NEARBY))||(((Item)affected).fitsOn(Wearable.WORN_FLOATING_NEARBY))))
			return true;
		return false;
	}

}
