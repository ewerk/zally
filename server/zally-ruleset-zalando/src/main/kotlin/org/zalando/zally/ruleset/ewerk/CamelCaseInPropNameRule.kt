package org.zalando.zally.ruleset.ewerk

import com.typesafe.config.Config
import org.zalando.zally.core.CaseChecker
import org.zalando.zally.rule.api.Check
import org.zalando.zally.rule.api.Context
import org.zalando.zally.rule.api.Rule
import org.zalando.zally.rule.api.Severity
import org.zalando.zally.rule.api.Violation

@Rule(
    ruleSet = EwerkRuleSet::class,
    id = "001",
    severity = Severity.MUST,
    title = "Property Names Must be ASCII camelCase"
)
class CamelCaseInPropNameRule(config: Config) {
    private val description = "Property name has to be camelCase"

    // private val checker = CaseChecker.load(config)

    companion object {
        private val CAMEL_CASE = "[a-z][a-z0-9]*(?:[A-Z0-9]+[a-z0-9]*)*".toRegex()
        private val WHITE_LIST_PROPERTY = "_links".toRegex()
    }

    private val cases = sortedMapOf(
        "camelCase" to CAMEL_CASE,
        "_links" to WHITE_LIST_PROPERTY
    )

    @Check(severity = Severity.MUST)
    fun checkPropertyNames(context: Context): List<Violation> {

        val check = CaseChecker.CaseCheck(listOf(CAMEL_CASE, WHITE_LIST_PROPERTY))
        val checker = CaseChecker(cases, check)

        return checker.checkPropertyNames(context).map { Violation(description, it.pointer) }
    }
}
