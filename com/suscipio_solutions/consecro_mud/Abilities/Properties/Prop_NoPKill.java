package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_NoPKill extends Property
{
	@Override public String ID() { return "Prop_NoPKill"; }
	@Override public String name(){ return "No Player Killing";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(((CMath.bset(msg.sourceMajor(),CMMsg.MASK_MALICIOUS))
		||(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
		||(CMath.bset(msg.othersMajor(),CMMsg.MASK_MALICIOUS)))
			&&(msg.target() instanceof MOB)
			&&(msg.target()!=msg.source())
			&&(!((MOB)msg.target()).isMonster())
			&&(!msg.source().isMonster()))
		{
			if(CMath.s_int(text())==0)
			{
				msg.source().tell(L("Player killing is forbidden here."));
				msg.source().setVictim(null);
				return false;
			}
			int levelDiff=msg.source().phyStats().level()-((MOB)msg.target()).phyStats().level();
			if(levelDiff<0) levelDiff=levelDiff*-1;
			if(levelDiff>CMath.s_int(text()))
			{
				msg.source().tell(L("Player killing is forbidden for characters whose level difference is greater than @x1.",""+CMath.s_int(text())));
				msg.source().setVictim(null);
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}
}
