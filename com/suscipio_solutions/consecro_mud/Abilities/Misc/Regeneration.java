package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.HealthCondition;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;




@SuppressWarnings("rawtypes")
public class Regeneration extends StdAbility implements HealthCondition
{
	private static final int maxTickDown=3;
	protected int regenTick=maxTickDown;

	@Override public String ID() { return "Regeneration"; }
	private final static String localizedName = CMLib.lang().L("Stat Regeneration");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Stat Regeneration)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override public boolean putInCommandlist(){return false;}
	private static final String[] triggerStrings =I(new String[] {"REGENERATE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL;}
	protected int permanentDamage=0;

	@Override
	public String getHealthConditionDesc()
	{
		return "Possesses regenerative cells.";
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		if((--regenTick)>0)
			return true;
		regenTick=maxTickDown;
		final MOB mob=(MOB)affected;
		if(mob==null) return true;
		if(mob.location()==null) return true;
		if(mob.amDead()) return true;

		boolean doneAnything=false;
		doneAnything=doneAnything||mob.curState().adjHitPoints((int)Math.round(CMath.div(mob.phyStats().level(),2.0)),mob.maxState());
		doneAnything=doneAnything||mob.curState().adjMana(mob.phyStats().level()*2,mob.maxState());
		doneAnything=doneAnything||mob.curState().adjMovement(mob.phyStats().level()*3,mob.maxState());
		if(doneAnything)
			mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> regenerate(s)."));
		return true;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		if((affected!=null)&&(affected instanceof MOB))
		{
			final MOB M=(MOB)affected;
			if(msg.amISource(M)&&(msg.sourceMinor()==CMMsg.TYP_DEATH))
			{
				permanentDamage=0;
				M.recoverMaxState();
			}
			else
			if((msg.amITarget(M))
			&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
			&&(msg.tool()!=null)
			&&(text().length()>0))
			{
				final String text=text().toUpperCase();
				boolean hurts=false;
				if(msg.tool() instanceof Weapon)
				{
					final Weapon W=(Weapon)msg.tool();
					int x=text.indexOf(Weapon.TYPE_DESCS[W.weaponType()]);
					if((x>=0)&&((x==0)||(text.charAt(x-1)=='+')))
						hurts=true;
					if(CMLib.flags().isABonusItems(W))
					{
						x=text.indexOf("MAGIC");
						if((x>=0)&&((x==0)||(text.charAt(x-1)=='+')))
							hurts=true;
					}
					x=text.indexOf("LEVEL");
					if((x>=0)&&((x==0)||(text.charAt(x-1)=='+')))
					{
						String lvl=text.substring(x+5);
						if(lvl.indexOf(' ')>=0)
							lvl=lvl.substring(lvl.indexOf(' '));
						if(W.phyStats().level()>=CMath.s_int(lvl))
							hurts=true;
					}
					x=text.indexOf(RawMaterial.CODES.NAME(W.material()));
					if((x>=0)&&((x==0)||(text.charAt(x-1)=='+')))
						hurts=true;
				}
				else
				if(msg.tool() instanceof Ability)
				{
					final int classType=((Ability)msg.tool()).classificationCode()&Ability.ALL_ACODES;
					switch(classType)
					{
					case Ability.ACODE_SPELL:
					case Ability.ACODE_PRAYER:
					case Ability.ACODE_CHANT:
					case Ability.ACODE_SONG:
						{
							final int x=text.indexOf("MAGIC");
							if((x>=0)&&((x==0)||(text.charAt(x-1)=='+')))
								hurts=true;
						}
						break;
					default:
						break;
					}
				}
				if(hurts)
				{
					permanentDamage+=msg.value();
					M.recoverMaxState();
				}
			}

		}
		return true;
	}

	@Override
	public void affectCharState(MOB mob, CharState state)
	{
		super.affectCharState(mob,state);
		state.setHitPoints(state.getHitPoints()-permanentDamage);
	}
	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();
		if(canBeUninvoked())
			mob.tell(L("You feel less regenerative."));
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final String str=auto?"":L("<S-NAME> lay(s) regenerative magic upon <T-NAMESELF>.");
			final CMMsg msg=CMClass.getMsg(mob,target,null,CMMsg.MSG_QUIETMOVEMENT,str);
			if(target.location().okMessage(target,msg))
			{
				target.location().send(target,msg);
				success=beneficialAffect(mob,target,asLevel,0)!=null;
			}
		}

		return success;

	}
}
