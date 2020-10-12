package org.zalando.zally.ruleset.ewerk

import com.typesafe.config.Config
import org.zalando.zally.core.CaseChecker
import org.zalando.zally.rule.api.Check
import org.zalando.zally.rule.api.Context
import org.zalando.zally.rule.api.Rule
import org.zalando.zally.rule.api.Severity
import org.zalando.zally.rule.api.Violation

/**
 * Lint for snake case for query params
 */
@Rule(
    ruleSet = EwerkRuleSet::class,
    id = "002",
    severity = Severity.MUST,
    title = "Use camelCase (never snake_case) for Query Parameters"
)
class CamelCaseForQueryParamsRule(config: Config) {

    val description = "Query parameter has to be camelCase"

    private val checker = CaseChecker.load(config)

    @Check(severity = Severity.MUST)
    fun checkQueryParameter(context: Context): List<Violation> =
        checker.checkQueryParameterNames(context).map { Violation(description, it.pointer) }
}
