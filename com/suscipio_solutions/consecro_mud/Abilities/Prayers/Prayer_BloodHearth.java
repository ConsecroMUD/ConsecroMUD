package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_BloodHearth extends Prayer
{
	@Override public String ID() { return "Prayer_BloodHearth"; }
	private final static String localizedName = CMLib.lang().L("Blood Hearth");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Blood Hearth)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_CORRUPTION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return CAN_ROOMS;}
	@Override protected int overrideMana(){return Ability.COST_ALL;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected==null)||(!(affected instanceof Room)))
			return super.okMessage(myHost,msg);

		final Room R=(Room)affected;

		if(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		{
			final Set<MOB> H=msg.source().getGroupMembers(new HashSet<MOB>());
			for (final Object element : H)
			{
				final MOB M=(MOB)element;
				if((CMLib.law().doesHavePriviledgesHere(M,R))
				||((text().length()>0)
					&&((M.Name().equals(text()))
						||(M.getClanRole(text())!=null))))
				{
					msg.setValue(msg.value()+(msg.value()/2));
					break;
				}
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if((mob!=null)&&(target instanceof Room))
		{
			if(!CMLib.law().doesOwnThisProperty(mob,mob.location()))
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=mob.location();
		if(target==null) return false;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(L("This place is already a blood hearth."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> @x1 to fill this place with blood.^?",prayForWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				setMiscText(mob.Name());
				if((target instanceof Room)
				&&(CMLib.law().doesOwnThisProperty(mob,((Room)target))))
				{
					final String landOwnerName=CMLib.law().getLandOwnerName((Room)target);
					if(CMLib.clans().getClan(landOwnerName)!=null)
						setMiscText(landOwnerName);
					target.addNonUninvokableEffect((Ability)this.copyOf());
					CMLib.database().DBUpdateRoom((Room)target);
				}
				else
					beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 to fill this place with blood, but <S-IS-ARE> not answered.",prayForWord(mob)));

		return success;
	}
}
