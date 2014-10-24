package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ExpertiseLibrary;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ExpertiseLibrary.ExpertiseDefinition;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;


@SuppressWarnings("rawtypes")
public class Teach extends StdCommand
{
	public Teach(){}

	private final String[] access=I(new String[]{"TEACH"});
	@Override public String[] getAccessWords(){return access;}


	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<3)
		{
			mob.tell(L("Teach who what?"));
			return false;
		}
		commands.removeElementAt(0);


		final MOB student=mob.location().fetchInhabitant((String)commands.elementAt(0));
		if((student==null)||(!CMLib.flags().canBeSeenBy(student,mob)))
		{
			mob.tell(L("That person doesn't seem to be here."));
			return false;
		}
		commands.removeElementAt(0);


		final String abilityName=CMParms.combine(commands,0);
		final Ability realAbility=CMClass.findAbility(abilityName,student.charStats());
		Ability myAbility=null;
		if(realAbility!=null)
			myAbility=mob.fetchAbility(realAbility.ID());
		else
			myAbility=mob.findAbility(abilityName);
		if(myAbility==null)
		{
			ExpertiseLibrary.ExpertiseDefinition theExpertise=null;
			final List<ExpertiseDefinition> V=CMLib.expertises().myListableExpertises(mob);
			for(final Enumeration<String> exi=mob.expertises();exi.hasMoreElements();)
			{
				final Pair<String,Integer> e=mob.fetchExpertise(exi.nextElement());
				final List<String> codes = CMLib.expertises().getStageCodes(e.getKey());
				if((codes==null)||(codes.size()==0))
					V.add(CMLib.expertises().getDefinition(e.getKey()));
				else
				for(final String ID : codes)
				{
					final ExpertiseLibrary.ExpertiseDefinition def=CMLib.expertises().getDefinition(ID);
					if((def != null) && (!V.contains(def)))
						V.add(def);
				}
			}
			for(int v=0;v<V.size();v++)
			{
				final ExpertiseLibrary.ExpertiseDefinition def=V.get(v);
				if((def.name.equalsIgnoreCase(abilityName))
				&&(theExpertise==null))
					theExpertise=def;
			}
			if(theExpertise==null)
				for(int v=0;v<V.size();v++)
				{
					final ExpertiseLibrary.ExpertiseDefinition def=V.get(v);
					if((CMLib.english().containsString(def.name,abilityName)
					&&(theExpertise==null)))
						theExpertise=def;
				}
			if(theExpertise!=null)
			{
				return CMLib.expertises().postTeach(mob,student,theExpertise);
			}
			mob.tell(L("You don't seem to know @x1.",abilityName));
			return false;
		}
		return CMLib.expertises().postTeach(mob,student,myAbility);
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
