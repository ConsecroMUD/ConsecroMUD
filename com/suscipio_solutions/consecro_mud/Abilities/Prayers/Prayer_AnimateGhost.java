package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_AnimateGhost extends Prayer
{
	@Override public String ID() { return "Prayer_AnimateGhost"; }
	private final static String localizedName = CMLib.lang().L("Animate Ghost");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_DEATHLORE;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public int enchantQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}
	@Override protected int canTargetCode(){return CAN_ITEMS;}

	public void makeGhostFrom(Room R, DeadBody body, MOB mob, int level)
	{
		String race="a";
		if((body.charStats()!=null)&&(body.charStats().getMyRace()!=null))
			race=CMLib.english().startWithAorAn(body.charStats().getMyRace().name()).toLowerCase();
		String description=body.mobDescription();
		if(description.trim().length()==0)
			description="It looks dead.";
		else
			description+="\n\rIt also looks dead.";

		final MOB newMOB=CMClass.getMOB("GenUndead");
		newMOB.setName(race+((mob==null)?" poltergeist":" ghost"));
		newMOB.setDescription(description);
		newMOB.setDisplayText(L("@x1 is here",newMOB.Name()));
		newMOB.basePhyStats().setLevel(level+(super.getX1Level(mob)*2)+super.getXLEVELLevel(mob));
		newMOB.baseCharStats().setStat(CharStats.STAT_GENDER,body.charStats().getStat(CharStats.STAT_GENDER));
		newMOB.baseCharStats().setMyRace(CMClass.getRace("Spirit"));
		newMOB.baseCharStats().setBodyPartsFromStringAfterRace(body.charStats().getBodyPartsAsString());
		final Ability P=CMClass.getAbility("Prop_StatTrainer");
		if(P!=null)
		{
			P.setMiscText("NOTEACH STR=2 INT=10 WIS=10 CON=10 DEX=35 CHA=2");
			newMOB.addNonUninvokableEffect(P);
		}
		newMOB.recoverCharStats();
		newMOB.basePhyStats().setAttackAdjustment(10);
		newMOB.basePhyStats().setDisposition(PhyStats.IS_FLYING|((mob==null)?PhyStats.IS_INVISIBLE:0));
		newMOB.basePhyStats().setSensesMask(PhyStats.CAN_SEE_DARK|PhyStats.CAN_SEE_INVISIBLE);
		newMOB.basePhyStats().setDamage(4);
		CMLib.factions().setAlignment(newMOB,Faction.Align.EVIL);
		newMOB.baseState().setHitPoints(10*newMOB.basePhyStats().level());
		newMOB.baseState().setMovement(CMLib.leveler().getLevelMove(newMOB));
		newMOB.basePhyStats().setArmor(CMLib.leveler().getLevelMOBArmor(newMOB));
		newMOB.baseState().setMana(100);
		newMOB.addNonUninvokableEffect(CMClass.getAbility("Prop_ModExperience"));
		final Ability A=CMClass.getAbility("Immunities");
		if(A!=null)
		{
			A.setMiscText("all");
			newMOB.addNonUninvokableEffect(A);
		}
		Behavior B=CMClass.getBehavior("Aggressive");
		if((B!=null)&&(mob!=null)){ B.setParms("+NAMES \"-"+mob.Name()+"\"");}
		if(B!=null) newMOB.addBehavior(B);
		newMOB.recoverCharStats();
		newMOB.recoverPhyStats();
		if(mob==null)
		{
			B=CMClass.getBehavior("Thiefness");
			if(B!=null) newMOB.addBehavior(B);
		}
		newMOB.recoverCharStats();
		newMOB.recoverPhyStats();
		newMOB.recoverMaxState();
		newMOB.resetToMaxState();
		newMOB.text();
		newMOB.bringToLife(R,true);
		CMLib.beanCounter().clearZeroMoney(newMOB,null);
		R.showOthers(newMOB,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> appears!"));
		int it=0;
		while(it<newMOB.location().numItems())
		{
			final Item item=newMOB.location().getItem(it);
			if((item!=null)&&(item.container()==body))
			{
				final CMMsg msg2=CMClass.getMsg(newMOB,body,item,CMMsg.MSG_GET,null);
				newMOB.location().send(newMOB,msg2);
				final CMMsg msg4=CMClass.getMsg(newMOB,item,null,CMMsg.MSG_GET,null);
				newMOB.location().send(newMOB,msg4);
				final CMMsg msg3=CMClass.getMsg(newMOB,item,null,CMMsg.MSG_WEAR,null);
				newMOB.location().send(newMOB,msg3);
				if(!newMOB.isMine(item))
					it++;
				else
					it=0;
			}
			else
				it++;
		}
		body.destroy();
		newMOB.setStartRoom(null);
		R.show(newMOB,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> begin(s) to rise!"));
		R.recoverRoomStats();
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_UNWORNONLY);
		if(target==null) return false;

		if(target==mob)
		{
			mob.tell(L("@x1 doesn't look dead yet.",target.name(mob)));
			return false;
		}
		if(!(target instanceof DeadBody))
		{
			mob.tell(L("You can't animate that."));
			return false;
		}

		final DeadBody body=(DeadBody)target;
		if(body.playerCorpse()||(body.mobName().length()==0)
		||((body.charStats()!=null)&&(body.charStats().getMyRace()!=null)&&(body.charStats().getMyRace().racialCategory().equalsIgnoreCase("Undead"))))
		{
			mob.tell(L("You can't animate that."));
			return false;
		}
		if(body.basePhyStats().level()<15)
		{
			mob.tell(L("This creature is too weak to create a ghost from."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> @x1 to animate <T-NAMESELF> as a ghost.^?",prayForWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				makeGhostFrom(mob.location(),body,mob,14);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 to animate <T-NAMESELF>, but fail(s) miserably.",prayForWord(mob)));

		// return whether it worked
		return success;
	}
}
