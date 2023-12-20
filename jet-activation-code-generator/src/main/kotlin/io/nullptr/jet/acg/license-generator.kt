package io.nullptr.jet.acg

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.crypto.digests.SHA1Digest
import org.bouncycastle.crypto.params.RSAKeyParameters
import org.bouncycastle.crypto.util.PrivateKeyFactory
import org.bouncycastle.util.encoders.Base64

@Serializable
data class License(
    val licenseId: String,

    /**
     * 受托人邮箱
     */
    val assigneeEmail: String,

    /**
     * 受托人名称
     */
    val assigneeName: String,

    /**
     * 自动延期？
     */
    val autoProlongated: Boolean,

    /**
     * 是否检查并发使用
     */
    val checkConcurrentUse: Boolean,

    /**
     * 宽限期
     */
    val gracePeriodDays: Int,

    /**
     * 许可证内容的哈希值？
     */
    val hash: String,

    /**
     * 自动延期？
     */
    val isAutoProlongated: Boolean,

    /**
     * 许可证限制：指在某些特定条件下颁发的许可证，其使用范围受到限制。
     */
    val licenseRestriction: String,

    /**
     * 许可证名称
     */
    val licenseeName: String,

    /**
     * 许可证元数据信息
     */
    val metadata: String,

    /**
     * 许可证所包含的产品列表
     */
    val products: List<Product>
) {


    @Serializable
    data class Product(
        val code: String,
        val extended: Boolean,
        val fallbackDate: String,
        val paidUpTo: String
    )
}

data class ActivationCode(
    val licenseId: String,
    val licenseContent: String,
    val licenseSignature: String,
    val certificate: String
) {
    override fun toString(): String {
        return "${licenseId}-${licenseContent}-${licenseSignature}-${certificate}"
    }
}

class LicenseGenerator {

    private lateinit var licenseId: String
    private lateinit var licenseName: String
    private lateinit var metadata: String
    private lateinit var hash: String

    private var assigneeEmail = ""
    private var assigneeName = ""
    private var autoProlongated = false
    private var checkConcurrentUse = false

    private var gracePeriodDays = 7

    private var isAutoProlongated = false
    private var licenseRestriction = ""

    /**
     * !@from https://data.services.jetbrains.com/products
     */
    private val productsCode = mutableListOf<String>()

    companion object {
        private const val TEST_PRODUCT_FALLBACK_DATE = "2345-06-07"
        private const val TEST_PRODUCT_PAID_UP_TO = "2345-06-07"

        private const val LICENSE_ID_LENGTH = 10

        private const val FIXED_METADATA = "0120220701PSAN000005"
        private const val FIXED_HASH = "TRIAL:-594988122"

        private const val RANDOM_LICENSE_ID_WORDS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    }

    fun withRandomLicenseId(): LicenseGenerator {
        val licenseId = buildString {
            repeat(LICENSE_ID_LENGTH) {
                append(RANDOM_LICENSE_ID_WORDS.random())
            }
        }

        return withLicenseId(licenseId)
    }

    fun withLicenseId(licenseId: String): LicenseGenerator {
        this.licenseId = licenseId
        return this
    }

    fun withLicenseName(licenseName: String): LicenseGenerator {
        this.licenseName = licenseName
        return this
    }

    fun withFixedMetadata(): LicenseGenerator = withMetadata(FIXED_METADATA)

    fun withMetadata(metadata: String): LicenseGenerator {
        this.metadata = metadata
        return this
    }

    fun withFixedHash() = withHash(FIXED_HASH)

    fun withHash(hash: String): LicenseGenerator {
        this.hash = hash
        return this
    }

    fun withProducts(vararg code: String): LicenseGenerator {
        productsCode.addAll(code)
        return this
    }

    fun generate(): License {
        val products =
            productsCode.map { License.Product(it, true, TEST_PRODUCT_FALLBACK_DATE, TEST_PRODUCT_PAID_UP_TO) }

        return License(
            licenseId,
            assigneeEmail,
            assigneeName,
            autoProlongated,
            checkConcurrentUse,
            gracePeriodDays,
            hash,
            isAutoProlongated,
            licenseRestriction,
            licenseName,
            metadata,
            products
        )
    }
}


fun main() {
    val testCertificateHolder = loadCertificate("x.509-certificate.pem")
    val testCertificate = JcaX509CertificateConverter().getCertificate(testCertificateHolder)
    println(testCertificate)

    // 证书签名验证 Hook 规则点
    printAgentEnhanceRule(testCertificate)

    val privateKey = readPKCS8PrivateKey("private-key.pem")
    val rsaKeyParameters = PrivateKeyFactory.createKey(privateKey.encoded) as RSAKeyParameters
    println(privateKey)


    val license = LicenseGenerator()
        .withRandomLicenseId()
        .withLicenseName("test-license-all-in-one")
        .withFixedHash()
        .withFixedMetadata()
        .withProducts("PC", "CL", "DB", "AC", "FL", "GO", "II", "QA", "RD", "RR", "TC", "WS")
        .generate()

    println(license)

    val serialized = Json.encodeToString(license)

    val signed = LicenseSigner()
        .withLicense(license)
        .withRSAKeyParameters(rsaKeyParameters)
        .withDigest(SHA1Digest())
        .sign()

    val activationCode = ActivationCode(
        license.licenseId,
        Base64.toBase64String(serialized.encodeToByteArray()),
        Base64.toBase64String(signed),
        Base64.toBase64String(testCertificate.encoded)
    )

    println(activationCode.toString())
}