package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Prisoner extends StdAbility
{
	@Override public String ID() { return "Prisoner"; }
	private final static String localizedName = CMLib.lang().L("Prisoner");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Prisoner's Geas)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected instanceof MOB)&&(msg.amISource((MOB)affected)))
			if(msg.sourceMinor()==CMMsg.TYP_RECALL)
			{
				if((msg.source()!=null)&&(msg.source().location()!=null))
					msg.source().location().show(msg.source(),null,CMMsg.MSG_OK_ACTION,L("<S-NAME> attempt(s) to recall, but a geas prevents <S-HIM-HER>."));
				return false;
			}
			else
			if(msg.sourceMinor()==CMMsg.TYP_FLEE)
			{
				msg.source().location().show(msg.source(),null,CMMsg.MSG_OK_ACTION,L("<S-NAME> attempt(s) to flee, but a geas prevents <S-HIM-HER>."));
				return false;
			}
			else
			if((msg.tool()!=null)&&(msg.tool() instanceof Ability)
			   &&(msg.targetMinor()==CMMsg.TYP_LEAVE))
			{
				msg.source().location().show(msg.source(),null,CMMsg.MSG_OK_ACTION,L("<S-NAME> attempt(s) to escape parole, but a geas prevents <S-HIM-HER>."));
				return false;
			}
			else
			if((msg.targetMinor()==CMMsg.TYP_ENTER)
			   &&(msg.target()!=null)
			   &&(msg.target() instanceof Room)
			   &&(msg.source().location()!=null)
			   &&(!msg.source().location().getArea().name().equals(((Room)msg.target()).getArea().name())))
			{
				msg.source().location().show(msg.source(),null,CMMsg.MSG_OK_ACTION,L("<S-NAME> attempt(s) to escape parole, but a geas prevents <S-HIM-HER>."));
				return false;
			}
		return super.okMessage(myHost,msg);
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
			mob.tell(L("Your sentence has been served."));
	}
}
