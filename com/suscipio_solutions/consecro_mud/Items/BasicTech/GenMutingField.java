package com.suscipio_solutions.consecro_mud.Items.BasicTech;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Electronics;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMStrings;


public class GenMutingField extends GenPersonalShield
{
	@Override public String ID(){	return "GenMutingField";}

	public GenMutingField()
	{
		super();
		setName("a muting field generator");
		setDisplayText("a muting field generator sits here.");
		setDescription("The muting field generator is worn about the body and activated to use. It neutralizes sonic and stunning weapon damage. ");
	}

	@Override
	protected String fieldOnStr(MOB viewerM)
	{
		return (owner() instanceof MOB)?
			"An dense field surrounds <O-NAME>.":
			"An dense field surrounds <T-NAME>.";
	}

	@Override
	protected String fieldDeadStr(MOB viewerM)
	{
		return (owner() instanceof MOB)?
			"The dense field around <O-NAME> flickers and dies out.":
			"The dense field around <T-NAME> flickers and dies out.";
	}

	@Override
	protected boolean doShield(MOB mob, CMMsg msg, double successFactor)
	{
		mob.phyStats().setSensesMask(mob.phyStats().sensesMask()|PhyStats.CAN_NOT_HEAR);
		if(mob.location()!=null)
		{
			if(msg.tool() instanceof Weapon)
			{
				final String s="^F"+((Weapon)msg.tool()).hitString(0)+"^N";
				if(s.indexOf("<DAMAGE> <T-HIM-HER>")>0)
					mob.location().show(msg.source(),msg.target(),msg.tool(),CMMsg.MSG_OK_VISUAL,CMStrings.replaceAll(s, "<DAMAGE>", "it`s absorbed by the shield around"));
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
		&& (((Weapon)msg.tool()).weaponType()==Weapon.TYPE_SONICING))
		{
			return true;
		}
		return false;
	}
}
