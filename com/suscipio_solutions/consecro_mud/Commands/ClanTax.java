package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan.Authority;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session.InputCallback;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;


@SuppressWarnings({"unchecked","rawtypes"})
public class ClanTax extends StdCommand
{
	public ClanTax(){}

	private final String[] access=I(new String[]{"CLANTAX"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(final MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		String taxStr=(commands.size()>1)?(String)commands.get(commands.size()-1):"";
		String clanName="";
		if(!CMath.isInteger(taxStr))
		{
			clanName=(commands.size()>2)?CMParms.combine(commands,1,commands.size()):"";
			taxStr="";
		}
		else
			clanName=(commands.size()>2)?CMParms.combine(commands,1,commands.size()-1):"";

		Clan chkC=null;
		final boolean skipChecks=mob.getClanRole(mob.Name())!=null;
		if(skipChecks) chkC=mob.getClanRole(mob.Name()).first;

		if(chkC==null)
		for(final Pair<Clan,Integer> c : mob.clans())
			if((clanName.length()==0)||(CMLib.english().containsString(c.first.getName(), clanName))
			&&(c.first.getAuthority(c.second.intValue(), Clan.Function.TAX)!=Authority.CAN_NOT_DO))
			{	chkC=c.first; break; }

		commands.setElementAt(getAccessWords()[0],0);

		final Clan C=chkC;
		if(C==null)
		{
			mob.tell(L("You aren't allowed to tax anyone from @x1.",((clanName.length()==0)?"anything":clanName)));
			return false;
		}
		if((!skipChecks)&&(!CMLib.clans().goForward(mob,chkC,commands,Clan.Function.TAX,false)))
		{
			mob.tell(L("You aren't in the right position to set the experience tax rate for your @x1.",C.getGovernmentName()));
			return false;
		}
		final Session S=mob.session();
		if((skipChecks)&&(commands.size()>1))
			setClanTaxRate(mob,chkC,skipChecks,commands,CMath.div(CMath.s_int(CMParms.combine(commands,1)),100));
		else
		if(S!=null)
		{
			if((taxStr.length()==0)||(!CMath.isNumber(taxStr)))
			{
				S.prompt(new InputCallback(InputCallback.Type.PROMPT,"",0)
				{
					@Override public void showPrompt() { S.promptPrint(L("Enter your @x1's new tax rate (0-25)\n\r: ",C.getGovernmentName()));}
					@Override public void timedOut() { }
					@Override public void callBack()
					{
						possiblySetClanTaxRate(mob,C,skipChecks,this.input);
					}
				});
			}
			else
				possiblySetClanTaxRate(mob,chkC,skipChecks,taxStr);
		}
		return false;
	}

	public void possiblySetClanTaxRate(MOB mob, Clan C, boolean skipChecks, String t)
	{
		if(t.length()==0)
			return;
		final int intt=CMath.s_int(t);
		if((intt<0)||(intt>25))
		{
			if(mob.session()!=null)
				mob.session().println(L("'@x1' is not a valid value.  Try 0-25.",t));
			return;
		}
		final Vector commands=new Vector();
		commands.addElement(getAccessWords()[0]);
		commands.addElement(t);
		setClanTaxRate(mob, C, skipChecks,commands,CMath.div(CMath.s_int(t),100));
	}

	public void setClanTaxRate(MOB mob, Clan C, boolean skipChecks, Vector commands, double newRate)
	{
		if(skipChecks||CMLib.clans().goForward(mob,C,commands,Clan.Function.TAX,true))
		{
			C.setTaxes(newRate);
			C.update();
			CMLib.clans().clanAnnounce(mob,L("The experience tax rate of @x1 @x2 has been changed to @x3",C.getGovernmentName(),C.clanID(),((int)Math.round(C.getTaxes()*100.0)+"%.")));
		}
	}

	@Override public boolean canBeOrdered(){return false;}


}
