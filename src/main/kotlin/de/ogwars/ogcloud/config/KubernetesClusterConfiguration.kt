package de.ogwars.ogcloud.config

import io.fabric8.kubernetes.client.ConfigBuilder
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class KubernetesClusterConfiguration(
    @Value("\${spring.kube.master-url}")
    val masterUrl: String,

    @Value("\${spring.kube.certificate-authority-data}")
    val certificateAuthorityData: String,

    @Value("\${spring.kube.client-certificate-data}")
    val clientCertificateData: String,

    @Value("\${spring.kube.client-key-data}")
    val clientKeyData: String,
) {

    @Bean
    fun kubernetesClient(): KubernetesClient = KubernetesClientBuilder()
        .withConfig(
            ConfigBuilder()
                .withMasterUrl(masterUrl.trimIndent())
                .withConnectionTimeout(Duration.ofMinutes(2).toMillis().toInt())
                .withRequestTimeout(Duration.ofMinutes(2).toMillis().toInt())
                .withCaCertData(certificateAuthorityData.trimIndent())
                .withClientCertData(clientCertificateData.trimIndent())
                .withClientKeyData(clientKeyData.trimIndent())
                .build()
        ).build()
}