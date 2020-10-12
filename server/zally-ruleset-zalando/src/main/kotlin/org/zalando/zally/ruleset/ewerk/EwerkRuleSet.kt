package org.zalando.zally.ruleset.ewerk

import org.zalando.zally.core.AbstractRuleSet
import org.zalando.zally.rule.api.Rule
import java.net.URI

class EwerkRuleSet : AbstractRuleSet() {

    override val url: URI = URI.create("https://zalando.github.io/restful-api-guidelines/")
    override fun url(rule: Rule): URI {
        return url.resolve("#${rule.id}")
    }
}
