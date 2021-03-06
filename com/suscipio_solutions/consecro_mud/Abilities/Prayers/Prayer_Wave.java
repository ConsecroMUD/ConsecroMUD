package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Prayer_Wave extends Prayer
{
	@Override public String ID() { return "Prayer_Wave"; }
	private final static String localizedName = CMLib.lang().L("Wave");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_CREATION;}
	@Override public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY;}
	private final static String localizedStaticDisplay = CMLib.lang().L("(Waved)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_EXITS;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if(h==null) return false;
		int dir=Directions.getGoodDirectionCode(CMParms.combine(commands,0));
		if(dir<0)
		{
			if(mob.isMonster())
			{
				for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
				{
					final Room destRoom=mob.location().getRoomInDir(d);
					final Exit exitRoom=mob.location().getExitInDir(d);
					if((destRoom!=null)||(exitRoom!=null)||(d!=Directions.UP))
					{ dir=d; break;}
				}
				if(dir<0) return false;
			}
			else
			{
				mob.tell(L("Wash your opponents which direction?"));
				return false;
			}
		}
		final Room destRoom=mob.location().getRoomInDir(dir);
		final Exit exitRoom=mob.location().getExitInDir(dir);
		if((destRoom==null)||(exitRoom==null)||(dir==Directions.UP))
		{
			mob.tell(L("You can't wash your opponents that way!"));
			return false;
		}

		if(!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		final int numEnemies=h.size();
		for (final Object element : h)
		{
			final MOB target=(MOB)element;
			if(target!=mob)
			{
				if(success)
				{
					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					final Room R=target.location();
					final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto)|CMMsg.MASK_MALICIOUS,auto?L("<T-NAME> <T-IS-ARE> swept away by a great wave!"):L("^S<S-NAME> sweep(s) <S-HIS-HER> hands over <T-NAMESELF>, @x1.^?",prayingWord(mob)));
					final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_WATER|(auto?CMMsg.MASK_ALWAYS:0),null);
					if((R.okMessage(mob,msg))&&((R.okMessage(mob,msg2))))
					{
						R.send(mob,msg);
						R.send(mob,msg2);
						if((msg.value()<=0)&&(msg2.value()<=0))
						{
							final int harming=CMLib.dice().roll(1,adjustedLevel(mob,asLevel)/numEnemies,numEnemies);
							CMLib.combat().postDamage(mob,target,this,harming,CMMsg.MASK_ALWAYS|CMMsg.TYP_WATER,Weapon.TYPE_BURSTING,"A crashing wave <DAMAGE> <T-NAME>!");
							final int chanceToStay=10+(target.charStats().getStat(CharStats.STAT_STRENGTH)-(mob.phyStats().level()+(2*super.getXLEVELLevel(mob)))*4);
							final int roll=CMLib.dice().rollPercentage();
							if((roll!=1)&&(roll>chanceToStay))
							{
								CMLib.tracking().walk(target,dir,true,false);
								if((!R.isInhabitant(target))&&(target.isMonster()))
									CMLib.tracking().markToWanderHomeLater(target);
							}
						}
					}
				}
				else
					maliciousFizzle(mob,target,L("<S-NAME> sweep(s) <S-HIS-HER> hands over <T-NAMESELF>, @x1, but @x2 does not heed.",prayingWord(mob),hisHerDiety(mob)));
			}
		}
		return success;
	}
}
