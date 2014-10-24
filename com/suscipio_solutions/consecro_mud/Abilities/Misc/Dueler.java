package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Dueler extends StdAbility
{
	@Override public String ID() { return "Dueler"; }
	private final static String localizedName = CMLib.lang().L("Dueler");
	@Override public String name() { return localizedName; }
	protected Dueler otherDueler = null;
	protected MOB otherDuelPartner=null;
	protected long lastTimeISawYou=System.currentTimeMillis();
	protected boolean oldPVPStatus = false;
	protected CharState oldCurState = null;
	protected List<Ability> oldEffects = new LinkedList<Ability>();
	protected Hashtable<Item,Item> oldEq = new Hashtable<Item,Item>();
	protected int autoWimp=0;

	@Override
	public String displayText()
	{
		if(otherDueler != null)
			return "(Dueling "+otherDueler.affecting().name()+")";
		return "";
	}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}

	@Override
	public void unInvoke()
	{
		if(affected instanceof MOB)
		{
			final MOB mob=(MOB)affected;
			if((mob != null)&&(oldCurState!=null))
			{
				final Dueler oDA=otherDueler;
				if(oDA!=null)
				{
					otherDueler=null;
					oDA.otherDueler=null;
					oDA.unInvoke();
				}
				if((canBeUninvoked())
				&&(!mob.amDead())
				&&(CMLib.flags().isInTheGame(mob,true)))
					mob.tell(L("Your duel has ended."));
				if(!oldPVPStatus)
					mob.setAttribute(MOB.Attrib.PLAYERKILL,false);
				oldCurState.copyInto(mob.curState());
				final LinkedList<Ability> cleanOut=new LinkedList<Ability>();
				for(final Enumeration<Ability> a=mob.effects();a.hasMoreElements();)
				{
					final Ability A=a.nextElement();
					if(!oldEffects.contains(A))
						cleanOut.add(A);
				}
				for(final Ability A : cleanOut)
				{
					if(!(A instanceof Dueler))
						A.unInvoke();
					mob.delEffect(A);
					A.destroy();
				}
				for(final Item I : oldEq.keySet())
				{
					final Item copyI=oldEq.get(I);
					if(I.amDestroyed())
						mob.addItem(copyI);
					else
					if(I.usesRemaining() < copyI.usesRemaining())
						I.setUsesRemaining(copyI.usesRemaining());
				}
				mob.setWimpHitPoint(autoWimp);
				mob.recoverCharStats();
				mob.recoverMaxState();
				mob.recoverPhyStats();
				mob.makePeace();
				final Ability A=CMClass.getAbility("Immunities");
				if(A!=null)
					A.invoke(mob, new XVector("LEGAL","TICKS=1"), mob, true, 0);
			}
		}
		oldEffects.clear();
		oldEq.clear();
		oldCurState=null;
		otherDuelPartner=null;
		super.unInvoke();
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if((msg.sourceMinor()==CMMsg.TYP_DEATH)
		&&(msg.source()==affecting()))
		{
			final MOB target=msg.source();
			final Room deathRoom=target.location();
			final String msp=CMLib.protocol().msp("death"+CMLib.dice().roll(1,7,0)+".wav",50);
			final CMMsg msg2=CMClass.getMsg(target,null,otherDuelPartner,
					CMMsg.MSG_OK_VISUAL,L("^f^*^<FIGHT^>!!!!!!!!!!!!!!YOU ARE DEFEATED!!!!!!!!!!!!!!^</FIGHT^>^?^.\n\r@x1",msp),
					CMMsg.MSG_OK_VISUAL,null,
					CMMsg.MSG_OK_VISUAL,L("^F^<FIGHT^><S-NAME> is DEFEATED!!!^</FIGHT^>^?\n\r@x1",msp));
			deathRoom.send(target, msg2);
			CMLib.combat().doDeathPostProcessing(msg);
			target.makePeace();
			//source.makePeace();
			unInvoke();
			return false;
		}
		else
		if((msg.target()==affecting())
		&&(msg.targetMinor()==CMMsg.TYP_LEGALWARRANT)
		&&(msg.tool()==otherDuelPartner))
			return false;
		return true;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((ticking instanceof MOB)&&(tickID==Tickable.TICKID_MOB))
		{
			final MOB mob=(MOB)ticking;
			mob.setWimpHitPoint(0);
			final Dueler tDuel=otherDueler;
			if((tDuel==null)
			||(tDuel.amDestroyed)
			||(tDuel.unInvoked)
			||(!(tDuel.affecting() instanceof MOB))
			||(mob.amDead())
			||(((MOB)tDuel.affecting()).amDead())
			||(!(CMLib.flags().isInTheGame(mob, false)))
			||(!(CMLib.flags().isInTheGame((MOB)tDuel.affecting(), false)))
			||(mob.getVictim()==null)
			||((mob.getVictim()!=tDuel.affecting())&&(mob.getVictim().amUltimatelyFollowing()!=tDuel.affecting()))
			)
				unInvoke();
			else
			{
				final MOB tMOB=(MOB)tDuel.affecting();
				if(mob.location()==tMOB.location())
					lastTimeISawYou=System.currentTimeMillis();
				if((System.currentTimeMillis()-lastTimeISawYou)>30000)
					unInvoke();
			}
		}
		return true;
	}

	public void init(MOB mob)
	{
		oldPVPStatus=mob.isAttribute(MOB.Attrib.PLAYERKILL);
		mob.setAttribute(MOB.Attrib.PLAYERKILL,true);
		oldCurState=(CharState)mob.curState().copyOf();
		oldEffects.clear();
		for(final Enumeration<Ability> a=mob.personalEffects();a.hasMoreElements();)
			oldEffects.add(a.nextElement());
		autoWimp=mob.getWimpHitPoint();
		mob.setWimpHitPoint(0);
		for(final Enumeration<Item> i=mob.items();i.hasMoreElements();)
		{
			final Item I=i.nextElement();
			if(((I instanceof Weapon)||(I instanceof Armor))
			&&(!I.amWearingAt(Wearable.IN_INVENTORY)))
				oldEq.put(I,(Item)I.copyOf());
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical target, boolean auto, int asLevel)
	{
		if(target==null) target=mob;
		if(!(target instanceof MOB)) return false;
		if(((MOB)target).location()==null) return false;
		if(((MOB)target).location().show(mob,target,this,CMMsg.MSG_OK_VISUAL,L("^R<S-NAME> and <T-NAME> start(s) dueling!^?")))
		{
			final MOB tmob = (MOB)target;
			Dueler A;
			Dueler tA;
			A=(Dueler)mob.fetchEffect(ID());
			if(A!=null){ A.unInvoke(); mob.delEffect(A); }
			A=(Dueler)tmob.fetchEffect(ID());
			if(A!=null){ A.unInvoke(); tmob.delEffect(A); }
			A=(Dueler)newInstance();
			tA=(Dueler)newInstance();
			A.otherDueler=tA;
			A.otherDuelPartner=tmob;
			tA.otherDueler=A;
			tA.otherDuelPartner=mob;
			A.init(mob);
			tA.init(tmob);
			mob.setVictim(tmob);
			tmob.setVictim(mob);
			A.startTickDown(mob, mob, Ability.TICKS_FOREVER);
			tA.startTickDown(tmob, tmob, Ability.TICKS_FOREVER);
		}
		return true;
	}
}
