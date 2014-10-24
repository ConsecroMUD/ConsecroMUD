package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_TattooAdder extends Property
{
	@Override public String ID() { return "Prop_TattooAdder"; }
	@Override public String name(){ return "A TattooAdder";}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS|Ability.CAN_ROOMS|Ability.CAN_AREAS|Ability.CAN_MOBS|Ability.CAN_EXITS;}
	int tattooCode=-1;

	public int tattooCode()
	{
		if(tattooCode>=0) return tattooCode;
		if(affected==null) return -1;
		if(affected instanceof Drink)
			tattooCode= CMMsg.TYP_DRINK;
		else
		if(affected instanceof Food)
			tattooCode= CMMsg.TYP_EAT;
		else
		if(affected instanceof MOB)
			tattooCode= CMMsg.TYP_DEATH;
		else
		if(affected instanceof Weapon)
			tattooCode= CMMsg.TYP_WEAPONATTACK;
		else
		if(affected instanceof Armor)
			tattooCode= CMMsg.TYP_WEAR;
		else
		if(affected instanceof Item)
			tattooCode= CMMsg.TYP_GET;
		else
		if(affected instanceof Room)
			tattooCode= CMMsg.TYP_ENTER;
		else
		if(affected instanceof Area)
			tattooCode= CMMsg.TYP_ENTER;
		else
		if(affected instanceof Exit)
			tattooCode= CMMsg.TYP_ENTER;
		return tattooCode;
	}

	public void applyTattooCodes(MOB mob, boolean addOnly, boolean subOnly)
	{
		String tattooName=text();
		if(tattooName.length()==0) return;

		boolean tattooPlus=true;
		boolean tattooMinus=false;


		if(tattooName.startsWith("+-")||tattooName.startsWith("-+"))
		{
			tattooMinus=true;
			tattooName=tattooName.substring(2);
		}
		else
		if(tattooName.startsWith("+"))
			tattooName=tattooName.substring(1);
		else
		if(tattooName.startsWith("-"))
		{
			tattooPlus=false;
			tattooMinus=true;
			tattooName=tattooName.substring(1);
		}

		final boolean silent=tattooName.startsWith("~");
		if(silent) tattooName=tattooName.substring(1);

		if(addOnly) tattooMinus=false;
		if(subOnly) tattooPlus=false;

		final MOB.Tattoo pT=CMLib.database().parseTattoo(tattooName);
		final MOB.Tattoo T = mob.findTattoo(pT.tattooName);
		if(T!=null)
		{
			if(tattooMinus)
			{
				if(!silent)
					mob.location().show(mob,affected,CMMsg.MSG_OK_ACTION,L("<T-NAME> takes away the @x1 tattoo from <S-NAME>.",pT.tattooName.toLowerCase()));
				mob.delTattoo(T);
			}
		}
		else
		{
			if(tattooPlus)
			{
				if(!silent)
					mob.location().show(mob,affected,CMMsg.MSG_OK_ACTION,L("<T-NAME> gives <S-NAME> the @x1 tattoo.",pT.tattooName.toLowerCase()));
				mob.addTattoo(pT);
			}
		}
	}


	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((tattooCode()==CMMsg.TYP_DEATH)&&(msg.sourceMinor()==tattooCode()))
		{
			if((msg.tool()==affected)&&(msg.source()!=affected))
				applyTattooCodes(msg.source(),false,true);
			else
			if((msg.source()==affected)
			&&(msg.tool() instanceof MOB)
			&&(msg.tool()!=affected))
				applyTattooCodes((MOB)msg.tool(),true,false);
		}
		else
		if(((msg.targetMinor()==tattooCode())||(msg.sourceMinor()==tattooCode()))
		&&(tattooCode()>=0)
		&&(msg.amITarget(affected)||(msg.tool()==affected)))
			applyTattooCodes(msg.source(),false,false);
		super.executeMsg(myHost,msg);
	}
}
