package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Trap_BearTrap extends StdTrap
{
	@Override public String ID() { return "Trap_BearTrap"; }
	private final static String localizedName = CMLib.lang().L("bear trap");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapLevel(){return 16;}
	@Override public String requiresToSet(){return "30 pounds of metal";}
	@Override public int baseRejuvTime(int level){ return 35;}

	protected int amountRemaining=250;
	protected MOB trapped=null;

	@Override
	public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm)
	{
		if(P==null) return null;
		if(mob!=null)
		{
			Item I=findMostOfMaterial(mob.location(),RawMaterial.MATERIAL_METAL);
			if(I==null) I=findMostOfMaterial(mob.location(),RawMaterial.MATERIAL_MITHRIL);
			if(I!=null)
				super.destroyResources(mob.location(),I.material(),30);
		}
		return super.setTrap(mob,P,trapBonus,qualifyingClassLevel,perm);
	}

	@Override
	public List<Item> getTrapComponents()
	{
		final Vector V=new Vector();
		for(int i=0;i<30;i++)
			V.addElement(CMLib.materials().makeItemResource(RawMaterial.RESOURCE_IRON));
		return V;
	}
	@Override
	public boolean canSetTrapOn(MOB mob, Physical P)
	{
		if(!super.canSetTrapOn(mob,P)) return false;
		if(mob!=null)
		{
			Item I=findMostOfMaterial(mob.location(),RawMaterial.MATERIAL_METAL);
			if(I==null)	I=findMostOfMaterial(mob.location(),RawMaterial.MATERIAL_MITHRIL);
			if((I==null)
			||(super.findNumberOfResource(mob.location(),I.material())<30))
			{
				mob.tell(L("You'll need to set down at least 30 pounds of metal first."));
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((sprung)
		&&(trapped!=null)
		&&(affected!=null)
		&&(msg.amISource(trapped))
		&&(trapped.location()!=null))
		{
			if((((msg.targetMinor()==CMMsg.TYP_LEAVE)||(msg.targetMinor()==CMMsg.TYP_FLEE))
				&&(msg.amITarget(affected))
			||(msg.sourceMinor()==CMMsg.TYP_ADVANCE)
			||(msg.sourceMinor()==CMMsg.TYP_RETREAT)))
			{
				if(trapped.location().show(trapped,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> struggle(s) to get out of the bear trap.")))
				{
					amountRemaining-=trapped.charStats().getStat(CharStats.STAT_STRENGTH);
					amountRemaining-=trapped.phyStats().level();
					if(amountRemaining<=0)
					{
						trapped.location().show(trapped,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> pull(s) free of the bear trap."));
						trapped=null;
					}
					else
						return false;
				}
				else
					return false;
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public void spring(MOB target)
	{
		trapped=null;
		if((target!=invoker())&&(target.location()!=null))
		{
			if((!invoker().mayIFight(target))
			||(isLocalExempt(target))
			||(CMLib.flags().isInFlight(target))
			||(invoker().getGroupMembers(new HashSet<MOB>()).contains(target))
			||(target==invoker())
			||(doesSaveVsTraps(target)))
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> avoid(s) a bear trap!"));
			else
			if(target.location().show(target,target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> step(s) on a bear trap!")))
			{
				super.spring(target);
				final int damage=CMLib.dice().roll(trapLevel()+abilityCode(),6,1);
				trapped=target;
				amountRemaining=250+((trapLevel()+abilityCode())*10);
				CMLib.combat().postDamage(invoker(),target,this,damage,CMMsg.MASK_MALICIOUS|CMMsg.MASK_ALWAYS|CMMsg.TYP_JUSTICE,Weapon.TYPE_PIERCING,"The bear trap <DAMAGE> <T-NAME>!");
			}
		}
	}
}
