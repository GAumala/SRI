(ns com.gaumala.sri.encoders
  (:require [com.gaumala.sri.comprobantes :refer [gen-factura]]
            [com.gaumala.utils.base64 :as base64]
            [com.gaumala.xml-map :as xml]))

(defn validar-comprobante [xml-string]
  (let [base64-str (base64/encode xml-string)
        body {:tag :validarComprobante
              :attrs {:xmlns "http://ec.gob.sri.ws.recepcion"}
              :content [{:tag :xml
                         :attrs {:xmlns ""}
                         :content [base64-str]}]}]
    (xml/encode-soap-message body)))

(defn factura
  ([params codigo] (->> (gen-factura params codigo)
                        (xml/encode-element)))
  ([params] (factura params nil)))
