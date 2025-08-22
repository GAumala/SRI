(ns com.gaumala.sri.xades-bes
  "Funciones para firmar facturas electrónicas usando la
  librería [xades4j](https://github.com/luisgoncalves/xades4j)"
  {:doc/format :markdown}
  (:require [com.gaumala.xml-doc :refer [string->document
                                         document->raw-string]])
  (:require [com.gaumala.sri.xades4j :as xades4j]))

(defn- new-signer-bes [stream pass]
  (try
    (xades4j/new-signer-bes stream pass)
    (catch Exception e
      (throw (ex-info (ex-message e) {:type :xml-signature} e)))))

(defn- sign-bes [signer xml-doc]
  (try
    (xades4j/sign-bes signer xml-doc)
    (catch Exception e
      (throw (ex-info (ex-message e) {:type :xml-signature} e)))))

(defn sign-comprobante
  "Firma un comprobante `xml-string` con un keystore especificado en el
  mapa `store` de la siguiente forma:

  | key       | Descripción |
  | ----------|-------------|
  | `:input`  | File path, byte array, o `InputStream` del keystore (archivo .p12)
  | `:pass`   | Contraseña del certificado

  El resultado de esta función es un string con el XML firmado.
  
  Esta función puede arrojar un `ExceptionInfo` si hay algún problema
  con el keystore o la contraseña que impida firmar correctamente. En 
  ese caso `(:type (ex-data e))` devuelve `:xml-signature`."
  {:doc/format :markdown}
  [xml-string store]
  (let [signer (new-signer-bes (:input store) (:pass store))
        xml-doc (string->document xml-string)]
    (sign-bes signer xml-doc)
    (document->raw-string xml-doc)))

(defn get-certificate-info
  "Devuelve un mapa con información del certificado de firma digital
  especificado en el mapa `store:

   ## Parámetros de entrada (mapa `store`)

  | key       | Descripción |
  | ----------|-------------|
  | `:input`  | File path, byte array, o `InputStream` del keystore (archivo .p12)
  | `:pass`   | Contraseña del certificado

  ## Valores de retorno (mapa con información del certificado)

  | key                  | Tipo        | Descripción |
  | -------------------- | ----------- | ----------- |
  | `:alias`             | String      | Alias del certificado en el keystore |
  | `:subject`           | Principal   | Sujeto del certificado (ej: CN=Nombre, O=Organización) |
  | `:issuer`            | Principal   | Emisor del certificado |
  | `:serial-number`     | BigInteger  | Número de serie del certificado |
  | `:valid-from`        | Long        | Fecha de inicio de validez (Unix timestamp en milisegundos) |
  | `:valid-until`       | Long        | Fecha de expiración (Unix timestamp en milisegundos) |
  | `:expired?`          | Boolean     | `true` si el certificado ya expiró, `false` si aún es válido |
  | `:days-until-expiry` | Long        | Días restantes hasta la expiración (solo si no ha expirado) |

  ## Ejemplo de retorno

  ```clojure
  {:alias \"1\"
   :subject \"CN=Juan Pérez, O=Mi Empresa, C=ES\"
   :issuer \"CN=Autoridad Certificadora, O=ACME\"
   :serial-number 1234567890
   :valid-from 1672531200000    ; 1 de Enero 2023
   :valid-until 1704067199000   ; 31 de Diciembre 2023
   :expired? false
   :days-until-expiry 45}
  ```"
  {:doc/format :markdown}
  [store]
  (let [keystore (xades4j/load-keystore (:input store) (:pass store))]
    (xades4j/get-certificate-info keystore)))
