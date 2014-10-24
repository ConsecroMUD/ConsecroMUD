package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Prop_HaveEnabler extends Prop_SpellAdder
{
	@Override public String ID() { return "Prop_HaveEnabler"; }
	@Override public String name(){ return "Granting skills when owned";}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}
	protected Item myItem=null;
	protected Vector lastMOBeffected=new Vector();
	protected boolean processing2=false;

	@Override public long flags(){return Ability.FLAG_ENABLER;}

	@Override public int triggerMask() { return TriggeredAffect.TRIGGER_GET; }

	@Override
	public String accountForYourself()
	{ return spellAccountingsWithMask("Grants "," to the owner.");}

	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		lastMOBeffected=new Vector();
	}
	public boolean addMeIfNeccessary(Environmental source, Environmental target, boolean makeLongLasting, short maxTicks)
	{
		if((!(target instanceof MOB))
		||((compiledMask!=null)&&(!CMLib.masking().maskCheck(compiledMask,target,true))))
			return false;
		final MOB newMOB=(MOB)target;
		final List<Ability> V=getMySpellsV();
		int proff=100;
		int x=text().indexOf('%');
		if(x>0)
		{
			int mul=1;
			int tot=0;
			while((--x)>=0)
			{
				if(Character.isDigit(text().charAt(x)))
					tot+=CMath.s_int(""+text().charAt(x))*mul;
				else
					x=-1;
				mul=mul*10;
			}
			proff=tot;
		}
		boolean clearedYet=false;
		for(int v=0;v<V.size();v++)
		{
			final Ability A=V.get(v);
			if(newMOB.fetchAbility(A.ID())==null)
			{
				final String t=A.text();
				if(t.length()>0)
				{
					x=t.indexOf('/');
					if(x<0)
						A.setMiscText("");
					else
						A.setMiscText(t.substring(x+1));
				}
				final Ability A2=newMOB.fetchEffect(A.ID());
				A.setProficiency(proff);
				newMOB.addAbility(A);
				A.setSavable(makeLongLasting);
				A.autoInvocation(newMOB);
				if(!clearedYet)
				{
					lastMOBeffected.clear();
					clearedYet=true;
				}
				if((A2==null)&&(!lastMOBeffected.contains(A.ID())))
					lastMOBeffected.addElement(A.ID());
			}
		}
		lastMOB=newMOB;
		return true;
	}

	@Override
	public void removeMyAffectsFrom(Physical P)
	{
		if(!(P instanceof MOB))
			return;
		final List<Ability> V=getMySpellsV();
		for(int v=0;v<V.size();v++)
		{
			final Ability A=V.get(v);
			((MOB)P).delAbility(A);
		}
		if(P==lastMOB)
		{
			for(final Iterator e=lastMOBeffected.iterator();e.hasNext();)
			{
				final String AID=(String)e.next();
				final Ability A2=lastMOB.fetchEffect(AID);
				if(A2!=null)
				{
					A2.unInvoke();
					lastMOB.delEffect(A2);
				}
			}
			lastMOBeffected.clear();
		}
	}

	public void removeMyAffectsFromLastMob()
	{
		if(!(lastMOB instanceof MOB))
			return;
		removeMyAffectsFrom(lastMOB);
		lastMOB=null;
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{}

	@Override
	public void affectPhyStats(Physical host, PhyStats affectableStats)
	{
		if(processing) return;
		processing=true;
		if(host instanceof Item)
		{
			myItem=(Item)host;

			if((lastMOB instanceof MOB)
			&&((myItem.owner()!=lastMOB)||(myItem.amDestroyed()))
			&&(((MOB)lastMOB).location()!=null))
				removeMyAffectsFromLastMob();

			if((lastMOB==null)
			&&(myItem.owner()!=null)
			&&(myItem.owner() instanceof MOB)
			&&(((MOB)myItem.owner()).location()!=null))
				addMeIfNeccessary(myItem.owner(),myItem.owner(),false,maxTicks);
		}
		processing=false;
	}
}
