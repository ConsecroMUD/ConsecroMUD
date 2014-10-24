package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.Basic.StdItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MagicDust;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.SpellHolder;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;

@SuppressWarnings({"unchecked","rawtypes"})
public class StdPowder extends StdItem implements MagicDust {
	@Override public String ID(){	return "StdPowder";}

	public StdPowder()
	{
		super();

		setName("a pile of powder");
		basePhyStats.setWeight(1);
		setDisplayText("A small pile of powder sits here.");
		setDescription("A small pile of powder.");
		secretIdentity="This is a pile of inert materials.";
		baseGoldValue=0;
		material=RawMaterial.RESOURCE_ASH;
		recoverPhyStats();
	}

	@Override
	public void spreadIfAble(MOB mob, Physical target)
	{
		final List<Ability> spells = getSpells();
		if (spells.size() > 0)
			for (int i = 0; i < spells.size(); i++)
			{
				final Ability thisOne = (Ability) spells.get(i).copyOf();
				if(thisOne.canTarget(target))
				{
					if((malicious(this))||(!(target instanceof MOB)))
						thisOne.invoke(mob, target, true, phyStats().level());
					else
						thisOne.invoke((MOB)target,(MOB)target, true, phyStats().level());
				}
			}
		destroy();
	}


// That which makes Powders work.  They're an item that when successfully dusted on a target, are 'cast' on the target
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(msg.sourceMinor()==CMMsg.TYP_THROW )
		{
			if((msg.tool()==this)&&(msg.target() instanceof Physical))
				spreadIfAble(msg.source(),(Physical)msg.target());
			else
				super.executeMsg(myHost,msg);
		}
		else
			super.executeMsg(myHost,msg);
	}

	@Override
	public String getSpellList()
	{ return miscText;}
	@Override public void setSpellList(String list){miscText=list;}

	public boolean malicious(SpellHolder me)
	{
		final List<Ability> spells=getSpells();
		for(final Ability checking : spells)
			if(checking.abstractQuality()==Ability.QUALITY_MALICIOUS)
				return true;
		return false;
	}
	@Override
	public List<Ability> getSpells()
	{
		final String names=getSpellList();

		final Vector theSpells=new Vector();
		final List<String> parsedSpells=CMParms.parseSemicolons(names, true);
		for(String thisOne : parsedSpells)
		{
			thisOne=thisOne.trim();
			String parms="";
			final int x=thisOne.indexOf('(');
			if((x>0)&&(thisOne.endsWith(")")))
			{
				parms=thisOne.substring(x+1,thisOne.length()-1);
				thisOne=thisOne.substring(0,x).trim();
			}
			Ability A=CMClass.getAbility(thisOne);
			if((A!=null)&&((A.classificationCode()&Ability.ALL_DOMAINS)!=Ability.DOMAIN_IMMORTAL))
			{
				A=(Ability)A.copyOf();
				A.setMiscText(parms);
				theSpells.addElement(A);
			}
		}
		recoverPhyStats();
		return theSpells;
	}

	@Override
	public String secretIdentity()
	{
		return description()+"\n\r"+super.secretIdentity();
	}

}
