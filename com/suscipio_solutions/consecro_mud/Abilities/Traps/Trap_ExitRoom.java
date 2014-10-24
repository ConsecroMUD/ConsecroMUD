package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import java.util.Iterator;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.collections.PairVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Trap_ExitRoom extends Trap_Trap
{
	@Override public String ID() { return "Trap_ExitRoom"; }
	private final static String localizedName = CMLib.lang().L("Exit Trap");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	public PairVector<MOB,Integer> safeDirs=new PairVector<MOB,Integer>();

	protected boolean mayNotLeave() { return true; }

	@Override @SuppressWarnings({ "unchecked", "rawtypes" })
	public CMObject copyOf()
	{
		final Trap_ExitRoom obj=(Trap_ExitRoom)super.copyOf();
		obj.safeDirs=(PairVector)safeDirs.clone();
		return obj;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(sprung) return super.okMessage(myHost,msg);
		if(!super.okMessage(myHost,msg))
			return false;

		if(msg.amITarget(affected)&& (affected instanceof Room) && (msg.tool() instanceof Exit))
		{
			final Room room=(Room)affected;
			if ((msg.targetMinor()==CMMsg.TYP_LEAVE)||(msg.targetMinor()==CMMsg.TYP_FLEE))
			{
				final int movingInDir=CMLib.map().getExitDir(room, (Exit)msg.tool());
				if((movingInDir!=Directions.DOWN)&&(movingInDir!=Directions.UP))
				{
					synchronized(safeDirs)
					{
						for(final Iterator<Pair<MOB,Integer>> i=safeDirs.iterator();i.hasNext();)
						{
							final Pair<MOB,Integer> p=i.next();
							if(p.first == msg.source())
							{
								i.remove();
								if(movingInDir==p.second.intValue())
									return true;
								spring(msg.source());
								return !mayNotLeave();
							}
						}
					}
				}
			}
			else
			if (msg.targetMinor()==CMMsg.TYP_ENTER)
			{
				final int movingInDir=CMLib.map().getExitDir((Room)affected, (Exit)msg.tool());
				if((movingInDir!=Directions.DOWN)&&(movingInDir!=Directions.UP))
				{
					synchronized(safeDirs)
					{
						final int dex=safeDirs.indexOf(msg.source());
						if(dex>=0)
							safeDirs.remove(dex);
						while(safeDirs.size()>room.numInhabitants()+1)
							safeDirs.remove(0);
						safeDirs.add(new Pair<MOB,Integer>(msg.source(),Integer.valueOf(movingInDir)));
					}
				}
			}
		}
		return true;
	}
}
