package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Iterator;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Climate;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;
import com.suscipio_solutions.consecro_mud.core.interfaces.ShopKeeper;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Spell_DetectWater extends Spell
{
	@Override public String ID() { return "Spell_DetectWater"; }
	private final static String localizedName = CMLib.lang().L("Detect Water");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Detecting Water)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}
	@Override public int enchantQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	Room lastRoom=null;
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;	}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
			lastRoom=null;
		super.unInvoke();
		if(canBeUninvoked())
			mob.tell(L("Your senses are no longer sensitive to liquids."));
	}
	public String waterCheck(MOB mob, Item I, Item container, StringBuffer msg)
	{
		if(I==null) return "";
		if(I.container()==container)
		{
			if(((I instanceof Drink))
			&&(((Drink)I).containsDrink())
			&&(CMLib.flags().canBeSeenBy(I,mob)))
				msg.append(L("@x1 contains some sort of liquid.\n\r",I.name(mob)));
		}
		else
		if((I.container()!=null)&&(I.container().container()==container))
			if(msg.toString().indexOf(I.container().name()+" contains some sort of liquid.")<0)
				msg.append(L("@x1 contains some sort of liquid.\n\r",I.container().name()));
		return msg.toString();
	}
	public String waterHere(MOB mob, Environmental E, Item container)
	{
		final StringBuffer msg=new StringBuffer("");
		if(E==null) return msg.toString();
		if((E instanceof Room)&&(CMLib.flags().canBeSeenBy(E,mob)))
		{
			final Room room=(Room)E;
			if((room.domainType()==Room.DOMAIN_OUTDOORS_UNDERWATER)
			||(room.domainType()==Room.DOMAIN_OUTDOORS_WATERSURFACE)
			||(room.domainType()==Room.DOMAIN_INDOORS_UNDERWATER)
			||(room.domainType()==Room.DOMAIN_INDOORS_WATERSURFACE))
				msg.append(L("Your liquid senses are saturated.  This is a very wet place.\n\r"));
			else
			if(CMath.bset(room.getClimateType(),Places.CLIMASK_WET))
				msg.append(L("Your liquid senses are saturated.  This is a damp place.\n\r"));
			else
			if((room.getArea().getClimateObj().weatherType(room)==Climate.WEATHER_RAIN)
			||(room.getArea().getClimateObj().weatherType(room)==Climate.WEATHER_THUNDERSTORM))
				msg.append(L("It is raining here! Your liquid senses are saturated!\n\r"));
			else
			if(room.getArea().getClimateObj().weatherType(room)==Climate.WEATHER_HAIL)
				msg.append(L("It is hailing here! Your liquid senses are saturated!\n\r"));
			else
			if(room.getArea().getClimateObj().weatherType(room)==Climate.WEATHER_SNOW)
				msg.append(L("It is snowing here! Your liquid senses are saturated!\n\r"));
			else
			{
				for(int i=0;i<room.numItems();i++)
				{
					final Item I=room.getItem(i);
					waterCheck(mob,I,container,msg);
				}
				for(int m=0;m<room.numInhabitants();m++)
				{
					final MOB M=room.fetchInhabitant(m);
					if((M!=null)&&(M!=mob))
						msg.append(waterHere(mob,M,null));
				}
			}
		}
		else
		if((E instanceof Item)&&(CMLib.flags().canBeSeenBy(E,mob)))
		{
			waterCheck(mob,(Item)E,container,msg);
			msg.append(waterHere(mob,((Item)E).owner(),(Item)E));
		}
		else
		if((E instanceof MOB)&&(CMLib.flags().canBeSeenBy(E,mob)))
		{
			for(int i=0;i<((MOB)E).numItems();i++)
			{
				final Item I=((MOB)E).getItem(i);
				final StringBuffer msg2=new StringBuffer("");
				waterCheck(mob,I,container,msg2);
				if(msg2.length()>0)
					return E.name()+" is carrying some liquids.";
			}
			final ShopKeeper SK=CMLib.coffeeShops().getShopKeeper(E);
			if(SK!=null)
			{
				final StringBuffer msg2=new StringBuffer("");
				for(final Iterator<Environmental> i=SK.getShop().getStoreInventory();i.hasNext();)
				{
					final Environmental E2=i.next();
					if(E2 instanceof Item)
						waterCheck(mob,(Item)E2,container,msg2);
					if(msg2.length()>0)
						return E.name()+" has some liquids in stock.";
				}
			}
		}
		return msg.toString();
	}
	public void messageTo(MOB mob)
	{
		String last="";
		String dirs="";
		for(int d=Directions.NUM_DIRECTIONS();d>=0;d--)
		{
			Room R=null;
			Exit E=null;
			if(d<Directions.NUM_DIRECTIONS())
			{
				R=mob.location().getRoomInDir(d);
				E=mob.location().getExitInDir(d);
			}
			else
			{
				R=mob.location();
				E=CMClass.getExit("StdExit");
			}
			if((R!=null)&&(E!=null))
			{
				boolean metalFound=false;
				if(waterHere(mob,R,null).length()>0)
					metalFound=true;
				else
				for(int m=0;m<R.numInhabitants();m++)
				{
					final MOB M=R.fetchInhabitant(m);
					if((M!=null)&&(M!=mob)&&(waterHere(mob,M,null).length()>0))
					{ metalFound=true; break;}
				}

				if(metalFound)
				{
					if(last.length()>0)
						dirs+=", "+last;
					if(d>=Directions.NUM_DIRECTIONS())
						last="here";
					else
						last=Directions.getFromDirectionName(d);
				}
			}
		}

		if((dirs.length()!=0)||(last.length()!=0))
		{
			if(dirs.length()==0)
				mob.tell(L("Water smells are coming from @x1.",last));
			else
				mob.tell(L("Water smells are coming from @x1, and @x2.",dirs.substring(2),last));
		}
	}
	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((tickID==Tickable.TICKID_MOB)
		   &&(affected!=null)
		   &&(affected instanceof MOB)
		   &&(((MOB)affected).location()!=null)
		   &&((lastRoom==null)||(((MOB)affected).location()!=lastRoom)))
		{
			lastRoom=((MOB)affected).location();
			messageTo((MOB)affected);
		}
		return true;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(((MOB)target).isInCombat()||((MOB)target).isMonster())
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected!=null)
		   &&(affected instanceof MOB)
		   &&(msg.target()!=null)
		   &&(msg.amISource((MOB)affected))
		   &&((msg.sourceMinor()==CMMsg.TYP_LOOK)||(msg.sourceMinor()==CMMsg.TYP_EXAMINE)))
		{
			if((msg.tool()!=null)&&(msg.tool().ID().equals(ID())))
			{
				final String str=waterHere((MOB)affected,msg.target(),null);
				if(str.length()>0)
					((MOB)affected).tell(str);
			}
			else
			if((msg.target()!=null)
			&&(waterHere((MOB)affected,msg.target(),null).length()>0)
			&&(msg.source()!=msg.target()))
			{
				final CMMsg msg2=CMClass.getMsg(msg.source(),msg.target(),this,CMMsg.MSG_LOOK,CMMsg.NO_EFFECT,CMMsg.NO_EFFECT,null);
				msg.addTrailerMsg(msg2);
			}
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already detecting liquid things."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> gain(s) liquid sensitivities!"):L("^S<S-NAME> incant(s) softly, and gain(s) liquid sensitivities!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> incant(s) and open(s) <S-HIS-HER> liquified eyes, but the spell fizzles."));

		return success;
	}
}
