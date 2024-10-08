(ns com.gaumala.sri.encoders
  (:require [com.gaumala.sri.comprobantes :refer [gen-factura]]
            [com.gaumala.soap :as soap]
            [com.gaumala.utils.base64 :as base64]
            [com.gaumala.xml :as xml]))

(defn validar-comprobante [xml-string]
  "codifica el `xml-string` de un comprobante en otro string xml como
  un mensaje soap para enviar al web service validarComprobante del sri"
  (let [base64-str (base64/encode xml-string)
        body {:tag :validarComprobante
              :attrs {:xmlns "http://ec.gob.sri.ws.recepcion"}
              :content [{:tag :xml
                         :attrs {:xmlns ""}
                         :content [base64-str]}]}]
    (-> body
        (xml/map->element)
        (soap/message-with-body)
        (xml/emit))))

(defn autorizacion-comprobante [clave-acceso]
  "codifica la `clave-acceso` de un comprobante en otro string xml como
  un mensaje soap para enviar al web service autorizacionComprobante del sri"
  (let [body {:tag :autorizacionComprobante
              :attrs {:xmlns "http://ec.gob.sri.ws.autorizacion"}
              :content [{:tag :claveAccesoComprobante
                         :attrs {:xmlns ""}
                         :content [clave-acceso]}]}]
    (-> body
        (xml/map->element)
        (soap/message-with-body)
        (xml/emit))))

(defn factura
  "Codifica un mapa `params` con datos de factura a un string xml. Si
  incluyes el `codigo` numérico de 8 dígitos como 2ndo parámetro, se
  generará tu clave de acesso en el xml resultante.
  ```clojure
  (sri-encoders/factura {:infoTributaria {:ambiente 1
                                          :codDoc \"01\"
                                          ; ...
                                         }
                         :infoFactura {:fechaEmision \"21/10/2012\"
                                       :obligadoContabilidad \"SI\"
                                       :tipoIdentificacionComprador \"04\"
                                       ; ...
                                      }
                         :detalles: [{:codigoPrincipal \"125BJC-01\"
                                      :cantidad \"10.00\"
                                      ; ...
                                     }]
                         :infoAdicional [{:nombre \"Codigo Impuesto ISD\"
                                          :texto \"4580\"}
                                         ; ...
                                        ]}
                        \"123456789\")
  ;; => <factura id=\"comprobante\" version=\"1.0.0\">...
```"
  ([params codigo] (->> (gen-factura params codigo)
                        (xml/emit)))
  ([params] (factura params nil)))
