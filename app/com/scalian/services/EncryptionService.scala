package com.scalian.services

import javax.inject.Singleton

import java.util.Arrays
import java.util.Base64

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest


@Singleton
class EncryptionService {
  var secretKey: String = "";
  
  private final val Algorithm = "AES/CBC/PKCS5Padding"
  private final val secretKeyDigest = MessageDigest.getInstance("SHA-1").digest(secretKey.getBytes)
  private final val Key = new SecretKeySpec(Arrays.copyOf(secretKeyDigest, 16), "AES")
  private final val IvSpec = new IvParameterSpec(new Array[Byte](16))

  def encrypt(text: String) = {
    val cipher = Cipher.getInstance(Algorithm)
    cipher.init(Cipher.ENCRYPT_MODE, Key, IvSpec)

    new String(Base64.getEncoder.encode(cipher.doFinal(text.getBytes("utf-8"))), "utf-8")
  }

  def decrypt(encodedText: String) = {
    val cipher = Cipher.getInstance(Algorithm)
    cipher.init(Cipher.DECRYPT_MODE, Key, IvSpec)

    new String(cipher.doFinal(Base64.getDecoder.decode(encodedText.getBytes("utf-8"))), "utf-8")
  }
}