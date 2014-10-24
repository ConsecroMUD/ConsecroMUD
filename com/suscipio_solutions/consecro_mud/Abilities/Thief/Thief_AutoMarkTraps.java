package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_AutoMarkTraps extends ThiefSkill
{
	@Override public String ID() { return "Thief_AutoMarkTraps"; }
	@Override public String displayText() {return "(Automarking traps)";}
	private final static String localizedName = CMLib.lang().L("AutoMark Traps");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"AUTOMARKTRAPS"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public int classificationCode(){	return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_ALERT;}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected instanceof MOB)
		&&(msg.targetMinor()==CMMsg.TYP_ENTER)
		&&(msg.source()==affected)
		&&(msg.target() instanceof Room)
		&&(msg.tool() instanceof Exit)
		&&(((MOB)affected).location()!=null))
		{
			final Room R=(Room)msg.target();
			Room R2=null;
			dropem(msg.source(),R);
			Exit E=null;
			Item I=null;
			for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
			{
				R2=R.getRoomInDir(d);
				E=R.getExitInDir(d);
				if((E!=null)&&(CMLib.utensils().fetchMyTrap(E)!=null)) dropem(msg.source(),E);
				E=R.getReverseExit(d);
				if((E!=null)&&(CMLib.utensils().fetchMyTrap(E)!=null)) dropem(msg.source(),E);
				if((R2!=null)&&(CMLib.utensils().fetchMyTrap(R2)!=null)) dropem(msg.source(),R2);
			}
			for(int i=0;i<R.numItems();i++)
			{
				I=R.getItem(i);
				if((I.container()==null)&&(CMLib.utensils().fetchMyTrap(I)!=null))
					dropem(msg.source(),I);
			}
		}
	}

	public void dropem(MOB mob, Physical P)
	{
		Ability A=mob.fetchAbility("Thief_DetectTraps");
		if(A==null)
		{
			A=CMClass.getAbility("Thief_DetectTraps");
			A.setProficiency(100);
		}
		final CharState savedState=(CharState)mob.curState().copyOf();
		if(A.invoke(mob,P,false,0))
		{
			A=mob.fetchAbility("Thief_MarkTrapped");
			if(A==null)
			{
				A=CMClass.getAbility("Thief_MarkTrapped");
				A.setProficiency(100);
			}
			A.invoke(mob,P,false,0);
		}
		mob.curState().setMana(savedState.getMana());
		mob.curState().setHitPoints(savedState.getHitPoints());
		mob.curState().setMovement(savedState.getMovement());
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=(givenTarget instanceof MOB)?(MOB)givenTarget:mob;
		if(target.fetchEffect(ID())!=null)
		{
			target.tell(L("You are no longer automatically marking traps."));
			target.delEffect(mob.fetchEffect(ID()));
			return false;
		}
		if((!auto)&&(target.fetchAbility("Thief_MarkTrapped")==null))
		{
			target.tell(L("You don't know how to mark traps yet!"));
			return false;
		}
		if((!auto)&&(target.fetchAbility("Thief_DetectTraps")==null))
		{
			target.tell(L("You don't know how to detect traps yet!"));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			target.tell(L("You will now automatically mark traps when you enter a room."));
			beneficialAffect(mob,target,asLevel,0);
			final Ability A=mob.fetchEffect(ID());
			if(A!=null) A.makeLongLasting();
			dropem(target,target.location());
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to mark traps, but can't seem to concentrate."));
		return success;
	}
}
