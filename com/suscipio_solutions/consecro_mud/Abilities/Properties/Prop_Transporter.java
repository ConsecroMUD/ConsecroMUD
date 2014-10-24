package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class Prop_Transporter extends Property implements TriggeredAffect
{
	@Override public String ID() { return "Prop_Transporter"; }
	@Override public String name(){ return "Room entering adjuster";}
	@Override protected int canAffectCode(){return Ability.CAN_EXITS|Ability.CAN_ROOMS;}
	int transCode=-1;

	@Override
	public String accountForYourself()
	{ return "Zap them elsewhere";	}

	@Override
	public int triggerMask()
	{
		return TriggeredAffect.TRIGGER_DROP_PUTIN;
	}

	public int transCode()
	{
		if(transCode>=0) return transCode;
		if(affected==null) return -1;
		if(affected instanceof Drink)
			transCode= CMMsg.TYP_DRINK;
		else
		if(affected instanceof Food)
			transCode= CMMsg.TYP_EAT;
		else
		if(affected instanceof Rideable)
		{
			transCode= CMMsg.TYP_MOUNT;
			switch(((Rideable)affected).rideBasis())
			{
			case Rideable.RIDEABLE_ENTERIN:
				transCode= CMMsg.TYP_ENTER; break;
			case Rideable.RIDEABLE_SIT:
			case Rideable.RIDEABLE_TABLE:
				transCode= CMMsg.TYP_SIT; break;
			case Rideable.RIDEABLE_SLEEP:
				transCode= CMMsg.TYP_SLEEP; break;
			}
		}
		else
		if(affected instanceof MOB)
			transCode= CMMsg.TYP_SPEAK;
		else
		if(affected instanceof Weapon)
			transCode= CMMsg.TYP_WEAPONATTACK;
		else
		if(affected instanceof Armor)
			transCode= CMMsg.TYP_WEAR;
		else
		if(affected instanceof Item)
			transCode= CMMsg.TYP_GET;
		else
		if(affected instanceof Room)
			transCode= CMMsg.TYP_ENTER;
		else
		if(affected instanceof Area)
			transCode= CMMsg.TYP_ENTER;
		else
		if(affected instanceof Exit)
			transCode= CMMsg.TYP_ENTER;
		return transCode;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((transCode()>=0)
		   &&((msg.targetMinor()==transCode())||(msg.sourceMinor()==transCode()))
		   &&(msg.amITarget(affected)||(msg.tool()==affected))
		   &&(text().length()>0))
		{
			final Room prevRoom=msg.source().location();
			final Room otherRoom=CMLib.map().getRoom(text());
			if(otherRoom==null)
				msg.source().tell(L("You are whisked nowhere at all, since '@x1' is nowhere to be found.",text()));
			else
			if(prevRoom!=otherRoom)
			{
				otherRoom.bringMobHere(msg.source(),true);
				CMLib.commands().postLook(msg.source(),true);
				if(affected instanceof Rideable)
					msg.addTrailerMsg(CMClass.getMsg(msg.source(),affected,CMMsg.TYP_DISMOUNT,null));
				if((affected instanceof Item)&&(prevRoom.isContent((Item)affected)))
					prevRoom.delItem((Item)affected);
			}

		}
		super.executeMsg(myHost,msg);
	}
}
