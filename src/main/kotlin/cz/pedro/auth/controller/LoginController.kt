package cz.pedro.auth.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/api/public"])
class LoginController {

    @GetMapping
    @RequestMapping("/user")
    fun test1(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello, friend")
    }

}
