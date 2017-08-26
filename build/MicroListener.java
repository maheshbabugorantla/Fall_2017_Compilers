// Generated from Micro.g4 by ANTLR 4.1
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MicroParser}.
 */
public interface MicroListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MicroParser#id}.
	 * @param ctx the parse tree
	 */
	void enterId(@NotNull MicroParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by {@link MicroParser#id}.
	 * @param ctx the parse tree
	 */
	void exitId(@NotNull MicroParser.IdContext ctx);
}