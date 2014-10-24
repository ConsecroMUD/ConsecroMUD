package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Dance_Swing extends Dance
{
	@Override public String ID() { return "Dance_Swing"; }
	private final static String localizedName = CMLib.lang().L("Swing");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	protected boolean doneThisRound=false;
	@Override protected String danceOf(){return name()+" Dancing";}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(tickID==Tickable.TICKID_MOB)
			doneThisRound=false;
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg)) return false;

		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;

		if(msg.amITarget(mob)
		   &&(CMLib.flags().aliveAwakeMobile(mob,true))
		   &&(msg.targetMinor()==CMMsg.TYP_WEAPONATTACK)
		   &&(!doneThisRound)
		   &&(mob.rangeToTarget()==0))
		{
			if((msg.tool()!=null)&&(msg.tool() instanceof Item))
			{
				final Item attackerWeapon=(Item)msg.tool();
				if((attackerWeapon!=null)
				&&(attackerWeapon instanceof Weapon)
				&&(((Weapon)attackerWeapon).weaponClassification()!=Weapon.CLASS_FLAILED)
				&&(((Weapon)attackerWeapon).weaponClassification()!=Weapon.CLASS_NATURAL)
				&&(((Weapon)attackerWeapon).weaponClassification()!=Weapon.CLASS_RANGED)
				&&(((Weapon)attackerWeapon).weaponClassification()!=Weapon.CLASS_THROWN))
				{
					if(proficiencyCheck(null,mob.charStats().getStat(CharStats.STAT_DEXTERITY)+(getXLEVELLevel(invoker())*2)-70,false))
					{
						final CMMsg msg2=CMClass.getMsg(mob,msg.source(),null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> parr(ys) @x1 attack from <T-NAME>!",attackerWeapon.name()));
						if(mob.location().okMessage(mob,msg2))
						{
							doneThisRound=true;
							mob.location().send(mob,msg2);
							return false;
						}
					}
				}
			}
		}
		return true;
	}

}
