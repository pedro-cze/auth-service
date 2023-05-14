package cz.pedro.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient
//import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
//@EnableDiscoveryClient
//@EnableFeignClients
class AuthApplication

fun main(args: Array<String>) {
    runApplication<AuthApplication>(*args)
}
