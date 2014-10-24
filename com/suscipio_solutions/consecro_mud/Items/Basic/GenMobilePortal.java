package com.suscipio_solutions.consecro_mud.Items.Basic;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;



@SuppressWarnings({"unchecked","rawtypes"})
public class GenMobilePortal extends GenPortal implements Rideable, Exit
{
	@Override public String ID(){ return "GenMobilePortal";}

	protected StdPortal myStationaryPortal=null;

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		switch(msg.targetMinor())
		{
		case CMMsg.TYP_DISMOUNT:
			break;
		case CMMsg.TYP_ENTER:
		{
			final Room R=CMLib.map().roomLocation(this);
			if((myStationaryPortal!=null)
			&&(!myStationaryPortal.amDestroyed()))
				myStationaryPortal.setReadableText(CMLib.map().getExtendedRoomID(R));
			else
			{
				myStationaryPortal=null;
				final Room destR=getDestinationRoom();
				final Vector choices=new Vector();
				for(int i=0;i<destR.numItems();i++)
				{
					final Item I=destR.getItem(i);
					if((I!=null)&&(I instanceof StdPortal))
						choices.addElement(I);
				}
				MOB M=null;
				for(int m=0;m<destR.numInhabitants();m++)
				{
					M=destR.fetchInhabitant(m);
					if(M!=null)
						for(int i=0;i<M.numItems();i++)
						{
							final Item I=M.getItem(i);
							if((I!=null)&&(I instanceof StdPortal))
								choices.addElement(I);
						}
				}
				if(choices.size()>0)
				{
					if(choices.size()==1)
						myStationaryPortal=(StdPortal)choices.firstElement();
					else
					{
						if(((myStationaryPortal==null)||(myStationaryPortal.amDestroyed()))&&(secretIdentity().length()>0))
						for(int i=0;i<choices.size();i++)
							if(((Item)choices.elementAt(i)).secretIdentity().equals(secretIdentity()))
							{ myStationaryPortal=(StdPortal)choices.elementAt(i); break;}
						if((myStationaryPortal==null)||(myStationaryPortal.amDestroyed()))
						for(int i=0;i<choices.size();i++)
							if(((Item)choices.elementAt(i)).Name().equals(Name()))
							{ myStationaryPortal=(StdPortal)choices.elementAt(i); break;}
						if((myStationaryPortal==null)||(myStationaryPortal.amDestroyed()))
							myStationaryPortal=(StdPortal)choices.firstElement();
					}
				}
			}
			break;
		}
		}
	}

}
