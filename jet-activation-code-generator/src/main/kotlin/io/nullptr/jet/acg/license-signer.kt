package io.nullptr.jet.acg

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bouncycastle.crypto.Digest
import org.bouncycastle.crypto.params.RSAKeyParameters
import org.bouncycastle.crypto.signers.RSADigestSigner

class LicenseSigner {
    private lateinit var license: License
    private lateinit var digest: Digest
    private lateinit var rsaKeyParameters: RSAKeyParameters

    fun withLicense(license: License): LicenseSigner {
        this.license = license
        return this
    }

    fun withDigest(digest: Digest): LicenseSigner {
        this.digest = digest
        return this
    }

    fun withRSAKeyParameters(rsaKeyParameters: RSAKeyParameters): LicenseSigner {
        this.rsaKeyParameters = rsaKeyParameters
        return this
    }

    fun sign(): ByteArray {
        val serialized = Json.encodeToString(license).encodeToByteArray()

        val signer = RSADigestSigner(digest)
        signer.init(true, rsaKeyParameters)
        signer.update(serialized, 0, serialized.size)
        return signer.generateSignature()
    }
}
