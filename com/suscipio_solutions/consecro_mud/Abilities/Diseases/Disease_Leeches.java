package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Disease_Leeches extends Disease
{
	@Override public String ID() { return "Disease_Leeches"; }
	private final static String localizedName = CMLib.lang().L("Leeches");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Leeches)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}

	@Override protected int DISEASE_TICKS(){return 35;}
	@Override protected int DISEASE_DELAY(){return 7;}
	@Override protected String DISEASE_DONE(){return "The leeches get full and fall off.";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> <S-HAS-HAVE> leeches covering <S-HIM-HER>!^?";}
	@Override protected String DISEASE_AFFECT(){return "<S-NAME> cringe(s) from the leeches.";}
	@Override public int spreadBitmap(){return DiseaseAffect.SPREAD_STD;}
	@Override public int difficultyLevel(){return 0;}
	protected int hp=Integer.MAX_VALUE;
	protected String thename="";

	public List<Ability> returnOffensiveAffects(Physical fromMe)
	{
		final Vector offenders=new Vector();

		for(int a=0;a<fromMe.numEffects();a++) // personal
		{
			final Ability A=fromMe.fetchEffect(a);
			if((A!=null)&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_POISON))
				offenders.addElement(A);
		}
		return offenders;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))	return false;
		if(affected==null) return false;
		if(!(affected instanceof MOB)) return true;

		final MOB mob=(MOB)affected;
		if((!mob.amDead())&&((--diseaseTick)<=0))
		{
			diseaseTick=DISEASE_DELAY();
			final List<Ability> offensiveEffects=returnOffensiveAffects(mob);
			for(int a=offensiveEffects.size()-1;a>=0;a--)
				offensiveEffects.get(a).unInvoke();
			mob.location().show(mob,null,CMMsg.MSG_NOISYMOVEMENT,DISEASE_AFFECT());
			MOB diseaser=invoker;
			if(diseaser==null) diseaser=mob;
			if(mob.curState().getHitPoints()>2)
			{
				mob.maxState().setHitPoints(mob.curState().getHitPoints()-1);
				CMLib.combat().postDamage(diseaser,mob,this,1,CMMsg.MASK_ALWAYS|CMMsg.TYP_DISEASE,-1,null);
			}
			return true;
		}
		return true;
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		if(affected==null) return;
		affectableStats.setStat(CharStats.STAT_CHARISMA,affectableStats.getStat(CharStats.STAT_CHARISMA)-4);
		if(affectableStats.getStat(CharStats.STAT_CHARISMA)<=0)
			affectableStats.setStat(CharStats.STAT_CHARISMA,1);
	}
	@Override
	public void affectCharState(MOB affected, CharState affectableState)
	{
		if(affected==null) return;
		if(!affected.Name().equals(thename))
		{
			hp=Integer.MAX_VALUE;
			thename=affected.Name();
		}
		if(affected.curState().getHitPoints()<hp)
			hp=affected.curState().getHitPoints();
		affectableState.setHitPoints(hp);
	}
}
