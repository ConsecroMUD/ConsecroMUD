package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_PeaceMaker extends Property
{
	@Override public String ID() { return "Prop_PeaceMaker"; }
	@Override public String name(){ return "Strike Neuralizing";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS|Ability.CAN_MOBS;}
	@Override
	public String accountForYourself()
	{ return "Peace Maker";	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((CMath.bset(msg.sourceMajor(),CMMsg.MASK_MALICIOUS))
		||(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
		||(CMath.bset(msg.othersMajor(),CMMsg.MASK_MALICIOUS)))
		{
			if((msg.source()!=null)
			&&(msg.target()!=null)
			&&(msg.source()!=affected)
			&&(msg.source()!=msg.target()))
			{
				if(affected instanceof MOB)
				{
					final MOB mob=(MOB)affected;
					if((CMLib.flags().aliveAwakeMobileUnbound(mob,true))
					&&(!mob.isInCombat()))
					{
						String t="No fighting!";
						if(text().length()>0)
						{
							final List<String> V=CMParms.parseSemicolons(text(),true);
							t=V.get(CMLib.dice().roll(1,V.size(),-1));
						}
						CMLib.commands().postSay(mob,msg.source(),t,false,false);
					}
					else
						return super.okMessage(myHost,msg);
				}
				else
				{
					String t="You feel too peaceful here.";
					if(text().length()>0)
					{
						final List<String> V=CMParms.parseSemicolons(text(),true);
						t=V.get(CMLib.dice().roll(1,V.size(),-1));
					}
					msg.source().tell(t);
				}
				final MOB victim=msg.source().getVictim();
				if(victim!=null) victim.makePeace();
				msg.source().makePeace();
				msg.modify(msg.source(),msg.target(),msg.tool(),CMMsg.NO_EFFECT,"",CMMsg.NO_EFFECT,"",CMMsg.NO_EFFECT,"");
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}
}
