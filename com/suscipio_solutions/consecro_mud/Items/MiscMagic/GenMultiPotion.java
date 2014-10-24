package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.Basic.GenDrink;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Potion;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class GenMultiPotion extends GenDrink implements Potion
{
	@Override public String ID(){	return "GenMultiPotion";}

	public GenMultiPotion()
	{
		super();

		material=RawMaterial.RESOURCE_GLASS;
		setName("a flask");
		basePhyStats.setWeight(1);
		setDisplayText("A flask sits here.");
		setDescription("A strange flask with stranger markings.");
		secretIdentity="";
		baseGoldValue=200;
		recoverPhyStats();
	}


	@Override public boolean isGeneric(){return true;}
	@Override public int liquidType(){return RawMaterial.RESOURCE_DRINKABLE;}

	@Override public boolean isDrunk(){return (readableText.toUpperCase().indexOf(";DRUNK")>=0);}
	@Override
	public void setDrunk(boolean isTrue)
	{
		if(isTrue&&isDrunk()) return;
		if((!isTrue)&&(!isDrunk())) return;
		if(isTrue)
			setSpellList(getSpellList()+";DRUNK");
		else
		{
			String list="";
			final List<Ability> theSpells=getSpells();
			for(int v=0;v<theSpells.size();v++)
				list+=theSpells.get(v).ID()+";";
			setSpellList(list);
		}
	}

	@Override
	public String secretIdentity()
	{
		return StdScroll.makeSecretIdentity("potion",super.secretIdentity(),"",getSpells());
	}

	@Override
	public int value()
	{
		if(isDrunk())
			return 0;
		return super.value();
	}

	@Override
	public String getSpellList()
	{ return readableText;}
	@Override public void setSpellList(String list){readableText=list;}
	@Override
	public List<Ability> getSpells()
	{
		return StdPotion.getSpells(this);
	}
	@Override
	public void setReadableText(String text)
	{
		readableText=text;
		setSpellList(readableText);
	}

	@Override
	public void drinkIfAble(MOB owner, Physical drinkerTarget)
	{
		final List<Ability> spells=getSpells();
		if(owner.isMine(this))
		{
			if((!isDrunk())&&(spells.size()>0))
			{
				final MOB caster=CMLib.map().getFactoryMOB(owner.location());
				final MOB finalCaster=(owner!=drinkerTarget)?owner:caster;
				for(int i=0;i<spells.size();i++)
				{
					final Ability thisOne=(Ability)spells.get(i).copyOf();
					if((drinkerTarget instanceof Item)
					&&((!thisOne.canTarget(drinkerTarget))&&(!thisOne.canAffect(drinkerTarget))))
						continue;
					int level=phyStats().level();
					final int lowest=CMLib.ableMapper().lowestQualifyingLevel(thisOne.ID());
					if(level<lowest)
						level=lowest;
					caster.basePhyStats().setLevel(level);
					caster.phyStats().setLevel(level);
					thisOne.invoke(finalCaster,drinkerTarget,true,level);
				}
				caster.destroy();
			}

			if((liquidRemaining()<=thirstQuenched())&&(!isDrunk()))
				setDrunk(true);
		}

	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((msg.amITarget(this))
		&&(msg.targetMinor()==CMMsg.TYP_DRINK)
		&&(msg.othersMessage()==null)
		&&(msg.sourceMessage()==null))
			return true;
		else
		if((msg.tool()==this)
		&&(msg.targetMinor()==CMMsg.TYP_FILL))
			return true;
		return super.okMessage(myHost,msg);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(this))
		{
			final MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_DRINK:
				if((msg.sourceMessage()==null)&&(msg.othersMessage()==null))
				{
					drinkIfAble(mob,mob);
					if(isDrunk())
					{
						mob.tell(L("@x1 vanishes!",name()));
						destroy();
					}
					mob.recoverPhyStats();
				}
				else
				{
					msg.addTrailerMsg(CMClass.getMsg(msg.source(),this,msg.tool(),CMMsg.NO_EFFECT,null,msg.targetCode(),msg.targetMessage(),CMMsg.NO_EFFECT,null));
					super.executeMsg(myHost,msg);
				}
				break;
			default:
				super.executeMsg(myHost,msg);
				break;
			}
		}
		else
		if((msg.tool()==this)&&(msg.targetMinor()==CMMsg.TYP_FILL)&&(msg.target() instanceof Physical))
		{
			if((msg.sourceMessage()==null)&&(msg.othersMessage()==null))
			{
				drinkIfAble(msg.source(),(Physical)msg.target());
				if(isDrunk())
				{
					msg.source().tell(L("@x1 vanishes!",name()));
					destroy();
				}
				msg.source().recoverPhyStats();
				((Physical)msg.target()).recoverPhyStats();
			}
			else
			{
				msg.addTrailerMsg(CMClass.getMsg(msg.source(),msg.target(),msg.tool(),CMMsg.NO_EFFECT,null,msg.targetCode(),msg.targetMessage(),CMMsg.NO_EFFECT,null));
				super.executeMsg(myHost,msg);
			}
		}
		else
			super.executeMsg(myHost,msg);
	}
	// stats handled by gendrink, spells by readabletext
}
