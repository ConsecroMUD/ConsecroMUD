package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_Trap extends ThiefSkill
{
	@Override public String ID() { return "Thief_Trap"; }
	private final static String localizedName = CMLib.lang().L("Lay Traps");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS|Ability.CAN_EXITS|Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS|Ability.CAN_EXITS|Ability.CAN_ROOMS;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_TRAPPING;}
	private static final String[] triggerStrings =I(new String[] {"TRAP"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}

	protected int maxLevel(){return Integer.MAX_VALUE;}

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
		Trap theTrap=null;
		final Vector traps=new Vector();
		int qualifyingClassLevel=CMLib.ableMapper().qualifyingClassLevel(mob,this)+(getXLEVELLevel(mob))-CMLib.ableMapper().qualifyingLevel(mob,this)+1;
		if(qualifyingClassLevel>maxLevel()) qualifyingClassLevel=maxLevel();
		for(final Enumeration<Ability> a=CMClass.abilities();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A instanceof Trap)
			   &&(!((Trap)A).isABomb())
			   &&(((Trap)A).maySetTrap(mob,qualifyingClassLevel)))
				traps.addElement(A);
		}
		Physical trapThis=givenTarget;
		if(trapThis!=null)
		{
			int cuts=0;
			while(((++cuts)<100)&&(theTrap==null))
			{
				theTrap=(Trap)traps.elementAt(CMLib.dice().roll(1,traps.size(),-1));
				if(!theTrap.canSetTrapOn(mob,trapThis))
					theTrap=null;
			}
		}
		else
		if(CMParms.combine(commands,0).equalsIgnoreCase("list"))
		{
			final StringBuffer buf=new StringBuffer(L("@x1 @x2 Requires\n\r",CMStrings.padRight(L("Trap Name"),15),CMStrings.padRight(L("Affects"),17)));
			for(int r=0;r<traps.size();r++)
			{
				final Trap T=(Trap)traps.elementAt(r);
				buf.append(CMStrings.padRight(T.name(),15)+" ");
				if(T.canAffect(Ability.CAN_ROOMS))
					buf.append(CMStrings.padRight(L("Rooms"),17)+" ");
				else
				if(T.canAffect(Ability.CAN_EXITS))
					buf.append(CMStrings.padRight(L("Exits, Containers"),17)+" ");
				else
				if(T.canAffect(Ability.CAN_ITEMS))
					buf.append(CMStrings.padRight(L("Items"),17)+" ");
				else
					buf.append(CMStrings.padRight(L("Unknown"),17)+" ");
				buf.append(T.requiresToSet()+"\n\r");
			}
			if(mob.session()!=null) mob.session().rawPrintln(buf.toString());
			return true;
		}
		else
		{
			if(mob.isInCombat())
			{
				mob.tell(L("You are too busy to be laying traps at the moment!"));
				return false;
			}

			final String cmdWord=triggerStrings()[0].toLowerCase();
			if(commands.size()<2)
			{
				mob.tell(L("Trap what, with what kind of trap? Use @x1 list for a list.",cmdWord));
				return false;
			}
			String name;
			if(commands.get(0).toString().equalsIgnoreCase("room")||commands.get(0).toString().equalsIgnoreCase("here"))
			{
				name=CMParms.combine(commands,1);
				while(commands.size()>1)
					commands.removeElementAt(commands.size()-1);
			}
			else
			{
				name=(String)commands.lastElement();
				commands.removeElementAt(commands.size()-1);
			}
			for(int r=0;r<traps.size();r++)
			{
				final Trap T=(Trap)traps.elementAt(r);
				if(T.name().equalsIgnoreCase(name))
					theTrap=T;
			}
			if(theTrap==null)
			for(int r=0;r<traps.size();r++)
			{
				final Trap T=(Trap)traps.elementAt(r);
				if(CMLib.english().containsString(T.name(),name))
					theTrap=T;
			}
			if(theTrap==null)
			{
				mob.tell(L("'@x1' is not a valid trap name.  Try @x2 LIST.",name,cmdWord.toUpperCase()));
				return false;
			}

			final String whatToTrap=CMParms.combine(commands,0);
			final int dirCode=Directions.getGoodDirectionCode(whatToTrap);
			if(whatToTrap.equalsIgnoreCase("room")||whatToTrap.equalsIgnoreCase("here"))
				trapThis=mob.location();
			if((dirCode>=0)&&(trapThis==null))
				trapThis=mob.location().getExitInDir(dirCode);
			if(trapThis==null)
				trapThis=this.getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_UNWORNONLY);
			if(trapThis==null) return false;
			if((!auto)&&(!theTrap.canSetTrapOn(mob,trapThis)))
				return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,+((mob.phyStats().level()+(getXLEVELLevel(mob)*2)
											 -trapThis.phyStats().level())*3),auto);
		final Trap theOldTrap=CMLib.utensils().fetchMyTrap(trapThis);
		if(theOldTrap!=null)
		{
			if(theOldTrap.disabled())
				success=false;
			else
			{
				theOldTrap.spring(mob);
				return false;
			}
		}

		final CMMsg msg=CMClass.getMsg(mob,trapThis,this,auto?CMMsg.MSG_OK_ACTION:CMMsg.MSG_THIEF_ACT,CMMsg.MASK_ALWAYS|CMMsg.MSG_THIEF_ACT,CMMsg.MSG_OK_ACTION,(auto?L("@x1 begins to glow!",trapThis.name()):L("<S-NAME> attempt(s) to lay a trap on <T-NAMESELF>.")));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			if(success)
			{
				mob.tell(L("You have completed your task."));
				boolean permanent=false;
				if((trapThis instanceof Room)
				&&(CMLib.law().doesOwnThisProperty(mob,((Room)trapThis))))
					permanent=true;
				else
				if(trapThis instanceof Exit)
				{
					final Room R=mob.location();
					Room R2=null;
					for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
						if(R.getExitInDir(d)==trapThis)
						{ R2=R.getRoomInDir(d); break;}
					if((CMLib.law().doesOwnThisProperty(mob,R))
					||((R2!=null)&&(CMLib.law().doesOwnThisProperty(mob,R2))))
						permanent=true;
				}
				if(theTrap!=null)
				{
					theTrap.setTrap(mob,trapThis,getXLEVELLevel(mob),adjustedLevel(mob,asLevel),permanent);
					if(permanent)
						CMLib.database().DBUpdateRoom(mob.location());
				}
			}
			else
			{
				if((CMLib.dice().rollPercentage()>50)&&(theTrap!=null))
				{
					final Trap T=theTrap.setTrap(mob,trapThis,getXLEVELLevel(mob),adjustedLevel(mob,asLevel),false);
					mob.location().show(mob,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> trigger(s) the trap on accident!"));
					T.spring(mob);
				}
				else
				{
					mob.tell(L("You fail in your attempt."));
				}
			}
		}
		return success;
	}
}
