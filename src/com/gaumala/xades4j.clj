(ns com.gaumala.xades4j
  (:import java.io.File

           javax.xml.transform.TransformerFactory
           javax.xml.transform.dom.DOMSource
           javax.xml.transform.stream.StreamResult

           xades4j.providers.impl.KeyStoreKeyingDataProvider$KeyStorePasswordProvider
           xades4j.providers.impl.KeyStoreKeyingDataProvider$KeyEntryPasswordProvider
           xades4j.providers.impl.KeyStoreKeyingDataProvider$SigningCertificateSelector
           xades4j.providers.impl.FileSystemKeyStoreKeyingDataProvider
           xades4j.algorithms.EnvelopedSignatureTransform
           xades4j.production.BasicSignatureOptions
           xades4j.production.DataObjectReference
           xades4j.production.Enveloped
           xades4j.production.SignedDataObjects
           xades4j.production.XadesBesSigningProfile))

(def KEY_USAGE_INDEX_DIGITAL_SIGNATURE 0)

(defn- direct-password-provider [pwd]
  (reify
    KeyStoreKeyingDataProvider$KeyStorePasswordProvider
    (getPassword [this] (.toCharArray pwd))
    KeyStoreKeyingDataProvider$KeyEntryPasswordProvider
    (getPassword [this alias cert] (.toCharArray pwd))))

(defn- has-digital-signature-key-usage
  [^java.security.cert.X509Certificate certificate]
  (get (.getKeyUsage certificate) KEY_USAGE_INDEX_DIGITAL_SIGNATURE))

(defn- digital-signature-certificate-selector []
  (reify
    KeyStoreKeyingDataProvider$SigningCertificateSelector
    (selectCertificate [this entries]
      (->> entries
           (filter #(has-digital-signature-key-usage (.getCertificate %)))
           (first)))))

(defn- output-doc [doc path]
  (let [factory (TransformerFactory/newInstance)
        transformer (.newTransformer factory)
        source (DOMSource. doc)
        result (StreamResult. (File. path))]
    (.transform transformer source result)))

(defn new-signer-bes [cert-path pwd]
  (let [password-provider (direct-password-provider pwd)
        kdp-builder (FileSystemKeyStoreKeyingDataProvider/builder
                     "pkcs12"
                     cert-path
                     (digital-signature-certificate-selector))
        kdp (-> kdp-builder
                (.storePassword password-provider)
                (.entryPassword password-provider)
                (.fullChain true)
                (.build))]
    (-> (XadesBesSigningProfile. kdp)
        (.withBasicSignatureOptions (.includePublicKey
                                     (BasicSignatureOptions.)
                                     true))
        (.newSigner))))

(defn sign-bes [signer ^org.w3c.dom.Document doc]
  (let [elem (.getDocumentElement doc)]
    (.sign (Enveloped. signer) elem)))
