package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Trap_Enter extends Trap_Trap
{
	@Override public String ID() { return "Trap_Enter"; }
	private final static String localizedName = CMLib.lang().L("Entry Trap");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_EXITS|Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(sprung) return super.okMessage(myHost,msg);
		if(!super.okMessage(myHost,msg))
			return false;

		   if((msg.amITarget(affected))
 			||((msg.tool()!=null)&&(msg.tool()==affected)))
		{
			if((msg.targetMinor()==CMMsg.TYP_ENTER)
			||(msg.targetMinor()==CMMsg.TYP_LEAVE)
			||(msg.targetMinor()==CMMsg.TYP_FLEE))
			{
				if(msg.targetMinor()==CMMsg.TYP_LEAVE)
					return true;
				spring(msg.source());
				return false;
			}
		}
		return true;
	}
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(sprung)
			return;

		if((msg.amITarget(affected))||((msg.tool()!=null)&&(msg.tool()==affected)))
		{
			if(msg.targetMinor()==CMMsg.TYP_LEAVE)
			{
				spring(msg.source());
			}
		}
	}
}
