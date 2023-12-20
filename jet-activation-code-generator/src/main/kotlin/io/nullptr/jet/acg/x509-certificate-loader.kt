package io.nullptr.jet.acg

import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import java.io.InputStream
import java.io.InputStreamReader
import java.security.PrivateKey


fun loadClassPathResource(path: String): InputStream? {
    val classLoader = LicenseGenerator::class.java.classLoader
    return classLoader.getResourceAsStream(path)
}

fun readPKCS8PrivateKey(path: String): PrivateKey {
    val resource = loadClassPathResource(path)!!
    val pemKeyPair = PEMParser(InputStreamReader(resource)).readObject() as PEMKeyPair
    return JcaPEMKeyConverter().getPrivateKey(pemKeyPair.privateKeyInfo)
}

fun loadCertificate(path: String): X509CertificateHolder {
    val resource = loadClassPathResource(path)!!
    return PEMParser(InputStreamReader(resource)).readObject() as X509CertificateHolder
}
