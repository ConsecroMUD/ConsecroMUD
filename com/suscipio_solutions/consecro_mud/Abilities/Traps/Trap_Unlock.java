package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Trap_Unlock extends Trap_Trap
{
	@Override public String ID() { return "Trap_Unlock"; }
	private final static String localizedName = CMLib.lang().L("Unlock Trap");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_EXITS|Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(sprung)
		{
			if(msg.source().isMine(affected))
				unInvoke();
			else
				super.executeMsg(myHost,msg);
			return;
		}
		super.executeMsg(myHost,msg);

		if(msg.amITarget(affected))
		{
			if((msg.targetMinor()==CMMsg.TYP_UNLOCK)
			||(msg.targetMinor()==CMMsg.TYP_JUSTICE)
			||(msg.targetMinor()==CMMsg.TYP_DELICATE_HANDS_ACT))
			{
				spring(msg.source());
			}
		}
	}
}
