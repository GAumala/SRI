(ns com.gaumala.xades4j
  (:import java.io.File

           java.security.KeyStore
           java.security.cert.X509Certificate

           javax.xml.transform.TransformerFactory
           javax.xml.transform.dom.DOMSource
           javax.xml.transform.stream.StreamResult

           xades4j.providers.impl.KeyStoreKeyingDataProvider$KeyStorePasswordProvider
           xades4j.providers.impl.KeyStoreKeyingDataProvider$KeyEntryPasswordProvider
           xades4j.providers.impl.KeyStoreKeyingDataProvider$SigningCertificateSelector
           xades4j.providers.impl.DirectKeyingDataProvider
           xades4j.providers.impl.FileSystemKeyStoreKeyingDataProvider
           xades4j.algorithms.EnvelopedSignatureTransform
           xades4j.production.BasicSignatureOptions
           xades4j.production.DataObjectReference
           xades4j.production.Enveloped
           xades4j.production.SignedDataObjects
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

(defn- new-keying-data-provider [keystore pwd]
  (let [target-alias (find-digital-signature-alias keystore)
        certificate (.getCertificate keystore target-alias)
        private-key (.getKey keystore target-alias (char-array pwd))]
    (DirectKeyingDataProvider. certificate private-key)))

(defn new-signer-bes [stream pwd]
  (let [keystore (load-keystore stream pwd)
        kdp (new-keying-data-provider keystore pwd)]
    (-> (XadesBesSigningProfile. kdp)
        (.withBasicSignatureOptions (.includePublicKey
                                     (BasicSignatureOptions.)
                                     true))
        (.newSigner))))

(defn sign-bes [signer ^org.w3c.dom.Document doc]
  (let [elem (.getDocumentElement doc)]
    (.sign (Enveloped. signer) elem)))
