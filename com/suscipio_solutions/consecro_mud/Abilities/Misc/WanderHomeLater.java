package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class WanderHomeLater extends StdAbility
{
	@Override public String ID() { return "WanderHomeLater"; }
	private final static String localizedName = CMLib.lang().L("WanderHomeLater");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Waiting til you're clear to go home)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}

	private boolean areaOk = false;
	private boolean ignorePCs = false;
	private boolean ignoreFollow = false;
	private boolean once = false;
	private int currentWait = 0;
	private int minTicks = 0;
	private int maxTicks = 0;
	
	@Override
	public void setMiscText(String newMiscText)
	{
		super.setMiscText(newMiscText);
		areaOk=CMParms.getParmBool(newMiscText,"areaok", areaOk);
		ignorePCs=CMParms.getParmBool(newMiscText,"ignorepcs", ignorePCs);
		ignoreFollow=CMParms.getParmBool(newMiscText,"ignorefollow", ignoreFollow);
		once=CMParms.getParmBool(newMiscText,"once", once);
		minTicks=CMParms.getParmInt(newMiscText, "minticks", minTicks);
		maxTicks=CMParms.getParmInt(newMiscText, "maxticks", maxTicks);
		currentWait = minTicks + ((maxTicks <= minTicks)?0:CMLib.dice().roll(1, maxTicks-minTicks, 0));
	}
	
	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(affected instanceof MOB)
		{
			final MOB M=(MOB)affected;
			if(currentWait > 0)
			{
				currentWait--;
				if(currentWait > 0)
					return super.tick(ticking, tickID);
				currentWait = minTicks + ((maxTicks <= minTicks)?0:CMLib.dice().roll(1, maxTicks-minTicks, 0));
			}
			if((once) && (M.getStartRoom()==M.location()))
				unInvoke();
			else
			if(M.amDead() && (once))
				unInvoke();
			else
			if(CMLib.flags().canActAtAll(M)
			&&(!M.isInCombat())
			&&(ignoreFollow || (M.amFollowing()==null))
			&&(M.getStartRoom()!=null))
			{
				final Room startRoom= M.getStartRoom();
				final Room curRoom=M.location();
				
				if(areaOk && (startRoom != null) && (curRoom != null))
				{
					if(startRoom.getArea() == curRoom.getArea())
						return super.tick(ticking, tickID);
				}
				
				if(startRoom != curRoom)
					CMLib.tracking().wanderAway(M, !ignorePCs, true);
				if(once)
				{
					if(startRoom==M.location())
						unInvoke();
				}
			}
		}
		return super.tick(ticking, tickID);
	}
}
