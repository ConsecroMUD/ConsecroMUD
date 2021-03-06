package com.suscipio_solutions.consecro_mud.Abilities.Ranger;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Ranger_Enemy1 extends StdAbility
{
	@Override public String ID() { return "Ranger_Enemy1"; }
	private final static String localizedName = CMLib.lang().L("Favored Enemy 1");
	@Override public String name() { return localizedName; }
	@Override public String displayText() { return L("(Enemy of the "+text()+")"); }
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_MARTIALLORE;}

	@Override
	public String text()
	{
		if(miscText.length()==0)
		{
			if(!(affected instanceof MOB))
				return super.text();
			final MOB mob=(MOB)affected;
			final Vector choices=new Vector();
			for(final Enumeration r=CMClass.races();r.hasMoreElements();)
			{
				final Race R=(Race)r.nextElement();
				if((!choices.contains(R.racialCategory()))
				&&(CMath.bset(R.availabilityCode(),Area.THEME_FANTASY)))
					choices.addElement(R.racialCategory());
			}
			for(int a=0;a<mob.numAbilities();a++)
			{
				final Ability A=mob.fetchAbility(a);
				if((A instanceof Ranger_Enemy1)
				   &&(((Ranger_Enemy1)A).miscText.length()>0))
					choices.remove(((Ranger_Enemy1)A).miscText);
			}
			for(final Enumeration<Ability> a=mob.effects();a.hasMoreElements();)
			{
				final Ability A=a.nextElement();
				if((A instanceof Ranger_Enemy1)
				   &&(((Ranger_Enemy1)A).miscText.length()>0))
					choices.remove(((Ranger_Enemy1)A).miscText);
			}
			choices.remove("Unique");
			choices.remove("Unknown");
			choices.remove(mob.charStats().getMyRace().racialCategory());
			miscText=(String)choices.elementAt(CMLib.dice().roll(1,choices.size(),-1));
			for(int a=0;a<mob.numAbilities();a++)
			{
				final Ability A=mob.fetchAbility(a);
				if((A!=null)&&(A.ID().equals(ID())))
					((Ranger_Enemy1)A).miscText=miscText;
			}
			for(int a=0;a<mob.numEffects();a++) // personal
			{
				final Ability A=mob.fetchEffect(a);
				if((A!=null)&&(A.ID().equals(ID())))
					((Ranger_Enemy1)A).miscText=miscText;
			}
		}
		return super.text();
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		final MOB victim=mob.getVictim();
		if((victim!=null)&&(victim.charStats().getMyRace().racialCategory().equals(text())))
		{
			final int level=1+adjustedLevel(mob,0);
			final double damBonus=CMath.mul(CMath.div(proficiency(),100.0),level);
			final double attBonus=CMath.mul(CMath.div(proficiency(),100.0),3*level);
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+(int)Math.round(attBonus));
			affectableStats.setDamage(affectableStats.damage()+(int)Math.round(damBonus));
		}
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(msg.source()==affected)
		&&(msg.target() instanceof MOB)
		&&(((MOB)msg.target()).charStats().getMyRace().racialCategory().equals(text()))
		&&(CMLib.dice().roll(1, 10, 0)==1))
			helpProficiency(msg.source(), 0);
		return super.okMessage(myHost, msg);
	}

	@Override
	public boolean autoInvocation(MOB mob)
	{
		if(mob.charStats().getCurrentClass().ID().equals("Immortal"))
			return false;
		return super.autoInvocation(mob);
	}
}
