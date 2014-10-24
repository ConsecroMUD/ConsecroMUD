package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Trap_AcidPit extends Trap_RoomPit
{
	@Override public String ID() { return "Trap_AcidPit"; }
	private final static String localizedName = CMLib.lang().L("acid pit");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapLevel(){return 18;}
	@Override public String requiresToSet(){return "";}
	@Override
	public int baseRejuvTime(int level)
	{
		int time=super.baseRejuvTime(level);
		if(time<15) time=15;
		return time;
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
			CMLib.combat().postDamage(invoker(),target,this,damage,CMMsg.MASK_MALICIOUS|CMMsg.TYP_ACID,-1,null);
			target.location().showHappens(CMMsg.MSG_OK_VISUAL,L("Acid starts pouring into the room!"));
		}
		CMLib.commands().postLook(target,true);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((tickID==Tickable.TICKID_TRAP_RESET)&&(getReset()>0))
		{
			if((sprung)
			&&(affected!=null)
			&&(affected instanceof Room)
			&&(pit!=null)
			&&(pit.size()>1)
			&&(!disabled()))
			{
				final Room R=(Room)pit.firstElement();
				for(int i=0;i<R.numInhabitants();i++)
				{
					final MOB M=R.fetchInhabitant(i);
					if((M!=null)&&(M!=invoker()))
					{
						final int damage=CMLib.dice().roll(trapLevel()+abilityCode(),6,1);
						CMLib.combat().postDamage(invoker(),M,this,damage,CMMsg.MASK_MALICIOUS|CMMsg.MASK_ALWAYS|CMMsg.TYP_ACID,Weapon.TYPE_MELTING,"The acid <DAMAGE> <T-NAME>!");
						if((!M.isInCombat())&&(M.isMonster())&&(M!=invoker)&&(invoker!=null)&&(M.location()==invoker.location())&&(M.location().isInhabitant(invoker))&&(CMLib.flags().canBeSeenBy(invoker,M)))
							CMLib.combat().postAttack(M,invoker,M.fetchWieldedItem());
					}
				}
				return super.tick(ticking,tickID);
			}
			return false;
		}
		return super.tick(ticking,tickID);
	}

}
