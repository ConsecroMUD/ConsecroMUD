package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Electronics;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings("rawtypes")
public class TypeCmd extends Go
{
	public TypeCmd(){}

	private final String[] access=I(new String[]{"TYPE","="});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final Room R=mob.location();
		final boolean consoleMode=(mob.riding() instanceof Electronics.Computer);
		if((commands.size()<=1)||(R==null))
		{
			if(consoleMode)
				mob.tell(L("Type what into this console?  Have you read the screen?"));
			else
				mob.tell(L("Type what into what?"));
			return false;
		}
		Environmental typeIntoThis=(consoleMode)?mob.riding():null;
		if(typeIntoThis==null)
		{
			int x=1;
			while((x<commands.size())&&(!commands.get(x).toString().equalsIgnoreCase("into")))
				x++;
			if(x<commands.size()-1)
			{
				final String typeWhere=CMParms.combine(commands,x+1);
				typeIntoThis=mob.location().fetchFromMOBRoomFavorsItems(mob,null,typeWhere,Wearable.FILTER_ANY);
				if(typeIntoThis==null)
					for(int i=0;i<R.numItems();i++)
					{
						final Item I=R.getItem(i);
						if((I instanceof Electronics.ElecPanel)
						&&(((Electronics.ElecPanel)I).isOpen()))
						{
							typeIntoThis=R.fetchFromRoomFavorItems(I, typeWhere);
							if(typeIntoThis!=null)
								break;
						}
					}
				if(typeIntoThis!=null)
				{
					while(commands.size()>x)
						commands.remove(commands.size()-1);
				}
				else
				{
					mob.tell(L("You don't see '@x1' here.",typeWhere.toLowerCase()));
				}
			}
		}

		final String enterWhat=CMParms.combine(commands,1);
		if(typeIntoThis!=null)
		{
			final String enterStr=L("^W<S-NAME> enter(s) '@x1' into <T-NAME>.^?",enterWhat);
			final CMMsg msg=CMClass.getMsg(mob,typeIntoThis,null,CMMsg.MSG_WRITE,enterStr,CMMsg.MSG_WRITE,enterWhat,CMMsg.MSG_WRITE,null);
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
			return true;
		}
		else
		{
			mob.tell(L("You don't see '@x1' here.",enterWhat.toLowerCase()));
		}
		return false;
	}
	@Override public boolean canBeOrdered(){return true;}
}
