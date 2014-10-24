package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings({"unchecked","rawtypes"})
public class Cloak extends StdCommand
{
	public Cloak(){}

	private final String[] access=I(new String[]{"CLOAK"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		String str=(String)commands.firstElement();
		if(Character.toUpperCase(str.charAt(0))!='C')
			commands.insertElementAt("OFF",1);
		commands.removeElementAt(0);
		final int abilityCode=PhyStats.IS_CLOAKED;
		str=L("Prop_WizInvis");
		Ability A=mob.fetchEffect(str);
		if(CMParms.combine(commands,0).trim().equalsIgnoreCase("OFF"))
		{
		   if(A!=null)
			   A.unInvoke();
		   else
			   mob.tell(L("You are not cloaked!"));
		   return false;
		}
		else
		if(A!=null)
		{
			if(CMath.bset(A.abilityCode(),abilityCode)&&(!CMath.bset(A.abilityCode(),PhyStats.IS_NOT_SEEN)))
			{
				mob.tell(L("You are already cloaked!"));
				return false;
			}
		}

		// it worked, so build a copy of this ability,
		// and add it to the affects list of the
		// affected MOB.  Then tell everyone else
		// what happened.
		if(A==null)
			A=CMClass.getAbility(str);
		if(A!=null)
		{
			mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> become(s) cloaked!"));
			if(mob.fetchEffect(A.ID())==null)
				mob.addPriorityEffect((Ability)A.copyOf());
			A=mob.fetchEffect(A.ID());
			if(A!=null) A.setAbilityCode(abilityCode);

			mob.recoverPhyStats();
			mob.location().recoverRoomStats();
			mob.tell(L("You may uninvoke CLOAK with 'CLOAK OFF' or 'WIZINV OFF'."));
			return false;
		}
		mob.tell(L("Cloaking is not available!"));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CLOAK);}


}
