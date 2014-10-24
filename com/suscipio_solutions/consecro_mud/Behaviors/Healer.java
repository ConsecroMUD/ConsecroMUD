package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Healer extends ActiveTicker
{
	@Override public String ID(){return "Healer";}
	@Override protected int canImproveCode(){return Behavior.CAN_MOBS;}
	protected static final List<Ability> healingVector=new Vector<Ability>();

	public Healer()
	{
		super();
		minTicks=10; maxTicks=20; chance=100;
		tickReset();
		if(healingVector.size()==0)
		{
			healingVector.add(CMClass.getAbility("Prayer_CureBlindness"));
			healingVector.add(CMClass.getAbility("Prayer_CureDisease"));
			healingVector.add(CMClass.getAbility("Prayer_CureLight"));
			healingVector.add(CMClass.getAbility("Prayer_RemoveCurse"));
			healingVector.add(CMClass.getAbility("Prayer_Bless"));
			healingVector.add(CMClass.getAbility("Prayer_Sanctuary"));
		}
	}

	@Override
	public String accountForYourself()
	{
		return "benevolent healing";
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);
		if((canAct(ticking,tickID))&&(ticking instanceof MOB))
		{
			final MOB mob=(MOB)ticking;
			final Room thisRoom=mob.location();
			if(thisRoom==null) return true;

			final double aChance=CMath.div(mob.curState().getMana(),mob.maxState().getMana());
			if((Math.random()>aChance)||(mob.curState().getMana()<50))
				return true;

			if(thisRoom.numPCInhabitants()>0)
			{
				final MOB target=thisRoom.fetchRandomInhabitant();
				MOB followMOB=target;
				if((target != null)&&(target.amFollowing()!=null))
					followMOB=target.amUltimatelyFollowing();
				if((target!=null)
				&&(target!=mob)
				&&(followMOB.getVictim()!=mob)
				&&(!followMOB.isMonster()))
				{
					final Ability tryThisOne=healingVector.get(CMLib.dice().roll(1,healingVector.size(),-1));
					Ability thisOne=mob.fetchAbility(tryThisOne.ID());
					if(thisOne==null)
					{
						thisOne=(Ability)tryThisOne.copyOf();
						thisOne.setSavable(false);
						mob.addAbility(thisOne);
					}
					thisOne.setProficiency(100);
					final Vector V=new Vector();
					if(!target.isMonster())
						V.addElement(target.name());
					thisOne.invoke(mob,V,target,false,0);
				}
			}
		}
		return true;
	}
}
