package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Play_Charge extends Play
{
	@Override public String ID() { return "Play_Charge"; }
	private final static String localizedName = CMLib.lang().L("Charge!");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected boolean persistantSong(){return false;}
	Vector chcommands=null;

	@Override
	protected void inpersistantAffect(MOB mob)
	{
		final Ability A=CMClass.getAbility("Fighter_Charge");
		if(A!=null)
		{
			A.setAbilityCode(4*getXLEVELLevel(invoker()));
			A.invoke(mob,chcommands,null,true,adjustedLevel(invoker(),0));
		}
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			final Ability A=CMClass.getAbility("Fighter_Charge");
			if(A!=null) return A.castingQuality(mob, target);
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((commands.size()==0)&&(!mob.isInCombat()))
		{
			mob.tell(L("Play charge at whom?"));
			return false;
		}
		if(commands.size()==0)
			commands.addElement(mob.getVictim().name());
		chcommands=commands;
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		return true;
	}
}
