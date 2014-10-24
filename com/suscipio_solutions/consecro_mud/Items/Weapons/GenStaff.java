package com.suscipio_solutions.consecro_mud.Items.Weapons;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.MiscMagic.StdWand;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wand;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;




public class GenStaff extends GenWeapon implements Wand
{
	@Override public String ID(){    return "GenStaff";}
	protected String secretWord=CMProps.getAnyListFileValue(CMProps.ListFile.MAGIC_WORDS);

	public GenStaff()
	{
		super();

		setName("a wooden staff");
		setDisplayText("a wooden staff lies in the corner of the room.");
		setDescription("");
		secretIdentity="";
		basePhyStats().setAbility(0);
		basePhyStats().setLevel(0);
		basePhyStats.setWeight(4);
		basePhyStats().setAttackAdjustment(0);
		basePhyStats().setDamage(4);
		setUsesRemaining(0);
		baseGoldValue=1;
		recoverPhyStats();
		wornLogicalAnd=true;
		material=RawMaterial.RESOURCE_OAK;
		properWornBitmap=Wearable.WORN_HELD|Wearable.WORN_WIELD;
		weaponType=TYPE_BASHING;
		weaponClassification=Weapon.CLASS_STAFF;
		recoverPhyStats();
	}

	protected int maxUses=Integer.MAX_VALUE;
	@Override public int maxUses(){return maxUses;}
	@Override public void setMaxUses(int newMaxUses){maxUses=newMaxUses;}

	@Override public boolean isGeneric(){return true;}

	@Override
	public int value()
	{
		if(usesRemaining()<=0)
			return 0;
		return super.value();
	}
	@Override
	public void setSpell(Ability theSpell)
	{
		readableText="";
		if(theSpell!=null)
			readableText=theSpell.ID();
		secretWord=StdWand.getWandWord(readableText);
	}
	@Override public String readableText(){return readableText;}
	@Override public void setReadableText(String text){ readableText=text;secretWord=StdWand.getWandWord(readableText);}

	@Override
	public String secretIdentity()
	{
		String id=super.secretIdentity();
		final Ability A=getSpell();
		if(A!=null)
			id="'A staff of "+A.name()+"' Charges: "+usesRemaining()+"\n\r"+id;
		return id+"\n\rSay the magic word :`"+secretWord+"` to the target.";
	}

	@Override
	public Ability getSpell()
	{
		return CMClass.getAbility(readableText());
	}

	@Override
	public String magicWord()
	{
		return secretWord;
	}

	@Override
	public void waveIfAble(MOB mob, Physical afftarget, String message)
	{
		StdWand.waveIfAble(mob,afftarget,message,this);
	}

	@Override
	public boolean checkWave(MOB mob, String message)
	{
		return StdWand.checkWave(mob, message, this);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		final MOB mob=msg.source();
		switch(msg.targetMinor())
		{
		case CMMsg.TYP_WAND_USE:
			if(msg.amITarget(this)&&((msg.tool()==null)||(msg.tool() instanceof Physical)))
				StdWand.waveIfAble(mob,(Physical)msg.tool(),msg.targetMessage(),this);
			break;
		case CMMsg.TYP_SPEAK:
			if((msg.sourceMinor()==CMMsg.TYP_SPEAK)&&(!amWearingAt(Wearable.IN_INVENTORY)))
			{
				boolean alreadyWanding=false;
				final List<CMMsg> trailers =msg.trailerMsgs();
				if(trailers!=null)
					for(final CMMsg msg2 : trailers)
						if(msg2.targetMinor()==CMMsg.TYP_WAND_USE)
							alreadyWanding=true;
				final String said=CMStrings.getSayFromMessage(msg.sourceMessage());
				if((!alreadyWanding)&&(checkWave(mob,said)))
					msg.addTrailerMsg(CMClass.getMsg(msg.source(),this,msg.target(),CMMsg.NO_EFFECT,null,CMMsg.MASK_ALWAYS|CMMsg.TYP_WAND_USE,said,CMMsg.NO_EFFECT,null));
			}
			break;
		default:
			break;
		}
		super.executeMsg(myHost,msg);
	}
	// wand stats handled by genweapon, filled by readableText
}
