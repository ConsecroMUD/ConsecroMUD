package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Prop_HereSpellCast extends Prop_HaveSpellCast
{
	@Override public String ID() { return "Prop_HereSpellCast"; }
	@Override public String name(){ return "Casting spells when here";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override public boolean bubbleAffect(){return true;}
	protected int lastNum=-1;
	private Vector lastMOBs=new Vector();

	@Override
	public String accountForYourself()
	{ return spellAccountingsWithMask("Casts "," on those here.");}

	@Override public int triggerMask() { return TriggeredAffect.TRIGGER_ENTER; }

	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		lastMOBs=new Vector();
	}

	public void process(MOB mob, Room room, int code) // code=0 add/sub, 1=addon, 2=subon
	{
		if((code==2)||((code==0)&&(lastNum!=room.numInhabitants())))
		{
			for(int v=lastMOBs.size()-1;v>=0;v--)
			{
				final MOB lastMOB=(MOB)lastMOBs.elementAt(v);
				if((lastMOB.location()!=room)
				||((mob==lastMOB)&&(code==2)))
				{
					removeMyAffectsFrom(lastMOB);
					lastMOBs.removeElementAt(v);
				}
			}
			lastNum=room.numInhabitants();
		}
		if((!lastMOBs.contains(mob))
		&&((code==1)||((code==0)&&(room.isInhabitant(mob)))))
		{
			if(addMeIfNeccessary(mob,mob,true,0,maxTicks))
				lastMOBs.addElement(mob);
		}
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		if(processing) return;
		if((((msg.targetMinor()==CMMsg.TYP_ENTER)&&(msg.target()==affected))
			||((msg.targetMinor()==CMMsg.TYP_RECALL)&&(msg.target()==affected)))
		&&(affected instanceof Room))
			process(msg.source(),(Room)affected,1);
		else
		if((((msg.targetMinor()==CMMsg.TYP_LEAVE)&&(msg.target()==affected))
			||((msg.targetMinor()==CMMsg.TYP_RECALL)&&(msg.target()!=affected)))
		&&(affected instanceof Room))
			process(msg.source(),(Room)affected,2);
	}

	@Override
	public void affectPhyStats(Physical host, PhyStats affectableStats)
	{
		if(processing) return;
		processing=true;
		if((host instanceof MOB)&&(affected instanceof Room))
			process((MOB)host, (Room)affected,0);
		processing=false;
	}
}
