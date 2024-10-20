(ns com.gaumala.xades4j
  (:import java.io.File

           java.security.KeyStore
           java.security.cert.X509Certificate

           java.util.Arrays
           java.util.List

           javax.xml.transform.TransformerFactory
           javax.xml.transform.dom.DOMSource
           javax.xml.transform.stream.StreamResult

           xades4j.properties.DataObjectFormatProperty
           xades4j.providers.KeyingDataProvider
           xades4j.providers.impl.KeyStoreKeyingDataProvider$KeyStorePasswordProvider
           xades4j.providers.impl.KeyStoreKeyingDataProvider$KeyEntryPasswordProvider
           xades4j.providers.impl.KeyStoreKeyingDataProvider$SigningCertificateSelector
           xades4j.algorithms.EnvelopedSignatureTransform
           xades4j.production.BasicSignatureOptions
           xades4j.production.DataObjectReference
           xades4j.production.SignatureAlgorithms
           xades4j.production.SignedDataObjects
           xades4j.production.SigningCertificateMode
           xades4j.production.XadesBesSigningProfile))

(def KEY_USAGE_INDEX_DIGITAL_SIGNATURE 0)

(defn- has-digital-signature-key-usage
  [^X509Certificate certificate]
  (get (.getKeyUsage certificate) KEY_USAGE_INDEX_DIGITAL_SIGNATURE))

(defn- load-keystore [stream pwd]
  (let [keystore (KeyStore/getInstance "PKCS12")]
    (.load keystore stream (char-array pwd))
    keystore))

(defn- find-digital-signature-alias [keystore]
  (let [aliases (enumeration-seq (.aliases keystore))
        digital-signature-filter
        (fn [a] (let [certificate (.getCertificate keystore a)]
                  (if (instance? X509Certificate certificate)
                    (has-digital-signature-key-usage certificate)
                    false)))]
    (first (filter digital-signature-filter aliases))))

(defn- get-certificate-chain [keystore target-alias full-chain]
  (if full-chain (-> keystore
                     (.getCertificateChain target-alias)
                     Arrays/asList)
      (-> keystore
          (.getCertificate target-alias)
          List/of)))

(defn- new-keying-data-provider [keystore pwd]
  (let [target-alias (find-digital-signature-alias keystore)
        cert-chain (get-certificate-chain keystore target-alias false)
        private-key (.getKey keystore target-alias (char-array pwd))]
    (reify KeyingDataProvider
      (getSigningCertificateChain [this] cert-chain)
      (getSigningKey [this signing-cert] private-key))))

(defn- basic-signature-options []
  (-> (BasicSignatureOptions.)
      (.includePublicKey true)
      (.signKeyInfo true)))

(def ALGO_SIG_RSA_SHA_1 "http://www.w3.org/2000/09/xmldsig#rsa-sha1")
(def ALGO_DIGEST_SHA_1 "http://www.w3.org/2000/09/xmldsig#sha1")

(defn- signature-algorithms []
  (-> (SignatureAlgorithms.)
      (.withSignatureAlgorithm "RSA" ALGO_SIG_RSA_SHA_1)
      (.withDigestAlgorithmForDataObjectReferences ALGO_DIGEST_SHA_1)
      (.withDigestAlgorithmForReferenceProperties ALGO_DIGEST_SHA_1)))

(defn new-signer-bes [stream pwd]
  (let [keystore (load-keystore stream pwd)
        kdp (new-keying-data-provider keystore pwd)]
    (-> (XadesBesSigningProfile. kdp)
        (.withBasicSignatureOptions (basic-signature-options))
        (.withSignatureAlgorithms (signature-algorithms))
        (.newSigner))))

(defn- comprobante-reference []
  (-> (DataObjectReference. "#comprobante")
      (.withTransform (EnvelopedSignatureTransform.))
      (.withDataObjectFormat (.withDescription
                              (DataObjectFormatProperty. "text/xml")
                              "contenido comprobante"))))

(defn sign-bes
  [signer ^org.w3c.dom.Document doc]
  (let [elem (.getDocumentElement doc)
        data-objs (->> [(comprobante-reference)]
                       (into-array DataObjectReference)
                       (SignedDataObjects.))]
    (.setIdAttribute elem "id" true)
    (.sign signer data-objs elem)))
