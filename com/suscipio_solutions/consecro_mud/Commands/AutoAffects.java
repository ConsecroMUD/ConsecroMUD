package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ListingLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class AutoAffects extends StdCommand
{
	private final String[] access=I(new String[]{"AUTOAFFECTS","AUTOAFF","AAF"});
	@Override public String[] getAccessWords(){return access;}

	public String getAutoAffects(MOB viewerMOB, Physical P)
	{
		final StringBuffer msg=new StringBuffer("");
		final int NUM_COLS=2;
		final int COL_LEN=ListingLibrary.ColFixer.fixColWidth(25.0,viewerMOB);
		int colnum=NUM_COLS;
		for(final Enumeration<Ability> a=P.effects();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if(A==null) continue;
			final String disp=A.name();
			if((A.displayText().length()==0)
			&&((!(P instanceof MOB))||(((MOB)P).fetchAbility(A.ID())!=null))
			&&(A.isAutoInvoked()))
			{
				if(((++colnum)>NUM_COLS)||(disp.length()>COL_LEN)){ msg.append("\n\r"); colnum=0;}
				msg.append("^S"+CMStrings.padRightPreserve("^<HELPNAME NAME='"+A.Name()+"'^>"+disp+"^</HELPNAME^>",COL_LEN));
				if(disp.length()>COL_LEN) colnum=99;
			}
		}
		msg.append("^N\n\r");
		return msg.toString();
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		Session S=mob.session();
		if((commands!=null)&&(commands.size()>0)&&(!(commands.firstElement() instanceof String)))
		{
			if(commands.firstElement() instanceof MOB)
				S=((MOB)commands.firstElement()).session();
			else
			if(commands.firstElement() instanceof StringBuffer)
			{
				((StringBuffer)commands.firstElement()).append(getAutoAffects(mob,mob));
				return false;
			}
			else
			if(commands.firstElement() instanceof List)
			{
				((List)commands.firstElement()).add(getAutoAffects(mob,mob));
				return false;
			}
			else
			{
				commands.clear();
				commands.addElement(getAutoAffects(mob,mob));
				return false;
			}
		}

		if(S!=null)
		{
			if(CMSecurity.isAllowed(mob, mob.location(),CMSecurity.SecFlag.CMDMOBS))
			{
				final String name=CMParms.combine(commands,1);
				if(name.length()>0)
				{
					final Physical P=mob.location().fetchFromMOBRoomFavorsItems(mob,null,name,Wearable.FILTER_ANY);
					if(P==null)
						S.colorOnlyPrint(L("You don't see @x1 here.",name));
					else
					{
						if(S==mob.session())
							S.colorOnlyPrint(L(" \n\r^!@x1 is affected by: ^?",P.name()));
						final String msg=getAutoAffects(mob,P);
						if(msg.length()<5)
							S.colorOnlyPrintln(L("Nothing!\n\r^N"));
						else
							S.colorOnlyPrintln(msg);
					}
					return false;
				}

			}
			if(S==mob.session())
				S.colorOnlyPrint(L(" \n\r^!Your auto-invoked skills are:^?"));
			final String msg=getAutoAffects(mob,mob);
			if(msg.length()<5)
				S.colorOnlyPrintln(L(" Non-existant!\n\r^N"));
			else
				S.colorOnlyPrintln(msg);
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
}

