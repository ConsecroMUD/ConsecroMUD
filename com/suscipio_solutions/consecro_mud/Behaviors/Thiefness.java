package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thiefness extends CombatAbilities
{
	@Override public String ID(){return "Thiefness";}
	@Override public long flags(){return Behavior.FLAG_TROUBLEMAKING;}
	protected int tickDown=0;

	@Override
	public String accountForYourself()
	{
		return "thiefliness";
	}

	@Override
	public void startBehavior(PhysicalAgent forMe)
	{
		super.startBehavior(forMe);
		if(!(forMe instanceof MOB)) return;
		final MOB mob=(MOB)forMe;
		combatMode=COMBAT_RANDOM;
		makeClass(mob,getParmsMinusCombatMode(),"Thief");
		newCharacter(mob);
		//%%%%%att,armor,damage,hp,mana,move
		if((preCastSet==Integer.MAX_VALUE)||(preCastSet<=0))
		{
			setCombatStats(mob,0,10,15,-15,-15,-15, true);
			setCharStats(mob);
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);

		if(tickID!=Tickable.TICKID_MOB) return true;
		if(!canActAtAll(ticking)) return true;
		if(!(ticking instanceof MOB)) return true;
		final MOB mob=(MOB)ticking;
		if((--tickDown)<=0)
		if((CMLib.dice().rollPercentage()<10)&&(mob.location()!=null))
		{
			tickDown=2;
			MOB victim=null;
			if(mob.isInCombat())
				victim=mob.getVictim();
			else
			for(int i=0;i<mob.location().numInhabitants();i++)
			{
				final MOB potentialVictim=mob.location().fetchInhabitant(i);
				if((potentialVictim!=null)
				&&(potentialVictim!=mob)
				&&(!potentialVictim.isMonster())
				&&(CMLib.flags().canBeSeenBy(potentialVictim,mob)))
					victim=potentialVictim;
			}
			if((victim!=null)
			&&(!CMSecurity.isAllowed(victim,victim.location(),CMSecurity.SecFlag.CMDROOMS))
			&&(!CMSecurity.isAllowed(victim,victim.location(),CMSecurity.SecFlag.ORDER)))
			{
				final Vector V=new Vector();
				final Ability A=mob.fetchAbility((CMLib.dice().rollPercentage()>50)?(mob.isInCombat()?"Thief_Mug":"Thief_Steal"):"Thief_Swipe");
				if(A!=null)
				{
					if(!A.ID().equalsIgnoreCase("Thief_Swipe"))
					{
						Item I=null;
						for(int i=0;i<victim.numItems();i++)
						{
							final Item potentialI=victim.getItem(i);
							if((potentialI!=null)
							&&(potentialI.amWearingAt(Wearable.IN_INVENTORY))
							&&(CMLib.flags().canBeSeenBy(potentialI,mob)))
								I=potentialI;
						}
						if(I!=null)
							V.addElement(I.ID());
					}
					if(!A.ID().equalsIgnoreCase("Thief_Mug"))
						V.addElement(victim.name());
					A.setProficiency(CMLib.dice().roll(1,50,A.adjustedLevel(mob,0)*15));
					A.invoke(mob,V,null,false,0);
				}
			}
		}
		return true;
	}
}
