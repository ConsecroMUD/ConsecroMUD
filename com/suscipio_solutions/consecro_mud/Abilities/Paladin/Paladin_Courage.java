package com.suscipio_solutions.consecro_mud.Abilities.Paladin;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



@SuppressWarnings("rawtypes")
public class Paladin_Courage extends PaladinSkill
{
	@Override public String ID() { return "Paladin_Courage"; }
	private final static String localizedName = CMLib.lang().L("Paladin`s Courage");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_HOLYPROTECTION;}
	public Paladin_Courage()
	{
		super();
		paladinsGroup=new Vector();
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		if((invoker==null)||(!(CMLib.flags().isGood(invoker))))
			return true;
		if(affected==null) return true;
		if(!(affected instanceof MOB)) return true;

		if((msg.target()!=null)
		   &&(paladinsGroup.contains(msg.target()))
		   &&(!paladinsGroup.contains(msg.source()))
		   &&(msg.target() instanceof MOB)
		   &&(msg.source()!=invoker))
		{
			if((CMLib.flags().isGood(invoker))
			&&(msg.tool()!=null)
			&&(msg.tool() instanceof Ability)
			&&((invoker==null)||(invoker.fetchAbility(ID())==null)||proficiencyCheck(null,0,false)))
			{
				final String str1=msg.tool().ID().toUpperCase();
				if((str1.indexOf("SPOOK")>=0)
				||(str1.indexOf("NIGHTMARE")>=0)
				||(str1.indexOf("FEAR")>=0))
				{
					final MOB mob=(MOB)msg.target();
					mob.location().showSource(mob,null,CMMsg.MSG_OK_VISUAL,L("Your courage protects you from the @x1 attack.",msg.tool().name()));
					mob.location().showOthers(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME>'s courage protects <S-HIM-HER> from the @x1 attack.",msg.tool().name()));
					return false;
				}
			}
		}
		return true;
	}
}
