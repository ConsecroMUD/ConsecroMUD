package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.HashSet;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_DemonicConsumption extends Prayer
{
	@Override public String ID() { return "Prayer_DemonicConsumption"; }
	private final static String localizedName = CMLib.lang().L("Demonic Consumption");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_VEXING;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canTargetCode(){return CAN_ITEMS|CAN_MOBS;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_ANY);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		boolean success=false;
		int affectType=CMMsg.MSG_CAST_VERBAL_SPELL;
		if(!(target instanceof Item))
		{
			if(!auto)
				affectType=affectType|CMMsg.MASK_MALICIOUS;
		}
		int levelDiff=target.phyStats().level()-(mob.phyStats().level()+(getXLEVELLevel(mob)/2));
		if(target instanceof MOB) levelDiff+=6;
		if(levelDiff<0) levelDiff=0;
		success=proficiencyCheck(mob,-(levelDiff*15),auto);

		if(auto)affectType=affectType|CMMsg.MASK_ALWAYS;

		final Room R=mob.location();
		if(success && (R!=null))
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,affectType,auto?"":L("^S<S-NAME> point(s) at <T-NAMESELF> and @x1 treacherously!^?",prayWord(mob)));
			if(R.okMessage(mob,msg))
			{
				R.send(mob,msg);
				if(msg.value()<=0)
				{
					final HashSet<DeadBody> oldBodies=new HashSet<DeadBody>();
					for(int i=0;i<R.numItems();i++)
					{
						final Item I=R.getItem(i);
						if((I instanceof DeadBody)&&(I.container()==null))
							oldBodies.add((DeadBody)I);
					}

					if(target instanceof MOB)
					{
						if(((MOB)target).curState().getHitPoints()>0)
							CMLib.combat().postDamage(mob,(MOB)target,this,(((MOB)target).curState().getHitPoints()*100),CMMsg.MASK_ALWAYS|CMMsg.TYP_UNDEAD,Weapon.TYPE_BURSTING,"^SThe evil <DAMAGE> <T-NAME>!^?");
						if(((MOB)target).amDead())
							R.show(mob,target,CMMsg.MSG_OK_ACTION,L("<T-NAME> <T-IS-ARE> consumed!"));
						else
							return false;
					}
					else
						R.show(mob,target,CMMsg.MSG_OK_ACTION,L("<T-NAME> is consumed!"));

					if(target instanceof Item)
						((Item)target).destroy();
					else // destroy any newly created bodies
					{
						for(int i=0;i<R.numItems();i++)
						{
							final Item I=R.getItem(i);
							if((I instanceof DeadBody)
							&&(I.container()==null)
							&&(!oldBodies.contains(I))
							&&(!((DeadBody)I).playerCorpse()))
							{
								I.destroy();
								break;
							}
						}
					}
					R.recoverRoomStats();
				}

			}

		}
		else
			maliciousFizzle(mob,target,L("<S-NAME> point(s) at <T-NAMESELF> and @x1 treacherously, but fizzle(s) the magic!",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
