package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Hashtable;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class DelayedTransporter extends ActiveTicker
{
	@Override public String ID(){return "DelayedTransporter";}
	protected Hashtable transportees=new Hashtable();
	protected Vector destRoomNames=new Vector();
	@Override protected int canImproveCode(){return Behavior.CAN_ITEMS|Behavior.CAN_MOBS|Behavior.CAN_ROOMS;}

	@Override
	public String accountForYourself()
	{
		return "away whisking";
	}

	public DelayedTransporter()
	{
		super();
		minTicks=5;maxTicks=5;chance=100;
		tickReset();
	}


	@Override
	public void setParms(String newParms)
	{
		String myParms=newParms;
		int x=myParms.indexOf(';');
		if(x>0)
		{
			final String parmText=myParms.substring(0,x);
			myParms=myParms.substring(x+1);
			super.setParms(parmText);
		}
		destRoomNames=new Vector();
		transportees=new Hashtable();
		while(myParms.length()>0)
		{
			String thisRoom=myParms;
			x=myParms.indexOf(';');
			if(x>0)
			{
				thisRoom=myParms.substring(0,x);
				myParms=myParms.substring(x+1);
			}
			else
				myParms="";

			if(CMLib.map().getRoom(thisRoom)!=null)
				destRoomNames.addElement(thisRoom);
		}
		parms=newParms;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);
		final Room room=this.getBehaversRoom(ticking);
		if((room!=null)&&(destRoomNames!=null)&&(destRoomNames.size()>0))
		for(int i=0;i<room.numInhabitants();i++)
		{
			final MOB inhab=room.fetchInhabitant(i);
			if(inhab!=null)
			{
				Integer I=(Integer)transportees.get(inhab.Name());
				if(I==null)
				{
					I=Integer.valueOf(0);
					transportees.put(inhab.Name(),I);
				}
				boolean gone=false;
				if(I.intValue()>=minTicks)
					if((CMLib.dice().rollPercentage()<chance)||(I.intValue()>maxTicks))
					{
						final String roomName=(String)destRoomNames.elementAt(CMLib.dice().roll(1,destRoomNames.size(),-1));
						final Room otherRoom=CMLib.map().getRoom(roomName);
						if(otherRoom==null)
							inhab.tell(L("You are whisked nowhere at all, since '@x1' is nowhere to be found.",roomName));
						else
							otherRoom.bringMobHere(inhab,true);
						transportees.remove(inhab.Name());
						gone=true;
					}
				if(!gone)
				{
					I=Integer.valueOf(I.intValue()+1);
					transportees.put(inhab.Name(),I);
				}
			}
		}
		return true;
	}
}
