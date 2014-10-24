package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_UseSpellCast2 extends Prop_UseSpellCast
{
	@Override public String ID() { return "Prop_UseSpellCast2"; }
	@Override public String name(){ return "Casting spells when used";}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(processing) return;
		processing=true;

		if(affected==null) return;
		final Item myItem=(Item)affected;
		if(myItem.owner()==null) return;
		switch(msg.sourceMinor())
		{
		case CMMsg.TYP_DRINK:
			if((myItem instanceof Drink)
			&&(msg.amITarget(myItem)))
				addMeIfNeccessary(msg.source(),msg.source(),0,maxTicks);
			break;
		case CMMsg.TYP_POUR:
			if((myItem instanceof Drink)
			&&(msg.tool()==myItem)
			&&(msg.target() instanceof Physical))
				addMeIfNeccessary(msg.source(),(Physical)msg.target(),0,maxTicks);
			break;
		case CMMsg.TYP_EAT:
			if((myItem instanceof Food)
			&&(msg.amITarget(myItem)))
				addMeIfNeccessary(msg.source(),msg.source(),0,maxTicks);
			break;
		case CMMsg.TYP_GET:
			if((!(myItem instanceof Drink))
			  &&(!(myItem instanceof Food))
			  &&(msg.amITarget(myItem)))
				addMeIfNeccessary(msg.source(),msg.source(),0,maxTicks);
			break;
		}
		processing=false;
	}
}
