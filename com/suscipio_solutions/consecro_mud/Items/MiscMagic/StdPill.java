package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.Basic.StdFood;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Pill;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.SpellHolder;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



@SuppressWarnings({"unchecked","rawtypes"})
public class StdPill extends StdFood implements Pill
{
	@Override public String ID(){	return "StdPill";}
	protected Ability theSpell;

	public StdPill()
	{
		super();

		setName("a pill");
		basePhyStats.setWeight(1);
		setDisplayText("A strange pill lies here.");
		setDescription("Large and round, with strange markings.");
		secretIdentity="Surely this is a potent pill!";
		baseGoldValue=200;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_CORN;
	}



	@Override
	public String secretIdentity()
	{
		return StdScroll.makeSecretIdentity("pill",super.secretIdentity(),"",getSpells(this));
	}

	@Override
	public void eatIfAble(MOB mob)
	{
		final List<Ability> spells=getSpells();
		if((mob.isMine(this))&&(spells.size()>0))
		{
			final MOB caster=CMLib.map().getFactoryMOB(mob.location());
			for(int i=0;i<spells.size();i++)
			{
				final Ability thisOne=(Ability)spells.get(i).copyOf();
				int level=phyStats().level();
				final int lowest=CMLib.ableMapper().lowestQualifyingLevel(thisOne.ID());
				if(level<lowest)
					level=lowest;
				caster.basePhyStats().setLevel(level);
				caster.phyStats().setLevel(level);
				thisOne.invoke(caster,mob,true,level);
			}
			caster.destroy();
		}
	}

	@Override
	public String getSpellList()
	{ return miscText;}
	@Override public void setSpellList(String list){miscText=list;}

	public static Vector getSpells(SpellHolder me)
	{
		final Vector theSpells=new Vector();
		final String names=me.getSpellList();
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
		me.recoverPhyStats();
		return theSpells;
	}

	@Override public List<Ability> getSpells(){ return getSpells(this);}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(this))
		{
			final MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_EAT:
				if((msg.sourceMessage()==null)&&(msg.othersMessage()==null))
				{
					eatIfAble(mob);
					super.executeMsg(myHost,msg);
				}
				else
					msg.addTrailerMsg(CMClass.getMsg(msg.source(),msg.target(),msg.tool(),CMMsg.NO_EFFECT,null,msg.targetCode(),msg.targetMessage(),CMMsg.NO_EFFECT,null));
				break;
			default:
				super.executeMsg(myHost,msg);
				break;
			}
		}
		else
			super.executeMsg(myHost,msg);
	}
}
