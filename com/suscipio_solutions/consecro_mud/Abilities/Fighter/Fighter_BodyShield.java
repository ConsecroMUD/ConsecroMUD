package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Fighter_BodyShield extends FighterSkill
{
	@Override public String ID() { return "Fighter_BodyShield"; }
	private final static String localizedName = CMLib.lang().L("Body Shield");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_GRAPPLING;}
	public boolean doneThisRound=false;

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;
		if(msg.amITarget(mob)
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(!mob.amDead())
		&&(!CMLib.flags().isSleeping(mob))
		&&(msg.source()!=mob.getVictim())
		&&(msg.source()!=mob)
		&&((msg.value())>0)
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Weapon)
		&&(mob.getVictim()!=null)
		&&(mob.getVictim().fetchEffect("Fighter_Pin")!=null)
		&&(!doneThisRound)
		&&(mob.getVictim().baseWeight()>=(mob.baseWeight()/2)))
		{
			final Ability A=mob.fetchEffect("Fighter_Pin");
			if((A!=null)&&(A.invoker()==mob))
			{
				doneThisRound=true;
				final int regain=(int)Math.round(CMath.mul((msg.value()),CMath.div(proficiency(),100.0)));
				msg.setValue(msg.value()-regain);
				final CMMsg msg2=CMClass.getMsg(mob,mob.getVictim(),this,CMMsg.MSG_DAMAGE,L("<S-NAME> use(s) <T-NAMESELF> as a body shield!"));
				msg2.setValue(regain);
				msg.addTrailerMsg(msg2);
				helpProficiency(mob, 0);
			}
		}
		return true;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(tickID==Tickable.TICKID_MOB)
			doneThisRound=false;
		return super.tick(ticking,tickID);
	}
}
