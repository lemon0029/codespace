package io.nullptr.jet.acg

import org.bouncycastle.asn1.ASN1OctetString
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.crypto.encodings.PKCS1Encoding
import org.bouncycastle.crypto.engines.RSAEngine
import org.bouncycastle.crypto.util.PublicKeyFactory
import java.math.BigInteger
import java.security.MessageDigest
import java.security.cert.X509Certificate
import java.security.interfaces.RSAPublicKey

private val jetProfileRootCertificateHolder = loadCertificate("jet-profile-root-certificate.pem")
private val jetProfileRootCertificate = JcaX509CertificateConverter().getCertificate(jetProfileRootCertificateHolder)

@OptIn(ExperimentalStdlibApi::class)
fun printAgentEnhanceRule(testCertificate: X509Certificate) {
    /*
        证书的验证（验签）流程：
            1. 获取证书的签发者（CA），找到对应的根证书
            2. 使用根证书的公钥对待验证证书的签名解密得到结果 T1
            3. 计算证书的 TBS 部分内容的 Hash 值，确认与 T2 一致即可

        因根证书基本不可伪造，但是根证书的公钥已知，测试证书签名已知，则把结果 Hook 即可，即对核心 RSA 解密方法增强让根据参数提前返回
     */

    val signature = BigInteger(testCertificate.signature)
    println(signature)

    // 用该证书的公钥直接解密，也就是 RSA 解密的流程
    // 这里的 RESULT 就是需要 Hook 替换的，在实际的验签过程中这里用的公钥应该是根证书的
    val publicKey = testCertificate.publicKey as RSAPublicKey
    val result = signature.modPow(publicKey.publicExponent, publicKey.modulus)
    println(result)

    // 偏真实的解密场景，这里解密的同时去掉了填充
    val keyParameter = PublicKeyFactory.createKey(publicKey.encoded)
    val rsaEngine = RSAEngine()
    val pkcs1Encoding = PKCS1Encoding(rsaEngine)
    pkcs1Encoding.init(false, keyParameter)
    val result1 = pkcs1Encoding.processBlock(testCertificate.signature, 0, testCertificate.signature.size)
    println(result1.toHexString())
    println(ASN1OctetString.fromByteArray(result1))

    // 测试计算证书的 TBS 部分内容的哈希值
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val hashBytes = messageDigest.digest(testCertificate.tbsCertificate)
    println(hashBytes.toHexString())

    // 最终的规则如下所示
    val jetProfileRootCertificatePublicKey = jetProfileRootCertificate.publicKey as RSAPublicKey
    val exponent = jetProfileRootCertificatePublicKey.publicExponent
    val modulus = jetProfileRootCertificatePublicKey.modulus

    println("EQUAL,$signature,$exponent,$modulus->$result")
}


