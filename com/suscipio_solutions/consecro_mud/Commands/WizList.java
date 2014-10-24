package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.MaskingLibrary;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.PlayerLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMStrings;


@SuppressWarnings("rawtypes")
public class WizList extends StdCommand
{
	public WizList(){}

	private final String[] access=I(new String[]{"WIZLIST"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final StringBuffer head=new StringBuffer("");
		final boolean isImmortalLooker=CMSecurity.isASysOp(mob);
		head.append("^x[");
		head.append(CMStrings.padRight(L("Class"),16)+" ");
		head.append(CMStrings.padRight(L("Race"),8)+" ");
		head.append(CMStrings.padRight(L("Lvl"),4)+" ");
		if(isImmortalLooker)
			head.append(CMStrings.padRight(L("Last"),18)+" ");
		head.append("] Character Name^.^?\n\r");
		mob.tell("^x["+CMStrings.centerPreserve(L("The Administrators of @x1",CMProps.getVar(CMProps.Str.MUDNAME)),head.length()-10)+"]^.^?");
		final java.util.List<PlayerLibrary.ThinPlayer> allUsers=CMLib.database().getExtendedUserList();
		String mask=CMProps.getVar(CMProps.Str.WIZLISTMASK);
		if(mask.length()==0) mask="-ANYCLASS +Immortal";
		final MaskingLibrary.CompiledZapperMask compiledMask=CMLib.masking().maskCompile(mask);
		for(final PlayerLibrary.ThinPlayer U : allUsers)
		{
			CharClass C;
			final MOB player = CMLib.players().getPlayer(U.name);
			if(player != null)
				C=player.charStats().getCurrentClass();
			else
				C=CMClass.getCharClass(U.charClass);
			if(C==null)
				C=CMClass.findCharClass(U.charClass);
			if(((player!=null)&&(CMLib.masking().maskCheck(compiledMask, player, true)))
			||(CMLib.masking().maskCheck(compiledMask, U)))
			{
				head.append("[");
				if(C!=null)
					head.append(CMStrings.padRight(C.name(),16)+" ");
				else
					head.append(CMStrings.padRight(L("Unknown"),16)+" ");
				head.append(CMStrings.padRight(U.race,8)+" ");
				if((C==null)||(!C.leveless()))
					head.append(CMStrings.padRight(""+U.level,4)+" ");
				else
					head.append(CMStrings.padRight("    ",4)+" ");
				if(isImmortalLooker)
					head.append(CMStrings.padRight(CMLib.time().date2String(U.last),18)+" ");
				head.append("] "+U.name);
				head.append("\n\r");
			}
		}
		mob.tell(head.toString());
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
