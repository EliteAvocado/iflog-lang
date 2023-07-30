// Generated from D:/_Documents/_Programming/iflog_prototype/IFLog/src/IFLog/Grammars\SQLTargetParser.g4 by ANTLR 4.12.0
package IFLog.Generated;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class SQLTargetParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.12.0", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		KW_COMMENT=1, KW_BASETYPES=2, KW_TYPE=3, KW_BASE=4, KW_COMMENT_LINE=5, 
		KW_COMMENT_BLOCK=6, KW_TABLE=7, KW_VIEW=8, KW_COMPOSITE_RULE=9, KW_SYMBOLS=10, 
		KW_OPERATORS=11, KW_CONSTANTS=12, ASSIGNMENT_SYMBOL=13, TERMINATOR_SYMBOL=14, 
		CURLY_OPEN=15, CURLY_CLOSE=16, COMMENT=17, ID=18, WS=19, STRING=20;
	public static final int
		RULE_prog = 0, RULE_comment_block = 1, RULE_comment_ele_head = 2, RULE_comment_ele = 3, 
		RULE_basetypes_block = 4, RULE_type_block = 5, RULE_base_ele = 6, RULE_table_block = 7, 
		RULE_view_block = 8, RULE_composite_rule_block = 9, RULE_symbols_block = 10, 
		RULE_operators_block = 11, RULE_constants_block = 12, RULE_ele = 13;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "comment_block", "comment_ele_head", "comment_ele", "basetypes_block", 
			"type_block", "base_ele", "table_block", "view_block", "composite_rule_block", 
			"symbols_block", "operators_block", "constants_block", "ele"
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

	@Override
	public String getGrammarFileName() { return "SQLTargetParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SQLTargetParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SQLTargetParser.EOF, 0); }
		public List<Comment_blockContext> comment_block() {
			return getRuleContexts(Comment_blockContext.class);
		}
		public Comment_blockContext comment_block(int i) {
			return getRuleContext(Comment_blockContext.class,i);
		}
		public List<Basetypes_blockContext> basetypes_block() {
			return getRuleContexts(Basetypes_blockContext.class);
		}
		public Basetypes_blockContext basetypes_block(int i) {
			return getRuleContext(Basetypes_blockContext.class,i);
		}
		public List<Type_blockContext> type_block() {
			return getRuleContexts(Type_blockContext.class);
		}
		public Type_blockContext type_block(int i) {
			return getRuleContext(Type_blockContext.class,i);
		}
		public List<Table_blockContext> table_block() {
			return getRuleContexts(Table_blockContext.class);
		}
		public Table_blockContext table_block(int i) {
			return getRuleContext(Table_blockContext.class,i);
		}
		public List<View_blockContext> view_block() {
			return getRuleContexts(View_blockContext.class);
		}
		public View_blockContext view_block(int i) {
			return getRuleContext(View_blockContext.class,i);
		}
		public List<Composite_rule_blockContext> composite_rule_block() {
			return getRuleContexts(Composite_rule_blockContext.class);
		}
		public Composite_rule_blockContext composite_rule_block(int i) {
			return getRuleContext(Composite_rule_blockContext.class,i);
		}
		public List<Symbols_blockContext> symbols_block() {
			return getRuleContexts(Symbols_blockContext.class);
		}
		public Symbols_blockContext symbols_block(int i) {
			return getRuleContext(Symbols_blockContext.class,i);
		}
		public List<Operators_blockContext> operators_block() {
			return getRuleContexts(Operators_blockContext.class);
		}
		public Operators_blockContext operators_block(int i) {
			return getRuleContext(Operators_blockContext.class,i);
		}
		public List<Constants_blockContext> constants_block() {
			return getRuleContexts(Constants_blockContext.class);
		}
		public Constants_blockContext constants_block(int i) {
			return getRuleContext(Constants_blockContext.class,i);
		}
		public ProgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prog; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).enterProg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).exitProg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLTargetParserVisitor ) return ((SQLTargetParserVisitor<? extends T>)visitor).visitProg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgContext prog() throws RecognitionException {
		ProgContext _localctx = new ProgContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_prog);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(39);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8078L) != 0)) {
				{
				setState(37);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case KW_COMMENT:
					{
					setState(28);
					comment_block();
					}
					break;
				case KW_BASETYPES:
					{
					setState(29);
					basetypes_block();
					}
					break;
				case KW_TYPE:
					{
					setState(30);
					type_block();
					}
					break;
				case KW_TABLE:
					{
					setState(31);
					table_block();
					}
					break;
				case KW_VIEW:
					{
					setState(32);
					view_block();
					}
					break;
				case KW_COMPOSITE_RULE:
					{
					setState(33);
					composite_rule_block();
					}
					break;
				case KW_SYMBOLS:
					{
					setState(34);
					symbols_block();
					}
					break;
				case KW_OPERATORS:
					{
					setState(35);
					operators_block();
					}
					break;
				case KW_CONSTANTS:
					{
					setState(36);
					constants_block();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(41);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(42);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comment_blockContext extends ParserRuleContext {
		public TerminalNode KW_COMMENT() { return getToken(SQLTargetParser.KW_COMMENT, 0); }
		public TerminalNode CURLY_OPEN() { return getToken(SQLTargetParser.CURLY_OPEN, 0); }
		public TerminalNode CURLY_CLOSE() { return getToken(SQLTargetParser.CURLY_CLOSE, 0); }
		public List<Comment_eleContext> comment_ele() {
			return getRuleContexts(Comment_eleContext.class);
		}
		public Comment_eleContext comment_ele(int i) {
			return getRuleContext(Comment_eleContext.class,i);
		}
		public Comment_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).enterComment_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).exitComment_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLTargetParserVisitor ) return ((SQLTargetParserVisitor<? extends T>)visitor).visitComment_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comment_blockContext comment_block() throws RecognitionException {
		Comment_blockContext _localctx = new Comment_blockContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_comment_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(KW_COMMENT);
			setState(45);
			match(CURLY_OPEN);
			setState(47); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(46);
				comment_ele();
				}
				}
				setState(49); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==KW_COMMENT_LINE || _la==KW_COMMENT_BLOCK );
			setState(51);
			match(CURLY_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comment_ele_headContext extends ParserRuleContext {
		public TerminalNode KW_COMMENT_LINE() { return getToken(SQLTargetParser.KW_COMMENT_LINE, 0); }
		public TerminalNode KW_COMMENT_BLOCK() { return getToken(SQLTargetParser.KW_COMMENT_BLOCK, 0); }
		public Comment_ele_headContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment_ele_head; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).enterComment_ele_head(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).exitComment_ele_head(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLTargetParserVisitor ) return ((SQLTargetParserVisitor<? extends T>)visitor).visitComment_ele_head(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comment_ele_headContext comment_ele_head() throws RecognitionException {
		Comment_ele_headContext _localctx = new Comment_ele_headContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_comment_ele_head);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			_la = _input.LA(1);
			if ( !(_la==KW_COMMENT_LINE || _la==KW_COMMENT_BLOCK) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Comment_eleContext extends ParserRuleContext {
		public Comment_ele_headContext key;
		public Token value;
		public TerminalNode ASSIGNMENT_SYMBOL() { return getToken(SQLTargetParser.ASSIGNMENT_SYMBOL, 0); }
		public TerminalNode TERMINATOR_SYMBOL() { return getToken(SQLTargetParser.TERMINATOR_SYMBOL, 0); }
		public Comment_ele_headContext comment_ele_head() {
			return getRuleContext(Comment_ele_headContext.class,0);
		}
		public TerminalNode STRING() { return getToken(SQLTargetParser.STRING, 0); }
		public Comment_eleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment_ele; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).enterComment_ele(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).exitComment_ele(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLTargetParserVisitor ) return ((SQLTargetParserVisitor<? extends T>)visitor).visitComment_ele(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Comment_eleContext comment_ele() throws RecognitionException {
		Comment_eleContext _localctx = new Comment_eleContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_comment_ele);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			((Comment_eleContext)_localctx).key = comment_ele_head();
			setState(56);
			match(ASSIGNMENT_SYMBOL);
			setState(57);
			((Comment_eleContext)_localctx).value = match(STRING);
			setState(58);
			match(TERMINATOR_SYMBOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Basetypes_blockContext extends ParserRuleContext {
		public TerminalNode KW_BASETYPES() { return getToken(SQLTargetParser.KW_BASETYPES, 0); }
		public TerminalNode CURLY_OPEN() { return getToken(SQLTargetParser.CURLY_OPEN, 0); }
		public TerminalNode CURLY_CLOSE() { return getToken(SQLTargetParser.CURLY_CLOSE, 0); }
		public List<EleContext> ele() {
			return getRuleContexts(EleContext.class);
		}
		public EleContext ele(int i) {
			return getRuleContext(EleContext.class,i);
		}
		public Basetypes_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_basetypes_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).enterBasetypes_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).exitBasetypes_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLTargetParserVisitor ) return ((SQLTargetParserVisitor<? extends T>)visitor).visitBasetypes_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Basetypes_blockContext basetypes_block() throws RecognitionException {
		Basetypes_blockContext _localctx = new Basetypes_blockContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_basetypes_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			match(KW_BASETYPES);
			setState(61);
			match(CURLY_OPEN);
			setState(63); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(62);
				ele();
				}
				}
				setState(65); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID );
			setState(67);
			match(CURLY_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Type_blockContext extends ParserRuleContext {
		public TerminalNode KW_TYPE() { return getToken(SQLTargetParser.KW_TYPE, 0); }
		public TerminalNode CURLY_OPEN() { return getToken(SQLTargetParser.CURLY_OPEN, 0); }
		public Base_eleContext base_ele() {
			return getRuleContext(Base_eleContext.class,0);
		}
		public TerminalNode CURLY_CLOSE() { return getToken(SQLTargetParser.CURLY_CLOSE, 0); }
		public List<EleContext> ele() {
			return getRuleContexts(EleContext.class);
		}
		public EleContext ele(int i) {
			return getRuleContext(EleContext.class,i);
		}
		public Type_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).enterType_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).exitType_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLTargetParserVisitor ) return ((SQLTargetParserVisitor<? extends T>)visitor).visitType_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_blockContext type_block() throws RecognitionException {
		Type_blockContext _localctx = new Type_blockContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_type_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			match(KW_TYPE);
			setState(70);
			match(CURLY_OPEN);
			setState(71);
			base_ele();
			setState(73); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(72);
				ele();
				}
				}
				setState(75); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID );
			setState(77);
			match(CURLY_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Base_eleContext extends ParserRuleContext {
		public Token key;
		public Token value;
		public TerminalNode ASSIGNMENT_SYMBOL() { return getToken(SQLTargetParser.ASSIGNMENT_SYMBOL, 0); }
		public TerminalNode TERMINATOR_SYMBOL() { return getToken(SQLTargetParser.TERMINATOR_SYMBOL, 0); }
		public TerminalNode KW_BASE() { return getToken(SQLTargetParser.KW_BASE, 0); }
		public TerminalNode ID() { return getToken(SQLTargetParser.ID, 0); }
		public Base_eleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_base_ele; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).enterBase_ele(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).exitBase_ele(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLTargetParserVisitor ) return ((SQLTargetParserVisitor<? extends T>)visitor).visitBase_ele(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Base_eleContext base_ele() throws RecognitionException {
		Base_eleContext _localctx = new Base_eleContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_base_ele);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			((Base_eleContext)_localctx).key = match(KW_BASE);
			setState(80);
			match(ASSIGNMENT_SYMBOL);
			setState(81);
			((Base_eleContext)_localctx).value = match(ID);
			setState(82);
			match(TERMINATOR_SYMBOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Table_blockContext extends ParserRuleContext {
		public TerminalNode KW_TABLE() { return getToken(SQLTargetParser.KW_TABLE, 0); }
		public TerminalNode CURLY_OPEN() { return getToken(SQLTargetParser.CURLY_OPEN, 0); }
		public TerminalNode CURLY_CLOSE() { return getToken(SQLTargetParser.CURLY_CLOSE, 0); }
		public List<EleContext> ele() {
			return getRuleContexts(EleContext.class);
		}
		public EleContext ele(int i) {
			return getRuleContext(EleContext.class,i);
		}
		public Table_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).enterTable_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).exitTable_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLTargetParserVisitor ) return ((SQLTargetParserVisitor<? extends T>)visitor).visitTable_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Table_blockContext table_block() throws RecognitionException {
		Table_blockContext _localctx = new Table_blockContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_table_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			match(KW_TABLE);
			setState(85);
			match(CURLY_OPEN);
			setState(87); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(86);
				ele();
				}
				}
				setState(89); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID );
			setState(91);
			match(CURLY_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class View_blockContext extends ParserRuleContext {
		public TerminalNode KW_VIEW() { return getToken(SQLTargetParser.KW_VIEW, 0); }
		public TerminalNode CURLY_OPEN() { return getToken(SQLTargetParser.CURLY_OPEN, 0); }
		public TerminalNode CURLY_CLOSE() { return getToken(SQLTargetParser.CURLY_CLOSE, 0); }
		public List<EleContext> ele() {
			return getRuleContexts(EleContext.class);
		}
		public EleContext ele(int i) {
			return getRuleContext(EleContext.class,i);
		}
		public View_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_view_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).enterView_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).exitView_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLTargetParserVisitor ) return ((SQLTargetParserVisitor<? extends T>)visitor).visitView_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final View_blockContext view_block() throws RecognitionException {
		View_blockContext _localctx = new View_blockContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_view_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			match(KW_VIEW);
			setState(94);
			match(CURLY_OPEN);
			setState(96); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(95);
				ele();
				}
				}
				setState(98); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID );
			setState(100);
			match(CURLY_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Composite_rule_blockContext extends ParserRuleContext {
		public TerminalNode KW_COMPOSITE_RULE() { return getToken(SQLTargetParser.KW_COMPOSITE_RULE, 0); }
		public TerminalNode CURLY_OPEN() { return getToken(SQLTargetParser.CURLY_OPEN, 0); }
		public TerminalNode CURLY_CLOSE() { return getToken(SQLTargetParser.CURLY_CLOSE, 0); }
		public List<EleContext> ele() {
			return getRuleContexts(EleContext.class);
		}
		public EleContext ele(int i) {
			return getRuleContext(EleContext.class,i);
		}
		public Composite_rule_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_composite_rule_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).enterComposite_rule_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).exitComposite_rule_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLTargetParserVisitor ) return ((SQLTargetParserVisitor<? extends T>)visitor).visitComposite_rule_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Composite_rule_blockContext composite_rule_block() throws RecognitionException {
		Composite_rule_blockContext _localctx = new Composite_rule_blockContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_composite_rule_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102);
			match(KW_COMPOSITE_RULE);
			setState(103);
			match(CURLY_OPEN);
			setState(105); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(104);
				ele();
				}
				}
				setState(107); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID );
			setState(109);
			match(CURLY_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Symbols_blockContext extends ParserRuleContext {
		public TerminalNode KW_SYMBOLS() { return getToken(SQLTargetParser.KW_SYMBOLS, 0); }
		public TerminalNode CURLY_OPEN() { return getToken(SQLTargetParser.CURLY_OPEN, 0); }
		public TerminalNode CURLY_CLOSE() { return getToken(SQLTargetParser.CURLY_CLOSE, 0); }
		public List<EleContext> ele() {
			return getRuleContexts(EleContext.class);
		}
		public EleContext ele(int i) {
			return getRuleContext(EleContext.class,i);
		}
		public Symbols_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_symbols_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).enterSymbols_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).exitSymbols_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLTargetParserVisitor ) return ((SQLTargetParserVisitor<? extends T>)visitor).visitSymbols_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Symbols_blockContext symbols_block() throws RecognitionException {
		Symbols_blockContext _localctx = new Symbols_blockContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_symbols_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			match(KW_SYMBOLS);
			setState(112);
			match(CURLY_OPEN);
			setState(114); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(113);
				ele();
				}
				}
				setState(116); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID );
			setState(118);
			match(CURLY_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Operators_blockContext extends ParserRuleContext {
		public TerminalNode KW_OPERATORS() { return getToken(SQLTargetParser.KW_OPERATORS, 0); }
		public TerminalNode CURLY_OPEN() { return getToken(SQLTargetParser.CURLY_OPEN, 0); }
		public TerminalNode CURLY_CLOSE() { return getToken(SQLTargetParser.CURLY_CLOSE, 0); }
		public List<EleContext> ele() {
			return getRuleContexts(EleContext.class);
		}
		public EleContext ele(int i) {
			return getRuleContext(EleContext.class,i);
		}
		public Operators_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operators_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).enterOperators_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).exitOperators_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLTargetParserVisitor ) return ((SQLTargetParserVisitor<? extends T>)visitor).visitOperators_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Operators_blockContext operators_block() throws RecognitionException {
		Operators_blockContext _localctx = new Operators_blockContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_operators_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
			match(KW_OPERATORS);
			setState(121);
			match(CURLY_OPEN);
			setState(123); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(122);
				ele();
				}
				}
				setState(125); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID );
			setState(127);
			match(CURLY_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Constants_blockContext extends ParserRuleContext {
		public TerminalNode KW_CONSTANTS() { return getToken(SQLTargetParser.KW_CONSTANTS, 0); }
		public TerminalNode CURLY_OPEN() { return getToken(SQLTargetParser.CURLY_OPEN, 0); }
		public TerminalNode CURLY_CLOSE() { return getToken(SQLTargetParser.CURLY_CLOSE, 0); }
		public List<EleContext> ele() {
			return getRuleContexts(EleContext.class);
		}
		public EleContext ele(int i) {
			return getRuleContext(EleContext.class,i);
		}
		public Constants_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constants_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).enterConstants_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).exitConstants_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLTargetParserVisitor ) return ((SQLTargetParserVisitor<? extends T>)visitor).visitConstants_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Constants_blockContext constants_block() throws RecognitionException {
		Constants_blockContext _localctx = new Constants_blockContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_constants_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			match(KW_CONSTANTS);
			setState(130);
			match(CURLY_OPEN);
			setState(132); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(131);
				ele();
				}
				}
				setState(134); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ID );
			setState(136);
			match(CURLY_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EleContext extends ParserRuleContext {
		public Token key;
		public Token value;
		public TerminalNode ASSIGNMENT_SYMBOL() { return getToken(SQLTargetParser.ASSIGNMENT_SYMBOL, 0); }
		public TerminalNode TERMINATOR_SYMBOL() { return getToken(SQLTargetParser.TERMINATOR_SYMBOL, 0); }
		public TerminalNode ID() { return getToken(SQLTargetParser.ID, 0); }
		public TerminalNode STRING() { return getToken(SQLTargetParser.STRING, 0); }
		public EleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ele; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).enterEle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLTargetParserListener ) ((SQLTargetParserListener)listener).exitEle(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SQLTargetParserVisitor ) return ((SQLTargetParserVisitor<? extends T>)visitor).visitEle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EleContext ele() throws RecognitionException {
		EleContext _localctx = new EleContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_ele);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(138);
			((EleContext)_localctx).key = match(ID);
			setState(139);
			match(ASSIGNMENT_SYMBOL);
			setState(140);
			((EleContext)_localctx).value = match(STRING);
			setState(141);
			match(TERMINATOR_SYMBOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0014\u0090\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0005"+
		"\u0000&\b\u0000\n\u0000\f\u0000)\t\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0004\u00010\b\u0001\u000b\u0001\f\u0001"+
		"1\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0004\u0004@\b\u0004\u000b\u0004\f\u0004A\u0001\u0004\u0001\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0004\u0005J\b\u0005\u000b"+
		"\u0005\f\u0005K\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0004"+
		"\u0007X\b\u0007\u000b\u0007\f\u0007Y\u0001\u0007\u0001\u0007\u0001\b\u0001"+
		"\b\u0001\b\u0004\ba\b\b\u000b\b\f\bb\u0001\b\u0001\b\u0001\t\u0001\t\u0001"+
		"\t\u0004\tj\b\t\u000b\t\f\tk\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0004"+
		"\ns\b\n\u000b\n\f\nt\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0004\u000b|\b\u000b\u000b\u000b\f\u000b}\u0001\u000b\u0001\u000b\u0001"+
		"\f\u0001\f\u0001\f\u0004\f\u0085\b\f\u000b\f\f\f\u0086\u0001\f\u0001\f"+
		"\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0000\u0000\u000e\u0000"+
		"\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u0000"+
		"\u0001\u0001\u0000\u0005\u0006\u0093\u0000\'\u0001\u0000\u0000\u0000\u0002"+
		",\u0001\u0000\u0000\u0000\u00045\u0001\u0000\u0000\u0000\u00067\u0001"+
		"\u0000\u0000\u0000\b<\u0001\u0000\u0000\u0000\nE\u0001\u0000\u0000\u0000"+
		"\fO\u0001\u0000\u0000\u0000\u000eT\u0001\u0000\u0000\u0000\u0010]\u0001"+
		"\u0000\u0000\u0000\u0012f\u0001\u0000\u0000\u0000\u0014o\u0001\u0000\u0000"+
		"\u0000\u0016x\u0001\u0000\u0000\u0000\u0018\u0081\u0001\u0000\u0000\u0000"+
		"\u001a\u008a\u0001\u0000\u0000\u0000\u001c&\u0003\u0002\u0001\u0000\u001d"+
		"&\u0003\b\u0004\u0000\u001e&\u0003\n\u0005\u0000\u001f&\u0003\u000e\u0007"+
		"\u0000 &\u0003\u0010\b\u0000!&\u0003\u0012\t\u0000\"&\u0003\u0014\n\u0000"+
		"#&\u0003\u0016\u000b\u0000$&\u0003\u0018\f\u0000%\u001c\u0001\u0000\u0000"+
		"\u0000%\u001d\u0001\u0000\u0000\u0000%\u001e\u0001\u0000\u0000\u0000%"+
		"\u001f\u0001\u0000\u0000\u0000% \u0001\u0000\u0000\u0000%!\u0001\u0000"+
		"\u0000\u0000%\"\u0001\u0000\u0000\u0000%#\u0001\u0000\u0000\u0000%$\u0001"+
		"\u0000\u0000\u0000&)\u0001\u0000\u0000\u0000\'%\u0001\u0000\u0000\u0000"+
		"\'(\u0001\u0000\u0000\u0000(*\u0001\u0000\u0000\u0000)\'\u0001\u0000\u0000"+
		"\u0000*+\u0005\u0000\u0000\u0001+\u0001\u0001\u0000\u0000\u0000,-\u0005"+
		"\u0001\u0000\u0000-/\u0005\u000f\u0000\u0000.0\u0003\u0006\u0003\u0000"+
		"/.\u0001\u0000\u0000\u000001\u0001\u0000\u0000\u00001/\u0001\u0000\u0000"+
		"\u000012\u0001\u0000\u0000\u000023\u0001\u0000\u0000\u000034\u0005\u0010"+
		"\u0000\u00004\u0003\u0001\u0000\u0000\u000056\u0007\u0000\u0000\u0000"+
		"6\u0005\u0001\u0000\u0000\u000078\u0003\u0004\u0002\u000089\u0005\r\u0000"+
		"\u00009:\u0005\u0014\u0000\u0000:;\u0005\u000e\u0000\u0000;\u0007\u0001"+
		"\u0000\u0000\u0000<=\u0005\u0002\u0000\u0000=?\u0005\u000f\u0000\u0000"+
		">@\u0003\u001a\r\u0000?>\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000"+
		"A?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000"+
		"\u0000CD\u0005\u0010\u0000\u0000D\t\u0001\u0000\u0000\u0000EF\u0005\u0003"+
		"\u0000\u0000FG\u0005\u000f\u0000\u0000GI\u0003\f\u0006\u0000HJ\u0003\u001a"+
		"\r\u0000IH\u0001\u0000\u0000\u0000JK\u0001\u0000\u0000\u0000KI\u0001\u0000"+
		"\u0000\u0000KL\u0001\u0000\u0000\u0000LM\u0001\u0000\u0000\u0000MN\u0005"+
		"\u0010\u0000\u0000N\u000b\u0001\u0000\u0000\u0000OP\u0005\u0004\u0000"+
		"\u0000PQ\u0005\r\u0000\u0000QR\u0005\u0012\u0000\u0000RS\u0005\u000e\u0000"+
		"\u0000S\r\u0001\u0000\u0000\u0000TU\u0005\u0007\u0000\u0000UW\u0005\u000f"+
		"\u0000\u0000VX\u0003\u001a\r\u0000WV\u0001\u0000\u0000\u0000XY\u0001\u0000"+
		"\u0000\u0000YW\u0001\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000Z[\u0001"+
		"\u0000\u0000\u0000[\\\u0005\u0010\u0000\u0000\\\u000f\u0001\u0000\u0000"+
		"\u0000]^\u0005\b\u0000\u0000^`\u0005\u000f\u0000\u0000_a\u0003\u001a\r"+
		"\u0000`_\u0001\u0000\u0000\u0000ab\u0001\u0000\u0000\u0000b`\u0001\u0000"+
		"\u0000\u0000bc\u0001\u0000\u0000\u0000cd\u0001\u0000\u0000\u0000de\u0005"+
		"\u0010\u0000\u0000e\u0011\u0001\u0000\u0000\u0000fg\u0005\t\u0000\u0000"+
		"gi\u0005\u000f\u0000\u0000hj\u0003\u001a\r\u0000ih\u0001\u0000\u0000\u0000"+
		"jk\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000\u0000kl\u0001\u0000\u0000"+
		"\u0000lm\u0001\u0000\u0000\u0000mn\u0005\u0010\u0000\u0000n\u0013\u0001"+
		"\u0000\u0000\u0000op\u0005\n\u0000\u0000pr\u0005\u000f\u0000\u0000qs\u0003"+
		"\u001a\r\u0000rq\u0001\u0000\u0000\u0000st\u0001\u0000\u0000\u0000tr\u0001"+
		"\u0000\u0000\u0000tu\u0001\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000"+
		"vw\u0005\u0010\u0000\u0000w\u0015\u0001\u0000\u0000\u0000xy\u0005\u000b"+
		"\u0000\u0000y{\u0005\u000f\u0000\u0000z|\u0003\u001a\r\u0000{z\u0001\u0000"+
		"\u0000\u0000|}\u0001\u0000\u0000\u0000}{\u0001\u0000\u0000\u0000}~\u0001"+
		"\u0000\u0000\u0000~\u007f\u0001\u0000\u0000\u0000\u007f\u0080\u0005\u0010"+
		"\u0000\u0000\u0080\u0017\u0001\u0000\u0000\u0000\u0081\u0082\u0005\f\u0000"+
		"\u0000\u0082\u0084\u0005\u000f\u0000\u0000\u0083\u0085\u0003\u001a\r\u0000"+
		"\u0084\u0083\u0001\u0000\u0000\u0000\u0085\u0086\u0001\u0000\u0000\u0000"+
		"\u0086\u0084\u0001\u0000\u0000\u0000\u0086\u0087\u0001\u0000\u0000\u0000"+
		"\u0087\u0088\u0001\u0000\u0000\u0000\u0088\u0089\u0005\u0010\u0000\u0000"+
		"\u0089\u0019\u0001\u0000\u0000\u0000\u008a\u008b\u0005\u0012\u0000\u0000"+
		"\u008b\u008c\u0005\r\u0000\u0000\u008c\u008d\u0005\u0014\u0000\u0000\u008d"+
		"\u008e\u0005\u000e\u0000\u0000\u008e\u001b\u0001\u0000\u0000\u0000\u000b"+
		"%\'1AKYbkt}\u0086";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}