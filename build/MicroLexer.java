// Generated from Micro.g4 by ANTLR 4.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MicroLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		IDENTIFIER=1, INTLITERAL=2, FLOATLITERAL=3, STRINGLITERAL=4, COMMENT=5, 
		WHITESPACE=6, KEYWORD=7, OPERATOR=8;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"IDENTIFIER", "INTLITERAL", "FLOATLITERAL", "STRINGLITERAL", "COMMENT", 
		"WHITESPACE", "KEYWORD", "OPERATOR"
	};
	public static final String[] ruleNames = {
		"DIGITS", "IDENTIFIER", "INTLITERAL", "FLOATLITERAL", "STRINGLITERAL", 
		"COMMENT", "WHITESPACE", "KEYWORD", "OPERATOR"
	};


	public MicroLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Micro.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 5: COMMENT_action((RuleContext)_localctx, actionIndex); break;

		case 6: WHITESPACE_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WHITESPACE_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1: skip();  break;
		}
	}
	private void COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\n\u00aa\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2"+
		"\3\2\3\3\3\3\7\3\32\n\3\f\3\16\3\35\13\3\3\4\6\4 \n\4\r\4\16\4!\3\5\6"+
		"\5%\n\5\r\5\16\5&\3\5\3\5\6\5+\n\5\r\5\16\5,\3\5\3\5\6\5\61\n\5\r\5\16"+
		"\5\62\5\5\65\n\5\3\6\3\6\7\69\n\6\f\6\16\6<\13\6\3\6\3\6\3\7\3\7\3\7\3"+
		"\7\7\7D\n\7\f\7\16\7G\13\7\3\7\3\7\3\7\5\7L\n\7\3\7\3\7\3\b\6\bQ\n\b\r"+
		"\b\16\bR\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\5\t\u009d\n\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\5\n\u00a9\n\n\3E\13\3\2\1\5\3\1\7\4\1\t\5\1\13\6\1\r\7\2\17\b\3\21\t"+
		"\1\23\n\1\3\2\t\3\2\62;\4\2C\\c|\5\2\62;C\\c|\3\2$$\5\2\13\f\16\17\"\""+
		"\6\2,-//\61\61??\6\2*+..=>@@\u00c6\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2"+
		"\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\3\25"+
		"\3\2\2\2\5\27\3\2\2\2\7\37\3\2\2\2\t\64\3\2\2\2\13\66\3\2\2\2\r?\3\2\2"+
		"\2\17P\3\2\2\2\21\u009c\3\2\2\2\23\u00a8\3\2\2\2\25\26\t\2\2\2\26\4\3"+
		"\2\2\2\27\33\t\3\2\2\30\32\t\4\2\2\31\30\3\2\2\2\32\35\3\2\2\2\33\31\3"+
		"\2\2\2\33\34\3\2\2\2\34\6\3\2\2\2\35\33\3\2\2\2\36 \5\3\2\2\37\36\3\2"+
		"\2\2 !\3\2\2\2!\37\3\2\2\2!\"\3\2\2\2\"\b\3\2\2\2#%\5\3\2\2$#\3\2\2\2"+
		"%&\3\2\2\2&$\3\2\2\2&\'\3\2\2\2\'(\3\2\2\2(*\7\60\2\2)+\5\3\2\2*)\3\2"+
		"\2\2+,\3\2\2\2,*\3\2\2\2,-\3\2\2\2-\65\3\2\2\2.\60\7\60\2\2/\61\5\3\2"+
		"\2\60/\3\2\2\2\61\62\3\2\2\2\62\60\3\2\2\2\62\63\3\2\2\2\63\65\3\2\2\2"+
		"\64$\3\2\2\2\64.\3\2\2\2\65\n\3\2\2\2\66:\7$\2\2\679\n\5\2\28\67\3\2\2"+
		"\29<\3\2\2\2:8\3\2\2\2:;\3\2\2\2;=\3\2\2\2<:\3\2\2\2=>\7$\2\2>\f\3\2\2"+
		"\2?@\7/\2\2@A\7/\2\2AE\3\2\2\2BD\13\2\2\2CB\3\2\2\2DG\3\2\2\2EF\3\2\2"+
		"\2EC\3\2\2\2FK\3\2\2\2GE\3\2\2\2HI\7\17\2\2IL\7\f\2\2JL\7\f\2\2KH\3\2"+
		"\2\2KJ\3\2\2\2LM\3\2\2\2MN\b\7\2\2N\16\3\2\2\2OQ\t\6\2\2PO\3\2\2\2QR\3"+
		"\2\2\2RP\3\2\2\2RS\3\2\2\2ST\3\2\2\2TU\b\b\3\2U\20\3\2\2\2VW\7R\2\2WX"+
		"\7T\2\2XY\7Q\2\2YZ\7I\2\2Z[\7T\2\2[\\\7C\2\2\\\u009d\7O\2\2]^\7D\2\2^"+
		"_\7G\2\2_`\7I\2\2`a\7K\2\2a\u009d\7P\2\2bc\7G\2\2cd\7P\2\2d\u009d\7F\2"+
		"\2ef\7H\2\2fg\7W\2\2gh\7P\2\2hi\7E\2\2ij\7V\2\2jk\7K\2\2kl\7Q\2\2l\u009d"+
		"\7P\2\2mn\7T\2\2no\7G\2\2op\7C\2\2p\u009d\7F\2\2qr\7Y\2\2rs\7T\2\2st\7"+
		"K\2\2tu\7V\2\2u\u009d\7G\2\2vw\7K\2\2w\u009d\7H\2\2xy\7G\2\2yz\7N\2\2"+
		"z{\7U\2\2{\u009d\7G\2\2|}\7H\2\2}\u009d\7K\2\2~\177\7H\2\2\177\u0080\7"+
		"Q\2\2\u0080\u009d\7T\2\2\u0081\u0082\7T\2\2\u0082\u0083\7Q\2\2\u0083\u009d"+
		"\7H\2\2\u0084\u0085\7T\2\2\u0085\u0086\7G\2\2\u0086\u0087\7V\2\2\u0087"+
		"\u0088\7W\2\2\u0088\u0089\7T\2\2\u0089\u009d\7P\2\2\u008a\u008b\7K\2\2"+
		"\u008b\u008c\7P\2\2\u008c\u009d\7V\2\2\u008d\u008e\7X\2\2\u008e\u008f"+
		"\7Q\2\2\u008f\u0090\7K\2\2\u0090\u009d\7F\2\2\u0091\u0092\7U\2\2\u0092"+
		"\u0093\7V\2\2\u0093\u0094\7T\2\2\u0094\u0095\7K\2\2\u0095\u0096\7P\2\2"+
		"\u0096\u009d\7I\2\2\u0097\u0098\7H\2\2\u0098\u0099\7N\2\2\u0099\u009a"+
		"\7Q\2\2\u009a\u009b\7C\2\2\u009b\u009d\7V\2\2\u009cV\3\2\2\2\u009c]\3"+
		"\2\2\2\u009cb\3\2\2\2\u009ce\3\2\2\2\u009cm\3\2\2\2\u009cq\3\2\2\2\u009c"+
		"v\3\2\2\2\u009cx\3\2\2\2\u009c|\3\2\2\2\u009c~\3\2\2\2\u009c\u0081\3\2"+
		"\2\2\u009c\u0084\3\2\2\2\u009c\u008a\3\2\2\2\u009c\u008d\3\2\2\2\u009c"+
		"\u0091\3\2\2\2\u009c\u0097\3\2\2\2\u009d\22\3\2\2\2\u009e\u009f\7<\2\2"+
		"\u009f\u00a9\7?\2\2\u00a0\u00a9\t\7\2\2\u00a1\u00a2\7#\2\2\u00a2\u00a9"+
		"\7?\2\2\u00a3\u00a9\t\b\2\2\u00a4\u00a5\7>\2\2\u00a5\u00a9\7?\2\2\u00a6"+
		"\u00a7\7@\2\2\u00a7\u00a9\7?\2\2\u00a8\u009e\3\2\2\2\u00a8\u00a0\3\2\2"+
		"\2\u00a8\u00a1\3\2\2\2\u00a8\u00a3\3\2\2\2\u00a8\u00a4\3\2\2\2\u00a8\u00a6"+
		"\3\2\2\2\u00a9\24\3\2\2\2\20\2\31\33!&,\62\64:EKR\u009c\u00a8";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}