package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;


@SuppressWarnings("rawtypes")
public class Dig extends StdCommand
{
	public Dig(){}

	private final String[] access=I(new String[]{"DIG"});
	@Override public String[] getAccessWords(){return access;}

	public int getDiggingDepth(Item item)
	{
		if(item==null) return 1;
		switch(item.material()&RawMaterial.MATERIAL_MASK)
		{
		case RawMaterial.MATERIAL_METAL:
		case RawMaterial.MATERIAL_MITHRIL:
		case RawMaterial.MATERIAL_WOODEN:
			if(item.Name().toLowerCase().indexOf("shovel")>=0)
				return 5+item.phyStats().weight();
			return 1+(item.phyStats().weight()/5);
		case RawMaterial.MATERIAL_SYNTHETIC:
		case RawMaterial.MATERIAL_ROCK:
		case RawMaterial.MATERIAL_GLASS:
			if(item.Name().toLowerCase().indexOf("shovel")>=0)
				return 14+item.phyStats().weight();
			return 1+(item.phyStats().weight()/7);
		default:
			return 1;
		}
	}

	public boolean isOccupiedWithOtherWork(MOB mob)
	{
		if(mob==null) return false;
		for(final Enumeration<Ability> a=mob.effects();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A!=null)
			&&(!A.isAutoInvoked())
			&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_COMMON_SKILL))
				return true;
		}
		return false;
	}

	@Override
	public boolean preExecute(MOB mob, Vector commands, int metaFlags, int secondsElapsed, double actionsRemaining)
	throws java.io.IOException
	{
		if(secondsElapsed==0)
		{
			if(isOccupiedWithOtherWork(mob))
			{
				mob.tell(L("You are too busy to dig right now."));
				return false;
			}

			final String msgStr=L("<S-NAME> start(s) digging a hole with <O-NAME>.");
			Item I=mob.fetchWieldedItem();
			if(I==null)  I=mob.myNaturalWeapon();
			final CMMsg msg=CMClass.getMsg(mob,mob.location(),I,CMMsg.MSG_DIG,msgStr);
			msg.setValue(1);
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
			else
				return false;
		}
		else
		if((secondsElapsed % 8)==0)
		{
			final String msgStr=L("<S-NAME> continue(s) digging a hole with <O-NAME>.");
			Item I=mob.fetchWieldedItem();
			if(I==null)  I=mob.myNaturalWeapon();
			final CMMsg msg=CMClass.getMsg(mob,mob.location(),I,CMMsg.MSG_DIG,msgStr);
			msg.setValue(getDiggingDepth(I));
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
			else
				return false;
		}
		return true;
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final CMMsg msg=CMClass.getMsg(mob,null,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> stop(s) digging."));
		if(mob.location().okMessage(mob,msg))
			mob.location().send(mob,msg);
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return 30.0 * mob.phyStats().speed();}
	@Override
	public double actionsCost(final MOB mob, final List<String> cmds)
	{
		return 10.0 * mob.phyStats().speed();
	}
	@Override public boolean canBeOrdered(){return true;}
}
