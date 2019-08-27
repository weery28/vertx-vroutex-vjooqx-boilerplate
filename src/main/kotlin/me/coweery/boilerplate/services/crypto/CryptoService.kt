package me.coweery.boilerplate.services.crypto

interface CryptoService {

    fun getHashPasswordAndSalt(password: String): Pair<String, String>

    fun checkPassword(checkablePassword: String, hashedPassword: String, salt: String): Boolean
}