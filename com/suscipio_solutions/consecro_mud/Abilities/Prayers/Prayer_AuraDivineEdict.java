package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_AuraDivineEdict extends Prayer
{
	@Override public String ID() { return "Prayer_AuraDivineEdict"; }
	private final static String localizedName = CMLib.lang().L("Aura of the Divine Edict");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Edict Aura)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_EVANGELISM;}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int overrideMana(){return Ability.COST_ALL;}
	@Override public long flags(){return Ability.FLAG_HOLY;}
	protected String godName="the gods";
	protected boolean noRecurse=false;


	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if((canBeUninvoked())&&(mob.location()!=null)&&(!mob.amDead()))
			mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("The divine edict aura around <S-NAME> fades."));
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if((affected==null)||(!(affected instanceof MOB))||(noRecurse))
			return true;

		if(CMath.bset(msg.sourceMajor(),CMMsg.MASK_MALICIOUS)
		   ||CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
		{
			msg.source().tell(L("@x1 DEMANDS NO FIGHTING!",godName));
			msg.source().makePeace();
			return false;
		}
		else
		if((msg.source()==invoker())
		&&(msg.targetMinor()==CMMsg.TYP_SPEAK)
		&&(!msg.sourceMajor(CMMsg.MASK_ALWAYS))
		&&(msg.target() instanceof MOB)
		&&(((MOB)msg.target()).phyStats().level()<invoker().phyStats().level()+(super.getXLEVELLevel(invoker())*2))
		&&(msg.sourceMessage()!=null)
		&&(CMStrings.getSayFromMessage(msg.sourceMessage().toUpperCase()).equals(CMStrings.getSayFromMessage(msg.sourceMessage()))))
		{
			final Vector<String> V=CMParms.parse("ORDER \""+msg.target().Name()+"\" "+CMStrings.getSayFromMessage(msg.sourceMessage()));
			final CMObject O=CMLib.english().findCommand((MOB)msg.target(),(List)V.clone());
			if((!((MOB)msg.target()).isMonster())
			&&(CMClass.classID(O).equalsIgnoreCase("DROP")
			   ||CMClass.classID(O).equalsIgnoreCase("SELL")
			   ||CMClass.classID(O).equalsIgnoreCase("GIVE")))
			{
			   msg.source().tell(L("The divine care not about such orders."));
			   return false;
			}
			noRecurse=true;
			final String oldLiege=((MOB)msg.target()).getLiegeID();
			((MOB)msg.target()).setLiegeID(msg.source().Name());
			msg.source().doCommand(V,Command.METAFLAG_FORCED);
			((MOB)msg.target()).setLiegeID(oldLiege);
			noRecurse=false;
			return false;
		}
		noRecurse=false;
		return true;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected==null)||(!(affected instanceof Room)))
			return super.tick(ticking,tickID);

		if(!super.tick(ticking,tickID))
			return false;
		if(invoker()==null) return true;

		final Room R=invoker().location();
		for(int i=0;i<R.numInhabitants();i++)
		{
			final MOB M=R.fetchInhabitant(i);
			if((M!=null)&&(M.isInCombat()))
			{
				M.tell(L("@x1 DEMANDS NO FIGHTING!",invoker().getWorshipCharID().toUpperCase()));
				M.makePeace();
			}
		}
		return true;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;

		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(target,null,null,L("The aura of the divine edict is already with <S-NAME>."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> @x1 for the aura of the divine edict.^?",prayWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				godName="THE GODS";
				if(mob.getWorshipCharID().length()>0)
					godName=mob.getWorshipCharID().toUpperCase();
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 for an aura of divine edict, but <S-HIS-HER> plea is not answered.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
