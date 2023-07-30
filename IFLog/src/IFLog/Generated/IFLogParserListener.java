// Generated from D:/_Documents/_Programming/iflog_prototype/IFLog/src/IFLog/Grammars\IFLogParser.g4 by ANTLR 4.12.0
package IFLog.Generated;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link IFLogParser}.
 */
public interface IFLogParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link IFLogParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(IFLogParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(IFLogParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#composite_rule}.
	 * @param ctx the parse tree
	 */
	void enterComposite_rule(IFLogParser.Composite_ruleContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#composite_rule}.
	 * @param ctx the parse tree
	 */
	void exitComposite_rule(IFLogParser.Composite_ruleContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#composite_rule_arg}.
	 * @param ctx the parse tree
	 */
	void enterComposite_rule_arg(IFLogParser.Composite_rule_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#composite_rule_arg}.
	 * @param ctx the parse tree
	 */
	void exitComposite_rule_arg(IFLogParser.Composite_rule_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#composite_rule_global}.
	 * @param ctx the parse tree
	 */
	void enterComposite_rule_global(IFLogParser.Composite_rule_globalContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#composite_rule_global}.
	 * @param ctx the parse tree
	 */
	void exitComposite_rule_global(IFLogParser.Composite_rule_globalContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#composite_rule_global_el}.
	 * @param ctx the parse tree
	 */
	void enterComposite_rule_global_el(IFLogParser.Composite_rule_global_elContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#composite_rule_global_el}.
	 * @param ctx the parse tree
	 */
	void exitComposite_rule_global_el(IFLogParser.Composite_rule_global_elContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#composite_rule_event}.
	 * @param ctx the parse tree
	 */
	void enterComposite_rule_event(IFLogParser.Composite_rule_eventContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#composite_rule_event}.
	 * @param ctx the parse tree
	 */
	void exitComposite_rule_event(IFLogParser.Composite_rule_eventContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#composite_rule_event_type}.
	 * @param ctx the parse tree
	 */
	void enterComposite_rule_event_type(IFLogParser.Composite_rule_event_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#composite_rule_event_type}.
	 * @param ctx the parse tree
	 */
	void exitComposite_rule_event_type(IFLogParser.Composite_rule_event_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#composite_rule_event_el}.
	 * @param ctx the parse tree
	 */
	void enterComposite_rule_event_el(IFLogParser.Composite_rule_event_elContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#composite_rule_event_el}.
	 * @param ctx the parse tree
	 */
	void exitComposite_rule_event_el(IFLogParser.Composite_rule_event_elContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#comp_rule_event_type}.
	 * @param ctx the parse tree
	 */
	void enterComp_rule_event_type(IFLogParser.Comp_rule_event_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#comp_rule_event_type}.
	 * @param ctx the parse tree
	 */
	void exitComp_rule_event_type(IFLogParser.Comp_rule_event_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#comp_rule}.
	 * @param ctx the parse tree
	 */
	void enterComp_rule(IFLogParser.Comp_ruleContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#comp_rule}.
	 * @param ctx the parse tree
	 */
	void exitComp_rule(IFLogParser.Comp_ruleContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#comp_rule_head}.
	 * @param ctx the parse tree
	 */
	void enterComp_rule_head(IFLogParser.Comp_rule_headContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#comp_rule_head}.
	 * @param ctx the parse tree
	 */
	void exitComp_rule_head(IFLogParser.Comp_rule_headContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#comp_rule_head_el}.
	 * @param ctx the parse tree
	 */
	void enterComp_rule_head_el(IFLogParser.Comp_rule_head_elContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#comp_rule_head_el}.
	 * @param ctx the parse tree
	 */
	void exitComp_rule_head_el(IFLogParser.Comp_rule_head_elContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_return}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_return(IFLogParser.Stat_comp_rule_returnContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_return}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_return(IFLogParser.Stat_comp_rule_returnContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_return_el}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_return_el(IFLogParser.Stat_comp_rule_return_elContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_return_el}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_return_el(IFLogParser.Stat_comp_rule_return_elContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_print}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_print(IFLogParser.Stat_comp_rule_printContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_print}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_print(IFLogParser.Stat_comp_rule_printContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_db_action}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_db_action(IFLogParser.Stat_comp_rule_db_actionContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_db_action}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_db_action(IFLogParser.Stat_comp_rule_db_actionContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_call}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_call(IFLogParser.Stat_comp_rule_callContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_call}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_call(IFLogParser.Stat_comp_rule_callContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#comp_rule_body}.
	 * @param ctx the parse tree
	 */
	void enterComp_rule_body(IFLogParser.Comp_rule_bodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#comp_rule_body}.
	 * @param ctx the parse tree
	 */
	void exitComp_rule_body(IFLogParser.Comp_rule_bodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_tags}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_tags(IFLogParser.Stat_comp_rule_tagsContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_tags}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_tags(IFLogParser.Stat_comp_rule_tagsContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_tags_el}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_tags_el(IFLogParser.Stat_comp_rule_tags_elContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_tags_el}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_tags_el(IFLogParser.Stat_comp_rule_tags_elContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_if}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_if(IFLogParser.Stat_comp_rule_ifContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_if}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_if(IFLogParser.Stat_comp_rule_ifContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_comparison}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_comparison(IFLogParser.Stat_comp_rule_comparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_comparison}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_comparison(IFLogParser.Stat_comp_rule_comparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_comparison_el}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_comparison_el(IFLogParser.Stat_comp_rule_comparison_elContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_comparison_el}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_comparison_el(IFLogParser.Stat_comp_rule_comparison_elContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_get}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_get(IFLogParser.Stat_comp_rule_getContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_get}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_get(IFLogParser.Stat_comp_rule_getContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_selects}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_selects(IFLogParser.Stat_comp_rule_selectsContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_selects}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_selects(IFLogParser.Stat_comp_rule_selectsContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_multi}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_multi(IFLogParser.Stat_comp_rule_multiContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_multi}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_multi(IFLogParser.Stat_comp_rule_multiContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_multi_el}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_multi_el(IFLogParser.Stat_comp_rule_multi_elContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_multi_el}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_multi_el(IFLogParser.Stat_comp_rule_multi_elContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_between}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_between(IFLogParser.Stat_comp_rule_betweenContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_between}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_between(IFLogParser.Stat_comp_rule_betweenContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_between_el}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_between_el(IFLogParser.Stat_comp_rule_between_elContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_between_el}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_between_el(IFLogParser.Stat_comp_rule_between_elContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comp_rule_between_el2}.
	 * @param ctx the parse tree
	 */
	void enterStat_comp_rule_between_el2(IFLogParser.Stat_comp_rule_between_el2Context ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comp_rule_between_el2}.
	 * @param ctx the parse tree
	 */
	void exitStat_comp_rule_between_el2(IFLogParser.Stat_comp_rule_between_el2Context ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#view}.
	 * @param ctx the parse tree
	 */
	void enterView(IFLogParser.ViewContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#view}.
	 * @param ctx the parse tree
	 */
	void exitView(IFLogParser.ViewContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#view_field}.
	 * @param ctx the parse tree
	 */
	void enterView_field(IFLogParser.View_fieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#view_field}.
	 * @param ctx the parse tree
	 */
	void exitView_field(IFLogParser.View_fieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#view_rule}.
	 * @param ctx the parse tree
	 */
	void enterView_rule(IFLogParser.View_ruleContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#view_rule}.
	 * @param ctx the parse tree
	 */
	void exitView_rule(IFLogParser.View_ruleContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#view_rule_lhs}.
	 * @param ctx the parse tree
	 */
	void enterView_rule_lhs(IFLogParser.View_rule_lhsContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#view_rule_lhs}.
	 * @param ctx the parse tree
	 */
	void exitView_rule_lhs(IFLogParser.View_rule_lhsContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#view_rule_rhs}.
	 * @param ctx the parse tree
	 */
	void enterView_rule_rhs(IFLogParser.View_rule_rhsContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#view_rule_rhs}.
	 * @param ctx the parse tree
	 */
	void exitView_rule_rhs(IFLogParser.View_rule_rhsContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#view_rule_ele}.
	 * @param ctx the parse tree
	 */
	void enterView_rule_ele(IFLogParser.View_rule_eleContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#view_rule_ele}.
	 * @param ctx the parse tree
	 */
	void exitView_rule_ele(IFLogParser.View_rule_eleContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_case_when}.
	 * @param ctx the parse tree
	 */
	void enterStat_case_when(IFLogParser.Stat_case_whenContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_case_when}.
	 * @param ctx the parse tree
	 */
	void exitStat_case_when(IFLogParser.Stat_case_whenContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_case_when_el}.
	 * @param ctx the parse tree
	 */
	void enterStat_case_when_el(IFLogParser.Stat_case_when_elContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_case_when_el}.
	 * @param ctx the parse tree
	 */
	void exitStat_case_when_el(IFLogParser.Stat_case_when_elContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_assignment}.
	 * @param ctx the parse tree
	 */
	void enterStat_assignment(IFLogParser.Stat_assignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_assignment}.
	 * @param ctx the parse tree
	 */
	void exitStat_assignment(IFLogParser.Stat_assignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_select}.
	 * @param ctx the parse tree
	 */
	void enterStat_select(IFLogParser.Stat_selectContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_select}.
	 * @param ctx the parse tree
	 */
	void exitStat_select(IFLogParser.Stat_selectContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_table}.
	 * @param ctx the parse tree
	 */
	void enterStat_table(IFLogParser.Stat_tableContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_table}.
	 * @param ctx the parse tree
	 */
	void exitStat_table(IFLogParser.Stat_tableContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_table_el}.
	 * @param ctx the parse tree
	 */
	void enterStat_table_el(IFLogParser.Stat_table_elContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_table_el}.
	 * @param ctx the parse tree
	 */
	void exitStat_table_el(IFLogParser.Stat_table_elContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table}.
	 * @param ctx the parse tree
	 */
	void enterTable(IFLogParser.TableContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table}.
	 * @param ctx the parse tree
	 */
	void exitTable(IFLogParser.TableContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_field}.
	 * @param ctx the parse tree
	 */
	void enterTable_field(IFLogParser.Table_fieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_field}.
	 * @param ctx the parse tree
	 */
	void exitTable_field(IFLogParser.Table_fieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_arg}.
	 * @param ctx the parse tree
	 */
	void enterTable_arg(IFLogParser.Table_argContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_arg}.
	 * @param ctx the parse tree
	 */
	void exitTable_arg(IFLogParser.Table_argContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_arg_extended}.
	 * @param ctx the parse tree
	 */
	void enterTable_arg_extended(IFLogParser.Table_arg_extendedContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_arg_extended}.
	 * @param ctx the parse tree
	 */
	void exitTable_arg_extended(IFLogParser.Table_arg_extendedContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_arg_alias}.
	 * @param ctx the parse tree
	 */
	void enterTable_arg_alias(IFLogParser.Table_arg_aliasContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_arg_alias}.
	 * @param ctx the parse tree
	 */
	void exitTable_arg_alias(IFLogParser.Table_arg_aliasContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_arg_default_value}.
	 * @param ctx the parse tree
	 */
	void enterTable_arg_default_value(IFLogParser.Table_arg_default_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_arg_default_value}.
	 * @param ctx the parse tree
	 */
	void exitTable_arg_default_value(IFLogParser.Table_arg_default_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_pk}.
	 * @param ctx the parse tree
	 */
	void enterTable_pk(IFLogParser.Table_pkContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_pk}.
	 * @param ctx the parse tree
	 */
	void exitTable_pk(IFLogParser.Table_pkContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_uks}.
	 * @param ctx the parse tree
	 */
	void enterTable_uks(IFLogParser.Table_uksContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_uks}.
	 * @param ctx the parse tree
	 */
	void exitTable_uks(IFLogParser.Table_uksContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_pk_uk_el}.
	 * @param ctx the parse tree
	 */
	void enterTable_pk_uk_el(IFLogParser.Table_pk_uk_elContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_pk_uk_el}.
	 * @param ctx the parse tree
	 */
	void exitTable_pk_uk_el(IFLogParser.Table_pk_uk_elContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_data}.
	 * @param ctx the parse tree
	 */
	void enterTable_data(IFLogParser.Table_dataContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_data}.
	 * @param ctx the parse tree
	 */
	void exitTable_data(IFLogParser.Table_dataContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_data_record}.
	 * @param ctx the parse tree
	 */
	void enterTable_data_record(IFLogParser.Table_data_recordContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_data_record}.
	 * @param ctx the parse tree
	 */
	void exitTable_data_record(IFLogParser.Table_data_recordContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_record}.
	 * @param ctx the parse tree
	 */
	void enterTable_record(IFLogParser.Table_recordContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_record}.
	 * @param ctx the parse tree
	 */
	void exitTable_record(IFLogParser.Table_recordContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_record_value}.
	 * @param ctx the parse tree
	 */
	void enterTable_record_value(IFLogParser.Table_record_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_record_value}.
	 * @param ctx the parse tree
	 */
	void exitTable_record_value(IFLogParser.Table_record_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_record_extended}.
	 * @param ctx the parse tree
	 */
	void enterTable_record_extended(IFLogParser.Table_record_extendedContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_record_extended}.
	 * @param ctx the parse tree
	 */
	void exitTable_record_extended(IFLogParser.Table_record_extendedContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_record_extended_value}.
	 * @param ctx the parse tree
	 */
	void enterTable_record_extended_value(IFLogParser.Table_record_extended_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_record_extended_value}.
	 * @param ctx the parse tree
	 */
	void exitTable_record_extended_value(IFLogParser.Table_record_extended_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_constraint}.
	 * @param ctx the parse tree
	 */
	void enterTable_constraint(IFLogParser.Table_constraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_constraint}.
	 * @param ctx the parse tree
	 */
	void exitTable_constraint(IFLogParser.Table_constraintContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#table_constraint_el}.
	 * @param ctx the parse tree
	 */
	void enterTable_constraint_el(IFLogParser.Table_constraint_elContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#table_constraint_el}.
	 * @param ctx the parse tree
	 */
	void exitTable_constraint_el(IFLogParser.Table_constraint_elContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_coalesce}.
	 * @param ctx the parse tree
	 */
	void enterStat_coalesce(IFLogParser.Stat_coalesceContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_coalesce}.
	 * @param ctx the parse tree
	 */
	void exitStat_coalesce(IFLogParser.Stat_coalesceContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_coalesce_el}.
	 * @param ctx the parse tree
	 */
	void enterStat_coalesce_el(IFLogParser.Stat_coalesce_elContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_coalesce_el}.
	 * @param ctx the parse tree
	 */
	void exitStat_coalesce_el(IFLogParser.Stat_coalesce_elContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_comparison}.
	 * @param ctx the parse tree
	 */
	void enterStat_comparison(IFLogParser.Stat_comparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_comparison}.
	 * @param ctx the parse tree
	 */
	void exitStat_comparison(IFLogParser.Stat_comparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_arithmetic}.
	 * @param ctx the parse tree
	 */
	void enterStat_arithmetic(IFLogParser.Stat_arithmeticContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_arithmetic}.
	 * @param ctx the parse tree
	 */
	void exitStat_arithmetic(IFLogParser.Stat_arithmeticContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_arithmetic_el}.
	 * @param ctx the parse tree
	 */
	void enterStat_arithmetic_el(IFLogParser.Stat_arithmetic_elContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_arithmetic_el}.
	 * @param ctx the parse tree
	 */
	void exitStat_arithmetic_el(IFLogParser.Stat_arithmetic_elContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#stat_in}.
	 * @param ctx the parse tree
	 */
	void enterStat_in(IFLogParser.Stat_inContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#stat_in}.
	 * @param ctx the parse tree
	 */
	void exitStat_in(IFLogParser.Stat_inContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#v_list}.
	 * @param ctx the parse tree
	 */
	void enterV_list(IFLogParser.V_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#v_list}.
	 * @param ctx the parse tree
	 */
	void exitV_list(IFLogParser.V_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#any_type}.
	 * @param ctx the parse tree
	 */
	void enterAny_type(IFLogParser.Any_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#any_type}.
	 * @param ctx the parse tree
	 */
	void exitAny_type(IFLogParser.Any_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#any_ret_type}.
	 * @param ctx the parse tree
	 */
	void enterAny_ret_type(IFLogParser.Any_ret_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#any_ret_type}.
	 * @param ctx the parse tree
	 */
	void exitAny_ret_type(IFLogParser.Any_ret_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#any_ret_value}.
	 * @param ctx the parse tree
	 */
	void enterAny_ret_value(IFLogParser.Any_ret_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#any_ret_value}.
	 * @param ctx the parse tree
	 */
	void exitAny_ret_value(IFLogParser.Any_ret_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#any_value}.
	 * @param ctx the parse tree
	 */
	void enterAny_value(IFLogParser.Any_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#any_value}.
	 * @param ctx the parse tree
	 */
	void exitAny_value(IFLogParser.Any_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#any_non_null_value}.
	 * @param ctx the parse tree
	 */
	void enterAny_non_null_value(IFLogParser.Any_non_null_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#any_non_null_value}.
	 * @param ctx the parse tree
	 */
	void exitAny_non_null_value(IFLogParser.Any_non_null_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#any_arithmetic_op}.
	 * @param ctx the parse tree
	 */
	void enterAny_arithmetic_op(IFLogParser.Any_arithmetic_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#any_arithmetic_op}.
	 * @param ctx the parse tree
	 */
	void exitAny_arithmetic_op(IFLogParser.Any_arithmetic_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#any_comparison_op}.
	 * @param ctx the parse tree
	 */
	void enterAny_comparison_op(IFLogParser.Any_comparison_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#any_comparison_op}.
	 * @param ctx the parse tree
	 */
	void exitAny_comparison_op(IFLogParser.Any_comparison_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link IFLogParser#any_logical_op}.
	 * @param ctx the parse tree
	 */
	void enterAny_logical_op(IFLogParser.Any_logical_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link IFLogParser#any_logical_op}.
	 * @param ctx the parse tree
	 */
	void exitAny_logical_op(IFLogParser.Any_logical_opContext ctx);
}