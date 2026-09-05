package com.sztorm.notecalendar.core.common

import android.util.Base64
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import java.security.SecureRandom
import java.util.Random
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

enum class EncryptionType(val label: String) {
    None("None"),
    Aes256("AES 256")
}

sealed class EncryptionParameters {
    class Aes(
        val salt: ByteArray, val iv: IvParameterSpec, val key: SecretKey
    ) : EncryptionParameters()
}

fun randomByteArray(length: Int, random: Random = SecureRandom()): ByteArray {
    val byteArray = ByteArray(length)
    random.nextBytes(byteArray)

    return byteArray
}

fun generateAes256Key(password: String, salt: ByteArray): SecretKeySpec {
    val key = Argon2Kt()
        .hash(
            mode = Argon2Mode.ARGON2_I,
            password = password.toByteArray(),
            salt = salt,
            hashLengthInBytes = 32
        )
        .rawHashAsByteArray()

    return SecretKeySpec(key, "AES")
}

fun encrypt(data: ByteArray, parameters: EncryptionParameters): Base64String =
    when (parameters) {
        is EncryptionParameters.Aes -> Cipher.getInstance("AES/CBC/PKCS5Padding")
            .apply {
                init(Cipher.ENCRYPT_MODE, parameters.key, parameters.iv)
            }
            .doFinal(data)
            .toBase64(flags = Base64.NO_WRAP)
    }

fun decrypt(data: Base64String, parameters: EncryptionParameters): Result<ByteArray> =
    when (parameters) {
        is EncryptionParameters.Aes -> runCatching {
            Cipher.getInstance("AES/CBC/PKCS5Padding")
                .apply {
                    init(Cipher.DECRYPT_MODE, parameters.key, parameters.iv)
                }
                .doFinal(data.decoded())
        }
    }