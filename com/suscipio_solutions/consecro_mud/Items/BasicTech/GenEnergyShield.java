package com.suscipio_solutions.consecro_mud.Items.BasicTech;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Electronics;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMStrings;


public class GenEnergyShield extends GenPersonalShield
{
	@Override public String ID(){	return "GenEnergyShield";}

	public GenEnergyShield()
	{
		super();
		setName("an energy shield generator");
		setDisplayText("an energy shield generator sits here.");
		setDescription("The energy shield generator is worn about the body and activated to use. It protects against standard blaster and energy fire. ");
	}

	@Override
	protected String fieldOnStr(MOB viewerM)
	{
		return (owner() instanceof MOB)?
			"An energy field surrounds <O-NAME>.":
			"An energy field surrounds <T-NAME>.";
	}

	@Override
	protected String fieldDeadStr(MOB viewerM)
	{
		return (owner() instanceof MOB)?
			"The energy field around <O-NAME> flickers and dies out.":
			"The energy field around <T-NAME> flickers and dies out.";
	}

	@Override
	protected boolean doShield(MOB mob, CMMsg msg, double successFactor)
	{
		if(mob.location()!=null)
		{
			if(msg.tool() instanceof Weapon)
			{
				final String s="^F"+((Weapon)msg.tool()).hitString(0)+"^N";
				if(s.indexOf("<DAMAGE> <T-HIM-HER>")>0)
					mob.location().show(msg.source(),msg.target(),msg.tool(),CMMsg.MSG_OK_VISUAL,CMStrings.replaceAll(s, "<DAMAGE>", "it is absorbed by the shield around"));
				else
				if(s.indexOf("<DAMAGES> <T-HIM-HER>")>0)
					mob.location().show(msg.source(),msg.target(),msg.tool(),CMMsg.MSG_OK_VISUAL,CMStrings.replaceAll(s, "<DAMAGES>", "is absorbed by the shield around"));
				else
					mob.location().show(mob,msg.source(),msg.tool(),CMMsg.MSG_OK_VISUAL,L("The field around <S-NAME> absorbs the <O-NAMENOART> damage."));
			}
			else
				mob.location().show(mob,msg.source(),msg.tool(),CMMsg.MSG_OK_VISUAL,L("The field around <S-NAME> absorbs the <O-NAMENOART> damage."));
		}
		return false;
	}

	@Override
	protected boolean doesShield(MOB mob, CMMsg msg, double successFactor)
	{
		if(!activated())
			return false;
		if((msg.tool() instanceof Electronics)
		&& (msg.tool() instanceof Weapon)
		&& (Math.random() >= successFactor)
		&& (((Weapon)msg.tool()).weaponType()==Weapon.TYPE_SHOOT))
		{
			return true;
		}
		return false;
	}
}
