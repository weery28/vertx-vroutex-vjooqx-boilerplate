package me.coweery.boilerplate.services.crypto

import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.auth.VertxContextPRNG
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@Service
@Lazy
class CryptoServiceImpl @Autowired constructor(
    val vertx: Vertx
) : CryptoService {

    private val iterations = 20 * 1000
    private val saltLen = 32
    private val desiredKeyLen = 256
    private val secretFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

    override fun getHashPasswordAndSalt(password: String): Pair<String, String> {

        val salt = VertxContextPRNG.current(vertx).nextString(saltLen)
        return Pair(hash(password, salt), salt)
    }

    override fun checkPassword(checkablePassword: String, hashedPassword: String, salt: String): Boolean {
        return hash(checkablePassword, salt) == hashedPassword
    }

    private fun hash(password: String, salt: String): String {
        val hash = secretFactory.generateSecret(
            PBEKeySpec(
                password.toCharArray(), salt.toByteArray(), iterations, desiredKeyLen
            )
        )
        return Base64.getEncoder().encodeToString(hash.encoded)
    }
}