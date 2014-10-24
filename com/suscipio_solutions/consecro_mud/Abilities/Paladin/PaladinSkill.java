package com.suscipio_solutions.consecro_mud.Abilities.Paladin;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class PaladinSkill extends StdAbility
{
	@Override public String ID() { return "PaladinSkill"; }
	private final static String localizedName = CMLib.lang().L("Paladin Skill");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){return "";}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_OTHERS;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	protected Vector paladinsGroup=null;
	@Override public int classificationCode(){ return Ability.ACODE_SKILL;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if(!(affected instanceof MOB))
			return false;
		if(invoker==null) invoker=(MOB)affected;
		if(!(CMLib.flags().isGood(invoker)))
			return false;
		if(paladinsGroup!=null)
		{
			final Set<MOB> H=((MOB)affected).getGroupMembers(new HashSet<MOB>());
			for (final Object element : H)
			{
				final MOB mob=(MOB)element;
				if(!paladinsGroup.contains(mob))
					paladinsGroup.addElement(mob);
			}
			for(int i=paladinsGroup.size()-1;i>=0;i--)
			{
				try
				{
					final MOB mob=(MOB)paladinsGroup.elementAt(i);
					if((!H.contains(mob))
					||(mob.location()!=invoker.location()))
						paladinsGroup.removeElement(mob);
				}
				catch(final java.lang.ArrayIndexOutOfBoundsException e)
				{
				}
			}
		}
		if(CMLib.dice().rollPercentage()==1)
			helpProficiency(invoker, 0);
		return true;
	}

	@Override
	public boolean autoInvocation(MOB mob)
	{
		if(mob.charStats().getCurrentClass().ID().equals("Immortal"))
			return false;
		return super.autoInvocation(mob);
	}
}
