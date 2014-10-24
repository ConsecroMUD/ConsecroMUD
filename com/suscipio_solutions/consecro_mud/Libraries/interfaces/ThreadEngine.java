package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.Iterator;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.TickClient;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;
import com.suscipio_solutions.consecro_mud.core.interfaces.TickableGroup;
import com.suscipio_solutions.consecro_mud.core.threads.CMRunnable;

public interface ThreadEngine extends CMLibrary, Runnable
{
	// tick related
	public TickClient startTickDown(Tickable E, int tickID, long TICK_TIME, int numTicks);
	public TickClient startTickDown(Tickable E, int tickID, int numTicks);
	public boolean deleteTick(Tickable E, int tickID);
	public boolean setTickPending(Tickable E, int tickID);
	public void deleteAllTicks(Tickable E);
	public void suspendTicking(Tickable E, int tickID);
	public void resumeTicking(Tickable E, int tickID);
	public void suspendResumeRecurse(CMObject O, boolean skipEmbeddedAreas, boolean suspend);
	public boolean isSuspended(Tickable E, int tickID);
	public void suspendAll(CMRunnable[] exceptRs);
	public void resumeAll();
	public boolean isAllSuspended();
	public void clearDebri(Room room, int taskCode);
	public String tickInfo(String which);
	public void tickAllTickers(Room here);
	public void rejuv(Room here, int tickID);
	public String systemReport(String itemCode);
	public boolean isTicking(Tickable E, int tickID);
	public  Iterator<TickableGroup> tickGroups();
	public String getTickStatusSummary(Tickable obj);
	public List<Tickable> getNamedTickingObjects(String name);
	public Runnable findRunnableByThread(final Thread thread);
	public void executeRunnable(Runnable R);
	public void executeRunnable(String threadGroupName, Runnable R);
	public void debugDumpStack(final String ID, Thread theThread);
	public long getTicksEllapsedSinceStartup();
}
