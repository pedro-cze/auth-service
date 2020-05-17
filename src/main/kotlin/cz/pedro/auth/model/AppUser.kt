package cz.pedro.auth.model

import cz.pedro.auth.data.UserDto
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import javax.persistence.*

@Entity
@Table(name="user", schema = "blog")
class AppUser(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long?,
        @Column(nullable = false)
        val username: String,
        @Column(nullable = false)
        val password: String,
        @Column(name = "roles")
        val roles: String = "",
        @Column(name = "permissions")
        val permissions: String = ""
) : Model {

    fun rolesAsList(): List<GrantedAuthority> {
        return roles.split(",").map { role ->
            SimpleGrantedAuthority("ROLE_$role")
        }
    }

    fun permissionsAsList(): List<String> {
        return permissions.split(",")
    }

    override fun toDto(): UserDto {
        TODO("Not yet implemented")
    }
}
