(ns com.gaumala.sri.xades-bes
  "Funciones para firmar facturas electrónicas usando la
  librería [xades4j](https://github.com/luisgoncalves/xades4j)"
  {:doc/format :markdown}
  (:require [com.gaumala.xml-doc :refer [string->document document->string]])
  (:require [com.gaumala.xades4j :refer [new-signer-bes sign-bes]]))

(defn sign-xml
  "Firma un documento `xml-string` con un keystore especificado en el
 mapa `store` de la siguiente forma:

 | key       | Descripción |
 | ----------|-------------|
 | `:stream` | `InputStream` del keystore (archivo .p12)
 | `:pass`   | Contraseña del certificado
  El resultado de  esta función es un string con el XML firmado."
  {:doc/format :markdown}
  [xml-string store]
  (let [signer (new-signer-bes (:stream store) (:pass store))
        xml-doc (string->document xml-string)]
    (sign-bes signer xml-doc)
    (document->string xml-doc)))
