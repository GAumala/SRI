(ns com.gaumala.sri
  (:require [org.httpkit.client :as http]
            [com.gaumala.sri.encoders :as encoders]
            [com.gaumala.sri.decoders :as decoders]))

(def soap-headers {"Content-Type" "text/xml"
                   "SOAPAction" ""})

(defn soap-opts-with-body [body] {:body body
                                  :headers soap-headers})

(defn validar-comprobante [url xml-string]
  (let [req (encoders/validar-comprobante xml-string)
        opts (soap-opts-with-body req)
        {:keys [body status]} @(http/post url opts)]
    (when (= 200 status) (decoders/respuesta-recepcion-comprobante body))))
