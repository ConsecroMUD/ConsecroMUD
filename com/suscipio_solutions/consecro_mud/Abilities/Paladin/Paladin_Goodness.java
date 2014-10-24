package com.suscipio_solutions.consecro_mud.Abilities.Paladin;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Paladin_Goodness extends PaladinSkill
{
	@Override public String ID() { return "Paladin_Goodness"; }
	private final static String localizedName = CMLib.lang().L("Paladin`s Goodness");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_HOLYPROTECTION;}
	protected boolean tickTock=false;
	public Paladin_Goodness()
	{
		super();
		paladinsGroup=new Vector();
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		tickTock=!tickTock;
		if(tickTock)
		{
			final MOB mob=invoker;
			final Room R=(mob!=null)?mob.location():null;
			if(R!=null)
			for(int m=0;m<R.numInhabitants();m++)
			{
				final MOB target=R.fetchInhabitant(m);
				if((target!=null)
				&&(CMLib.flags().isEvil(target))
				&&((paladinsGroup!=null)&&(paladinsGroup.contains(target))
					||((target.getVictim()==invoker)&&(target.rangeToTarget()==0)))
				&&((invoker==null)||(invoker.fetchAbility(ID())==null)||proficiencyCheck(null,0,false)))
				{

					final int harming=CMLib.dice().roll(1,(invoker!=null)?adjustedLevel(invoker,0):15,0);
					if(CMLib.flags().isEvil(target))
						CMLib.combat().postDamage(invoker,target,this,harming,CMMsg.MASK_MALICIOUS|CMMsg.MASK_ALWAYS|CMMsg.TYP_UNDEAD,Weapon.TYPE_BURSTING,"^SThe aura of goodness around <S-NAME> <DAMAGES> <T-NAME>!^?");
				}
			}
		}
		return true;
	}
}
