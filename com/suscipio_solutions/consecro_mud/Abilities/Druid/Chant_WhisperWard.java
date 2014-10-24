package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Chant_WhisperWard extends Chant implements Trap
{
	@Override public String ID() { return "Chant_WhisperWard"; }
	private final static String localizedName = CMLib.lang().L("Whisperward");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Whisperward)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PRESERVING;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return Ability.CAN_EXITS|Ability.CAN_ITEMS|Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return Ability.CAN_EXITS|Ability.CAN_ITEMS|Ability.CAN_ROOMS;}
	Room myRoomContainer=null;
	int myTrigger=CMMsg.TYP_ENTER;
	boolean waitingForLook=false;

	@Override public boolean isABomb(){return false;}
	@Override public void activateBomb(){}
	@Override public void setReset(int Reset){}
	@Override public int getReset(){return 0;}
	@Override public boolean maySetTrap(MOB mob, int asLevel){return false;}
	@Override public boolean canSetTrapOn(MOB mob, Physical P){return false;}
	@Override public List<Item> getTrapComponents() { return new Vector();}
	@Override public String requiresToSet(){return "";}
	@Override
	public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm)
	{beneficialAffect(mob,P,qualifyingClassLevel+trapBonus,0); return (Trap)P.fetchEffect(ID());}

	@Override public boolean disabled(){return false;}
	@Override public boolean sprung(){return false;}
	@Override public void disable(){unInvoke();}
	@Override
	public void spring(MOB M)
	{
		doMyThing();
	}

	public void doMyThing()
	{
		if(invoker!=null)
			invoker.tell(L("** You hear the wind whisper to you; your ward has been triggered."));
		unInvoke();
		return;
	}
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);


		if(affected==null)
		{
			this.unInvoke();
			return;
		}

		if(msg.amITarget(myRoomContainer))
		{
			if((waitingForLook)&&(msg.targetMinor()==CMMsg.TYP_LOOK))
			{
				doMyThing();
				return;
			}
			else
			if(msg.targetMinor()==myTrigger)
				waitingForLook=true;
		}
		else
		if(msg.amITarget(affected))
		{
			if((msg.targetMinor()==myTrigger)&&(!CMLib.flags().isSneaking(msg.source())))
			{
				doMyThing();
				return;
			}
			else
			if((myTrigger==CMMsg.TYP_GET)
			&&((msg.targetMinor()==CMMsg.TYP_OPEN)
				 ||(msg.targetMinor()==CMMsg.TYP_GIVE)
				 ||(msg.targetMinor()==CMMsg.TYP_DELICATE_HANDS_ACT)
				 ||(msg.targetMinor()==CMMsg.TYP_JUSTICE)
				 ||(msg.targetMinor()==CMMsg.TYP_GENERAL)
				 ||(msg.targetMinor()==CMMsg.TYP_LOCK)
				 ||(msg.targetMinor()==CMMsg.TYP_PULL)
				 ||(msg.targetMinor()==CMMsg.TYP_PUSH)
				 ||(msg.targetMinor()==CMMsg.TYP_UNLOCK)))
			{
				doMyThing();
				return;
			}
		}

	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{

		if(commands.size()<2)
		{
			mob.tell(L("You must specify:\n\r What object you want the spell cast on.\n\r AND Whether it is triggered by TOUCH, HOLD, WIELD, WEAR, or someone ENTERing the same room. "));
			return false;
		}
		final String triggerStr=((String)commands.lastElement()).trim().toUpperCase();

		if(triggerStr.startsWith("HOLD"))
			myTrigger=CMMsg.TYP_HOLD;
		else
		if(triggerStr.startsWith("WIELD"))
			myTrigger=CMMsg.TYP_WIELD;
		else
		if(triggerStr.startsWith("WEAR"))
			myTrigger=CMMsg.TYP_WEAR;
		else
		if(triggerStr.startsWith("TOUCH"))
			myTrigger=CMMsg.TYP_GET;
		else
		if(triggerStr.startsWith("ENTER"))
			myTrigger=CMMsg.TYP_ENTER;
		else
		{
			mob.tell(L("You must specify the trigger event that will cause the wind to whisper to you.\n\r'@x1' is not correct, but you can try TOUCH, WEAR, WIELD, HOLD, or ENTER.\n\r",triggerStr));
			return false;
		}

		Physical target;
		final String itemName=CMParms.combine(commands,0,commands.size()-1);
		if(itemName.equalsIgnoreCase("room"))
			target=mob.location();
		else
			target=mob.location().fetchFromMOBRoomFavorsItems(mob,null,itemName,Wearable.FILTER_UNWORNONLY);
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",((String)commands.elementAt(0))));
			return false;
		}
		if(target instanceof MOB)
		{
			mob.tell(L("You can't can't cast this on @x1.",target.name(mob)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L("^S<S-NAME> chant(s) to <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				myRoomContainer=mob.location();
				beneficialAffect(mob,target,asLevel,0);
			}

		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> chant(s) to <T-NAMESELF>, but the magic fizzles."));


		// return whether it worked
		return success;
	}
}
