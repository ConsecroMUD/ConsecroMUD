package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.CagedAnimal;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Trap_SnakePit extends Trap_RoomPit
{
	@Override public String ID() { return "Trap_SnakePit"; }
	private final static String localizedName = CMLib.lang().L("snake pit");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapLevel(){return 10;}
	@Override public String requiresToSet(){return "some caged snakes";}

	protected Vector monsters=null;

	protected Item getCagedAnimal(MOB mob)
	{
		if(mob==null) return null;
		if(mob.location()==null) return null;
		for(int i=0;i<mob.location().numItems();i++)
		{
			final Item I=mob.location().getItem(i);
			if(I instanceof CagedAnimal)
			{
				final MOB M=((CagedAnimal)I).unCageMe();
				if((M!=null)&&(M.baseCharStats().getMyRace().racialCategory().equalsIgnoreCase("Serpent")))
					return I;
			}
		}
		return null;
	}

	@Override
	public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm)
	{
		if(P==null) return null;
		Item I=getCagedAnimal(mob);
		final StringBuffer buf=new StringBuffer("<SNAKES>");
		int num=0;
		while((I!=null)&&((++num)<6))
		{
			buf.append(((CagedAnimal)I).cageText());
			I.destroy();
			I=getCagedAnimal(mob);
		}
		buf.append("</SNAKES>");
		setMiscText(buf.toString());
		return super.setTrap(mob,P,trapBonus,qualifyingClassLevel,perm);
	}

	@Override
	public List<Item> getTrapComponents()
	{
		final Vector V=new Vector();
		final Item I=CMClass.getItem("GenCaged");
		((CagedAnimal)I).setCageText(text());
		I.recoverPhyStats();
		I.text();
		V.addElement(I);
		return V;
	}

	@Override
	public boolean canSetTrapOn(MOB mob, Physical P)
	{
		if(!super.canSetTrapOn(mob,P)) return false;
		if(getCagedAnimal(mob)==null)
		{
			if(mob!=null)
				mob.tell(L("You'll need to set down some caged snakes first."));
			return false;
		}
		return true;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((tickID==Tickable.TICKID_TRAP_RESET)&&(getReset()>0))
		{
			// recage the motherfather
			if((tickDown<=1)&&(monsters!=null))
			{
				for(int i=0;i<monsters.size();i++)
				{
					final MOB M=(MOB)monsters.elementAt(i);
					if(M.amDead()||(!M.isInCombat()))
						M.destroy();
				}
				monsters=null;
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public void finishSpringing(MOB target)
	{
		if((!invoker().mayIFight(target))||(target.phyStats().weight()<5))
			target.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> float(s) gently into the pit!"));
		else
		{
			target.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> hit(s) the pit floor with a THUMP!"));
			final int damage=CMLib.dice().roll(trapLevel()+abilityCode(),6,1);
			CMLib.combat().postDamage(invoker(),target,this,damage,CMMsg.MASK_MALICIOUS|CMMsg.MASK_ALWAYS|CMMsg.TYP_JUSTICE,-1,null);
		}
		final Vector snakes=new Vector();
		String t=text();
		int x=t.indexOf("</MOBITEM><MOBITEM>");
		while(x>=0)
		{
			snakes.addElement(t.substring(0,x+10));
			t=t.substring(x+10);
			x=t.indexOf("</MOBITEM><MOBITEM>");
		}
		if(t.length()>0) snakes.addElement(t);
		if(snakes.size()>0)
			monsters=new Vector();
		for(int i=0;i<snakes.size();i++)
		{
			t=(String)snakes.elementAt(i);
			final Item I=CMClass.getItem("GenCaged");
			((CagedAnimal)I).setCageText(t);
			final MOB monster=((CagedAnimal)I).unCageMe();
			if(monster!=null)
			{
				monsters.addElement(monster);
				monster.basePhyStats().setRejuv(PhyStats.NO_REJUV);
				monster.bringToLife(target.location(),true);
				monster.setVictim(target);
				if(target.getVictim()==null)
					target.setVictim(monster);
			}
		}
		CMLib.commands().postLook(target,true);
	}
}
