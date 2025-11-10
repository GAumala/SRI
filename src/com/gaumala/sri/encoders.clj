(ns com.gaumala.sri.encoders
  "Funciones para codificar información a strings XML que se pueden
  envíar a los web services del SRI."
  (:require [com.gaumala.sri.comprobantes :refer [gen-factura
                                                  gen-nota-credito]]
            [com.gaumala.soap :as soap]
            [com.gaumala.utils.base64 :as base64]
            [com.gaumala.xml :as xml]))

(defn validar-comprobante
  "codifica el `xml-string` de un comprobante en otro string XML como
  un mensaje soap para enviar al web service `validarComprobante` del SRI."
  {:doc/format :markdown}
  [xml-string]
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

(defn autorizacion-comprobante
  "codifica la `clave-acceso` de un comprobante en otro string xml como
  un mensaje soap para enviar al web service `autorizacionComprobante` del SRI."
  {:doc/format :markdown}
  [clave-acceso]
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
  "Codifica un mapa `params` con datos de factura a un string XML Si
  incluyes el `codigo` numérico de 8 dígitos como 2ndo parámetro, se
  generará tu clave de acesso en el XML resultante.
  ```clojure
  (factura {:infoTributaria {:ambiente 1
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
  ;; => <?xml version='1.0' encoding='UTF-8'?>
  ;;      <factura id=\"comprobante\" version=\"1.0.0\">...
  ```
  
  El mapa `params` debe conformarse al spec `:sri.comprobantes/factura`,
  de lo contrario se arroja un `ExceptionInfo` a través de [[validate]]."
  {:doc/format :markdown}
  ([params codigo]
   (->> (gen-factura params codigo)
        (xml/emit)))
  ([params] (factura params nil)))

(defn nota-credito
  "Codifica un mapa `params` con datos de nota de crédito a un string XML. Si
  incluyes el `codigo` numérico de 8 dígitos como segundo parámetro, se
  generará la clave de acceso en el XML resultante a menos que el mapa la
  incluya explícitamente.

  El mapa `params` debe conformarse al spec `:sri.comprobantes/notaCredito`,
  de lo contrario se arroja un `ExceptionInfo` a través de [[validate]]."
  {:doc/format :markdown}
  ([params codigo]
   (->> (gen-nota-credito params codigo)
        (xml/emit)))
  ([params] (nota-credito params nil)))
