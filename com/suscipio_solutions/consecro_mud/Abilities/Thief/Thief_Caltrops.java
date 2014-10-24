package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_Caltrops extends ThiefSkill implements Trap
{
	@Override public String ID() { return "Thief_Caltrops"; }
	private final static String localizedName = CMLib.lang().L("Caltrops");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return Ability.CAN_ROOMS;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_TRAPPING;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"CALTROPS"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	public String caltropTypeName(){return "";}

	@Override public boolean isABomb(){return false;}
	@Override public void activateBomb(){}
	@Override public boolean disabled(){return false;}
	@Override public void disable(){ unInvoke();}
	@Override public void setReset(int Reset){}
	@Override public int getReset(){return 0;}
	@Override public boolean maySetTrap(MOB mob, int asLevel){return false;}
	@Override public boolean canSetTrapOn(MOB mob, Physical P){return false;}
	@Override public List<Item> getTrapComponents() { return new Vector(); }
	@Override public String requiresToSet(){return "";}
	@Override
	public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm)
	{maliciousAffect(mob,P,qualifyingClassLevel+trapBonus,0,-1); return (Trap)P.fetchEffect(ID());}

	@Override public boolean sprung(){return false;}
	@Override
	public void spring(MOB mob)
	{
		if((!invoker().mayIFight(mob))
		||(invoker().getGroupMembers(new HashSet<MOB>()).contains(mob))
		||(CMLib.dice().rollPercentage()<mob.charStats().getSave(CharStats.STAT_SAVE_TRAPS)))
			mob.location().show(mob,affected,this,CMMsg.MSG_OK_ACTION,L("<S-NAME> avoid(s) some @x1caltrops on the floor.",caltropTypeName()));
		else
			CMLib.combat().postDamage(invoker(),mob,null,CMLib.dice().roll(1,6,adjustedLevel(invoker(),0)),
					CMMsg.MASK_MALICIOUS|CMMsg.TYP_JUSTICE,Weapon.TYPE_PIERCING,"The "+caltropTypeName()+"caltrops on the ground <DAMAGE> <T-NAME>.");
		// does not set sprung flag -- as this trap never goes out of use
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if((target!=null)&&(!(target instanceof Room)))
				return Ability.QUALITY_INDIFFERENT;
			target=(target!=null)?target:mob.location();
			if(target.fetchEffect(ID())!=null)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(affected==null) return super.okMessage(myHost,msg);
		if(!(affected instanceof Room)) return super.okMessage(myHost,msg);
		if(invoker()==null) return super.okMessage(myHost,msg);
		final Room room=(Room)affected;
		if((msg.amITarget(room)||room.isInhabitant(msg.source()))
		&&(!msg.amISource(invoker()))
		&&((msg.sourceMinor()==CMMsg.TYP_ENTER)
			||(msg.sourceMinor()==CMMsg.TYP_LEAVE)
			||(msg.sourceMinor()==CMMsg.TYP_FLEE)
			||(msg.sourceMinor()==CMMsg.TYP_ADVANCE)
			||(msg.sourceMinor()==CMMsg.TYP_RETREAT)))
				spring(msg.source());
		return true;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=(givenTarget!=null)?givenTarget:mob.location();
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(L("@x1Caltrops have already been tossed down here.",CMStrings.capitalizeFirstLetter(caltropTypeName())));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			if(mob.location().show(mob,target,(auto?CMMsg.MASK_ALWAYS:0)|CMMsg.MSG_THIEF_ACT,L("<S-NAME> throw(s) down @x1caltrops!",caltropTypeName())))
				maliciousAffect(mob,target,asLevel,0,-1);
			else
				success=false;
		}
		else
			maliciousFizzle(mob,target,L("<S-NAME> fail(s) to throw down <S-HIS-HER> @x1caltrops properly.",caltropTypeName()));
		return success;
	}
}
