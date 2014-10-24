package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.Map;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;

public interface ItemBalanceLibrary extends CMLibrary
{
	public int timsLevelCalculator(Item I);
	public int timsLevelCalculator(Item I, Ability ADJ, Ability RES, Ability CAST, int castMul);
	public boolean fixRejuvItem(Item I);
	public void toneDownWeapon(Weapon W, Ability ADJ);
	public void toneDownArmor(Armor A, Ability ADJ);
	public void toneDownAdjuster(Item I, Ability ADJ);
	public boolean toneDownValue(Item I);
	public int timsBaseLevel(Item I);
	public void balanceItemByLevel(Item I);
	public int levelsFromCaster(Item savedI, Ability CAST);
	public int levelsFromAdjuster(Item savedI, Ability ADJ);
	public boolean itemFix(Item I, int lvlOr0, StringBuffer changes);
	public Ability[] getTimsAdjResCast(Item I, int[] castMul);
	public Item enchant(Item I, int pct);
	public int levelsFromAbility(Item savedI);
	public Map<String, String> timsItemAdjustments(Item I,
										 int level,
										 int material,
										 int hands,
										 int wclass,
										 int reach,
										 long worndata);
}
