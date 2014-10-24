package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_Panhandling extends ThiefSkill
{
	@Override public String ID() { return "Thief_Panhandling"; }
	private final static String localizedName = CMLib.lang().L("Panhandling");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Panhandling)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"PANHANDLE","PANHANDLING"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STREETSMARTS;}

	Vector mobsHitUp=new Vector();
	int tickTock=0;

	@Override
	public void executeMsg(Environmental oking, CMMsg msg)
	{
		super.executeMsg(oking,msg);
		if(affected instanceof MOB)
		{
			final MOB mob=(MOB)affected;
			if((msg.source()==mob)
			&&(msg.target()==mob.location())
			&&(msg.targetMinor()==CMMsg.TYP_LEAVE))
				unInvoke();
			else
			if((CMLib.flags().isStanding(mob))||(CMLib.flags().isSleeping(mob)))
				unInvoke();
			else
			if((msg.amITarget(mob))&&(msg.targetMinor()==CMMsg.TYP_GIVE))
				msg.addTrailerMsg(CMClass.getMsg(mob,msg.source(),CMMsg.MSG_SPEAK,L("^T<S-NAME> say(s) 'Thank you gov'ner!' to <T-NAME> ^?")));
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		if(affected instanceof MOB)
		{
			tickTock++;
			if(tickTock<2) return true;
			tickTock=0;
			final MOB mob=(MOB)affected;
			for(int i=0;i<mob.location().numInhabitants();i++)
			{
				final MOB mob2=mob.location().fetchInhabitant(i);
				if((mob2!=null)
				&&(CMLib.flags().canBeSeenBy(mob2,mob))
				&&(mob2!=mob)
				&&(!mobsHitUp.contains(mob2))
				&&(proficiencyCheck(mob,0,false)))
				{
					switch(CMLib.dice().roll(1,10,0))
					{
					case 1:
						CMLib.commands().postSay(mob,mob2,L("A little something for a vet please?"),false,false);
						break;
					case 2:
						CMLib.commands().postSay(mob,mob2,L("Spare a gold piece @x1",((mob2.charStats().getStat(CharStats.STAT_GENDER)=='M')?"mister?":"madam?")),false,false);
						break;
					case 3:
						CMLib.commands().postSay(mob,mob2,L("Spare some change?"),false,false);
						break;
					case 4:
						CMLib.commands().postSay(mob,mob2,L("Please @x1, a little something for an poor soul down on @x2 luck?",((mob2.charStats().getStat(CharStats.STAT_GENDER)=='M')?"mister":"madam"),mob.charStats().hisher()),false,false);
						break;
					case 5:
						CMLib.commands().postSay(mob,mob2,L("Hey, I lost my 'Will Work For Food' sign.  Can you spare me the money to buy one?"),false,false);
						break;
					case 6:
						CMLib.commands().postSay(mob,mob2,L("Spread a little joy to an poor soul?"),false,false);
						break;
					case 7:
						CMLib.commands().postSay(mob,mob2,L("Change?"),false,false);
						break;
					case 8:
						CMLib.commands().postSay(mob,mob2,L("Can you spare a little change?"),false,false);
						break;
					case 9:
						CMLib.commands().postSay(mob,mob2,L("Can you spare a little gold?"),false,false);
						break;
					case 10:
						CMLib.commands().postSay(mob,mob2,L("Gold piece for a poor soul down on @x1 luck?",mob.charStats().hisher()),false,false);
						break;
					}
					if(CMLib.dice().rollPercentage()>(mob2.charStats().getSave(CharStats.STAT_SAVE_JUSTICE)+(CMLib.flags().isGood(mob)?10:0)))
					{
						double total=CMLib.beanCounter().getTotalAbsoluteNativeValue(mob2);
						if(total>1.0)
						{
							total=total/(20.0-getXLEVELLevel(mob));
							if(total<1.0) total=1.0;
							final Coins C=CMLib.beanCounter().makeBestCurrency(mob2,total);
							if(C!=null)
							{
								CMLib.beanCounter().subtractMoney(mob2,total);
								mob2.addItem(C);
								mob2.doCommand(CMParms.parse("GIVE \""+C.name()+"\" \""+mob.Name()+"\""),Command.METAFLAG_FORCED);
								if(!C.amDestroyed()) C.putCoinsBack();
							}
						}
					}

					mobsHitUp.addElement(mob2);
					break;
				}
			}
			if((mobsHitUp.size()>0)&&(CMLib.dice().rollPercentage()<10))
				mobsHitUp.removeElementAt(0);
		}
		return true;
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		super.unInvoke();

		if((canBeUninvoked())&&(mob.location()!=null))
			mob.tell(L("You stop panhandling."));
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already panhandling."));
			return false;
		}

		if(!CMLib.flags().isSitting(mob))
		{
			mob.tell(L("You must be sitting!"));
			return false;
		}
		if(mob.location().domainType()!=Room.DOMAIN_OUTDOORS_CITY)
		{
			mob.tell(L("You must be on a city street to panhandle."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		final CMMsg msg=CMClass.getMsg(mob,null,this,auto?CMMsg.MASK_ALWAYS:CMMsg.MSG_DELICATE_SMALL_HANDS_ACT,CMMsg.MSG_DELICATE_SMALL_HANDS_ACT,CMMsg.MSG_DELICATE_SMALL_HANDS_ACT,auto?"":L("<S-NAME> start(s) panhandling."));
		if(!success)
			return beneficialVisualFizzle(mob,null,auto?"":L("<S-NAME> can't seem to get <S-HIS-HER> panhandling act started."));
		else
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			beneficialAffect(mob,target,asLevel,0);
		}
		return success;
	}
}
