package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.HashSet;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Dance_Cotillon extends Dance
{
	@Override public String ID() { return "Dance_Cotillon"; }
	private final static String localizedName = CMLib.lang().L("Cotillon");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected String danceOf(){return name()+" Dance";}
	@Override protected boolean HAS_QUANTITATIVE_ASPECT(){return false;}
	protected MOB whichLast=null;

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if((!mob.isInCombat())
			||(mob.getGroupMembers(new HashSet<MOB>()).size()<2))
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking, tickID))
			return false;
		if((affected==invoker())&&((invoker()).isInCombat()))
		{
			if(whichLast==null)
				whichLast=invoker();
			else
			{
				final MOB M=(MOB)affected;
				boolean pass=false;
				boolean found=false;
				for(int i=0;i<M.location().numInhabitants();i++)
				{
					final MOB M2=M.location().fetchInhabitant(i);
					if(M2==whichLast)
						found=true;
					else
					if((M2!=whichLast)
					&&(found)
					&&(M2.fetchEffect(ID())!=null)
					&&(M2.isInCombat()))
					{
						whichLast=M2;
						break;
					}
					if(i==(M.location().numInhabitants()-1))
					{
						if(pass)
							return true;
						pass=true;
						i=-1;
					}
				}
				if((whichLast!=null)
				&&(M.isInCombat())
				&&(M.getVictim().getVictim()!=whichLast)
				&&(whichLast.location().show(whichLast,null,M.getVictim(),CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> dance(s) into <O-YOUPOSS> way."))))
					M.getVictim().setVictim(whichLast);
			}
		}
		return true;
	}

}
