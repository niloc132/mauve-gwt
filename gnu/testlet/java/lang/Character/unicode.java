// Tags: JDK1.1

package gnu.testlet.java.lang.Character;

import java.io.*;
import gnu.testlet.Testlet;
import gnu.testlet.TestHarness;

/*
MISSING:
Instance tests
( constructor, charValue, serialization): should be in other file
*/

public class unicode implements Testlet
{

	public static boolean testDeprecated;
	public static boolean verbose;
	public static boolean benchmark;

	public class CharInfo
	{
		String name;
		String category;
		int digit;
		int numericValue;
		char uppercase;
		char lowercase;
		char titlecase;
	}

	public CharInfo[] chars = new CharInfo[0x10000];
	public int failures;
	public int tests;

	public unicode() 
	{
	}

	public unicode(TestHarness harness, String filename) throws IOException
	{
			BufferedReader bir = new BufferedReader(
				new FileReader(harness.getSourceDirectory() +
                                    File.separator + 
                                    "gnu/testlet/java/lang/Character/" +
                                    filename));
			harness.debug("Reading unicode database...");
			while ( bir.ready() )
			{
				String str;
				CharInfo ci = new CharInfo();
				str = getNext(bir);
				int code = (char)Integer.parseInt(str,16);
				ci.name = getNext(bir);
				ci.category = getNext(bir);
				getNext(bir);
				getNext(bir);
				getNext(bir);
				str = getNext(bir);
				if ( !str.equals("") )
					ci.digit = Integer.parseInt(str, 10 );
				else
					ci.digit = -1;
				getNext(bir);
				str = getNext(bir);
				if ( str.equals("") )
				{
					ci.numericValue = -1;
				}
				else
				{
					try {
						ci.numericValue = Integer.parseInt(str,10);
						if ( ci.numericValue < 0 )
							ci.numericValue = -2;
					} catch ( NumberFormatException e )
						{
							ci.numericValue = -2;
						}
				}

				getNext(bir);
				getNext(bir);
				getNext(bir);
				str = getNext(bir);
				if ( !str.equals("") )
					ci.uppercase = (char)Integer.parseInt(str, 16);
				str = getNext(bir);
				if ( !str.equals("") )
					ci.lowercase = (char)Integer.parseInt(str, 16);
				str = getNext(bir);
				if ( !str.equals("") )
					ci.titlecase = (char)Integer.parseInt(str, 16);
				chars[code] = ci;
			}

		CharInfo ch = new CharInfo();
		ch.name = "CJK Ideograph";
		ch.category = "Lo";
		ch.digit = -1;
		ch.numericValue = -1;
		for ( int i = 0x4E01; i <= 0x9FA4; i++ )
		{
			chars[i] = ch;
		}

		ch = new CharInfo();
		ch.name = "Hangul Syllable";
		ch.category = "Lo";
		ch.digit = -1;
		ch.numericValue = -1;
		for ( int i = 0xAC01; i <= 0xD7A2; i++ )
		{

			chars[i] = ch;
		}

		ch = new CharInfo();
		ch.name = "CJK Compatibility Ideograph";
		ch.category = "Lo";
		ch.digit = -1;
		ch.numericValue = -1;
		for ( int i = 0xF901; i <= 0xFA2C; i++ )
		{
			chars[i] = ch;
		}

		ch = new CharInfo();
		ch.name = "Surrogate";
		ch.category= "Cs";
		ch.digit = -1;
		ch.numericValue = -1;
		for ( int i = 0xD800; i <= 0xDFFFl; i++ )
		{
			chars[i] = ch;
		}

		ch = new CharInfo();
		ch.name = "Private Use";
		ch.category = "Co";
		ch.digit = -1;
		ch.numericValue = -1;
		for ( int i = 0xE000; i <= 0xF8FF; i++ )
		{
			chars[i] = ch;
		}

		ch = new CharInfo();
		ch.name = "UNDEFINED";
		ch.category = "Cn";
		ch.digit = -1;
		ch.numericValue = -1;
		for ( int i =0; i <= 0xFFFF; i++ )
		{
			if ( chars[i] == null )
				chars[i] = ch;
		}

		/*
				It is not stated that A-Z and a-z should
				have getNumericValue() (as it is in digit())
		 */
		for ( int i = 'A'; i <= 'Z'; i++ )
		{
			chars[i].digit = i - 'A' + 10;
			chars[i].numericValue = chars[i].digit; // ??
		}

		for ( int i = 'a'; i <= 'z'; i++ )
		{
			chars[i].digit = i - 'a' + 10;
			chars[i].numericValue = chars[i].digit; // ??
		}

		/*
			Following two ranges are not clearly stated in
			spec, but they are reasonable;
		*/
		for ( int i = 0xFF21; i <= 0xFF3A; i++ )
		{
			chars[i].digit = i - 0xFF21 + 10;
			chars[i].numericValue = chars[i].digit; // ??
		}
		for ( int i = 0xFF41; i <= 0xFF5A; i++ )
		{
			chars[i].digit = i - 0xFF41 + 10;
			chars[i].numericValue = chars[i].digit; // ??
		}

		System.out.println("done");
	}

	private String getNext( Reader r ) throws IOException
	{
		StringBuffer sb = new StringBuffer();
		while ( r.ready() )
		{
			char ch = (char)r.read();
			if ( ch == '\r' )
			{
				continue;
			}
			else if (ch == ';' ||  ch == '\n' )
			{
				return sb.toString();
			}
			else
				sb.append(ch);
		}
		return sb.toString();
	}

	public String stringChar(int ch )
	{
		return 	"Character " + Integer.toString(ch,16) + ":"
				+ chars[ch].name;
	}

	public boolean range( int mid, int low, int high )
	{
		return (mid >= low && mid <= high);
	}


	public void performTests(TestHarness harness)
	{
		for ( int x =0; x <= 0xffff; x++ )
		{

// isLowerCase
			char i = (char)x;
			if (  "Ll".equals(chars[i].category) !=
				Character.isLowerCase((char)i) )
			{
				harness.check(false, i + " " +
(Character.isLowerCase((char)i) ? "lowercase" : "not-lowercase" ));

			}
			else harness.check(true);

// isUpperCase
			if (  "Lu".equals(chars[i].category) !=
				Character.isUpperCase((char)i) )
			{
				harness.check(false, i + " " +
(Character.isUpperCase((char)i) ? "uppercase" : "not-uppercase" ) );
			}
			else harness.check(true);

// isTitleCase
			if (  "Lt".equals(chars[i].category) !=
				Character.isTitleCase((char)i) )
			{
				harness.check(false, i + " " +
(Character.isTitleCase((char)i) ? "titlecase" : "not-titlecase" ) );
			}
			else harness.check(true);

// isDigit
//			if ( (chars[i].category.charAt(0) == 'N') !=
//				Character.isDigit((char)i) )
			if ((
					(chars[i].name.indexOf("DIGIT") > -1) &&
					!range(i,0x2000,0x2FFF)
				) !=Character.isDigit((char)i) )
			{
				harness.check(false, i + " " +
(Character.isDigit((char)i) ? "digit" : "not-digit" ) );
			}
			else harness.check(true);

// isDefined
/*			if ( ((chars[i] != null) ||
					(i >= 0xD800 && i <= 0xFA2D ) )*/
				if ( !chars[i].category.equals("Cn")
				 != Character.isDefined((char)i) )
			{
				harness.check(false, i + " " + 
					(Character.isDefined((char)i) ? "defined" : "not-defined" ) );
			}
			else harness.check(true);

// isLetter
			if ( (
			(chars[i].category.charAt(0) == 'L')) !=
				Character.isLetter((char)i) )
			{
				harness.check(false, i + " " +
(Character.isLetter((char)i) ? "letter" : "not-letter" ) );
			}
			else harness.check(true);

// isLetterOrDigit
			if ( Character.isLetterOrDigit(i) !=
				(Character.isLetter(i) || Character.isDigit(i)) )
			{
				harness.check(false, i + " " +
(Character.isLetterOrDigit(i) ? "letterordigit" : "not-letterordigit") );
			}
			else harness.check(true);

// isSpaceChar
			if ( (
			(chars[i].category.charAt(0) == 'Z')) !=
				Character.isSpaceChar(i) )
			{
				harness.check(false, i + " " +
(Character.isSpaceChar(i) ? "spacechar" : "not-spacechar" ) );
			}
			else harness.check(true);

// isWhiteSpace
		boolean t = false;

		if ( chars[i].category.charAt(0) == 'Z' && i !=0x00a0
					&& i != 0xFEFF )
					t = true;
		if ( range(i,0x0009,0x000D)|| range(i,0x001C,0x001F) )
					t = true;

		if ( t != Character.isWhitespace(i) )
		{
			harness.check(false, 
Character.isWhitespace(i) ? "whitespace" : "not-whitespace" );
		}
		else harness.check(true);

// isISOControl
		if (  ((i <= 0x001F)
		|| range(i,0x007F,0x009F) ) !=
			Character.isISOControl(i) )
		{
			harness.check(false, 
Character.isISOControl(i) ? "isocontrol" : "not-isocontrol");
		}
		else harness.check(true);

		int type = Character.getType(i);
		String typeStr = null;
		switch (type)
		{
			case Character.UNASSIGNED: typeStr = "Cn"; break;
			case Character.UPPERCASE_LETTER: typeStr = "Lu"; break;
			case Character.LOWERCASE_LETTER: typeStr = "Ll"; break;
			case Character.TITLECASE_LETTER: typeStr = "Lt"; break;
			case Character.MODIFIER_LETTER: typeStr = "Lm"; break;
			case Character.OTHER_LETTER: typeStr = "Lo"; break;
			case Character.NON_SPACING_MARK: typeStr = "Mn"; break;
			case Character.ENCLOSING_MARK: typeStr = "Me"; break;
			case Character.COMBINING_SPACING_MARK: typeStr = "Mc"; break;
			case Character.DECIMAL_DIGIT_NUMBER: typeStr = "Nd"; break;
			case Character.LETTER_NUMBER: typeStr = "Nl"; break;
			case Character.OTHER_NUMBER: typeStr = "No"; break;
			case Character.SPACE_SEPARATOR: typeStr = "Zs"; break;
			case Character.LINE_SEPARATOR: typeStr = "Zl"; break;
			case Character.PARAGRAPH_SEPARATOR: typeStr = "Zp"; break;
			case Character.CONTROL: typeStr = "Cc"; break;
			case Character.FORMAT: typeStr = "Cf"; break;
			case Character.PRIVATE_USE: typeStr = "Co"; break;
			case Character.SURROGATE: typeStr = "Cs"; break;
			case Character.DASH_PUNCTUATION: typeStr = "Pd"; break;
			case Character.START_PUNCTUATION: typeStr = "Ps"; break;
			case Character.END_PUNCTUATION: typeStr = "Pe"; break;
			case Character.CONNECTOR_PUNCTUATION: typeStr = "Pc"; break;
			case Character.OTHER_PUNCTUATION: typeStr = "Po"; break;
			case Character.MATH_SYMBOL: typeStr = "Sm"; break;
			case Character.CURRENCY_SYMBOL: typeStr = "Sc"; break;
			case Character.MODIFIER_SYMBOL: typeStr = "Sk"; break;
			case Character.OTHER_SYMBOL: typeStr = "So"; break;
			default: typeStr = "ERROR"; break;
		}

		if ( !chars[i].category.equals(typeStr) )
		{
			harness.check(false, 
				stringChar(i) + " is reported to be type " + typeStr +
					" instead of " + chars[i].category);
		}
		else harness.check(true);

// isJavaIdentifierStart
		if ( ( (chars[i].category.charAt(0) == 'L') ||
				chars[i].category.equals("Sc") ||
				chars[i].category.equals("Pc") ) !=
			Character.isJavaIdentifierStart(i) )
		{
			harness.check(false, 
Character.isJavaIdentifierStart(i) ? "javaindetifierstart" : "not-javaidentfierstart");
		}
		else harness.check(true);

// isJavaIdentifierPart
		boolean shouldbe = false;
		typeStr = chars[i].category;
		if (
			(
			typeStr.charAt(0) == 'L' ||
			typeStr.equals("Sc") ||
			typeStr.equals("Pc") ||
			typeStr.equals("Nd") ||
			typeStr.equals("Nl") ||
			typeStr.equals("Mc") ||
			typeStr.equals("Mn") ||
			typeStr.equals("Cf") ||
			(typeStr.equals("Cc") &&
				!range(i,0x0009,0x000D) && !range(i,0x001C,0x001F))
			) != Character.isJavaIdentifierPart(i) )
		{
			harness.check(false, 
Character.isJavaIdentifierPart(i) ? "javaidentifierpart" : "not-javaidentifierpart" );
		}
		else harness.check(true);

//isUnicodeIdentifierStart
		if ( (chars[i].category.charAt(0) == 'L') !=
			Character.isUnicodeIdentifierStart(i) )
		{
			harness.check(false, 
Character.isUnicodeIdentifierStart(i) ? "unicodeidentifierstart" : "not-unicodeidentifierstart" );
		}
		else harness.check(true);

//isUnicodeIdentifierPart
		shouldbe = false;
		typeStr = chars[i].category;
		if (
			(
			typeStr.charAt(0) == 'L' ||
			typeStr.equals("Pc") ||
			typeStr.equals("Nd") ||
			typeStr.equals("Nl") ||
			typeStr.equals("Mc") ||
			typeStr.equals("Mn") ||
			typeStr.equals("Cf") ||
			(typeStr.equals("Cc") &&
				!range(i,0x0009,0x000D) && !range(i,0x001C,0x001F))
			) != Character.isUnicodeIdentifierPart(i) )
		{
			harness.check(false, 
Character.isUnicodeIdentifierPart(i) ? "unicodeidentifierpart" : "not-unicodeidentifierpart" );
		}
		else harness.check(true);


//isIdentifierIgnorable
		if (
			(
			range(i,0x0000,0x0008) ||
			range(i,0x000E,0x001B) ||
			range(i,0x007F,0x009F) ||
			range(i,0x200C,0x200F) ||
			range(i,0x202A,0x202E) ||
			range(i,0x206A,0x206F) ||
				i == 0xFEFF
			) != Character.isIdentifierIgnorable(i) )
		{
			harness.check(false, 
Character.isIdentifierIgnorable(i)? "identifierignorable": "not-identifierignorable");
		}
		else harness.check(true);

// toLowerCase
			char cs = (chars[i].lowercase != 0 ? chars[i].lowercase : i);
			if ( Character.toLowerCase(i) != cs )
			{
				harness.check(false, stringChar(i) + " has wrong lowercase form of " +
				stringChar(Character.toLowerCase(i)) +" instead of " +
				stringChar(cs) );
			}
			else harness.check(true);

// toUpperCase
			cs =(chars[i].uppercase != 0 ? chars[i].uppercase : i);
			if ( Character.toUpperCase(i) != cs )
			{
				harness.check(false, stringChar(i) +
				" has wrong uppercase form of " +
				stringChar(Character.toUpperCase(i)) +
				" instead of " +
				stringChar(cs) );
			}
			else harness.check(true);

// toTitleCase
			cs = (chars[i].titlecase != 0 ? chars[i].titlecase :
					chars[i].uppercase !=0 ? chars[i].uppercase : i);

			if ( "Lt".equals(chars[i].category) )
			{
				cs = i;
			}

			if ( Character.toTitleCase(i) != cs )
			{
				harness.check(false, stringChar(i) +
				" has wrong titlecase form of " +
				stringChar(Character.toTitleCase(i)) +
				" instead of " + 
				stringChar(cs) );
			}
			else harness.check(true);

// digit
			int	digit = chars[i].digit;

			for ( int radix = Character.MIN_RADIX; radix <= Character.MAX_RADIX; radix++)
			{
				int sb = digit < radix ? digit : -1;
				if ( Character.digit(i,radix) != sb )
				{
					harness.check(false, stringChar(i) + " has wrong digit form of "
						+ Character.digit(i,radix) + " for radix " + radix + " instead of "
						+ sb );
				}
				else harness.check(true);
			}

// getNumericValue
			if ( chars[i].numericValue !=
				Character.getNumericValue(i) )
			{
				harness.check(false, stringChar(i) + " has wrong numeric value of " +
					Character.getNumericValue(i) + " instead of " + chars[i].numericValue);
			}



			if ( testDeprecated )
			{

// isJavaLetter
				if ( (i == '$' || i == '_' || Character.isLetter(i)) !=
					Character.isJavaLetter(i) )
				{
					harness.check(false, i + " " +
(Character.isJavaLetter(i)? "javaletter" : "not-javaletter"));
				}
				else harness.check(true);

// isJavaLetterOrDigit
				if ((Character.isJavaLetter(i) || Character.isDigit(i) ||
					i == '$' || i == '_') !=
					Character.isJavaLetterOrDigit(i)
					 )
				{
					harness.check(false, i + " " +
(Character.isJavaLetterOrDigit(i) ? "javaletterordigit" : "not-javaletterordigit") );
				}
				else harness.check(true);

// isSpace
				if (((i == ' ' || i == '\t' || i == '\n' || i == '\r' ||
						i == '\f')) != Character.isSpace(i) )
				{
					harness.check(false, i + " " +
(Character.isSpace(i) ? "space" : "non-space" ) );
				}
				else harness.check(true);
			} // testDeprecated

		} // for

// forDigit
	for ( int r = -100; r < 100; r++ )
	{
		for ( int d = -100; d < 100; d++ )
		{
			char dch = Character.forDigit(d,r);
			char wantch = 0;
			if ( range(r, Character.MIN_RADIX, Character.MAX_RADIX) &&
				range(d,0,r-1) )
			{
				if ( d < 10 )
				{
					wantch = (char)('0' + (char)d);
				}
				else if ( d < 36 )
				{
					wantch = (char)('a' + d - 10);
				}
			}

			if ( dch != wantch )
			{
				harness.check(false, "Error in forDigit(" + d +
					"," + r + "), got " + dch + " wanted " +
					wantch );
			}
			else harness.check(true);
		}
	}


	}

	public void test(TestHarness harness)
	{
		try {
			long start = System.currentTimeMillis();
			unicode t = new unicode(harness, "UnicodeData.txt");
			long midtime = System.currentTimeMillis();
			t.performTests(harness);
			harness.debug("Bechmark : load:" + (midtime-start) + 
				"ms   tests:" +  (System.currentTimeMillis() - midtime) + "ms");
		}
		catch ( Exception e )
			{
				harness.debug(e);
				harness.check(false);
			}
	}

}
