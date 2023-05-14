package cz.pedro.auth.controller

import cz.pedro.auth.data.UserDto
import cz.pedro.auth.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@RestController
@RequestMapping(path = ["/users"])
@CrossOrigin(origins = ["http://localhost:3000"])
class UserController(
    @Autowired val userService: UserService
) {

    @GetMapping
    fun getUsers(): ResponseEntity<List<UserDto>> {
        return ResponseEntity.ok(userService.getUsers())
    }

    @GetMapping("/{resourceId}")
    fun getResourceUsers(@PathVariable resourceId: String): ResponseEntity<List<UserDto>> {
        userService.getUsersByServiceName(serviceName = resourceId)
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }
}
