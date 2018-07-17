package com.github.arvacims.jobcreator

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.core.env.Environment
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.client.support.BasicAuthorizationInterceptor
import org.springframework.web.client.RestTemplate

@SpringBootApplication
@ComponentScan("com.github.arvacims.jobcreator")
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
        val gerritHostName = env.getRequiredProperty("gerrit.ssh.hostname")
        val gerritSshPort = env.getRequiredProperty("gerrit.ssh.port", Integer::class.java).toInt()
        val gerritSshKeyFile = env.getRequiredProperty("gerrit.ssh.keyfile")
        val gerritUser = env.getRequiredProperty("gerrit.ssh.user")
        return GerritConfig(gerritHostName, gerritUser, gerritSshPort, gerritSshKeyFile)
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(JobCreatorApplication::class.java, *args)
}
