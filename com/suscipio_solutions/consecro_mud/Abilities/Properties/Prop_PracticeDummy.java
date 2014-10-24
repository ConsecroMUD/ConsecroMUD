package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_PracticeDummy extends Property
{
	boolean disabled=false;
	@Override public String ID() { return "Prop_PracticeDummy"; }
	@Override public String name(){ return "Practice Dummy";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	protected boolean unkillable=true;

	@Override
	public String accountForYourself()
	{ return "Undefeatable";	}

	@Override
	public void setMiscText(String newMiscText)
	{
		super.setMiscText(newMiscText);
		unkillable=newMiscText.toUpperCase().indexOf("KILL")<0;
	}

	@Override
	public void affectCharState(MOB mob, CharState affectableMaxState)
	{
		super.affectCharState(mob,affectableMaxState);
		if(unkillable)
			affectableMaxState.setHitPoints(99999);
	}

	@Override
	public void affectPhyStats(Physical E, PhyStats affectableStats)
	{
		super.affectPhyStats(E,affectableStats);
		if(unkillable)
			affectableStats.setArmor(100);
	}


	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		if((affected instanceof MOB)
		&&(msg.amISource((MOB)affected)))
		{
			if((msg.sourceMinor()==CMMsg.TYP_DEATH)&&(unkillable))
			{
				msg.source().tell(L("You are not allowed to die."));
				return false;
			}
			else
			if(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
			{
				if(unkillable)
					msg.source().curState().setHitPoints(99999);
				((MOB)affected).makePeace();
				final Room room=((MOB)affected).location();
				if(room!=null)
				for(int i=0;i<room.numInhabitants();i++)
				{
					final MOB mob=room.fetchInhabitant(i);
					if((mob.getVictim()!=null)&&(mob.getVictim()==affected))
						mob.makePeace();
				}
				return false;
			}
			else
			if(((msg.targetMinor()==CMMsg.TYP_GET)||(msg.targetMinor()==CMMsg.TYP_PUSH)||(msg.targetMinor()==CMMsg.TYP_PULL))
			&&(msg.target()!=null)
			&&(msg.target() instanceof Item))
			{
				msg.source().tell(L("Dummys cant get anything."));
				return false;
			}
		}
		return true;
	}
}
