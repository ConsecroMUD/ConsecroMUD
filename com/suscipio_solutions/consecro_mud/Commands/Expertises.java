package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ExpertiseLibrary;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ListingLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;


@SuppressWarnings("rawtypes")
public class Expertises extends StdCommand
{
	public Expertises(){}

	private final String[] access=I(new String[]{"EXPERTISES","EXPS"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final StringBuffer msg=new StringBuffer("");
		msg.append(L("\n\r^HYour expertises:^? \n\r"));
		int col=0;
		final int COL_LEN=ListingLibrary.ColFixer.fixColWidth(25.0,mob);
		final XVector<String> expers=new XVector<String>();
		for(final Enumeration<String> e=mob.expertises();e.hasMoreElements();)
		{
			final String exper=e.nextElement();
			final ExpertiseLibrary.ExpertiseDefinition def=CMLib.expertises().getDefinition(exper);
			if(def==null)
			{
				final Pair<String,Integer> p=mob.fetchExpertise(exper);
				if(p==null)
					expers.add("?"+CMStrings.capitalizeAllFirstLettersAndLower(exper));
				else
				if(p.first.endsWith("%"))
					expers.add("?"+CMStrings.capitalizeAllFirstLettersAndLower(p.first.substring(0,p.first.length()-1))+" ("+p.second.intValue()+"%)");
				else
					expers.add("?"+CMStrings.capitalizeAllFirstLettersAndLower(p.first)+" "+p.second.intValue());
			}
			else
				expers.add(def.name);
		}
		expers.sort();
		for(final String expName : expers)
		{
			if(expName.startsWith("?"))
			{
				msg.append(CMStrings.padRight(expName.substring(1),COL_LEN));
			}
			else
			if(expName.length()>=COL_LEN)
			{
				if(col>=2)
				{
					msg.append("\n\r");
					col=0;
				}
				msg.append(CMStrings.padRightPreserve("^<HELP^>"+expName+"^</HELP^>",COL_LEN));
				final int spaces=(COL_LEN*2)-expName.length();
				for(int i=0;i<spaces;i++) msg.append(" ");
				col++;
			}
			else
				msg.append(CMStrings.padRight("^<HELP^>"+expName+"^</HELP^>",COL_LEN));
			if((++col)>=3)
			{
				msg.append("\n\r");
				col=0;
			}
		}
		if(!msg.toString().endsWith("\n\r")) msg.append("\n\r");
		if(!mob.isMonster())
			mob.session().wraplessPrintln(msg.toString());
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
