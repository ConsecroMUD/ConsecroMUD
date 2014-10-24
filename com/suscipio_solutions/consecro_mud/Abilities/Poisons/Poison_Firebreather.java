package com.suscipio_solutions.consecro_mud.Abilities.Poisons;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;




public class Poison_Firebreather extends Poison_Liquor
{
	@Override public String ID() { return "Poison_Firebreather"; }
	private final static String localizedName = CMLib.lang().L("Firebreather");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"LIQUORFIRE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int POISON_TICKS(){return 35;}

	@Override protected int alchoholContribution(){return 3;}
	@Override protected int level(){return 3;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))	return false;
		if((affected==null)||(invoker==null)) return false;
		if(!(affected instanceof MOB)) return super.tick(ticking,tickID);

		final MOB mob=(MOB)affected;
		final Room room=mob.location();
		if((CMLib.dice().rollPercentage()<drunkness)&&(CMLib.flags().aliveAwakeMobile(mob,true))&&(room!=null))
		{
			if(CMLib.dice().rollPercentage()<40)
			{
				room.show(mob,null,this,CMMsg.MSG_QUIETMOVEMENT,L("<S-NAME> belch(es) fire!@x1",CMLib.protocol().msp("fireball.wav",20)));
				for(int i=0;i<room.numInhabitants();i++)
				{
					final MOB target=room.fetchInhabitant(i);

					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_FIRE,null);
					if((mob!=target)&&(mob.mayPhysicallyAttack(target))&&(room.okMessage(mob,msg)))
					{
						room.send(mob,msg);
						invoker=mob;

						int damage = 0;
						int maxDie =  mob.phyStats().level();
						if (maxDie > 10)
							maxDie = 10;
						damage += CMLib.dice().roll(maxDie,6,1);
						if(msg.value()>0)
							damage = (int)Math.round(CMath.div(damage,2.0));
						CMLib.combat().postDamage(mob,target,this,damage,CMMsg.MASK_ALWAYS|CMMsg.MASK_SOUND|CMMsg.TYP_FIRE,Weapon.TYPE_BURNING,"^F^<FIGHT^>The fire <DAMAGE> <T-NAME>!^</FIGHT^>^?");
					}
				}
			}
			else
				room.show(mob,null,this,CMMsg.MSG_QUIETMOVEMENT,L("<S-NAME> belch(es) smoke!"));
			disableHappiness=true;
		}
		return super.tick(ticking,tickID);
	}
}
