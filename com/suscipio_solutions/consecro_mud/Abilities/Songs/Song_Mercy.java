package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Song_Mercy extends Song
{
	@Override public String ID() { return "Song_Mercy"; }
	private final static String localizedName = CMLib.lang().L("Mercy");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override protected boolean HAS_QUANTITATIVE_ASPECT(){return false;}

	protected Room lastRoom=null;
	protected int count=3;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if(!(affected instanceof MOB))
			return true;
		final MOB mob=(MOB)affected;
		if(mob.location()!=lastRoom)
		{
			count=3;
			lastRoom=mob.location();
		}
		else
			count--;
		return true;
	}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		final MOB mob=(MOB)affected;
		if(((msg.targetMajor()&CMMsg.MASK_MALICIOUS)>0)
		&&(!CMath.bset(msg.sourceMajor(),CMMsg.MASK_ALWAYS))
		&&(mob.location()!=null)
		&&((msg.amITarget(mob)))
		&&((count>0)||(lastRoom==null)||(lastRoom!=mob.location())))
		{
			final MOB target=(MOB)msg.target();
			if((!target.isInCombat())
			&&(mob.location()==target.location())
			&&(msg.source().getVictim()!=target))
			{
				msg.source().tell(L("You feel like showing @x1 mercy right now.",target.name(msg.source())));
				if(target.getVictim()==msg.source())
				{
					target.makePeace();
					target.setVictim(null);
				}
				return false;
			}

		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		count=3;
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		count=3;
		return true;
	}
}
