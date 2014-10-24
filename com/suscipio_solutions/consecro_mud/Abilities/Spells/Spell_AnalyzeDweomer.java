package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DoorKey;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Electronics;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.InnKey;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Light;
import com.suscipio_solutions.consecro_mud.Items.interfaces.MiscMagic;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Pill;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Potion;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Scroll;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wand;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.LandTitle;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_AnalyzeDweomer extends Spell
{
	@Override public String ID() { return "Spell_AnalyzeDweomer"; }
	private final static String localizedName = CMLib.lang().L("Analyze Item");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> analyze(s) the nature of <T-NAMESELF> carefully.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final StringBuffer str=new StringBuffer("");
				if(target instanceof Armor)
				{
					str.append(L("It is a kind of armor.  "));
					if(!target.rawLogicalAnd())
						str.append(L("It is worn on any one of the following: "));
					else
						str.append(L("It is worn on all of the following: "));
					final Wearable.CODES codes = Wearable.CODES.instance();
					for(final long wornCode : codes.all())
						if(wornCode!=Wearable.IN_INVENTORY)
						{
							if((codes.name(wornCode).length()>0)
							&&(((target.rawProperLocationBitmap()&wornCode)==wornCode)))
								str.append(codes.name(wornCode).toLowerCase()+" ");
						}
					str.append(".  ");
				}
				if((target instanceof Container)&&(((Container)target).capacity()>0))
					str.append(L("It is a container.  "));
				if(target instanceof Coins)
					str.append(L("It is currency. "));
				if(target instanceof Drink)
					str.append(L("You can drink it. "));
				if(target instanceof Food)
					str.append(L("You can eat it.  "));
				if(target instanceof Pill)
					str.append(L("It is a magic pill.  "));
				if(target instanceof Potion)
					str.append(L("It is a magic potion.  "));
				if(target instanceof Light)
					str.append(L("It is a light source.  "));
				if(target instanceof com.suscipio_solutions.consecro_mud.Items.interfaces.RoomMap)
					str.append(L("It is a map.  "));
				if(target instanceof MiscMagic)
					str.append(L("It has a magical aura.  "));
				if(target instanceof Scroll)
					str.append(L("It is a magic scroll.  "));
				if(target instanceof Wand)
					str.append(L("It is a magic wand.  "));
				if(target instanceof Electronics)
					str.append(L("It is some sort of high technology.  "));
				if(target instanceof InnKey)
					str.append(L("It is an Inn key.  "));
				else
				if(target instanceof DoorKey)
					str.append(L("It is a key.  "));
				if(target instanceof LandTitle)
					str.append(L("It is a property title.  "));
				if(target.isReadable())
					str.append(L("It is readable.  "));
				if(target instanceof DeadBody)
					str.append(L("It is a corpse of a @x1.  ",((DeadBody)target).charStats().getMyRace().name()));
				if(target instanceof Weapon)
				{
					final Weapon w=(Weapon)target;
					str.append(L("It is a @x1 weapon.  ",Weapon.CLASS_DESCS[w.weaponClassification()].toLowerCase()));
					str.append(L("It does @x1 damage.  ",Weapon.TYPE_DESCS[w.weaponType()].toLowerCase()));
					if(w.minRange()>0)
						str.append(L("It has a minimum range of @x1.  ",""+w.minRange()));
					if(w.maxRange()>w.minRange())
						str.append(L("It has a maximum range of @x1.  ",""+w.maxRange()));
				}
				str.append(L("It is made of @x1.  ",RawMaterial.CODES.NAME(target.material()).toLowerCase()));
				final Command C=CMClass.getCommand("Affect");
				try
				{
					final String affectStr=C.executeInternal(mob,0,target).toString();
					if(affectStr.length()<5)
						str.append(L("It is affected by: @x1",affectStr));
				}
				catch(final Exception e){}
				if(mob.isMonster())
					CMLib.commands().postSay(mob,null,str.toString().trim(),false,false);
				else
					mob.tell(str.toString().trim());
			}

		}
		else
			beneficialVisualFizzle(mob,target,L("<S-NAME> analyze(s) the nature of <T-NAMESELF>, looking more frustrated every second."));


		// return whether it worked
		return success;
	}
}
