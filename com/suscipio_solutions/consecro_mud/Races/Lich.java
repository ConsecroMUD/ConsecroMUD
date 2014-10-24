package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Lich extends Skeleton
{
	@Override public String ID(){	return "Lich"; }
	@Override public String name(){ return "Lich"; }

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();

	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.STAT_CONSTITUTION,affectableStats.getStat(CharStats.STAT_CONSTITUTION)-4);
		affectableStats.setStat(CharStats.STAT_CHARISMA,affectableStats.getStat(CharStats.STAT_CHARISMA)+6);
	}
	@Override
	public List<RawMaterial> myResources()
	{
		return resources;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!(ticking instanceof MOB)) return super.tick(ticking,tickID);
		final MOB myChar=(MOB)ticking;
		if((tickID==Tickable.TICKID_MOB)&&(CMLib.dice().rollPercentage()<10))
		{
			final Ability A=CMClass.getAbility("Spell_Fear");
			if(A!=null)
			{
				A.setMiscText("WEAK");
				A.invoke(myChar,null,true,0);
			}
		}
		return super.tick(myChar,tickID);
	}
}
