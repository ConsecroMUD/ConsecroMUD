package com.suscipio_solutions.consecro_mud.Items.Weapons;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Items.MiscMagic.StdWand;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ImmortalOnly;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wand;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class ImmortalStaff extends Staff implements Wand, MiscMagic, ImmortalOnly
{
	@Override public String ID(){    return "ImmortalStaff";}
	private static Wand theWand=(Wand)CMClass.getMiscMagic("StdWand");
	protected final static String[] MAGIC_WORDS={"LEVEL","RESTORE","REFRESH","BLAST","BURN"};

	public ImmortalStaff()
	{
		super();

		setName("a wooden staff");
		setDisplayText("a wooden staff lies in the corner of the room.");
		setDescription("It`s long and wooden, just like a staff ought to be.");
		secretIdentity="The Immortal`s Staff of Power!";
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(30);
		basePhyStats.setWeight(4);
		basePhyStats().setAttackAdjustment(10);
		basePhyStats().setDamage(12);
		baseGoldValue=10000;
		recoverPhyStats();
		wornLogicalAnd=true;
		material=RawMaterial.RESOURCE_OAK;
		properWornBitmap=Wearable.WORN_HELD|Wearable.WORN_WIELD;
		weaponType=TYPE_BASHING;
		weaponClassification=Weapon.CLASS_STAFF;
		if(theWand==null)
			theWand=(Wand)CMClass.getMiscMagic("StdWand");
		secretWord="REFRESH, RESTORE, BLAST, LEVEL X UP, LEVEL X DOWN, BURN";
	}

	@Override public int maxUses(){return Integer.MAX_VALUE;}
	@Override public void setMaxUses(int newMaxUses){}

	@Override
	public void setSpell(Ability theSpell)
	{
		super.setSpell(theSpell);
		secretWord="REFRESH, RESTORE, BLAST, LEVEL X UP, LEVEL X DOWN, BURN";
	}
	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		secretWord="REFRESH, RESTORE, BLAST, LEVEL X UP, LEVEL X DOWN, BURN";
	}

	public boolean safetyCheck(MOB mob, String message)
	{
		if((!mob.isMonster())
		&&(message.length()>0)
		&&(mob.session().getPreviousCMD()!=null)
		&&(CMParms.combine(mob.session().getPreviousCMD(),0).toUpperCase().indexOf(message.toUpperCase())<0))
		{
			mob.tell(L("The wand fizzles in an irritating way."));
			return false;
		}
		return true;
	}

	@Override
	public boolean checkWave(MOB mob, String message)
	{
		if(message==null)
			return false;
		final List<String> parms=CMParms.cleanParameterList(message.toUpperCase());
		for (final String element : MAGIC_WORDS)
			if(parms.contains(element))
			{
				return (mob.isMine(this)) && (!amWearingAt(Wearable.IN_INVENTORY));
			}
		return super.checkWave(mob, message);
	}

	@Override
	public void waveIfAble(MOB mob, Physical afftarget, String message)
	{
		if((mob.isMine(this))
		   &&(!this.amWearingAt(Wearable.IN_INVENTORY)))
		{
			if((mob.location()!=null)&&(afftarget!=null)&&(afftarget instanceof MOB))
			{
				final MOB target=(MOB)afftarget;
				if(message.toUpperCase().indexOf("LEVEL ALL UP")>0)
				{
					if(!safetyCheck(mob,message)) return;
					mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("@x1 glows brightly at <T-NAME>.",this.name()));
					int destLevel=CMProps.getIntVar(CMProps.Int.LASTPLAYERLEVEL);
					if(destLevel==0) destLevel=30;
					if(destLevel<=target.basePhyStats().level())
						destLevel=100;
					if((target.charStats().getCurrentClass().leveless())
					||(target.charStats().isLevelCapped(target.charStats().getCurrentClass()))
					||(target.charStats().getMyRace().leveless())
					||(CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS)))
						mob.tell(L("The wand will not work on such as @x1.",target.name(mob)));
					else
					while(target.basePhyStats().level()<destLevel)
					{
						if((target.getExpNeededLevel()==Integer.MAX_VALUE)
						||(target.charStats().getCurrentClass().expless())
						||(target.charStats().getMyRace().expless()))
							CMLib.leveler().level(target);
						else
							CMLib.leveler().postExperience(target,null,null,target.getExpNeededLevel()+1,false);
					}
				}
				else
				if(message.toUpperCase().startsWith("LEVEL ")&&message.toUpperCase().endsWith(" UP"))
				{
					if(!safetyCheck(mob,message)) return;
					message=message.substring(6).trim();
					message=message.substring(0,message.length()-2).trim();
					int num=1;
					if(CMath.isInteger(message)) num=CMath.s_int(message);
					mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("@x1 glows brightly at <T-NAME>.",this.name()));
					if((target.charStats().getCurrentClass().leveless())
					||(target.charStats().isLevelCapped(target.charStats().getCurrentClass()))
					||(target.charStats().getMyRace().leveless())
					||(CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS)))
						mob.tell(L("The wand will not work on such as @x1.",target.name(mob)));
					else
					for(int i=0;i<num;i++)
					{
						if((target.getExpNeededLevel()==Integer.MAX_VALUE)
						||(target.charStats().getCurrentClass().expless())
						||(target.charStats().getMyRace().expless()))
							CMLib.leveler().level(target);
						else
							CMLib.leveler().postExperience(target,null,null,target.getExpNeededLevel()+1,false);
					}
					return;
				}
				else
				if(message.toUpperCase().startsWith("LEVEL ")&&message.toUpperCase().endsWith(" DOWN"))
				{
					if(!safetyCheck(mob,message)) return;
					message=message.substring(6).trim();
					message=message.substring(0,message.length()-4).trim();
					int num=1;
					if(CMath.isInteger(message)) num=CMath.s_int(message);
					mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("@x1 glows brightly at <T-NAME>.",this.name()));
					if((target.charStats().getCurrentClass().leveless())
					||(target.charStats().isLevelCapped(target.charStats().getCurrentClass()))
					||(target.charStats().getMyRace().leveless())
					||(CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS)))
						mob.tell(L("The wand will not work on such as @x1.",target.name(mob)));
					else
					for(int i=0;i<num;i++)
					{
						if((target.getExpNeededLevel()==Integer.MAX_VALUE)
						||(target.charStats().getCurrentClass().expless())
						||(target.charStats().getMyRace().expless()))
							CMLib.leveler().unLevel(target);
						else
						{
							final int xpLevelBelow=CMLib.leveler().getLevelExperience(target.basePhyStats().level()-2);
							final int levelDown=(target.getExperience()-xpLevelBelow)+1;
							CMLib.leveler().postExperience(target,null,null,-levelDown,false);
						}
					}
					return;
				}
				else
				if(message.toUpperCase().indexOf("RESTORE")>=0)
				{
					if(!safetyCheck(mob,message)) return;
					mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("@x1 glows brightly at <T-NAME>.",this.name()));
					final List<Ability> diseaseV=CMLib.flags().domainAffects(target,Ability.ACODE_DISEASE);
					if(diseaseV.size()>0){ final Ability A=CMClass.getAbility("Prayer_CureDisease"); if(A!=null) A.invoke(mob,target,true,0);}
					final List<Ability> poisonV=CMLib.flags().domainAffects(target,Ability.ACODE_POISON);
					if(poisonV.size()>0){ final Ability A=CMClass.getAbility("Prayer_RemovePoison"); if(A!=null) A.invoke(mob,target,true,0);}
					final Ability bleed=target.fetchEffect("Bleeding"); if(bleed!=null){ bleed.unInvoke(); target.delEffect(bleed);}
					final Ability injury=target.fetchEffect("Injury"); if(injury!=null){ injury.unInvoke(); target.delEffect(injury);}
					final Ability ampu=target.fetchEffect("Amputation"); if(ampu!=null){ ampu.unInvoke(); target.delEffect(ampu);}

					target.recoverMaxState();
					target.resetToMaxState();
					target.tell(L("You feel refreshed!"));
					return;
				}
				else
				if(message.toUpperCase().indexOf("REFRESH")>=0)
				{
					if(!safetyCheck(mob,message)) return;
					mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("@x1 glows brightly at <T-NAME>.",this.name()));
					final Ability bleed=target.fetchEffect("Bleeding"); if(bleed!=null){ bleed.unInvoke(); target.delEffect(bleed);}
					target.recoverMaxState();
					target.resetToMaxState();
					target.tell(L("You feel refreshed!"));
					return;
				}
				else
				if(message.toUpperCase().equals("BLAST"))
				{
					if(!safetyCheck(mob,message)) return;
					mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("@x1 zaps <T-NAME> with unworldly energy.",this.name()));
					target.curState().setHitPoints(1);
					target.curState().setMana(1);
					target.curState().setMovement(1);
					return;
				}
				else
				if(message.toUpperCase().indexOf("BURN")>=0)
				{
					if(!safetyCheck(mob,message)) return;
					mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("@x1 wielded by <S-NAME> shoots forth magical green flames at <T-NAME>.",this.name()));
					int flameDamage = (int) Math.round( Math.random() * 6 );
					flameDamage *= 3;
					CMLib.combat().postDamage(mob,target,null,(++flameDamage),CMMsg.MASK_ALWAYS|CMMsg.TYP_FIRE,Weapon.TYPE_BURNING,(this.name()+" <DAMAGE> <T-NAME>!")+CMLib.protocol().msp("fireball.wav",30));
					return;
				}
			}
		}
		StdWand.waveIfAble(mob,afftarget,message,this);
	}

	@Override
	public void affectCharState(MOB mob, CharState affectableState)
	{
		super.affectCharState(mob,affectableState);
		if(!amWearingAt(Wearable.IN_INVENTORY))
		{
			affectableState.setHunger(99999999);
			affectableState.setThirst(99999999);
			mob.curState().setHunger(9999999);
			mob.curState().setThirst(9999999);
		}
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		final MOB mob=msg.source();
		if(mob.location()==null)
			return true;

		if(msg.amITarget(this))
		switch(msg.targetMinor())
		{
		case CMMsg.TYP_HOLD:
		case CMMsg.TYP_WEAR:
		case CMMsg.TYP_WIELD:
		case CMMsg.TYP_GET:
		case CMMsg.TYP_PUSH:
		case CMMsg.TYP_PULL:
			if(!CMSecurity.isASysOp(msg.source()))
			{
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("@x1 flashes and falls out of <S-HIS-HER> hands!",name()));
				return false;
			}
			break;
		}
		if((msg.targetMinor()==CMMsg.TYP_SPEAK)&&(msg.sourceMessage()!=null))
		{
			String said=CMStrings.getSayFromMessage(msg.sourceMessage());
			if(said!=null)
			{
				said=said.trim().toUpperCase();
				final int x=said.indexOf(' ');
				if(x>0)
					said=said.substring(0,x);
				if(CMParms.indexOf(MAGIC_WORDS, said)>=0)
					super.secretWord=said;
			}
		}
		return true;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((msg.source().location()!=null)
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&((msg.value())>0)
		&&(msg.tool()==this)
		&&(msg.target() instanceof MOB)
		&&(!((MOB)msg.target()).amDead()))
		{
			final CMMsg msg2=CMClass.getMsg(msg.source(),msg.target(),new ImmortalStaff(),CMMsg.MSG_OK_ACTION,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_FIRE,CMMsg.MSG_NOISYMOVEMENT,null);
			if(msg.source().location().okMessage(msg.source(),msg2))
			{
				msg.source().location().send(msg.source(), msg2);
				if(msg2.value()<=0)
				{
					int flameDamage = (int) Math.round( Math.random() * 6 );
					flameDamage *= basePhyStats().level();
					if(!((MOB)msg.target()).amDead())
						CMLib.combat().postDamage(msg.source(),(MOB)msg.target(),null,flameDamage,CMMsg.TYP_FIRE,Weapon.TYPE_BURNING,name()+" shoots a flame which <DAMAGE> <T-NAME>!");
				}
			}
		}
	}
}
