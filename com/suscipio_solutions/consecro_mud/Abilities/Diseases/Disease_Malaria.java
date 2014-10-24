package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Disease_Malaria extends Disease
{
	@Override public String ID() { return "Disease_Malaria"; }
	private final static String localizedName = CMLib.lang().L("Malaria");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Malaria)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}

	@Override protected int DISEASE_TICKS(){return 9*CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);}
	@Override protected int DISEASE_DELAY(){return 5;}
	@Override protected String DISEASE_DONE(){return "Your malaria clears up.";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> come(s) down with malaria.^?";}
	@Override protected String DISEASE_AFFECT(){return "<S-NAME> ache(s) and sneeze(s). AAAAAAAAAAAAAACHOOO!!!!";}
	@Override public int spreadBitmap(){return DiseaseAffect.SPREAD_CONSUMPTION|DiseaseAffect.SPREAD_PROXIMITY|DiseaseAffect.SPREAD_CONTACT|DiseaseAffect.SPREAD_STD;}
	@Override public int difficultyLevel(){return 1;}
	private boolean norecurse=false;
	protected int conDown=0;
	protected int tickUp=0;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))	return false;
		if(affected==null) return false;
		if(!(affected instanceof MOB)) return true;

		final MOB mob=(MOB)affected;
		MOB diseaser=invoker;
		if(diseaser==null) diseaser=mob;
		if((!mob.amDead())&&((++tickUp)==CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY)))
		{
			tickUp=0;
			conDown++;
			if((CMLib.dice().rollPercentage()<20)
			&&(CMLib.dice().rollPercentage()<mob.charStats().getSave(CharStats.STAT_SAVE_DISEASE)))
			{
				unInvoke();
				return false;
			}
		}
		if((!mob.amDead())&&((--diseaseTick)<=0))
		{
			diseaseTick=DISEASE_DELAY();
			mob.location().show(mob,null,CMMsg.MSG_NOISE,DISEASE_AFFECT());
			final int damage=CMLib.dice().roll(2,mob.phyStats().level()+1,1);
			CMLib.combat().postDamage(diseaser,mob,this,damage,CMMsg.MASK_ALWAYS|CMMsg.TYP_DISEASE,-1,null);
			catchIt(mob);
			if(CMLib.dice().rollPercentage()==1)
			{
				final Ability A=CMClass.getAbility("Disease_Fever");
				if(A!=null) A.invoke(diseaser,mob,true,0);
			}
			return true;
		}
		return true;
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		if(affected==null) return;
		affectableStats.setStat(CharStats.STAT_STRENGTH,affectableStats.getStat(CharStats.STAT_STRENGTH)-5);
		if(affectableStats.getStat(CharStats.STAT_STRENGTH)<=0)
			affectableStats.setStat(CharStats.STAT_STRENGTH,1);
		affectableStats.setStat(CharStats.STAT_CONSTITUTION,affectableStats.getStat(CharStats.STAT_CONSTITUTION)-(5+conDown));
		if((affectableStats.getStat(CharStats.STAT_CONSTITUTION)<=0)&&(!norecurse))
		{
			conDown=-1;
			MOB diseaser=invoker;
			if(diseaser==null) diseaser=affected;
			norecurse=true;
			CMLib.combat().postDeath(diseaser,affected,null);
			norecurse=false;
		}
	}

	@Override
	public void affectCharState(MOB affected, CharState affectableState)
	{
		if(affected==null) return;
		affectableState.setMovement(affectableState.getMovement()/2);
		affectableState.setMana(affectableState.getMana()-(affectableState.getMana()/3));
		affectableState.setHitPoints(affectableState.getHitPoints()-affected.phyStats().level());
	}
}
