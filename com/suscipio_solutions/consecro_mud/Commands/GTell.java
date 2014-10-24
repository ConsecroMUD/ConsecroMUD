package com.suscipio_solutions.consecro_mud.Commands;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Social;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings("rawtypes")
public class GTell extends StdCommand
{
	public GTell(){}

	private final String[] access=I(new String[]{"GTELL","GT"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		String text=CMParms.combine(commands,1);
		if(text.length()==0)
		{
			mob.tell(L("Tell the group what?"));
			return false;
		}
		text=CMProps.applyINIFilter(text,CMProps.Str.SAYFILTER);

		if((commands.size()>2)
		&&((((String)commands.elementAt(1)).equalsIgnoreCase("last"))
		&&(CMath.isNumber(CMParms.combine(commands,2))))
		&&(mob.playerStats()!=null))
		{
			final java.util.List<String> V=mob.playerStats().getGTellStack();
			if(V.size()==0)
				mob.tell(L("No telling."));
			else
			{
				int num=CMath.s_int(CMParms.combine(commands,2));
				if(num>V.size()) num=V.size();
				for(int i=V.size()-num;i<V.size();i++)
					mob.tell(V.get(i));
			}
			return false;
		}

		CMMsg tellMsg=CMClass.getMsg(mob,null,null,CMMsg.MSG_TELL,null,CMMsg.NO_EFFECT,null,CMMsg.MSG_TELL,null);
		text=text.trim();
		if(text.startsWith(",")
		||(text.startsWith(":")
			&&(text.length()>1)
			&&(Character.isLetter(text.charAt(1))||text.charAt(1)==' ')))
		{
			text=text.substring(1);
			final Vector<String> V=CMParms.parse(text);
			Social S=CMLib.socials().fetchSocial(V,true,false);
			if(S==null) S=CMLib.socials().fetchSocial(V,false,false);
			if(S!=null)
			{
				tellMsg=S.makeMessage(mob,
						"^t^<GTELL \""+CMStrings.removeColors(mob.name())+"\"^>[GTELL] ",
						"^</GTELL^>^?^.",
						CMMsg.MASK_ALWAYS,
						CMMsg.MSG_TELL,
						V,
						null,
						false);
			}
			else
			{
				if(text.trim().startsWith("'")||text.trim().startsWith("`"))
					text=text.trim();
				else
					text=" "+text.trim();
				tellMsg.setSourceMessage("^t^<GTELL \""+CMStrings.removeColors(mob.name())+"\"^>[GTELL] <S-NAME>"+text+"^</GTELL^>^?^.");
				tellMsg.setOthersMessage(tellMsg.sourceMessage());
			}
		}
		else
		{
			tellMsg.setSourceMessage("^t^<GTELL \""+CMStrings.removeColors(mob.name())+"\"^><S-NAME> tell(s) the group '"+text+"'^</GTELL^>^?^.");
			tellMsg.setOthersMessage(tellMsg.sourceMessage());
		}

		final Set<MOB> group=mob.getGroupMembers(new HashSet<MOB>());
		final CMMsg msg=tellMsg;
		for (final Object element : group)
		{
			final MOB target=(MOB)element;
			if((mob.location().okMessage(mob,msg))
			&&(target.okMessage(target,msg)))
			{
				if(target.playerStats()!=null)
				{
					final String tellStr=(target==mob)?msg.sourceMessage():(
									(target==msg.target())?msg.targetMessage():msg.othersMessage()
									);
					target.playerStats().addGTellStack(CMLib.coffeeFilter().fullOutFilter(target.session(),target,mob,msg.target(),null,CMStrings.removeColors(tellStr),false));
				}
				target.executeMsg(target,msg);
				if(msg.trailerMsgs()!=null)
				{
					for(final CMMsg msg2 : msg.trailerMsgs())
						if((msg2!=msg)&&(target.okMessage(target,msg2)))
							target.executeMsg(target,msg2);
					msg.trailerMsgs().clear();
				}
			}
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
