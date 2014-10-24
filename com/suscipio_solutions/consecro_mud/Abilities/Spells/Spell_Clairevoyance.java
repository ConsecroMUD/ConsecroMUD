package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Deity;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.collections.DVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Spell_Clairevoyance extends Spell
{
	@Override public String ID() { return "Spell_Clairevoyance"; }
	private final static String localizedName = CMLib.lang().L("Clairevoyance");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){	return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;	}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	public static final DVector scries=new DVector(2);

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		if(canBeUninvoked()) scries.removeElement(mob);
		if((canBeUninvoked())&&(invoker!=null))
			invoker.tell(invoker,mob,null,L("Your visions of <T-NAME> fade."));
		super.unInvoke();

	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected instanceof MOB)
		&&(msg.amISource((MOB)affected))
		&&((msg.sourceMinor()==CMMsg.TYP_LOOK)||(msg.sourceMinor()==CMMsg.TYP_EXAMINE)))
		{
			final Environmental target=msg.target();
			if((invoker!=null)
			&&(target!=null)
			&&((invoker.location()!=((MOB)affected).location())||(!(target instanceof Room))))
			{
				final CMMsg newAffect=CMClass.getMsg(invoker,target,msg.sourceMinor(),null);
				target.executeMsg(target,newAffect);
			}
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((auto||mob.isMonster())&&((commands.size()<1)||(((String)commands.firstElement()).equals(mob.name()))))
		{
			commands.clear();
			MOB M=null;
			int tries=0;
			while(((++tries)<100)&&(M==null))
			{
				final Room R=CMLib.map().getRandomRoom();
				if(R.numInhabitants()>0)
					M=R.fetchRandomInhabitant();
				if((M!=null)&&(M.name().equals(mob.name())))
					M=null;
			}
			if(M!=null)
				commands.addElement(M.Name());
		}
		if(commands.size()<1)
		{
			final StringBuffer scryList=new StringBuffer("");
			for(int e=0;e<scries.size();e++)
				if(scries.elementAt(e,2)==mob)
					scryList.append(((e>0)?", ":"")+((MOB)scries.elementAt(e,1)).name());
			if(scryList.length()>0)
				mob.tell(L("Cast on or revoke from whom?  You currently have @x1 on the following: @x2.",name(),scryList.toString()));
			else
				mob.tell(L("Cast on whom?"));
			return false;
		}
		final String mobName=CMParms.combine(commands,0).trim().toUpperCase();
		MOB target=null;
		if(givenTarget instanceof MOB)
			target=(MOB)givenTarget;
		if(target==null)
			target=mob.location().fetchInhabitant(mobName);
		if(target==null)
		{
			try
			{
				List<MOB> targets=CMLib.map().findInhabitants(mob.location().getArea().getProperMap(), mob, mobName, 10);
				if(targets.size()==0)
					targets=CMLib.map().findInhabitants(CMLib.map().rooms(), mob, mobName, 10);
				if(targets.size()>0)
					target=targets.get(CMLib.dice().roll(1,targets.size(),-1));
			}catch(final NoSuchElementException nse){}
		}
		if(target instanceof Deity) target=null;
		Room newRoom=mob.location();
		if(target!=null)
			newRoom=target.location();
		else
		{
			mob.tell(L("You can't seem to focus on '@x1'.",mobName));
			return false;
		}

		if(mob==target)
		{
			mob.tell(L("You can't cast this on yourself!"));
			return false;
		}

		final Ability A=target.fetchEffect(ID());
		if((A!=null)&&(A.invoker()==mob))
		{
			A.unInvoke();
			return true;
		}
		else
		if((A!=null)||(scries.contains(target)))
		{
			mob.tell(L("You can't seem to focus on '@x1'.",mobName));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> invoke(s) clairevoyance, calling '@x1'.^?",mobName));
			final CMMsg msg2=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),null);
			if((mob.location().okMessage(mob,msg))&&((newRoom==mob.location())||(newRoom.okMessage(mob,msg2))))
			{
				mob.location().send(mob,msg);
				if(newRoom!=mob.location()) newRoom.send(target,msg2);
				scries.addElement(target,mob);
				beneficialAffect(mob,target,asLevel,0);
			}

		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to invoke clairevoyance, but fizzle(s) the spell."));


		// return whether it worked
		return success;
	}
}
