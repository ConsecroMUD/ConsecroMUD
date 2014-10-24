package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Wimpy extends StdBehavior
{
	@Override public String ID(){return "Wimpy";}
	protected int tickWait=0;
	protected int tickDown=0;
	protected boolean veryWimpy=false;

	@Override
	public boolean grantsAggressivenessTo(MOB M)
	{
		return false;
	}

	@Override
	public String accountForYourself()
	{
		if(getParms().trim().length()>0)
			return "wimpy fear of "+CMLib.masking().maskDesc(getParms(),true).toLowerCase();
		else
			return "wimpy fear of combat";
	}

	@Override
	public void setParms(String newParms)
	{
		super.setParms(newParms);
		tickWait=CMParms.getParmInt(newParms,"delay",0);
		tickDown=tickWait;
		veryWimpy=CMParms.getParmInt(newParms,"very",0)==1;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);
		if(tickID!=Tickable.TICKID_MOB) return true;
		if(((--tickDown)<0)&&(ticking instanceof MOB))
		{
			tickDown=tickWait;
			final MOB monster=(MOB)ticking;
			if(monster.location()!=null)
			for(int m=0;m<monster.location().numInhabitants();m++)
			{
				final MOB M=monster.location().fetchInhabitant(m);
				if((M!=null)&&(M!=monster)&&(CMLib.masking().maskCheck(getParms(),M,false)))
				{
					if(M.getVictim()==monster)
					{
						CMLib.commands().postFlee(monster,"");
						return true;
					}
					else
					if((veryWimpy)&&(!monster.isInCombat()))
					{
						final Room oldRoom=monster.location();
						final List<Behavior> V=CMLib.flags().flaggedBehaviors(monster,Behavior.FLAG_MOBILITY);
						for(final Behavior B : V)
						{
							int tries=0;
							while(((++tries)<100)&&(oldRoom==monster.location()))
								B.tick(monster,Tickable.TICKID_MOB);
							if(oldRoom!=monster.location())
								return true;
						}
						if(oldRoom==monster)
							CMLib.tracking().beMobile(monster,false,false,false,false,null,null);
					}
				}
			}
		}
		return true;
	}
}
