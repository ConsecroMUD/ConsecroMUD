package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Spell_FlamingSword extends Spell
{
	@Override public String ID() { return "Spell_FlamingSword"; }
	private final static String localizedName = CMLib.lang().L("Flaming Sword");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){return "";}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return CAN_ITEMS;}
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_LIGHTSOURCE);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(msg.tool()==affected)
		&&((msg.value())>0)
		&&(msg.target() instanceof MOB)
		&&(affected instanceof Item)
		&&(!((MOB)msg.target()).amDead())
		&&(msg.source()==((Item)affected).owner()))
		{
			final Room room=msg.source().location();
			final CMMsg msg2=CMClass.getMsg(msg.source(),msg.target(),affected,
					CMMsg.MSG_OK_ACTION,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_FIRE,CMMsg.MSG_NOISYMOVEMENT,null);
			if((room!=null) && (room.okMessage(msg.source(),msg2)))
			{
				room.send(msg.source(), msg2);
				if(msg2.value()<=0)
				{
					final int flameDamage = CMLib.dice().roll(1, (2+affected.basePhyStats().level())/2, 1);
					CMLib.combat().postDamage(msg.source(),(MOB)msg.target(),null,flameDamage,
							CMMsg.TYP_FIRE,Weapon.TYPE_BURNING,name()+" <DAMAGE> <T-NAME>!");
				}
			}
		}
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		final Environmental item=affected;
		if(item==null) return;
		final Room room=CMLib.map().roomLocation(item);
		if((canBeUninvoked())&&(room!=null))
			room.showHappens(CMMsg.MSG_OK_VISUAL,item,L("<S-YOUPOSS> flaming sword is consumed!"));
		super.unInvoke();
		if((canBeUninvoked())&&(room!=null))
		{
			room.recoverRoomStats();
		  item.destroy();
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null)
		{
			return false;
		}
		if((!(target instanceof Weapon))
		||(((Weapon)target).weaponClassification()!=Weapon.CLASS_SWORD)
		||(((((Item)target).material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_METAL)
			&&((((Item)target).material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_MITHRIL)))
		{
			mob.tell(L("This magic only affects metal swords."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("^S<T-NAME> erupts into flame!"):L("^S<S-NAME> invoke(s) a writhing flame around <T-NAMESELF>!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
				mob.location().recoverRoomStats(); // attempt to handle followers
			}
		}
		else
			beneficialWordsFizzle(mob,mob.location(),L("<S-NAME> attempt(s) to invoke a flame, but cause(s) a puff of smoke."));

		return success;
	}
}
