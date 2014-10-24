package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session.InputCallback;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;



@SuppressWarnings("rawtypes")
public class AutoInvoke extends StdCommand
{
	public AutoInvoke(){}

	private final String[] access=I(new String[]{"AUTOINVOKE"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(final MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final Vector<String> abilities=new Vector<String>();
		for(int a=0;a<mob.numAbilities();a++)
		{
			final Ability A=mob.fetchAbility(a);
			if((A!=null)
			&&(A.isAutoInvoked())
			&&((A.classificationCode()&Ability.ALL_ACODES)!=Ability.ACODE_LANGUAGE)
			&&((A.classificationCode()&Ability.ALL_ACODES)!=Ability.ACODE_PROPERTY))
				abilities.addElement(A.ID());
		}

		final Vector<String> effects=new Vector<String>();
		for(int a=0;a<mob.numEffects();a++)
		{
			final Ability A=mob.fetchEffect(a);
			if((A!=null)
			&&(abilities.contains(A.ID()))
			&&(!A.isSavable()))
				effects.addElement(A.ID());
		}

		final StringBuffer str=new StringBuffer(L("^xAuto-invoking abilities:^?^.\n\r^N"));
		int col=0;
		for(int a=0;a<abilities.size();a++)
		{
			final Ability A=mob.fetchAbility(abilities.elementAt(a));
			if(A!=null)
			{
				if(effects.contains(A.ID()))
					str.append(L("@x1.^xACTIVE^?^.^N ",CMStrings.padRightWith(A.Name(),'.',30)));
				else
					str.append(L("@x1^xINACTIVE^?^.^N",CMStrings.padRightWith(A.Name(),'.',30)));
				if(++col==2)
				{
					col=0;
					str.append("\n\r");
				}
				else
					str.append("  ");
			}
		}
		if(col==1)
			str.append("\n\r");

		mob.tell(str.toString());
		final Session session=mob.session();
		if(session!=null)
		{
			session.prompt(new InputCallback(InputCallback.Type.PROMPT,"",0)
			{
				@Override public void showPrompt() { session.promptPrint(L("Enter one to toggle or RETURN: "));}
				@Override public void timedOut() { }
				@Override public void callBack()
				{
					final String s=this.input;
					Ability foundA=null;
					if(s.length()>0)
					{
						for(int a=0;a<abilities.size();a++)
						{
							final Ability A=mob.fetchAbility(abilities.elementAt(a));
							if((A!=null)&&(A.name().equalsIgnoreCase(s)))
							{ foundA=A; break;}
						}
						if(foundA==null)
						for(int a=0;a<abilities.size();a++)
						{
							final Ability A=mob.fetchAbility(abilities.elementAt(a));
							if((A!=null)&&(CMLib.english().containsString(A.name(),s)))
							{ foundA=A; break;}
						}
						if(foundA==null)
							mob.tell(L("'@x1' is invalid.",s));
						else
						if(effects.contains(foundA.ID()))
						{
							foundA=mob.fetchEffect(foundA.ID());
							if(foundA!=null)
							{
								mob.delEffect(foundA);
								if(mob.fetchEffect(foundA.ID())!=null)
									mob.tell(L("@x1 failed to successfully deactivate.",foundA.name()));
								else
									mob.tell(L("@x1 successfully deactivated.",foundA.name()));
							}
						}
						else
						{
							foundA.autoInvocation(mob);
							if(mob.fetchEffect(foundA.ID())!=null)
								mob.tell(L("@x1 successfully invoked.",foundA.name()));
							else
								mob.tell(L("@x1 failed to successfully invoke.",foundA.name()));
						}
					}
				}
			});
		}
		return false;
	}


	@Override public boolean canBeOrdered(){return true;}

}
