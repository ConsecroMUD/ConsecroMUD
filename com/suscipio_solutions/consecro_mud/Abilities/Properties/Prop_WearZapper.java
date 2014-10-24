package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_WearZapper extends Prop_HaveZapper
{
	@Override public String ID() { return "Prop_WearZapper"; }
	@Override public String name(){ return "Restrictions to wielding/wearing/holding";}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}

	@Override
	public String accountForYourself()
	{
		return "Wearing restricted as follows: "+CMLib.masking().maskDesc(miscText);
	}

	@Override
	public int triggerMask()
	{
		return TriggeredAffect.TRIGGER_WEAR_WIELD;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(affected==null) return super.okMessage(myHost, msg);
		if(!(affected instanceof Item)) return super.okMessage(myHost, msg);
		final Item myItem=(Item)affected;

		final MOB mob=msg.source();
		if(mob.location()==null)
			return true;

		if(msg.amITarget(myItem))
		switch(msg.targetMinor())
		{
		case CMMsg.TYP_HOLD:
			if((!CMLib.masking().maskCheck(text(),mob,actual))&&(CMLib.dice().rollPercentage()<=percent))
			{
				mob.location().show(mob,null,myItem,CMMsg.MSG_OK_VISUAL,msgStr);
				return false;
			}
			break;
		case CMMsg.TYP_WEAR:
			if((!CMLib.masking().maskCheck(text(),mob,actual))&&(CMLib.dice().rollPercentage()<=percent))
			{
				mob.location().show(mob,null,myItem,CMMsg.MSG_OK_VISUAL,msgStr);
				return false;
			}
			break;
		case CMMsg.TYP_WIELD:
			if((!CMLib.masking().maskCheck(text(),mob,actual))&&(CMLib.dice().rollPercentage()<=percent))
			{
				mob.location().show(mob,null,myItem,CMMsg.MSG_OK_VISUAL,msgStr);
				return false;
			}
			break;
		case CMMsg.TYP_GET:
			break;
		default:
			break;
		}
		return true;
	}
}
