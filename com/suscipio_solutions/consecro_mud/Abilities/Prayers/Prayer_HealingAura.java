package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.HashSet;
import java.util.Set;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Prayer_HealingAura extends Prayer
{
	@Override public String ID() { return "Prayer_HealingAura"; }
	private final static String localizedName = CMLib.lang().L("Healing Aura");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_HEALING;}
	@Override public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY;}
	private final static String localizedStaticDisplay = CMLib.lang().L("(Healing Aura)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public boolean  canBeUninvoked(){return false;}
	@Override public boolean  isAutoInvoked(){return true;}
	protected int fiveDown=5;
	protected int tenDown=10;
	protected int twentyDown=20;

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if((mob.isInCombat())&&(!mob.charStats().getMyRace().racialCategory().equalsIgnoreCase("Undead")))
				return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_SELF);
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if(!(affected instanceof MOB))
		   return false;
		if(tickID!=Tickable.TICKID_MOB) return true;
		final MOB myChar=(MOB)affected;
		if(((--fiveDown)>0)&&((--tenDown)>0)&&((--twentyDown)>0)) return true;

		final Set<MOB> followers=myChar.getGroupMembers(new HashSet<MOB>());
		if(myChar.location()!=null)
			for(int i=0;i<myChar.location().numInhabitants();i++)
			{
				final MOB M=myChar.location().fetchInhabitant(i);
				if((M!=null)
				&&((M.getVictim()==null)||(!followers.contains(M.getVictim()))))
					followers.add(M);
			}
		if((fiveDown)<=0)
		{
			fiveDown=5;
			final Ability A=CMClass.getAbility("Prayer_CureLight");
			if(A!=null)
				for (final Object element : followers)
					A.invoke(myChar,((MOB)element),true,0);
		}
		if((tenDown)<=0)
		{
			tenDown=10;
			final Ability A=CMClass.getAbility("Prayer_RemovePoison");
			if(A!=null)
				for (final Object element : followers)
					A.invoke(myChar,((MOB)element),true,0);
		}
		if((twentyDown)<=0)
		{
			twentyDown=20;
			final Ability A=CMClass.getAbility("Prayer_CureDisease");
			if(A!=null)
				for (final Object element : followers)
					A.invoke(myChar,((MOB)element),true,0);
		}
		return true;
	}
}
