package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rider;


@SuppressWarnings({"unchecked","rawtypes"})
public class Whisper extends StdCommand
{
	public Whisper(){}

	private final String[] access=I(new String[]{"WHISPER"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()==1)
		{
			mob.tell(L("Whisper what?"));
			return false;
		}
		Environmental target=null;
		final Room R = mob.location();
		if(commands.size()>2)
		{
			final String possibleTarget=(String)commands.elementAt(1);
			target=R.fetchFromRoomFavorMOBs(null,possibleTarget);
			if((target!=null)&&(!target.name().equalsIgnoreCase(possibleTarget))&&(possibleTarget.length()<4))
			   target=null;
			if((target!=null)
			&&(CMLib.flags().canBeSeenBy(target,mob))
			&&((!(target instanceof Rider))
			   ||(((Rider)target).riding()==mob.riding())))
				commands.removeElementAt(1);
			else
				target=null;
		}
		for(int i=1;i<commands.size();i++)
		{
			final String s=(String)commands.elementAt(i);
			if(s.indexOf(' ')>=0)
				commands.setElementAt("\""+s+"\"",i);
		}
		final String combinedCommands=CMParms.combine(commands,1);
		if(combinedCommands.equals(""))
		{
			mob.tell(L("Whisper what?"));
			return false;
		}

		CMMsg msg=null;
		if(target==null)
		{
			final Rideable riddenR=mob.riding();
			if(riddenR==null)
			{
				msg=CMClass.getMsg(mob,null,null,CMMsg.MSG_SPEAK,L("^T<S-NAME> whisper(s) to <S-HIM-HERSELF> '@x1'.^?@x2",combinedCommands,CMLib.protocol().msp("whisper.wav",40)),
											  CMMsg.NO_EFFECT,null,
											  CMMsg.MSG_QUIETMOVEMENT,L("^T<S-NAME> whisper(s) to <S-HIM-HERSELF>.^?@x1",CMLib.protocol().msp("whisper.wav",40)));
				if(R.okMessage(mob,msg))
					R.send(mob,msg);
			}
			else
			{
				msg=CMClass.getMsg(mob,riddenR,null,CMMsg.MSG_SPEAK,L("^T<S-NAME> whisper(s) around <T-NAMESELF> '@x1'.^?@x2",combinedCommands,CMLib.protocol().msp("whisper.wav",40)),
								CMMsg.MSG_SPEAK,L("^T<S-NAME> whisper(s) around <T-NAMESELF> '@x1'.^?@x2",combinedCommands,CMLib.protocol().msp("whisper.wav",40)),
								CMMsg.NO_EFFECT,null);
				if(R.okMessage(mob,msg))
				{
					R.send(mob,msg);
					final Vector<Environmental> targets = new Vector<Environmental>();
					for(int i=0;i<R.numInhabitants();i++)
						targets.addElement(R.fetchInhabitant(i));
					for (final Environmental E : targets)
					{
						if(E!=null)
						{
							if( (E instanceof MOB) && riddenR.amRiding((MOB)E))
								msg=CMClass.getMsg(mob,E,null,CMMsg.MSG_SPEAK,L("^T<S-NAME> whisper(s) around @x1 '@x2'.^?@x3",riddenR.name(),combinedCommands,CMLib.protocol().msp("whisper.wav",40)),
												CMMsg.MSG_SPEAK,L("^T<S-NAME> whisper(s) around @x1 '@x2'.^?@x3",riddenR.name(),combinedCommands,CMLib.protocol().msp("whisper.wav",40)),
												CMMsg.NO_EFFECT,null);
							else
								msg=CMClass.getMsg(mob,E,null,CMMsg.MSG_SPEAK,L("^T<S-NAME> whisper(s) around @x1 '@x2'.^?@x3",riddenR.name(),combinedCommands,CMLib.protocol().msp("whisper.wav",40)),
												CMMsg.MSG_SPEAK,L("^T<S-NAME> whisper(s) something around @x1.^?@x2",riddenR.name(),CMLib.protocol().msp("whisper.wav",40)),
												CMMsg.NO_EFFECT,null);
							if(R.okMessage(mob,msg))
								R.sendOthers(mob,msg);
						}
					}
				}
			}
		}
		else
		{
			msg=CMClass.getMsg(mob,target,null,CMMsg.MSG_SPEAK,L("^T^<WHISPER \"@x1\"^><S-NAME> whisper(s) to <T-NAMESELF> '@x2'.^</WHISPER^>^?@x3",CMStrings.removeColors(target.name()),combinedCommands,CMLib.protocol().msp("whisper.wav",40)),CMMsg.MSG_SPEAK,L("^T^<WHISPER \"@x1\"^><S-NAME> whisper(s) to <T-NAMESELF> '@x2'^</WHISPER^>.^?@x3",CMStrings.removeColors(target.name()),combinedCommands,CMLib.protocol().msp("whisper.wav",40)),CMMsg.MSG_QUIETMOVEMENT,L("^T<S-NAME> whisper(s) something to <T-NAMESELF>.^?@x1",CMLib.protocol().msp("whisper.wav",40)));
			if(R.okMessage(mob,msg))
				R.send(mob,msg);
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
