package io.nullptr.jet.acg

import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Security
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.io.path.Path
import kotlin.random.Random

class X509CertificateBuilder {

    private lateinit var keyPair: KeyPair
    private lateinit var issuedAt: Date
    private lateinit var expiredAt: Date
    private lateinit var subject: X500Name
    private lateinit var issuer: X500Name
    private lateinit var serialNumber: BigInteger
    private lateinit var signatureAlgorithm: String

    companion object {
        private const val DEFAULT_SIGNATURE_ALGORITHM = "SHA256WithRSA"
    }

    fun withDefaultSignatureAlgorithm() = withSignatureAlgorithm(DEFAULT_SIGNATURE_ALGORITHM)

    fun withSignatureAlgorithm(signatureAlgorithm: String): X509CertificateBuilder {
        this.signatureAlgorithm = signatureAlgorithm
        return this
    }

    fun withRandomSerialNumber() = withSerialNumber(BigInteger(Random.nextBytes(20)))

    fun withSerialNumber(serialNumber: BigInteger): X509CertificateBuilder {
        this.serialNumber = serialNumber
        return this
    }

    fun withKeyPair(keyPair: KeyPair): X509CertificateBuilder {
        this.keyPair = keyPair
        return this
    }

    fun withSubject(subjectCommonName: String) = withSubject(X500Name("CN=$subjectCommonName"))

    fun withSubject(subject: X500Name): X509CertificateBuilder {
        this.subject = subject
        return this
    }

    fun withIssuer(issuerCommonName: String) = withIssuer(X500Name("CN=$issuerCommonName"))

    fun withIssuer(issuer: X500Name): X509CertificateBuilder {
        this.issuer = issuer
        return this
    }

    fun withIssueAtNow() = withIssueAt(LocalDateTime.now())

    fun withIssueAtYesterday() = withIssueAt(LocalDateTime.now().minusDays(1L))

    fun withIssueAt(issuedAt: LocalDateTime): X509CertificateBuilder {
        this.issuedAt = Date.from(issuedAt.toInstant(ZoneOffset.UTC))
        return this
    }

    fun withNeverExpired() = withExpiredAt(LocalDateTime.now().plusYears(99))

    fun withExpiredAt(expiredAt: LocalDateTime): X509CertificateBuilder {
        this.expiredAt = Date.from(expiredAt.toInstant(ZoneOffset.UTC))
        return this
    }

    fun build(): X509CertificateHolder {
        val subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.public.encoded)
        val builder = X509v3CertificateBuilder(issuer, serialNumber, issuedAt, expiredAt, subject, subjectPublicKeyInfo)

        val signer = JcaContentSignerBuilder(signatureAlgorithm)
            .setProvider(BouncyCastleProvider())
            .build(keyPair.private)

        return builder.build(signer)
    }
}

fun OutputStream.asJcaPEMWriter() = JcaPEMWriter(OutputStreamWriter(this))

fun main() {
    Security.addProvider(BouncyCastleProvider())

    val keyPairGenerator = KeyPairGenerator.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME)
    keyPairGenerator.initialize(4096)

    val keyPair = keyPairGenerator.generateKeyPair()

    val x509CertificateHolder = X509CertificateBuilder()
        .withKeyPair(keyPair)
        .withDefaultSignatureAlgorithm()
        .withIssuer("JetProfile CA")
        .withSubject("acg-from-2023-12-20")
        .withIssueAtNow()
        .withNeverExpired()
        .withRandomSerialNumber()
        .build()

    val certificatePEMOutputStream = FileOutputStream(Path("x.509-certificate.pem").toFile())
    certificatePEMOutputStream.asJcaPEMWriter().use { it.writeObject(x509CertificateHolder) }

    val privateKeyPEMOutputStream = FileOutputStream(Path("private-key.pem").toFile())
    privateKeyPEMOutputStream.asJcaPEMWriter().use { it.writeObject(keyPair.private) }
}