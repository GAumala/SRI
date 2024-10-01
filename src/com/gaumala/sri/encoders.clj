(ns com.gaumala.sri.encoders
  (:require [com.gaumala.xml-map :as xml]
            [com.gaumala.utils.base64 :as base64]))

(defn validar-comprobante [xml-string]
  (let [base64-str (base64/encode xml-string)
        body {:tag :validarComprobante
              :attrs {:xmlns "http://ec.gob.sri.ws.recepcion"}
              :content [{:tag :xml
                         :attrs {:xmlns ""}
                         :content [base64-str]}]}]
    (xml/encode-soap-message body)))
