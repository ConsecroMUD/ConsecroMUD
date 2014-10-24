package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Trap_Avalanche extends StdTrap
{
	@Override public String ID() { return "Trap_Avalanche"; }
	private final static String localizedName = CMLib.lang().L("avalanche");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapLevel(){return 25;}
	@Override public String requiresToSet(){return "100 pounds of stone";}

	@Override
	public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm)
	{
		if(P==null) return null;
		if(mob!=null)
		{
			final Item I=findMostOfMaterial(mob.location(),RawMaterial.MATERIAL_ROCK);
			if(I!=null)
				super.destroyResources(mob.location(),I.material(),100);
		}
		return super.setTrap(mob,P,trapBonus,qualifyingClassLevel,perm);
	}

	@Override
	public List<Item> getTrapComponents()
	{
		final Vector V=new Vector();
		for(int i=0;i<100;i++)
			V.addElement(CMLib.materials().makeItemResource(RawMaterial.RESOURCE_STONE));
		return V;
	}
	@Override
	public boolean canSetTrapOn(MOB mob, Physical P)
	{
		if(!super.canSetTrapOn(mob,P)) return false;
		if(mob!=null)
		{
			final Item I=findMostOfMaterial(mob.location(),RawMaterial.MATERIAL_ROCK);
			if((I==null)
			||(super.findNumberOfResource(mob.location(),I.material())<100))
			{
				mob.tell(L("You'll need to set down at least 100 pounds of stone first."));
				return false;
			}
		}
		if(P instanceof Room)
		{
			final Room R=(Room)P;
			if(R.domainType()!=Room.DOMAIN_OUTDOORS_MOUNTAINS)
			{
				if(mob!=null)
					mob.tell(L("You can only set this trap in the mountains."));
				return false;
			}
		}
		return true;
	}

	@Override
	public void spring(MOB target)
	{
		if((target!=invoker())&&(target.location()!=null))
		{
			if((doesSaveVsTraps(target))
			||(invoker().getGroupMembers(new HashSet<MOB>()).contains(target)))
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> avoid(s) setting off an avalanche!"));
			else
			if(target.location().show(target,target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> trigger(s) an avalanche!")))
			{
				super.spring(target);
				if((affected!=null)
				&&(affected instanceof Room))
				{
					final Room R=(Room)affected;
					for(int i=0;i<R.numInhabitants();i++)
					{
						final MOB M=R.fetchInhabitant(i);
						if((M!=null)&&(M!=invoker()))
							if(invoker().mayIFight(M))
							{
								final int damage=CMLib.dice().roll(trapLevel()+abilityCode(),20,1);
								CMLib.combat().postDamage(invoker(),M,this,damage,CMMsg.MASK_MALICIOUS|CMMsg.MASK_ALWAYS|CMMsg.TYP_JUSTICE,Weapon.TYPE_BASHING,"The avalanche <DAMAGE> <T-NAME>!");
							}
					}
				}
			}
		}
	}
}
