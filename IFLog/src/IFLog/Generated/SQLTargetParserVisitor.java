// Generated from D:/_Documents/_Programming/iflog_prototype/IFLog/src/IFLog/Grammars\SQLTargetParser.g4 by ANTLR 4.12.0
package IFLog.Generated;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SQLTargetParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SQLTargetParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SQLTargetParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(SQLTargetParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLTargetParser#comment_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComment_block(SQLTargetParser.Comment_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLTargetParser#comment_ele_head}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComment_ele_head(SQLTargetParser.Comment_ele_headContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLTargetParser#comment_ele}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComment_ele(SQLTargetParser.Comment_eleContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLTargetParser#basetypes_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBasetypes_block(SQLTargetParser.Basetypes_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLTargetParser#type_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_block(SQLTargetParser.Type_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLTargetParser#base_ele}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBase_ele(SQLTargetParser.Base_eleContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLTargetParser#table_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_block(SQLTargetParser.Table_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLTargetParser#view_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitView_block(SQLTargetParser.View_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLTargetParser#composite_rule_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComposite_rule_block(SQLTargetParser.Composite_rule_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLTargetParser#symbols_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbols_block(SQLTargetParser.Symbols_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLTargetParser#operators_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperators_block(SQLTargetParser.Operators_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLTargetParser#constants_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstants_block(SQLTargetParser.Constants_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SQLTargetParser#ele}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEle(SQLTargetParser.EleContext ctx);
}