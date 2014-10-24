package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.List;
import java.util.Random;

public interface DiceLibrary extends CMLibrary
{
	public boolean normalizeAndRollLess(int score);
	public int normalizeBy5(int score);
	public int rollHP(int level, int code);
	public int getHPCode(String str);
	public int getHPCode(int roll, int dice, int plus);
	public int[] getHPBreakup(int level, int code);
	public int roll(int number, int die, int modifier);
	public Object pick(Object[] set, Object not);
	public Object pick(Object[] set);
	public int pick(int[] set, int not);
	public int pick(int[] set);
	public Object doublePick(Object[][] set);
	public Object pick(List<? extends Object> set);
	public int rollPercentage();
	public int rollNormalDistribution(int number, int die, int modifier);
	public int rollLow(int number, int die, int modifier);
	public Random getRandomizer();
	public long plusOrMinus(final long range);
	public int plusOrMinus(final int range);
	public int inRange(final int min, final int max);
	public long inRange(final long min, final long max);
}
