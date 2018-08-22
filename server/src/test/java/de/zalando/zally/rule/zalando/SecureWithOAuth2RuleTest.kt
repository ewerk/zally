package de.zalando.zally.rule.zalando

import de.zalando.zally.getOpenApiContextFromContent
import de.zalando.zally.getSwaggerContextFromContent
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.Test

class SecureWithOAuth2RuleTest {

    private val rule = SecureWithOAuth2Rule()

    @Test
    fun `checkSecuritySchemesOAuth2IsUsed should return violation if no OAuth2 security definition is specified`() {
        @Language("YAML")
        val content = """
            openapi: 3.0.1
        """.trimIndent()
        val context = getOpenApiContextFromContent(content)

        val violation = rule.checkSecuritySchemesOAuth2IsUsed(context)

        assertThat(violation).isNotNull
        assertThat(violation!!.description).isEqualTo("API has to be secured by OAuth2")
        assertThat(violation.pointer.toString()).isEqualTo("/components/securitySchemes")
    }

    @Test
    fun `checkSecuritySchemesOAuth2IsUsed should return no violation if OAuth2 security definition is specified`() {
        @Language("YAML")
        val content = """
            openapi: 3.0.1
            components:
              securitySchemes:
                company-oauth2:
                  type: oauth2
                  scheme: Bearer
                  flows:
                    clientCredentials:
                      tokenUrl: https://identity.company.com/oauth2
                      scopes:
                        read: read access to the resources of this API
        """.trimIndent()
        val context = getOpenApiContextFromContent(content)

        val violation = rule.checkSecuritySchemesOAuth2IsUsed(context)

        assertThat(violation).isNull()
    }

    @Test
    fun `checkSecuritySchemesOnlyOAuth2IsUsed should return violation if non-OAuth2 security definition is specified`() {
        @Language("YAML")
        val content = """
            openapi: 3.0.1
            components:
              securitySchemes:
                company-oauth2:
                  type: apiKey
        """.trimIndent()
        val context = getOpenApiContextFromContent(content)

        val violation = rule.checkSecuritySchemesOnlyOAuth2IsUsed(context)

        assertThat(violation).isNotNull
        assertThat(violation!!.description).isEqualTo("Only OAuth2 is allowed to secure the API")
        assertThat(violation.pointer.toString()).isEqualTo("/components/securitySchemes")
    }

    @Test
    fun `checkSecuritySchemesOnlyOAuth2IsUsed should return no violation if no non-OAuth2 security definition is specified`() {
        @Language("YAML")
        val content = """
            openapi: 3.0.1
        """.trimIndent()
        val context = getOpenApiContextFromContent(content)

        val violation = rule.checkSecuritySchemesOnlyOAuth2IsUsed(context)

        assertThat(violation).isNull()
    }

    @Test
    fun `checkUsedScopesAreDefined should return violation for each undefined scope`() {
        @Language("YAML")
        val content = """
            openapi: 3.0.1
            paths:
              /article:
                post:
                  security:
                    - oauth2:
                        - write # is not defined in the security schemes
            components:
              securitySchemes:
                oauth2:
                  type: oauth2
                  scheme: Bearer
                  flows:
                    clientCredentials:
                      tokenUrl: https://identity.company.com/oauth2
                      scopes:
                        read: read access to the resources of this API
        """.trimIndent()
        val context = getOpenApiContextFromContent(content)

        val violations = rule.checkUsedScopesAreSpecified(context)

        assertThat(violations).isNotEmpty
        assertThat(violations[0].description).containsPattern("The scope 'oauth2/write' is not specified in the security schemes")
        assertThat(violations[0].pointer.toString()).isEqualTo("/paths/~1article/post/security/0/oauth2/0")
    }

    @Test
    fun `checkUsedScopesAreDefined should return no violation if only defined scopes are used`() {
        @Language("YAML")
        val content = """
            openapi: 3.0.1
            paths:
              /article:
                post:
                  security:
                    - oauth2:
                        - read # is defined in the security schemes
            components:
              securitySchemes:
                oauth2:
                  type: oauth2
                  scheme: Bearer
                  flows:
                    clientCredentials:
                      tokenUrl: https://identity.company.com/oauth2
                      scopes:
                        read: read access to the resources of this API
        """.trimIndent()
        val context = getOpenApiContextFromContent(content)

        val violations = rule.checkUsedScopesAreSpecified(context)

        assertThat(violations).isEmpty()
    }

    @Test
    fun `checkUsedScopesAreDefined should ignore OpenAPI 2 (Swagger) specification`() {
        @Language("YAML")
        val content = """
            swagger: 2.0
            info:
              title: Old API
              version: 1
        """.trimIndent()
        val context = getSwaggerContextFromContent(content)

        val violations = rule.checkUsedScopesAreSpecified(context)

        assertThat(violations).isEmpty()
    }
}
