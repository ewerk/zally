package org.zalando.zally.ruleset.ewerk

import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.Test
import org.zalando.zally.core.DefaultContextFactory
import org.zalando.zally.core.rulesConfig

internal class CamelCaseInPropNameRuleTest {

    companion object {
        private const val DESCRIPTION = "Property name has to be camelCase"
    }

    private val rule = CamelCaseInPropNameRule(rulesConfig)

    @Test
    fun `checkPropertyNames should return violation if a property name is not camelCase`() {
        @Language("YAML")
        val spec = """
            openapi: '3.0.1'
            components:
              schemas:
                article:
                  properties:
                    wrong_prop_here:
                      type: String
            """.trimIndent()
        val context = DefaultContextFactory().getOpenApiContext(spec)

        val violations = rule.checkPropertyNames(context)
        assertThat(violations).isNotEmpty
        assertThat(violations[0].description).isEqualTo(DESCRIPTION)
        assertThat(violations[0].pointer.toString()).isEqualTo("/components/schemas/article/properties/wrong_prop_here")
    }

    @Test
    fun `checkPropertyNames should return no violation if only camelCase properties are used`() {
        @Language("YAML")
        val spec = """
            openapi: '3.0.1'
            components: 
              schemas: 
                article:
                  properties: 
                    rightPropLookSo:
                      type: String
        """.trimIndent()
        val context = DefaultContextFactory().getOpenApiContext(spec)

        val violations = rule.checkPropertyNames(context)

        assertThat(violations).isEmpty()
    }

    @Test
    fun `checkPropertyNames should return no violation if only whitelisted properties are used`() {
        @Language("YAML")
        val spec = """
            openapi: '3.0.1'
            components: 
              schemas: 
                article:
                  properties: 
                    _links:
                      type: String
        """.trimIndent()
        val context = DefaultContextFactory().getOpenApiContext(spec)

        val violations = rule.checkPropertyNames(context)

        assertThat(violations).isEmpty()
    }
}
