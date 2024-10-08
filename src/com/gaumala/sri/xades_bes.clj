(ns com.gaumala.sri.xades-bes
  "Funciones para firmar facturas electrónicas usando la
  librería [xades4j](https://github.com/luisgoncalves/xades4j)"
  {:doc/format :markdown}
  (:require [com.gaumala.xml-doc :refer [string->document document->string]])
  (:require [com.gaumala.xades4j :refer [new-signer-bes sign-bes]]))

(defn sign-xml
  "Firma un documento `xml-string` con un certificado especificado en el
 mapa `cert` de la siguiente forma:

 | key     | Descripción |
 | --------|-------------|
 | `:path` | Ruta del archivo del certificado
 | `:pass` | Contraseña del certificado
  El resultado de  esta función es un string con el XML firmado."
  {:doc/format :markdown}
  [xml-string cert]
  (let [signer (new-signer-bes (:path cert) (:pass cert))
        xml-doc (string->document xml-string)]
    (sign-bes signer xml-doc)
    (document->string xml-doc)))
