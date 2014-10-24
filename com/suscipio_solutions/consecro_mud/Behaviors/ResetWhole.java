package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Enumeration;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class ResetWhole extends StdBehavior
{
	@Override public String ID(){return "ResetWhole";}
	@Override protected int canImproveCode(){return Behavior.CAN_ROOMS|Behavior.CAN_AREAS;}

	protected long lastAccess=-1;
	long time=1800000;

	@Override
	public String accountForYourself()
	{
		return "periodic resetting";
	}

	@Override
	public void setParms(String parameters)
	{
		super.setParms(parameters);
		try
		{
			time=Long.parseLong(parameters);
			time=time*CMProps.getTickMillis();
		}
		catch(final Exception e){}
	}
	
	@Override
	public void executeMsg(Environmental E, CMMsg msg)
	{
		super.executeMsg(E,msg);
		if(!msg.source().isMonster())
		{
			final Room R=msg.source().location();
			if(R!=null)
			{
				if((E instanceof Area)
				&&(((Area)E).inMyMetroArea(R.getArea())))
					lastAccess=System.currentTimeMillis();
				else
				if((E instanceof Room) &&(R==E))
					lastAccess=System.currentTimeMillis();
			}
		}
	}

	private boolean isRoomBeingCamped(final Room R)
	{
		if(CMLib.flags().canNotBeCamped(R)
		&& (R.numPCInhabitants() > 0) 
		&& (!CMLib.tracking().isAnAdminHere(R,false)))
		{
			return true;
		}
		return false;
	}
	
	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);
		if(lastAccess<0) return true;

		if((lastAccess+time)<System.currentTimeMillis())
		{
			if(ticking instanceof Area)
			{
				for(final Enumeration r=((Area)ticking).getMetroMap();r.hasMoreElements();)
				{
					Room R=(Room)r.nextElement();
					for(final Enumeration<Behavior> e=R.behaviors();e.hasMoreElements();)
					{
						final Behavior B=e.nextElement();
						if((B!=null)&&(B.ID().equals(ID())))
						{ R=null; break;}
					}
					if((R!=null)&&(!this.isRoomBeingCamped(R)))
					{
						CMLib.map().resetRoom(R, true);
					}
				}
			}
			else
			if(ticking instanceof Room)
			{
				if(!this.isRoomBeingCamped((Room)ticking))
					CMLib.map().resetRoom((Room)ticking, true);
			}
			else
			{
				final Room room=super.getBehaversRoom(ticking);
				if((room!=null) && (!this.isRoomBeingCamped(room)))
					CMLib.map().resetRoom(room, true);
			}
			lastAccess=System.currentTimeMillis();
		}
		return true;
	}
}
