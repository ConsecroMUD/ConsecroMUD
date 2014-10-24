package com.suscipio_solutions.consecro_mud.Items.BasicTech;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Electronics;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMStrings;


public class GenReflectionShield extends GenPersonalShield
{
	@Override public String ID(){	return "GenReflectionShield";}

	public GenReflectionShield()
	{
		super();
		setName("a reflection shield generator");
		setDisplayText("a reflection shield generator sits here.");
		setDescription("The reflection shield generator is worn about the body and activated to use. It protects against laser type weapons. ");
	}

	@Override
	protected String fieldOnStr(MOB viewerM)
	{
		return (owner() instanceof MOB)?
			"A reflecting field of energy surrounds <O-NAME>.":
			"A reflecting field of energy surrounds <T-NAME>.";
	}

	@Override
	protected String fieldDeadStr(MOB viewerM)
	{
		return (owner() instanceof MOB)?
			"The reflecting field around <O-NAME> flickers and dies out.":
			"The reflecting field around <T-NAME> flickers and dies out.";
	}

	@Override
	protected boolean doShield(MOB mob, CMMsg msg, double successFactor)
	{
		if(mob.location()!=null)
		{
			if(msg.tool() instanceof Weapon)
			{
				final String s="^F"+((Weapon)msg.tool()).hitString(0)+"^N";
				if(s.indexOf("<DAMAGE>")>0)
					mob.location().show(msg.source(),msg.target(),msg.tool(),CMMsg.MSG_OK_VISUAL,CMStrings.replaceAll(s, "<DAMAGE>", "it reflects off the shield around"));
				else
				if(s.indexOf("<DAMAGES>")>0)
					mob.location().show(msg.source(),msg.target(),msg.tool(),CMMsg.MSG_OK_VISUAL,CMStrings.replaceAll(s, "<DAMAGES>", "reflects off the shield around"));
				else
					mob.location().show(mob,msg.source(),msg.tool(),CMMsg.MSG_OK_VISUAL,L("The field around <S-NAME> reflects the <O-NAMENOART> damage."));
			}
			else
				mob.location().show(mob,msg.source(),msg.tool(),CMMsg.MSG_OK_VISUAL,L("The field around <S-NAME> reflects the <O-NAMENOART> damage."));
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
		&& (((Weapon)msg.tool()).weaponType()==Weapon.TYPE_LASERING))
		{
			return true;
		}
		return false;
	}
}
