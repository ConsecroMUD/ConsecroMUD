package com.suscipio_solutions.consecro_mud.MOBS.interfaces;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.ShopKeeper;



public interface Auctioneer extends ShopKeeper
{
	public static class AuctionData
	{
		public Item			 auctioningI=null;
		public MOB			 auctioningM=null;
		public MOB  		 highBidderM=null;
		public String   	 currency="";
		public double   	 highBid=0.0;
		public double   	 bid=0.0;
		public double   	 buyOutPrice=0.0;
		public int  		 state=-1;
		public long 		 tickDown=0;
		public long 		 start=0;
		public String   	 auctionDBKey="";
		public int daysRemaining(MOB mob, MOB mob2)
		{
			if(System.currentTimeMillis()>=tickDown) return 0;
			Area A=CMLib.map().getStartArea(mob);
			if(A==null) A=CMLib.map().getStartArea(mob2);
			long daysRemain=tickDown-System.currentTimeMillis();
			daysRemain=Math.round(Math.floor(CMath.div(CMath.div(daysRemain,CMProps.getMillisPerMudHour()),A.getTimeObj().getHoursInDay())));
			return (int)daysRemain;
		}
		public int daysEllapsed(MOB mob, MOB mob2)
		{
			if(System.currentTimeMillis()<start) return 0;
			Area A=CMLib.map().getStartArea(mob);
			if(A==null) A=CMLib.map().getStartArea(mob2);
			long daysRemain=System.currentTimeMillis()-start;
			daysRemain=Math.round(Math.floor(CMath.div(CMath.div(daysRemain,CMProps.getMillisPerMudHour()),A.getTimeObj().getHoursInDay())));
			return (int)daysRemain;
		}
	}

	public static final int STATE_START=0;
	public static final int STATE_RUNOUT=1;
	public static final int STATE_ONCE=2;
	public static final int STATE_TWICE=3;
	public static final int STATE_THREE=4;
	public static final int STATE_CLOSED=5;

	public String auctionHouse();
	public void setAuctionHouse(String named);

	/*public double liveListingPrice();
	public void setLiveListingPrice(double d);

	public double liveFinalCutPct();
	public void setLiveFinalCutPct(double d);
	*/

	public double timedListingPrice();
	public void setTimedListingPrice(double d);

	public double timedListingPct();
	public void setTimedListingPct(double d);

	public double timedFinalCutPct();
	public void setTimedFinalCutPct(double d);

	public int maxTimedAuctionDays();
	public void setMaxTimedAuctionDays(int d);

	public int minTimedAuctionDays();
	public void setMinTimedAuctionDays(int d);

	public static class AuctionRates
	{
		public double liveListPrice=0.0;
		public double timeListPrice=0.0;
		public double timeListPct=0.0;
		public double liveCutPct=0.0;
		public double timeCutPct=0.0;
		public int maxDays=Integer.MAX_VALUE;
		public int minDays=0;
		public AuctionRates()
		{
			final List<String> ratesV=CMParms.parseCommas(CMProps.getVar(CMProps.Str.AUCTIONRATES),true);
			while(ratesV.size()<7)
				ratesV.add("0");
			liveListPrice=CMath.s_double(ratesV.get(0));
			timeListPrice=CMath.s_double(ratesV.get(1));
			timeListPct=CMath.s_pct(ratesV.get(2));
			liveCutPct=CMath.s_pct(ratesV.get(3));
			timeCutPct=CMath.s_pct(ratesV.get(4));
			minDays=CMath.s_int(ratesV.get(5));
			maxDays=CMath.s_int(ratesV.get(6));
			if(minDays>maxDays)
				minDays=maxDays;
		}
		public AuctionRates(Auctioneer A)
		{
			if(A==null) return;
			final AuctionRates base=new AuctionRates();
			liveListPrice=base.liveListPrice;
			timeListPrice=A.timedListingPrice()<0.0?base.timeListPrice:A.timedListingPrice();
			timeListPct=A.timedListingPct()<0.0?base.timeListPct:A.timedListingPct();
			liveCutPct=base.liveCutPct;
			timeCutPct=A.timedFinalCutPct()<0.0?base.timeCutPct:A.timedFinalCutPct();
			maxDays=A.maxTimedAuctionDays()<0?base.maxDays:A.maxTimedAuctionDays();
			minDays=A.minTimedAuctionDays()<0?base.minDays:A.minTimedAuctionDays();
			if(minDays>maxDays) minDays=maxDays;
		}
	}
}
