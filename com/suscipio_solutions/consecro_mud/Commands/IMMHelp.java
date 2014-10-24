package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.Resources;

@SuppressWarnings({"unchecked","rawtypes"})
public class IMMHelp extends StdCommand
{
	public IMMHelp(){}

	private final String[] access=I(new String[]{"IMMHELP"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final String helpStr=CMParms.combine(commands,1);
		if(CMLib.help().getImmHelpFile().size()==0)
		{
			mob.tell(L("No immortal help is available."));
			return false;
		}
		StringBuffer thisTag=null;
		if(helpStr.length()==0)
		{
			thisTag=Resources.getFileResource("help/imm_help.txt",true);
			if((thisTag!=null)&&(helpStr.equalsIgnoreCase("more")))
			{
				StringBuffer theRest=(StringBuffer)Resources.getResource("imm_help.therest");
				if(theRest==null)
				{
					final Vector V=new Vector();
					theRest=new StringBuffer("");

					for(final Enumeration<Ability> a=CMClass.abilities();a.hasMoreElements();)
					{
						final Ability A=a.nextElement();
						if((A!=null)&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_PROPERTY))
							V.addElement(A.ID());
					}
					if(V.size()>0)
					{
						theRest.append("\n\rProperties:\n\r");
						theRest.append(CMLib.lister().fourColumns(mob,V));
					}

					V.clear();
					for(final Enumeration<Ability> a=CMClass.abilities();a.hasMoreElements();)
					{
						final Ability A=a.nextElement();
						if((A!=null)&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_DISEASE))
							V.addElement(A.ID());
					}
					if(V.size()>0)
					{
						theRest.append("\n\rDiseases:\n\r");
						theRest.append(CMLib.lister().fourColumns(mob,V));
					}

					V.clear();
					for(final Enumeration<Ability> a=CMClass.abilities();a.hasMoreElements();)
					{
						final Ability A=a.nextElement();
						if((A!=null)&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_POISON))
							V.addElement(A.ID());
					}
					if(V.size()>0)
					{
						theRest.append("\n\rPoisons:\n\r");
						theRest.append(CMLib.lister().fourColumns(mob,V));
					}

					V.clear();
					for(final Enumeration<Ability> a=CMClass.abilities();a.hasMoreElements();)
					{
						final Ability A=a.nextElement();
						if((A!=null)&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SUPERPOWER))
							V.addElement(A.ID());
					}
					if(V.size()>0)
					{
						theRest.append("\n\rSuper Powers:\n\r");
						theRest.append(CMLib.lister().fourColumns(mob,V));
					}

					V.clear();
					for(final Enumeration<Ability> a=CMClass.abilities();a.hasMoreElements();)
					{
						final Ability A=a.nextElement();
						if((A!=null)&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_TECH))
							V.addElement(A.ID());
					}
					if(V.size()>0)
					{
						theRest.append("\n\rTech Skills:\n\r");
						theRest.append(CMLib.lister().fourColumns(mob,V));
					}

					V.clear();
					for(final Enumeration b=CMClass.behaviors();b.hasMoreElements();)
					{
						final Behavior B=(Behavior)b.nextElement();
						if(B!=null) V.addElement(B.ID());
					}
					if(V.size()>0)
					{
						theRest.append("\n\r\n\rBehaviors:\n\r");
						theRest.append(CMLib.lister().fourColumns(mob,V));
					}
					Resources.submitResource("imm_help.therest",theRest);
				}
				thisTag=new StringBuffer(thisTag.toString());
				thisTag.append(theRest);
			}
		}
		else
		{
			final StringBuilder text = CMLib.help().getHelpText(helpStr,CMLib.help().getImmHelpFile(),mob);
			if(text != null)
				thisTag=new StringBuffer(text.toString());
		}
		if(thisTag==null)
		{
			mob.tell(L("No immortal help is available on @x1 .\n\rEnter 'COMMANDS' for a command list, or 'TOPICS' for a complete list.",helpStr));
			Log.errOut("Help: "+mob.Name()+" wanted immortal help on "+helpStr);
		}
		else
		if(!mob.isMonster())
			mob.session().wraplessPrintln(thisTag.toString());
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.IMMHELP);}


}
