package com.suscipio_solutions.consecro_mud.Races;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class WereWolf extends GiantWolf
{
	@Override public String ID(){	return "WereWolf"; }
	@Override public String name(){ return "WereWolf"; }
	@Override public int shortestMale(){return 59;}
	@Override public int shortestFemale(){return 59;}
	@Override public int heightVariance(){return 12;}
	@Override public int lightestWeight(){return 80;}
	@Override public int weightVariance(){return 80;}
	@Override public long forbiddenWornBits(){return 0;}
	@Override public String racialCategory(){return "Canine";}

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={0 ,2 ,2 ,1 ,1 ,2 ,2 ,1 ,2 ,2 ,1 ,0 ,1 ,1 ,0 ,0 };
	@Override public int[] bodyMask(){return parts;}

	private final int[] agingChart={0,4,8,12,16,20,24,28,32};
	@Override public int[] getAgingChart(){return agingChart;}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();
	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		affectableStats.setRacialStat(CharStats.STAT_DEXTERITY,affectableStats.getStat(CharStats.STAT_DEXTERITY)+3);
		affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE,affectableStats.getStat(CharStats.STAT_INTELLIGENCE)+3);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		final MOB mob=(MOB)myHost;
		if(msg.amISource(mob)
		&&(!msg.amITarget(mob))
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(msg.target() instanceof MOB)
		&&(mob.fetchWieldedItem()==null)
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Weapon)
		&&(CMLib.dice().rollPercentage()<50)
		&&(((Weapon)msg.tool()).weaponClassification()==Weapon.CLASS_NATURAL)
		&&(!((MOB)msg.target()).isMonster())
		&&(((msg.value())>(((MOB)msg.target()).maxState().getHitPoints()/5)))
		&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE)))
		{
			final Ability A=CMClass.getAbility("Disease_Lycanthropy");
			if((A!=null)&&(((MOB)msg.target()).fetchEffect(A.ID())==null))
				A.invoke(mob,(MOB)msg.target(),true,0);
		}
		super.executeMsg(myHost,msg);
	}

	@Override
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" claws",RawMaterial.RESOURCE_BONE));
				for(int i=0;i<4;i++)
					resources.addElement(makeResource
					("a strip of "+name().toLowerCase()+" hide",RawMaterial.RESOURCE_FUR));
				for(int i=0;i<2;i++)
				{
					final RawMaterial meat=makeResource
					("some "+name().toLowerCase()+" meat",RawMaterial.RESOURCE_MEAT);
					if(!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE))
					{
						final Ability A=CMClass.getAbility("Disease_Lycanthropy");
						if(A!=null)	meat.addNonUninvokableEffect(A);
					}
					resources.addElement(meat);
					resources.addElement(makeResource
					("a pound of "+name().toLowerCase()+" meat",RawMaterial.RESOURCE_MEAT));
				}
				resources.addElement(makeResource
				("some "+name().toLowerCase()+" blood",RawMaterial.RESOURCE_BLOOD));
				resources.addElement(makeResource
				("a pile of "+name().toLowerCase()+" bones",RawMaterial.RESOURCE_BONE));
			}
		}
		return resources;
	}
}
