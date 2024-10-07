(ns com.gaumala.sri.xades-bes
  (:require [com.gaumala.xml-doc :refer [string->document document->string]])
  (:require [com.gaumala.xades4j :refer [new-signer-bes sign-bes]]))

(defn sign-xml [xml-string cert]
  "Firma un documento `xml-string` con un certificado especificado en el
 mapa `cert` de la siguiente forma:
 | key     | Descripción |
 | --------|-------------|
 | `:path` | Ruta del archivo del certificado
 | `:pass` | Contraseña del certificado
  El resultado de  esta funcion es un string con el XML firmado."
  (let [signer (new-signer-bes (:path cert) (:pass cert))
        xml-doc (string->document xml-string)]
    (sign-bes signer xml-doc)
    (document->string xml-doc)))
