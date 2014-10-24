package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;


public class Skill_Resistance extends StdSkill
{
	@Override public String ID() { return "Skill_Resistance"; }
	private final static String localizedName = CMLib.lang().L("Resistance");
	@Override public String name() { return localizedName; }
	protected String displayText="";
	@Override public String displayText(){ return displayText;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	public int resistanceCode=0;

	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		resistanceCode=0;
		for(final int i : CharStats.CODES.SAVING_THROWS())
			if(newText.equalsIgnoreCase(CharStats.CODES.NAME(i))||newText.equalsIgnoreCase(CharStats.CODES.DESC(i)))
				resistanceCode=i;
		if(resistanceCode>0)
			displayText=L("(Resistance to @x1)",newText.trim().toLowerCase());
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(invoker==null) return;
		final int amount=(int)Math.round(CMath.mul(CMath.div(proficiency(),100.0),affected.phyStats().level()));
		if(resistanceCode>0)
			affectableStats.setStat(resistanceCode,affectableStats.getStat(resistanceCode)+amount);
		else
		for(final int i : CharStats.CODES.SAVING_THROWS())
			affectableStats.setStat(i,affectableStats.getStat(i)+amount);
	}
}
