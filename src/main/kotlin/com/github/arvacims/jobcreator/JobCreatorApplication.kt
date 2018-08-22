package com.github.arvacims.jobcreator

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
    fun restTemplateJenkins(env: Environment): RestTemplate {
        // set timeouts
        val requestFactory = HttpComponentsClientHttpRequestFactory().apply {
            setConnectTimeout(env.getRequiredProperty("rest.timeout.connect", Int::class.java))
            setReadTimeout(env.getRequiredProperty("rest.timeout.read", Int::class.java))
        }
        val restTemplate = RestTemplate(BufferingClientHttpRequestFactory(requestFactory))
        restTemplate.interceptors.add(
                BasicAuthorizationInterceptor(
                        env.getRequiredProperty("jenkins.user"),
                        env.getRequiredProperty("jenkins.password")
                )
        )
        restTemplate.interceptors.add(RestTemplateLoggingInterceptor())
        return restTemplate
    }

    @Bean
    fun gerritConfig(env: Environment): GerritConfig {
        val user = env.getRequiredProperty("gerrit.ssh.user")
        val hostname = env.getRequiredProperty("gerrit.ssh.hostname")
        val sshPort = env.getRequiredProperty("gerrit.ssh.port", Integer::class.java).toInt()
        val sshKeyFile = env.getRequiredProperty("gerrit.ssh.key.file")
        val sshKeyPass = env.getRequiredProperty("gerrit.ssh.key.pass")
        return GerritConfig(
                user = user,
                hostname = hostname,
                sshPort = sshPort,
                sshKeyFile = sshKeyFile,
                sshKeyPass = sshKeyPass
        )
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(JobCreatorApplication::class.java, *args)
}
