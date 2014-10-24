package com.suscipio_solutions.consecro_mud.WebMacros;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class StatRejuvCharts extends StdWebMacro
{
	@Override public String name()	{return "StatRejuvCharts";}

	protected String getReq(HTTPRequest httpReq, String tag)
	{
		String s=httpReq.getUrlParameter(tag);
		if(s==null) s="";
		return s;
	}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final StringBuffer buf=new StringBuffer("");
		final String which=httpReq.getUrlParameter("WHICH");
		final MOB mob=CMClass.getMOB("StdMOB");
		mob.baseState().setMana(100);
		mob.baseState().setMovement(100);
		mob.baseState().setHitPoints(100);
		mob.recoverMaxState();
		mob.resetToMaxState();
		mob.curState().setHunger(1000);
		mob.curState().setThirst(1000);
		if((which!=null)&&(which.equals("HP")))
			buf.append("<BR>Chart: Hit Points<BR>");
		else
		if((which!=null)&&(which.equals("MN")))
			buf.append("<BR>Chart: Mana<BR>");
		else
		if((which!=null)&&(which.equals("MV")))
			buf.append("<BR>Chart: Movement<BR>");
		else
			buf.append("<BR>Chart: Hit Points<BR>");
		buf.append("Flags: ");
		int disposition=0;

		if((getReq(httpReq,"SITTING").length()>0))
		{ disposition=PhyStats.IS_SITTING; buf.append("Sitting ");}
		if((getReq(httpReq,"SLEEPING").length()>0))
		{ disposition=PhyStats.IS_SLEEPING; buf.append("Sleeping ");}
		if((getReq(httpReq,"FLYING").length()>0))
		{ disposition=PhyStats.IS_FLYING; buf.append("Flying ");}
		if((getReq(httpReq,"SWIMMING").length()>0))
		{ disposition=PhyStats.IS_SWIMMING; buf.append("Swimming ");}
		if((getReq(httpReq,"RIDING").length()>0))
		{ mob.setRiding((Rideable)CMClass.getMOB("GenRideable")); buf.append("Riding ");}
		final boolean hungry=(httpReq.getUrlParameter("HUNGRY")!=null)&&(httpReq.getUrlParameter("HUNGRY").length()>0);
		if(hungry){ buf.append("Hungry ");		mob.curState().setHunger(0);}
		final boolean thirsty=(httpReq.getUrlParameter("THIRSTY")!=null)&&(httpReq.getUrlParameter("THIRSTY").length()>0);
		if(thirsty){ buf.append("Thirsty ");		mob.curState().setThirst(0);}
		mob.basePhyStats().setDisposition(disposition);
		mob.recoverPhyStats();

		buf.append("<P><TABLE WIDTH=100% BORDER=1>");
		buf.append("<TR><TD><B>STATS:</B></TD>");
		for(int stats=4;stats<=25;stats++)
			buf.append("<TD><B>"+stats+"</B></TD>");
		buf.append("</TR>");
		for(int level=1;level<=30;level++)
		{
			buf.append("<TR>");
			buf.append("<TD><B>LVL "+level+"</B></TD>");
			for(int stats=4;stats<=25;stats++)
			{
				for(final int c: CharStats.CODES.BASECODES())
					mob.baseCharStats().setStat(c,stats);
				mob.recoverCharStats();
				mob.basePhyStats().setLevel(level);
				mob.recoverPhyStats();
				mob.curState().setMana(0);
				mob.curState().setMovement(0);
				mob.curState().setHitPoints(0);

				double con=mob.charStats().getStat(CharStats.STAT_CONSTITUTION);
				double man=mob.charStats().getStat(CharStats.STAT_INTELLIGENCE)+mob.charStats().getStat(CharStats.STAT_WISDOM);
				double str=mob.charStats().getStat(CharStats.STAT_STRENGTH);
				if(mob.curState().getHunger()<1)
				{
					con=con*0.85;
					man=man*0.75;
					str=str*0.85;
				}
				if(mob.curState().getThirst()<1)
				{
					con=con*0.85;
					man=man*0.75;
					str=str*0.85;
				}
				if(mob.curState().getFatigue()>CharState.FATIGUED_MILLIS)
					man=man*.5;

				final double lvl=mob.phyStats().level();
				final double lvlby1p5=CMath.div(lvl,1.5);
				//double lvlby2=CMath.div(lvl,2.0);
				//double lvlby3=CMath.div(lvl,3.0);

				double hpGain=(con>1.0)?((con/40.0)*lvlby1p5)+(con/4.5)+2.0:1.0;
				double manaGain=(man>2.0)?((man/80.0)*lvl)+(man/4.5)+2.0:1.0;
				double moveGain=(str>1.0)?((str/40.0)*lvl)+(str/3.0)+5.0:1.0;

				if(CMLib.flags().isSleeping(mob))
				{
					hpGain+=(hpGain/2.0);
					manaGain+=(manaGain/2.0);
					moveGain+=(moveGain/2.0);
					if((mob.riding()!=null)&&(mob.riding() instanceof Item))
					{
						hpGain+=(hpGain/8.0);
						manaGain+=(manaGain/8.0);
						moveGain+=(moveGain/8.0);
					}
				}
				else
				if((CMLib.flags().isSitting(mob))||(mob.riding()!=null))
				{
					hpGain+=(hpGain/4.0);
					manaGain+=(manaGain/4.0);
					moveGain+=(moveGain/4.0);
					if((mob.riding()!=null)&&(mob.riding() instanceof Item))
					{
						hpGain+=(hpGain/8.0);
						manaGain+=(manaGain/8.0);
						moveGain+=(moveGain/8.0);
					}
				}
				else
				{
					if(CMLib.flags().isFlying(mob))
						moveGain+=(moveGain/8.0);
					else
					if(CMLib.flags().isSwimming(mob))
					{
						hpGain-=(hpGain/2.0);
						manaGain-=(manaGain/4.0);
						moveGain-=(moveGain/2.0);
					}
				}

				if((!mob.isInCombat())
				&&(!CMLib.flags().isClimbing(mob)))
				{
					if((hpGain>0)&&(!CMLib.flags().isGolem(mob)))
						mob.curState().adjHitPoints((int)Math.round(hpGain),mob.maxState());
					if(manaGain>0)
						mob.curState().adjMana((int)Math.round(manaGain),mob.maxState());
					if(moveGain>0)
						mob.curState().adjMovement((int)Math.round(moveGain),mob.maxState());
				}

				if((which!=null)&&(which.equals("HP")))
					buf.append("<TD>"+mob.curState().getHitPoints()+"</TD>");
				else
				if((which!=null)&&(which.equals("MN")))
					buf.append("<TD>"+mob.curState().getMana()+"</TD>");
				else
				if((which!=null)&&(which.equals("MV")))
					buf.append("<TD>"+mob.curState().getMovement()+"</TD>");
				else
					buf.append("<TD>"+mob.curState().getHitPoints()+"</TD>");
			}
			buf.append("</TR>");
		}
		mob.destroy();
		buf.append("</TABLE>");
		return clearWebMacros(buf);
	}

}
