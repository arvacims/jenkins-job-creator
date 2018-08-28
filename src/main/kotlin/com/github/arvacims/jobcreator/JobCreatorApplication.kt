package com.github.arvacims.jobcreator

import com.github.arvacims.jobcreator.gerrit.GerritConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.client.support.BasicAuthorizationInterceptor
import org.springframework.web.client.RestTemplate

@SpringBootApplication
class JobCreatorApplication {

    @Bean
    fun gerritConfig(env: Environment): GerritConfig =
            createGerritConfig(env)

}

fun main(args: Array<String>) {
    SpringApplication.run(JobCreatorApplication::class.java, *args)
}

fun createGerritConfig(env: Environment): GerritConfig {
    val user = env.getRequiredProperty("gerrit.user")
    val hostname = env.getRequiredProperty("gerrit.ssh.hostname")
    val sshPort = env.getRequiredProperty("gerrit.ssh.port", Integer::class.java).toInt()
    val sshKeyFile = env.getRequiredProperty("gerrit.ssh.key.file")

    val rawSshKeyPass = env.getProperty("gerrit.ssh.key.pass")
    val sshKeyPass = if (rawSshKeyPass.isNullOrEmpty()) null else rawSshKeyPass

    return GerritConfig(
            user = user,
            hostname = hostname,
            sshPort = sshPort,
            sshKeyFile = sshKeyFile,
            sshKeyPass = sshKeyPass
    )
}

fun restTemplate(env: Environment, user: String, password: String): RestTemplate {
    val requestFactory = HttpComponentsClientHttpRequestFactory().apply {
        setConnectTimeout(env.getRequiredProperty("rest.timeout.connect", Int::class.java))
        setReadTimeout(env.getRequiredProperty("rest.timeout.read", Int::class.java))
    }

    val restTemplate = RestTemplate(BufferingClientHttpRequestFactory(requestFactory))

    val authorizationInterceptor = BasicAuthorizationInterceptor(user, password)

    restTemplate.interceptors.add(authorizationInterceptor)
    restTemplate.interceptors.add(RestTemplateLoggingInterceptor())

    return restTemplate
}
