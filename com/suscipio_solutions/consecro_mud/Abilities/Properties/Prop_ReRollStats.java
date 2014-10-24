package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.io.IOException;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_ReRollStats extends Property
{
	@Override public String ID() { return "Prop_ReRollStats"; }
	@Override public String name(){ return "Re Roll Stats";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	protected int bonusPointsPerStat=0;
	protected boolean reRollFlag=true;
	protected boolean rePickClass=false;

	@Override
	public String accountForYourself()
	{
		return "Will cause a player to re-roll their stats.";
	}

	@Override
	public void setMiscText(String newMiscText)
	{
		super.setMiscText(newMiscText);
		bonusPointsPerStat=CMParms.getParmInt(newMiscText, "BONUSPOINTS", 0);
		rePickClass=CMParms.getParmBool(newMiscText, "PICKCLASS", false);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost, msg);
		if((reRollFlag)
		&&(affected instanceof MOB)
		&&(msg.sourceMinor()==CMMsg.TYP_LOOK)
		&&(msg.source()==affected))
		{
			final MOB M=msg.source();
			if((M.session()!=null)
			&&(M.playerStats()!=null))
			{
				final Ability me=this;
				CMLib.threads().executeRunnable(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							CMLib.login().promptPlayerStats(M.playerStats().getTheme(), M, M.session(), bonusPointsPerStat);
							M.recoverCharStats();
							if(rePickClass)
								M.baseCharStats().setCurrentClass(CMLib.login().promptCharClass(M.playerStats().getTheme(), M, M.session()));
							M.recoverCharStats();
							M.delEffect(me);
							M.baseCharStats().getCurrentClass().grantAbilities(M, false);
						}
						catch (final IOException e){}
					}
				});
			}
		}
	}
}
