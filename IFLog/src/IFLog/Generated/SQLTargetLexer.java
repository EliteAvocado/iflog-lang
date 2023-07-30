// Generated from D:/_Documents/_Programming/iflog_prototype/IFLog/src/IFLog/Grammars\SQLTargetLexer.g4 by ANTLR 4.12.0
package IFLog.Generated;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class SQLTargetLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.12.0", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		KW_COMMENT=1, KW_BASETYPES=2, KW_TYPE=3, KW_BASE=4, KW_COMMENT_LINE=5, 
		KW_COMMENT_BLOCK=6, KW_TABLE=7, KW_VIEW=8, KW_COMPOSITE_RULE=9, KW_SYMBOLS=10, 
		KW_OPERATORS=11, KW_CONSTANTS=12, ASSIGNMENT_SYMBOL=13, TERMINATOR_SYMBOL=14, 
		CURLY_OPEN=15, CURLY_CLOSE=16, COMMENT=17, ID=18, WS=19, STRING=20;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"KW_COMMENT", "KW_BASETYPES", "KW_TYPE", "KW_BASE", "KW_COMMENT_LINE", 
			"KW_COMMENT_BLOCK", "KW_TABLE", "KW_VIEW", "KW_COMPOSITE_RULE", "KW_SYMBOLS", 
			"KW_OPERATORS", "KW_CONSTANTS", "ASSIGNMENT_SYMBOL", "TERMINATOR_SYMBOL", 
			"CURLY_OPEN", "CURLY_CLOSE", "COMMENT", "ID", "WS", "STRING"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'comment'", "'basetypes'", "'type'", "'base'", "'line'", "'block'", 
			"'table'", "'view'", "'composite_rule'", "'symbols'", "'operators'", 
			"'constants'", "':'", "';'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "KW_COMMENT", "KW_BASETYPES", "KW_TYPE", "KW_BASE", "KW_COMMENT_LINE", 
			"KW_COMMENT_BLOCK", "KW_TABLE", "KW_VIEW", "KW_COMPOSITE_RULE", "KW_SYMBOLS", 
			"KW_OPERATORS", "KW_CONSTANTS", "ASSIGNMENT_SYMBOL", "TERMINATOR_SYMBOL", 
			"CURLY_OPEN", "CURLY_CLOSE", "COMMENT", "ID", "WS", "STRING"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public SQLTargetLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SQLTargetLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u0014\u00b9\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011"+
		"\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001"+
		"\u000e\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0005\u0010\u0093\b\u0010\n\u0010\f\u0010\u0096\t\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0004\u0011"+
		"\u009e\b\u0011\u000b\u0011\f\u0011\u009f\u0001\u0011\u0005\u0011\u00a3"+
		"\b\u0011\n\u0011\f\u0011\u00a6\t\u0011\u0001\u0012\u0004\u0012\u00a9\b"+
		"\u0012\u000b\u0012\f\u0012\u00aa\u0001\u0012\u0001\u0012\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0005\u0013\u00b3\b\u0013\n\u0013\f\u0013"+
		"\u00b6\t\u0013\u0001\u0013\u0001\u0013\u0001\u0094\u0000\u0014\u0001\u0001"+
		"\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f"+
		"\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d\u000f"+
		"\u001f\u0010!\u0011#\u0012%\u0013\'\u0014\u0001\u0000\u0004\u0003\u0000"+
		"AZ__az\u0004\u000009AZ__az\u0003\u0000\t\n\r\r  \u0001\u0000\"\"\u00be"+
		"\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000"+
		"\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000"+
		"\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000"+
		"\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011"+
		"\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015"+
		"\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019"+
		"\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d"+
		"\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001"+
		"\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000"+
		"\u0000\u0000\'\u0001\u0000\u0000\u0000\u0001)\u0001\u0000\u0000\u0000"+
		"\u00031\u0001\u0000\u0000\u0000\u0005;\u0001\u0000\u0000\u0000\u0007@"+
		"\u0001\u0000\u0000\u0000\tE\u0001\u0000\u0000\u0000\u000bJ\u0001\u0000"+
		"\u0000\u0000\rP\u0001\u0000\u0000\u0000\u000fV\u0001\u0000\u0000\u0000"+
		"\u0011[\u0001\u0000\u0000\u0000\u0013j\u0001\u0000\u0000\u0000\u0015r"+
		"\u0001\u0000\u0000\u0000\u0017|\u0001\u0000\u0000\u0000\u0019\u0086\u0001"+
		"\u0000\u0000\u0000\u001b\u0088\u0001\u0000\u0000\u0000\u001d\u008a\u0001"+
		"\u0000\u0000\u0000\u001f\u008c\u0001\u0000\u0000\u0000!\u008e\u0001\u0000"+
		"\u0000\u0000#\u009d\u0001\u0000\u0000\u0000%\u00a8\u0001\u0000\u0000\u0000"+
		"\'\u00ae\u0001\u0000\u0000\u0000)*\u0005c\u0000\u0000*+\u0005o\u0000\u0000"+
		"+,\u0005m\u0000\u0000,-\u0005m\u0000\u0000-.\u0005e\u0000\u0000./\u0005"+
		"n\u0000\u0000/0\u0005t\u0000\u00000\u0002\u0001\u0000\u0000\u000012\u0005"+
		"b\u0000\u000023\u0005a\u0000\u000034\u0005s\u0000\u000045\u0005e\u0000"+
		"\u000056\u0005t\u0000\u000067\u0005y\u0000\u000078\u0005p\u0000\u0000"+
		"89\u0005e\u0000\u00009:\u0005s\u0000\u0000:\u0004\u0001\u0000\u0000\u0000"+
		";<\u0005t\u0000\u0000<=\u0005y\u0000\u0000=>\u0005p\u0000\u0000>?\u0005"+
		"e\u0000\u0000?\u0006\u0001\u0000\u0000\u0000@A\u0005b\u0000\u0000AB\u0005"+
		"a\u0000\u0000BC\u0005s\u0000\u0000CD\u0005e\u0000\u0000D\b\u0001\u0000"+
		"\u0000\u0000EF\u0005l\u0000\u0000FG\u0005i\u0000\u0000GH\u0005n\u0000"+
		"\u0000HI\u0005e\u0000\u0000I\n\u0001\u0000\u0000\u0000JK\u0005b\u0000"+
		"\u0000KL\u0005l\u0000\u0000LM\u0005o\u0000\u0000MN\u0005c\u0000\u0000"+
		"NO\u0005k\u0000\u0000O\f\u0001\u0000\u0000\u0000PQ\u0005t\u0000\u0000"+
		"QR\u0005a\u0000\u0000RS\u0005b\u0000\u0000ST\u0005l\u0000\u0000TU\u0005"+
		"e\u0000\u0000U\u000e\u0001\u0000\u0000\u0000VW\u0005v\u0000\u0000WX\u0005"+
		"i\u0000\u0000XY\u0005e\u0000\u0000YZ\u0005w\u0000\u0000Z\u0010\u0001\u0000"+
		"\u0000\u0000[\\\u0005c\u0000\u0000\\]\u0005o\u0000\u0000]^\u0005m\u0000"+
		"\u0000^_\u0005p\u0000\u0000_`\u0005o\u0000\u0000`a\u0005s\u0000\u0000"+
		"ab\u0005i\u0000\u0000bc\u0005t\u0000\u0000cd\u0005e\u0000\u0000de\u0005"+
		"_\u0000\u0000ef\u0005r\u0000\u0000fg\u0005u\u0000\u0000gh\u0005l\u0000"+
		"\u0000hi\u0005e\u0000\u0000i\u0012\u0001\u0000\u0000\u0000jk\u0005s\u0000"+
		"\u0000kl\u0005y\u0000\u0000lm\u0005m\u0000\u0000mn\u0005b\u0000\u0000"+
		"no\u0005o\u0000\u0000op\u0005l\u0000\u0000pq\u0005s\u0000\u0000q\u0014"+
		"\u0001\u0000\u0000\u0000rs\u0005o\u0000\u0000st\u0005p\u0000\u0000tu\u0005"+
		"e\u0000\u0000uv\u0005r\u0000\u0000vw\u0005a\u0000\u0000wx\u0005t\u0000"+
		"\u0000xy\u0005o\u0000\u0000yz\u0005r\u0000\u0000z{\u0005s\u0000\u0000"+
		"{\u0016\u0001\u0000\u0000\u0000|}\u0005c\u0000\u0000}~\u0005o\u0000\u0000"+
		"~\u007f\u0005n\u0000\u0000\u007f\u0080\u0005s\u0000\u0000\u0080\u0081"+
		"\u0005t\u0000\u0000\u0081\u0082\u0005a\u0000\u0000\u0082\u0083\u0005n"+
		"\u0000\u0000\u0083\u0084\u0005t\u0000\u0000\u0084\u0085\u0005s\u0000\u0000"+
		"\u0085\u0018\u0001\u0000\u0000\u0000\u0086\u0087\u0005:\u0000\u0000\u0087"+
		"\u001a\u0001\u0000\u0000\u0000\u0088\u0089\u0005;\u0000\u0000\u0089\u001c"+
		"\u0001\u0000\u0000\u0000\u008a\u008b\u0005{\u0000\u0000\u008b\u001e\u0001"+
		"\u0000\u0000\u0000\u008c\u008d\u0005}\u0000\u0000\u008d \u0001\u0000\u0000"+
		"\u0000\u008e\u008f\u0005/\u0000\u0000\u008f\u0090\u0005*\u0000\u0000\u0090"+
		"\u0094\u0001\u0000\u0000\u0000\u0091\u0093\t\u0000\u0000\u0000\u0092\u0091"+
		"\u0001\u0000\u0000\u0000\u0093\u0096\u0001\u0000\u0000\u0000\u0094\u0095"+
		"\u0001\u0000\u0000\u0000\u0094\u0092\u0001\u0000\u0000\u0000\u0095\u0097"+
		"\u0001\u0000\u0000\u0000\u0096\u0094\u0001\u0000\u0000\u0000\u0097\u0098"+
		"\u0005*\u0000\u0000\u0098\u0099\u0005/\u0000\u0000\u0099\u009a\u0001\u0000"+
		"\u0000\u0000\u009a\u009b\u0006\u0010\u0000\u0000\u009b\"\u0001\u0000\u0000"+
		"\u0000\u009c\u009e\u0007\u0000\u0000\u0000\u009d\u009c\u0001\u0000\u0000"+
		"\u0000\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u009d\u0001\u0000\u0000"+
		"\u0000\u009f\u00a0\u0001\u0000\u0000\u0000\u00a0\u00a4\u0001\u0000\u0000"+
		"\u0000\u00a1\u00a3\u0007\u0001\u0000\u0000\u00a2\u00a1\u0001\u0000\u0000"+
		"\u0000\u00a3\u00a6\u0001\u0000\u0000\u0000\u00a4\u00a2\u0001\u0000\u0000"+
		"\u0000\u00a4\u00a5\u0001\u0000\u0000\u0000\u00a5$\u0001\u0000\u0000\u0000"+
		"\u00a6\u00a4\u0001\u0000\u0000\u0000\u00a7\u00a9\u0007\u0002\u0000\u0000"+
		"\u00a8\u00a7\u0001\u0000\u0000\u0000\u00a9\u00aa\u0001\u0000\u0000\u0000"+
		"\u00aa\u00a8\u0001\u0000\u0000\u0000\u00aa\u00ab\u0001\u0000\u0000\u0000"+
		"\u00ab\u00ac\u0001\u0000\u0000\u0000\u00ac\u00ad\u0006\u0012\u0001\u0000"+
		"\u00ad&\u0001\u0000\u0000\u0000\u00ae\u00b4\u0005\"\u0000\u0000\u00af"+
		"\u00b3\b\u0003\u0000\u0000\u00b0\u00b1\u0005\\\u0000\u0000\u00b1\u00b3"+
		"\u0005\"\u0000\u0000\u00b2\u00af\u0001\u0000\u0000\u0000\u00b2\u00b0\u0001"+
		"\u0000\u0000\u0000\u00b3\u00b6\u0001\u0000\u0000\u0000\u00b4\u00b2\u0001"+
		"\u0000\u0000\u0000\u00b4\u00b5\u0001\u0000\u0000\u0000\u00b5\u00b7\u0001"+
		"\u0000\u0000\u0000\u00b6\u00b4\u0001\u0000\u0000\u0000\u00b7\u00b8\u0005"+
		"\"\u0000\u0000\u00b8(\u0001\u0000\u0000\u0000\u0007\u0000\u0094\u009f"+
		"\u00a4\u00aa\u00b2\u00b4\u0002\u0000\u0001\u0000\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}