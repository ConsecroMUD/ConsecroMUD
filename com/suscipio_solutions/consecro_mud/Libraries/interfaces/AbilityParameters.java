package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.ItemCraftor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.collections.DVector;
import com.suscipio_solutions.consecro_mud.core.exceptions.CMException;
import com.suscipio_solutions.consecro_mud.core.interfaces.Affectable;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public interface AbilityParameters extends CMLibrary
{
	public static final int PARMTYPE_CHOICES=0;
	public static final int PARMTYPE_STRING=1;
	public static final int PARMTYPE_NUMBER=2;
	public static final int PARMTYPE_STRINGORNULL=3;
	public static final int PARMTYPE_ONEWORD=4;
	public static final int PARMTYPE_MULTICHOICES=5;
	public static final int PARMTYPE_SPECIAL=6;

	public static interface AbilityParmEditor
	{
		public String ID();
		public int parmType();
		public DVector createChoices(Enumeration<? extends Object> e);
		public DVector createChoices(Vector<? extends Object> V);
		public DVector createChoices(String[] S);
		public DVector choices();
		public int appliesToClass(Object o);
		public boolean confirmValue(String oldVal);
		public String[] fakeUserInput(String oldVal);
		public String commandLinePrompt(MOB mob, String oldVal, int[] showNumber, int showFlag) throws java.io.IOException;
		public String colHeader();
		public String prompt();
		public String defaultValue();
		public String webValue(HTTPRequest httpReq, java.util.Map<String,String> parms, String oldVal, String fieldName);
		public String webField(HTTPRequest httpReq, java.util.Map<String,String> parms, String oldVal, String fieldName);
		public String webTableField(HTTPRequest httpReq, java.util.Map<String,String> parms, String oldVal);
		public String convertFromItem(final ItemCraftor A, final Item I);
	}

	public String encodeCodedSpells(Affectable I);
	public List<Ability> getCodedSpells(String spells);
	public void parseWearLocation(short[] layerAtt, short[] layers, long[] wornLoc, boolean[] logicalAnd, double[] hardBonus, String wearLocation);
	public void modifyRecipesList(MOB mob, String recipeFilename, String recipeFormat) throws java.io.IOException;
	public void testRecipeParsing(String recipeFilename, String recipeFormat, boolean save);
	public void testRecipeParsing(StringBuffer recipesString, String recipeFormat) throws CMException;
	public AbilityRecipeData parseRecipe(String recipeFilename, String recipeFormat);
	public Map<String,AbilityParmEditor> getEditors();
	public void resaveRecipeFile(MOB mob, String recipeFilename, Vector<DVector> rowsV, Vector<? extends Object> columnsV, boolean saveVFS);
	public StringBuffer getRecipeList(ItemCraftor iA);
	public String makeRecipeFromItem(final ItemCraftor C, final Item I) throws CMException;

	public static interface AbilityRecipeData
	{
		public String recipeFilename();
		public String recipeFormat();
		public Vector<DVector> dataRows();
		@SuppressWarnings("rawtypes")
		public Vector columns();
		public int[] columnLengths();
		public String[] columnHeaders();
		public int numberOfDataColumns();
		public String parseError();
		public int getClassFieldIndex();
		public DVector newRow(String classFieldData);
		public DVector blankRow();
		public boolean wasVFS();
	}

}
