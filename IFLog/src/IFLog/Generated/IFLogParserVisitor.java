// Generated from D:/_Documents/_Programming/iflog_prototype/IFLog/src/IFLog/Grammars\IFLogParser.g4 by ANTLR 4.12.0
package IFLog.Generated;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link IFLogParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface IFLogParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link IFLogParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(IFLogParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#composite_rule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComposite_rule(IFLogParser.Composite_ruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#composite_rule_arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComposite_rule_arg(IFLogParser.Composite_rule_argContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#composite_rule_global}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComposite_rule_global(IFLogParser.Composite_rule_globalContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#composite_rule_global_el}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComposite_rule_global_el(IFLogParser.Composite_rule_global_elContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#composite_rule_event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComposite_rule_event(IFLogParser.Composite_rule_eventContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#composite_rule_event_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComposite_rule_event_type(IFLogParser.Composite_rule_event_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#composite_rule_event_el}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComposite_rule_event_el(IFLogParser.Composite_rule_event_elContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#comp_rule_event_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComp_rule_event_type(IFLogParser.Comp_rule_event_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#comp_rule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComp_rule(IFLogParser.Comp_ruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#comp_rule_head}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComp_rule_head(IFLogParser.Comp_rule_headContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#comp_rule_head_el}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComp_rule_head_el(IFLogParser.Comp_rule_head_elContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_return}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_return(IFLogParser.Stat_comp_rule_returnContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_return_el}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_return_el(IFLogParser.Stat_comp_rule_return_elContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_print}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_print(IFLogParser.Stat_comp_rule_printContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_db_action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_db_action(IFLogParser.Stat_comp_rule_db_actionContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_call(IFLogParser.Stat_comp_rule_callContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#comp_rule_body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComp_rule_body(IFLogParser.Comp_rule_bodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_tags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_tags(IFLogParser.Stat_comp_rule_tagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_tags_el}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_tags_el(IFLogParser.Stat_comp_rule_tags_elContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_if}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_if(IFLogParser.Stat_comp_rule_ifContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_comparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_comparison(IFLogParser.Stat_comp_rule_comparisonContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_comparison_el}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_comparison_el(IFLogParser.Stat_comp_rule_comparison_elContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_get}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_get(IFLogParser.Stat_comp_rule_getContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_selects}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_selects(IFLogParser.Stat_comp_rule_selectsContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_multi}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_multi(IFLogParser.Stat_comp_rule_multiContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_multi_el}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_multi_el(IFLogParser.Stat_comp_rule_multi_elContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_between}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_between(IFLogParser.Stat_comp_rule_betweenContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_between_el}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_between_el(IFLogParser.Stat_comp_rule_between_elContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comp_rule_between_el2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comp_rule_between_el2(IFLogParser.Stat_comp_rule_between_el2Context ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#view}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitView(IFLogParser.ViewContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#view_field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitView_field(IFLogParser.View_fieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#view_rule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitView_rule(IFLogParser.View_ruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#view_rule_lhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitView_rule_lhs(IFLogParser.View_rule_lhsContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#view_rule_rhs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitView_rule_rhs(IFLogParser.View_rule_rhsContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#view_rule_ele}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitView_rule_ele(IFLogParser.View_rule_eleContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_case_when}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_case_when(IFLogParser.Stat_case_whenContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_case_when_el}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_case_when_el(IFLogParser.Stat_case_when_elContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_assignment(IFLogParser.Stat_assignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_select}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_select(IFLogParser.Stat_selectContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_table}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_table(IFLogParser.Stat_tableContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_table_el}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_table_el(IFLogParser.Stat_table_elContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable(IFLogParser.TableContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_field(IFLogParser.Table_fieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_arg(IFLogParser.Table_argContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_arg_extended}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_arg_extended(IFLogParser.Table_arg_extendedContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_arg_alias}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_arg_alias(IFLogParser.Table_arg_aliasContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_arg_default_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_arg_default_value(IFLogParser.Table_arg_default_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_pk}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_pk(IFLogParser.Table_pkContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_uks}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_uks(IFLogParser.Table_uksContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_pk_uk_el}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_pk_uk_el(IFLogParser.Table_pk_uk_elContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_data}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_data(IFLogParser.Table_dataContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_data_record}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_data_record(IFLogParser.Table_data_recordContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_record}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_record(IFLogParser.Table_recordContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_record_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_record_value(IFLogParser.Table_record_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_record_extended}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_record_extended(IFLogParser.Table_record_extendedContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_record_extended_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_record_extended_value(IFLogParser.Table_record_extended_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_constraint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_constraint(IFLogParser.Table_constraintContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#table_constraint_el}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable_constraint_el(IFLogParser.Table_constraint_elContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_coalesce}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_coalesce(IFLogParser.Stat_coalesceContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_coalesce_el}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_coalesce_el(IFLogParser.Stat_coalesce_elContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_comparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_comparison(IFLogParser.Stat_comparisonContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_arithmetic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_arithmetic(IFLogParser.Stat_arithmeticContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_arithmetic_el}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_arithmetic_el(IFLogParser.Stat_arithmetic_elContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#stat_in}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat_in(IFLogParser.Stat_inContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#v_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitV_list(IFLogParser.V_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#any_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAny_type(IFLogParser.Any_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#any_ret_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAny_ret_type(IFLogParser.Any_ret_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#any_ret_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAny_ret_value(IFLogParser.Any_ret_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#any_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAny_value(IFLogParser.Any_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#any_non_null_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAny_non_null_value(IFLogParser.Any_non_null_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#any_arithmetic_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAny_arithmetic_op(IFLogParser.Any_arithmetic_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#any_comparison_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAny_comparison_op(IFLogParser.Any_comparison_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link IFLogParser#any_logical_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAny_logical_op(IFLogParser.Any_logical_opContext ctx);
}