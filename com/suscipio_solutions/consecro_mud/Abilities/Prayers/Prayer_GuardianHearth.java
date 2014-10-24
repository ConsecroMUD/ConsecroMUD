package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_GuardianHearth extends Prayer
{
	@Override public String ID() { return "Prayer_GuardianHearth"; }
	private final static String localizedName = CMLib.lang().L("Guardian Hearth");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Guardian Hearth)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_WARDING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return CAN_ROOMS;}
	@Override protected int overrideMana(){return Ability.COST_ALL;}
	@Override public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY;}
	protected static HashSet prots=null;

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected==null)||(!(affected instanceof Room)))
			return super.okMessage(myHost,msg);

		if(prots==null)
		{
			prots=new HashSet();
			final int[] CMMSGMAP=CharStats.CODES.CMMSGMAP();
			for(final int i : CharStats.CODES.SAVING_THROWS())
				if(CMMSGMAP[i]>=0)
				   prots.add(Integer.valueOf(CMMSGMAP[i]));
		}
		final Room R=(Room)affected;
		if(((msg.tool() instanceof Trap)
		||(prots.contains(Integer.valueOf(msg.sourceMinor())))
		||(prots.contains(Integer.valueOf(msg.targetMinor()))))
		   &&(msg.target() instanceof MOB)
		   &&((msg.source()!=msg.target())||(msg.sourceMajor(CMMsg.MASK_ALWAYS))))
		{
			final Set<MOB> H=((MOB)msg.target()).getGroupMembers(new HashSet<MOB>());
			for (final Object element : H)
			{
				final MOB M=(MOB)element;
				if((CMLib.law().doesHavePriviledgesHere(M,R))
				||((text().length()>0)
					&&((M.Name().equals(text()))
						||(M.getClanRole(text())!=null))))
				{
					R.show(((MOB)msg.target()),null,this,CMMsg.MSG_OK_VISUAL,L("The guardian hearth protect(s) <S-NAME>!"));
					break;
				}
			}
		}
		return super.okMessage(myHost,msg);
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=mob.location();
		if(target==null) return false;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(L("This place is already a guarded hearth."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> @x1 to guard this place.^?",prayForWord(mob)));
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
			beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 to guard this place, but <S-IS-ARE> not answered.",prayForWord(mob)));

		return success;
	}
}
