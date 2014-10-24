package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ListingLibrary;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.XMLLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Thief_KillLog extends ThiefSkill
{
	@Override public String ID() { return "Thief_KillLog"; }
	private final static String localizedName = CMLib.lang().L("Kill Log");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	private static final String[] triggerStrings =I(new String[] {"KILLLOG"});
	@Override protected boolean disregardsArmorCheck(MOB mob){return true;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_COMBATLORE;}
	protected Map<String,String[]> theList=new Hashtable<String,String[]>();
	protected Thief_Mark lastMarker=null;
	public MOB mark=null;

	public MOB getMark(MOB mob)
	{
		Thief_Mark A=null;
		if((lastMarker != null)&&(lastMarker.affecting()==mob)&&(!lastMarker.amDestroyed())&&(lastMarker.mark!=null))
			A=lastMarker;
		if(A==null)
		{
			A=(Thief_Mark)mob.fetchEffect("Thief_Mark");
			lastMarker=A;
		}
		if(A!=null)
			return A.mark;
		return null;
	}

	@Override
	public String text()
	{
		final StringBuffer str=new StringBuffer("<MOBS>");
		for(final String[] one : theList.values())
		{
			str.append("<MOB>");
			str.append(CMLib.xml().convertXMLtoTag("NAME",one[0]));
			str.append(CMLib.xml().convertXMLtoTag("LEVEL",one[1]));
			str.append(CMLib.xml().convertXMLtoTag("TOTAL",one[2]));
			str.append(CMLib.xml().convertXMLtoTag("KILLS",one[3]));
			str.append("</MOB>");
		}
		str.append("</MOBS>");
		return str.toString();
	}

	@Override
	public CMObject copyOf()
	{
		final Thief_KillLog obj=(Thief_KillLog)super.copyOf();
		obj.theList=new Hashtable<String,String[]>();
		obj.theList.putAll(theList);
		return obj;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((mark!=null)
		&&msg.amISource(mark)
		&&(msg.sourceMinor()==CMMsg.TYP_DEATH))
		{
			String[] set=theList.get(mark.Name());
			if(set==null)
			{
				set=new String[4];
				set[0]=mark.Name();
				set[2]="1";
				set[3]="0";
				theList.put(mark.Name(),set);
				final MOB mob=(MOB)affected;
				mob.tell(L("Ah, a new one for your kill log."));
				CMLib.leveler().postExperience(mob,null,null,mark.phyStats().level(),false);
			}
			set[1]=""+mark.phyStats().level();
			set[3]=Integer.toString(CMath.s_int(set[3])+1);
			mark=null;
			if((affected!=null)&&(affected instanceof MOB))
			{
				final Ability A=((MOB)affected).fetchAbility(ID());
				if(A!=null)	A.setMiscText(text());
			}
		}
		super.executeMsg(myHost,msg);
	}

	@Override
	public void setMiscText(String str)
	{
		theList.clear();
		if((str.trim().length()>0)&&(str.trim().startsWith("<MOBS>")))
		{
			final List<XMLLibrary.XMLpiece> buf=CMLib.xml().parseAllXML(str);
			final List<XMLLibrary.XMLpiece> V=CMLib.xml().getContentsFromPieces(buf,"MOBS");
			if(V!=null)
			for(int i=0;i<V.size();i++)
			{
				final XMLLibrary.XMLpiece ablk=V.get(i);
				if(ablk.tag.equalsIgnoreCase("MOB"))
				{
					final String[] one=new String[4];
					one[0]=CMLib.xml().getValFromPieces(ablk.contents,"NAME");
					one[1]=CMLib.xml().getValFromPieces(ablk.contents,"LEVEL");
					one[2]=CMLib.xml().getValFromPieces(ablk.contents,"TOTAL");
					one[3]=CMLib.xml().getValFromPieces(ablk.contents,"KILLS");
					theList.put(one[0],one);
				}
			}
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!(affected instanceof MOB))
			return super.tick(ticking,tickID);
		final MOB mob=(MOB)affected;
		final MOB m=getMark(mob);
		if(m!=mark)
		{
			mark=m;
			if(mark!=null)
			{
				String[] set=theList.get(mark.Name());
				if(set==null)
				{
					set=new String[4];
					set[0]=mark.Name();
					set[2]="0";
					set[3]="0";
					theList.put(mark.Name(),set);
				}
				set[1]=""+mark.phyStats().level();
				set[2]=Integer.toString(CMath.s_int(set[2])+1);
				if((affected!=null)&&(affected instanceof MOB))
				{
					final Ability A=((MOB)affected).fetchAbility(ID());
					if(A!=null)	A.setMiscText(text());
				}
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		if(proficiencyCheck(mob,0,auto))
		{
			final StringBuffer str=new StringBuffer("");
			final int[] cols={
					ListingLibrary.ColFixer.fixColWidth(20,mob.session()),
					ListingLibrary.ColFixer.fixColWidth(6,mob.session())
				};
			str.append(L("@x1@x2Kill Pct.\n\r",CMStrings.padRight(L("Name"),cols[0]),CMStrings.padRight(L("Level"),cols[1])));
			final Vector<String[]> order=new Vector<String[]>();
			int lowLevel=Integer.MIN_VALUE;
			String[] addOne=null;
			while(theList.size()>order.size())
			{
				addOne=null;
				lowLevel=Integer.MIN_VALUE;
				for(final String[] one : theList.values())
				{
					if((CMath.s_int(one[1])>=lowLevel)
					&&(!order.contains(one)))
					{
						lowLevel=CMath.s_int(one[1]);
						addOne=one;
					}
				}
				if(addOne==null)
					break;
				order.addElement(addOne);
			}
			for(int i=0;i<order.size();i++)
			{
				final String[] one=order.elementAt(i);
				int pct=0;
				final int total=CMath.s_int(one[2]);
				final int kills=CMath.s_int(one[3]);
				if(total>0)
					pct=(int)Math.round((CMath.div(kills,total)*100.0));
				str.append(CMStrings.padRight(CMStrings.removeColors(one[0]),cols[0])+CMStrings.padRight(one[1],cols[1])+pct+"%\n\r");
			}
			if(mob.session()!=null)
				mob.session().rawPrintln(str.toString());
			return true;
		}
		mob.tell(L("You failed to recall your log."));
		return false;
	}
}
