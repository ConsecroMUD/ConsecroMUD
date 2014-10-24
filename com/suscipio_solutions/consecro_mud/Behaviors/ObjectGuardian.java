package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class ObjectGuardian extends StdBehavior
{
	@Override public String ID(){return "ObjectGuardian";}

	protected boolean sentinal=false;

	@Override
	public void setParms(String parameters)
	{
		super.setParms(parameters);
		final List<String> parts=CMParms.parse(parameters.toUpperCase());
		sentinal=parts.contains("SENTINAL")||parts.contains("SENTINEL");
	}

	@Override
	public String accountForYourself()
	{
		return "valuable object guarding";
	}

	@Override
	public boolean okMessage(Environmental oking, CMMsg msg)
	{
		if(!super.okMessage(oking,msg)) return false;
		final MOB mob=msg.source();
		final MOB monster=(MOB)oking;
		if(sentinal)
		{
			if(!canActAtAll(monster)) return true;
			if(monster.amFollowing()!=null)  return true;
			if(monster.curState().getHitPoints()<((int)Math.round(monster.maxState().getHitPoints()/4.0)))
				return true;
		}
		else
		if(!canFreelyBehaveNormal(oking))
			return true;

		if((mob!=monster)
		&&(((msg.sourceMinor()==CMMsg.TYP_THROW)&&(monster.location()==CMLib.map().roomLocation(msg.target())))
		||(msg.sourceMinor()==CMMsg.TYP_DROP)))
		{
			final CMMsg msgs=CMClass.getMsg(monster,mob,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> won't let <T-NAME> drop that."));
			if(monster.location().okMessage(monster,msgs))
			{
				monster.location().send(monster,msgs);
				return false;
			}
		}
		else
		if((mob!=monster)
		&&((msg.sourceMinor()==CMMsg.TYP_GET)||(msg.targetMinor()==CMMsg.TYP_PUSH)||(msg.targetMinor()==CMMsg.TYP_PULL)))
		{
			final CMMsg msgs=CMClass.getMsg(monster,mob,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> won't let <T-NAME> touch that."));
			if(monster.location().okMessage(monster,msgs))
			{
				monster.location().send(monster,msgs);
				return false;
			}
		}
		return true;
	}
}
