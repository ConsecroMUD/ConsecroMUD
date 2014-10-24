package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ExpertiseLibrary;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ExpertiseLibrary.ExpertiseDefinition;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;


@SuppressWarnings({"unchecked","rawtypes"})
public class Learn extends StdCommand
{
	public Learn(){}

	private final String[] access=I(new String[]{"LEARN"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.location().numInhabitants()==1)
		{
			mob.tell(L("You will need to find someone to teach you first."));
			return false;
		}
		if(commands.size()==1)
		{
			mob.tell(L("Learn what?  Enter QUALIFY or TRAIN to see what you can learn."));
			return false;
		}
		commands.removeElementAt(0);
		String teacherName="";
		String sayTo="SAY";
		if(commands.size()>1)
		{
			teacherName="\""+((String)commands.lastElement())+"\" ";
			if((teacherName.length()>1)&&(mob.location().fetchFromRoomFavorMOBs(null, (String)commands.lastElement()) instanceof MOB))
			{
				sayTo="SAYTO";
				commands.removeElementAt(commands.size()-1);
				if((commands.size()>1)&&(((String)commands.lastElement()).equalsIgnoreCase("FROM")))
					commands.removeElementAt(commands.size()-1);
			}
			else
				teacherName="";
		}

		final String what=CMParms.combine(commands,0);
		final Vector V=Train.getAllPossibleThingsToTrainFor();
		if(V.contains(what.toUpperCase().trim()))
		{
			final Vector CC=CMParms.parse(sayTo+" "+teacherName+"I would like to be trained in "+what);
			mob.doCommand(CC,metaFlags);
			final Command C=CMClass.getCommand("TRAIN");
			if(C!=null) C.execute(mob, commands,metaFlags);
			return true;
		}
		if(CMClass.findAbility(what, mob)!=null)
		{
			final Vector CC=CMParms.parse(sayTo+" "+teacherName+"I would like you to teach me "+what);
			mob.doCommand(CC,metaFlags);
			return true;
		}
		ExpertiseLibrary.ExpertiseDefinition theExpertise=null;
		final List<ExpertiseDefinition> V2=CMLib.expertises().myListableExpertises(mob);
		for (final ExpertiseDefinition def : V2)
		{
			if((def.name.equalsIgnoreCase(what)
			||def.name.equalsIgnoreCase(what))
			||(def.name.toLowerCase().startsWith((what).toLowerCase())
					&&(CMath.isRomanNumeral(def.name.substring((what).length()).trim())||CMath.isNumber(def.name.substring((what).length()).trim())))
			)
			{ theExpertise=def; break;}
		}
		if(theExpertise==null)
		for(final Enumeration<ExpertiseDefinition> e=CMLib.expertises().definitions();e.hasMoreElements();)
		{
			final ExpertiseLibrary.ExpertiseDefinition def=e.nextElement();
			if(def.name.equalsIgnoreCase(what))
			{ theExpertise=def; break;}
		}
		if(theExpertise!=null)
		{
			final Vector CC=new XVector("SAY","I would like you to teach me "+theExpertise.name);
			mob.doCommand(CC,metaFlags);
			return true;
		}

		for(int v=0;v<V.size();v++)
			if(((String)V.elementAt(v)).startsWith(what.toUpperCase().trim()))
			{
				final Vector CC=CMParms.parse(sayTo+" "+teacherName+"I would like to be trained in "+what);
				mob.doCommand(CC,metaFlags);
				final Command C=CMClass.getCommand("TRAIN");
				if(C!=null) C.execute(mob, commands,metaFlags);
				return true;

			}
		final Vector CC=CMParms.parse(sayTo+" "+teacherName+"I would like you to teach me "+what);
		mob.doCommand(CC,metaFlags);
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return false;}


}
