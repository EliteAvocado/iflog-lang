// Generated from D:/_Documents/_Programming/iflog_prototype/IFLog/src/IFLog/Grammars\SQLTargetParser.g4 by ANTLR 4.12.0
package IFLog.Generated;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SQLTargetParser}.
 */
public interface SQLTargetParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SQLTargetParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(SQLTargetParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLTargetParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(SQLTargetParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLTargetParser#comment_block}.
	 * @param ctx the parse tree
	 */
	void enterComment_block(SQLTargetParser.Comment_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLTargetParser#comment_block}.
	 * @param ctx the parse tree
	 */
	void exitComment_block(SQLTargetParser.Comment_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLTargetParser#comment_ele_head}.
	 * @param ctx the parse tree
	 */
	void enterComment_ele_head(SQLTargetParser.Comment_ele_headContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLTargetParser#comment_ele_head}.
	 * @param ctx the parse tree
	 */
	void exitComment_ele_head(SQLTargetParser.Comment_ele_headContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLTargetParser#comment_ele}.
	 * @param ctx the parse tree
	 */
	void enterComment_ele(SQLTargetParser.Comment_eleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLTargetParser#comment_ele}.
	 * @param ctx the parse tree
	 */
	void exitComment_ele(SQLTargetParser.Comment_eleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLTargetParser#basetypes_block}.
	 * @param ctx the parse tree
	 */
	void enterBasetypes_block(SQLTargetParser.Basetypes_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLTargetParser#basetypes_block}.
	 * @param ctx the parse tree
	 */
	void exitBasetypes_block(SQLTargetParser.Basetypes_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLTargetParser#type_block}.
	 * @param ctx the parse tree
	 */
	void enterType_block(SQLTargetParser.Type_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLTargetParser#type_block}.
	 * @param ctx the parse tree
	 */
	void exitType_block(SQLTargetParser.Type_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLTargetParser#base_ele}.
	 * @param ctx the parse tree
	 */
	void enterBase_ele(SQLTargetParser.Base_eleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLTargetParser#base_ele}.
	 * @param ctx the parse tree
	 */
	void exitBase_ele(SQLTargetParser.Base_eleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLTargetParser#table_block}.
	 * @param ctx the parse tree
	 */
	void enterTable_block(SQLTargetParser.Table_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLTargetParser#table_block}.
	 * @param ctx the parse tree
	 */
	void exitTable_block(SQLTargetParser.Table_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLTargetParser#view_block}.
	 * @param ctx the parse tree
	 */
	void enterView_block(SQLTargetParser.View_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLTargetParser#view_block}.
	 * @param ctx the parse tree
	 */
	void exitView_block(SQLTargetParser.View_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLTargetParser#composite_rule_block}.
	 * @param ctx the parse tree
	 */
	void enterComposite_rule_block(SQLTargetParser.Composite_rule_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLTargetParser#composite_rule_block}.
	 * @param ctx the parse tree
	 */
	void exitComposite_rule_block(SQLTargetParser.Composite_rule_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLTargetParser#symbols_block}.
	 * @param ctx the parse tree
	 */
	void enterSymbols_block(SQLTargetParser.Symbols_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLTargetParser#symbols_block}.
	 * @param ctx the parse tree
	 */
	void exitSymbols_block(SQLTargetParser.Symbols_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLTargetParser#operators_block}.
	 * @param ctx the parse tree
	 */
	void enterOperators_block(SQLTargetParser.Operators_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLTargetParser#operators_block}.
	 * @param ctx the parse tree
	 */
	void exitOperators_block(SQLTargetParser.Operators_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLTargetParser#constants_block}.
	 * @param ctx the parse tree
	 */
	void enterConstants_block(SQLTargetParser.Constants_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLTargetParser#constants_block}.
	 * @param ctx the parse tree
	 */
	void exitConstants_block(SQLTargetParser.Constants_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLTargetParser#ele}.
	 * @param ctx the parse tree
	 */
	void enterEle(SQLTargetParser.EleContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLTargetParser#ele}.
	 * @param ctx the parse tree
	 */
	void exitEle(SQLTargetParser.EleContext ctx);
}