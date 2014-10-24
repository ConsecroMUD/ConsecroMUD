package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Dance_Swords extends Dance
{
	@Override public String ID() { return "Dance_Swords"; }
	private final static String localizedName = CMLib.lang().L("Swords");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override protected int canAffectCode(){return CAN_MOBS|CAN_ITEMS;}
	@Override protected String danceOf(){return name()+" Dance";}
	@Override protected boolean skipStandardDanceInvoke(){return true;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((!super.okMessage(myHost,msg))
		||(affected==null))
		{
			if(affected instanceof MOB)
				undanceMe((MOB)affected,null);
			else
				unInvoke();
			return false;
		}
		else
		if(msg.amITarget(affected))
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_GET:
			case CMMsg.TYP_PUSH:
			case CMMsg.TYP_PULL:
			case CMMsg.TYP_REMOVE:
				if(affected instanceof MOB)
					undanceMe((MOB)affected,null);
				else
					unInvoke();
				break;
			}
		return true;
	}

	@Override
	public void unInvoke()
	{
		final Environmental E=affected;
		super.unInvoke();
		if((E!=null)
		&&(E instanceof Item)
		&&(((Item)E).owner()!=null)
		&&(((Item)E).owner() instanceof Room))
		{
			((Room)((Item)E).owner()).showHappens(CMMsg.MSG_OK_ACTION,L("@x1 stops dancing!",E.name()));
		}
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected instanceof Item)
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_FLYING);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected instanceof MOB)&&(!super.tick(ticking,tickID)))
			return false;
		final MOB M=invoker();
		if(affected==M)
		{
			Weapon sword=null;
			int num=0;
			for(int i=0;i<M.location().numItems();i++)
			{
				final Item I=M.location().getItem(i);
				if((I!=null)
				&&(I instanceof Weapon)
				&&(((Weapon)I).weaponClassification()==Weapon.CLASS_SWORD))
				{
					if((!CMLib.flags().isFlying(I))&&(I.fetchEffect(ID())==null))
					{
						sword=(Weapon)I;
						break;
					}
					else
					{
						num++;
					}
				}
			}
			if(sword==null) return true;
			final int max=3+(super.getXLEVELLevel(invoker())/2);
			if(num>max) return true;
			final Dance newOne=(Dance)this.copyOf();
			newOne.invokerManaCost=-1;
			newOne.startTickDown(M,sword,99999);
			return true;
		}
		else
		if((affected instanceof Weapon)
		&&(((Weapon)affected).owner() instanceof Room)
		&&(M!=null)
		&&(M.location().isContent((Weapon)affected))
		&&(M.fetchEffect(ID())!=null)
		&&(CMLib.flags().aliveAwakeMobile(M,true)))
		{
			final MOB victiM=M.getVictim();
			if(M.isInCombat())
			{
				final boolean isHit=(CMLib.combat().rollToHit(CMLib.combat().adjustedAttackBonus(M,victiM)+((Weapon)affected).phyStats().attackAdjustment(), CMLib.combat().adjustedArmor(victiM), 0));
				if((!isHit)||(!(affected instanceof Weapon)))
					M.location().show(M,victiM,affected,CMMsg.MSG_OK_ACTION,L("<O-NAME> attacks <T-NAME> and misses!"));
				else
				{
					final int bonusDamage=(affected.phyStats().damage()+5+getXLEVELLevel(M))-M.phyStats().damage();
					final int damage=CMLib.combat().adjustedDamage(M, (Weapon)affected, victiM, bonusDamage,false);
					CMLib.combat().postDamage(M,victiM,affected,damage,CMMsg.MASK_ALWAYS|CMMsg.TYP_WEAPONATTACK,
											((Weapon)affected).weaponType(),affected.name()+" attacks and <DAMAGE> <T-NAME>!");
				}
			}
			else
			if(CMLib.dice().rollPercentage()>75)
			switch(CMLib.dice().roll(1,5,0))
			{
			case 1:
				M.location().showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 twiches a bit.",affected.name()));
				break;
			case 2:
				M.location().showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 is looking for trouble.",affected.name()));
				break;
			case 3:
				M.location().showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 practices its moves.",affected.name()));
				break;
			case 4:
				M.location().showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 makes a few fake attacks.",affected.name()));
				break;
			case 5:
				M.location().showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 dances around.",affected.name()));
				break;
			}
		}
		else
		{
			if(affected instanceof MOB)
				undanceMe((MOB)affected,null);
			else
				unInvoke();
			return false;
		}
		return true;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		timeOut=0;
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		if((!auto)&&(!CMLib.flags().aliveAwakeMobile(mob,false)))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		undanceAll(mob,null);
		if(success)
		{
			invoker=mob;
			originRoom=mob.location();
			commonRoomSet=getInvokerScopeRoomSet(null);
			String str=auto?L("^SThe @x1 begins!^?",danceOf()):L("^S<S-NAME> begin(s) to dance the @x1.^?",danceOf());
			if((!auto)&&(mob.fetchEffect(this.ID())!=null))
				str=L("^S<S-NAME> start(s) the @x1 over again.^?",danceOf());

			for(int v=0;v<commonRoomSet.size();v++)
			{
				final Room R=(Room)commonRoomSet.elementAt(v);
				final String msgStr=getCorrectMsgString(R,str,v);
				final CMMsg msg=CMClass.getMsg(mob,null,this,somanticCastCode(mob,null,auto),msgStr);
				if(R.okMessage(mob,msg))
				{
					if(originRoom==R)
						R.send(mob,msg);
					else
						R.sendOthers(mob,msg);
					invoker=mob;
					final Dance newOne=(Dance)this.copyOf();
					newOne.invokerManaCost=-1;

					final MOB follower=mob;

					// malicious dances must not affect the invoker!
					int affectType=CMMsg.MSG_CAST_SOMANTIC_SPELL;
					if(auto) affectType=affectType|CMMsg.MASK_ALWAYS;

					if((CMLib.flags().canBeSeenBy(invoker,follower)&&(follower.fetchEffect(this.ID())==null)))
					{
						final CMMsg msg2=CMClass.getMsg(mob,follower,this,affectType,null);
						if(R.okMessage(mob,msg2))
						{
							follower.location().send(follower,msg2);
							if((msg2.value()<=0)&&(follower.fetchEffect(newOne.ID())==null))
								follower.addEffect(newOne);
						}
					}
					R.recoverRoomStats();
				}
			}
		}
		else
			mob.location().show(mob,null,CMMsg.MSG_NOISE,L("<S-NAME> make(s) a false step."));

		return success;
	}
}
