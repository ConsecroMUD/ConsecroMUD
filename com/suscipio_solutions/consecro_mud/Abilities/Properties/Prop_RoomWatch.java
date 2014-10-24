package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;

@SuppressWarnings({"unchecked","rawtypes"})
public class Prop_RoomWatch extends Property
{
	@Override public String ID() { return "Prop_RoomWatch"; }
	@Override public String name(){ return "Different Room Can Watch";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_ITEMS;}
	protected Vector newRooms=null;
	protected String prefix=null;

	@Override
	public String accountForYourself()
	{ return "Different View of "+text();	}

	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		newRooms=null;
		prefix=null;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(newRooms==null)
		{
			final List<String> V=CMParms.parseSemicolons(text(),true);
			newRooms=new Vector();
			for(int v=0;v<V.size();v++)
			{
				final String roomID=V.get(v);
				final int x=roomID.indexOf('=');
				if(x>0)
				{
					final String var=roomID.substring(0,x).trim().toLowerCase();
					if(var.equalsIgnoreCase("prefix"))
					{
						prefix=CMStrings.trimQuotes(roomID.substring(x+1).trim());
						continue;
					}
				}
				final Room R=CMLib.map().getRoom(roomID);
				if(R!=null) newRooms.addElement(R);
			}
		}

		if((affected!=null)
		&&(msg.othersCode()!=CMMsg.NO_EFFECT)
		&&(msg.othersMessage()!=null)
		&&(msg.othersMessage().length()>0))
		{
			final Room thisRoom=CMLib.map().roomLocation(affected);
			for(int r=0;r<newRooms.size();r++)
			{
				final Room R=(Room)newRooms.elementAt(r);
				if((R!=null)&&(R.fetchEffect(ID())==null)&&(R!=thisRoom))
				{
					final CMMsg msg2=CMClass.getMsg(msg.source(),msg.target(),msg.tool(),
								  CMMsg.NO_EFFECT,null,
								  CMMsg.NO_EFFECT,null,
								  CMMsg.MSG_OK_VISUAL,(prefix!=null)?(prefix+msg.othersMessage()):msg.othersMessage());
					if(R.okMessage(msg.source(),msg2))
					for(int i=0;i<R.numInhabitants();i++)
					{
						final MOB M=R.fetchInhabitant(i);
						if((M!=null)
						&&(CMLib.flags().canSee(M))
						&&(CMLib.flags().canBeSeenBy(R,M))
						&&(CMLib.flags().canBeSeenBy(msg2.source(),M)))
							M.executeMsg(M,msg2);
					}
				}
			}
		}
	}
}
