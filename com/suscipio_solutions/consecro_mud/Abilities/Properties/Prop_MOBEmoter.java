package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Prop_MOBEmoter extends Property
{
	@Override public String ID(){return "Prop_MOBEmoter";}

	Behavior emoter=null;
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(emoter==null)
		{
			emoter=CMClass.getBehavior("Emoter");
			emoter.setParms(text());
		}
		emoter.executeMsg(myHost,msg);
	}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		if(emoter==null)
		{
			emoter=CMClass.getBehavior("Emoter");
			emoter.setParms(text());
		}
		return emoter.okMessage(myHost,msg);
	}
	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((ticking instanceof MOB)&&(tickID==Tickable.TICKID_MOB))
		{
			if(emoter==null)
			{
				emoter=CMClass.getBehavior("Emoter");
				emoter.setParms(text());
			}
			if(!emoter.tick(ticking,tickID))
			{
				if(CMParms.getParmInt(emoter.getParms(),"expires",0)>0)
					((MOB)ticking).delEffect(this);
			}
		}
		return true;
	}
}
