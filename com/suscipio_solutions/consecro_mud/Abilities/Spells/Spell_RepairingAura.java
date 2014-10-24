package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Spell_RepairingAura extends Spell
{
	@Override public String ID() { return "Spell_RepairingAura"; }
	private final static String localizedName = CMLib.lang().L("Repairing Aura");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return CAN_ITEMS;}
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_ABJURATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public int overrideMana(){ return 50;}
	public static final int REPAIR_MAX=30;
	public int repairDown=REPAIR_MAX;
	public int adjustedLevel=1;

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_BONUS);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		repairDown-=adjustedLevel;
		if((repairDown<=0)&&(affected instanceof Item))
		{
			repairDown=REPAIR_MAX;
			final Item I=(Item)affected;
			if((I.subjectToWearAndTear())&&(I.usesRemaining()<100))
			{
				if(I.owner() instanceof Room)
					((Room)I.owner()).showHappens(CMMsg.MSG_OK_VISUAL,I,L("<S-NAME> is magically repairing itself."));
				else
				if(I.owner() instanceof MOB)
					((MOB)I.owner()).tell(L("@x1 is magically repairing itself.",I.name()));
				I.setUsesRemaining(I.usesRemaining()+1);
			}
		}
		return true;
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_ANY);
		if(target==null) return false;
		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(L("@x1 is already repairing!",target.name(mob)));
			return false;
		}
		if((!(target instanceof Item))&&(!(target instanceof MOB)))
		{
			mob.tell(L("@x1 would not be affected by this spell.",target.name(mob)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);
		Item realTarget=null;
		if(target instanceof Item)
			realTarget=(Item)target;
		else
		if(target instanceof MOB)
		{
			final Vector choices=new Vector();
			final Vector inventory=new Vector();
			final MOB M=(MOB)target;
			Item I=null;
			for(int i=0;i<M.numItems();i++)
			{
				I=M.getItem(i);
				if((I!=null)&&(I.subjectToWearAndTear())&&(I.fetchEffect(ID())==null))
				{
					if(I.amWearingAt(Wearable.IN_INVENTORY))
						inventory.addElement(I);
					else
						choices.addElement(I);
				}
			}
			Vector chooseFrom=inventory;
			if(choices.size()<3)
				inventory.addAll(choices);
			else
				chooseFrom=choices;
			if(chooseFrom.size()<1)
				success=false;
			else
				realTarget=(Item)chooseFrom.elementAt(CMLib.dice().roll(1,chooseFrom.size(),-1));
		}

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAMESELF>, incanting.^?"));
			final CMMsg msg2=(target==realTarget)?null:CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),null);
			if(mob.location().okMessage(mob,msg)
			&&(realTarget!=null)
			&&((msg2==null)||mob.location().okMessage(mob,msg2)))
			{
				mob.location().send(mob,msg);
				if(msg2!=null) mob.location().send(mob,msg2);
				mob.location().show(mob,realTarget,CMMsg.MSG_OK_ACTION,L("<T-NAME> attain(s) a repairing aura."));
				beneficialAffect(mob,realTarget,asLevel,0);
				final Spell_RepairingAura A=(Spell_RepairingAura)realTarget.fetchEffect(ID());
				if(A!=null) A.adjustedLevel=adjustedLevel(mob,asLevel);
				realTarget.recoverPhyStats();
				mob.recoverPhyStats();
				mob.location().recoverRoomStats();
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAMESELF>, incanting, but nothing happens."));

		// return whether it worked
		return success;
	}
}
