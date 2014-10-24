package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rider;


@SuppressWarnings({"unchecked","rawtypes"})
public class Prop_RideSpellCast extends Prop_HaveSpellCast
{
	@Override public String ID() { return "Prop_RideSpellCast"; }
	@Override public String name(){ return "Casting spells when ridden";}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS|Ability.CAN_MOBS;}
	protected Vector lastRiders=new Vector();
	@Override
	public String accountForYourself()
	{ return spellAccountingsWithMask("Casts "," on those mounted.");}

	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		lastRiders=new Vector();
	}

	@Override public int triggerMask() { return TriggeredAffect.TRIGGER_MOUNT; }

	@Override
	public void affectPhyStats(Physical host, PhyStats affectableStats)
	{
		if(processing) return;
		processing=true;
		if(affected instanceof Rideable)
		{
			final Rideable RI=(Rideable)affected;
			for(int r=0;r<RI.numRiders();r++)
			{
				final Rider R=RI.fetchRider(r);
				if(R instanceof MOB)
				{
					final MOB M=(MOB)R;
					if((!lastRiders.contains(M))&&(RI.amRiding(M)))
					{
						if(addMeIfNeccessary(M,M,true,0,maxTicks))
							lastRiders.addElement(M);
					}
				}
			}
			for(int i=lastRiders.size()-1;i>=0;i--)
			{
				final MOB M=(MOB)lastRiders.elementAt(i);
				if(!RI.amRiding(M))
				{
					removeMyAffectsFrom(M);
					while(lastRiders.contains(M))
						lastRiders.removeElement(M);
				}
			}
		}
		processing=false;
	}
}
