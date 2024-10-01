(ns com.gaumala.sri
  (:require [org.httpkit.client :as http]
            [com.gaumala.xml-map :as xml]
            [com.gaumala.utils.base64 :as base64]))

(defn transform-mensaje [mensaje-elem]
  (let [reducer (fn [res-map elem]
                  (condp = (:tag elem)
                    :identificador (assoc res-map
                                          :identificador
                                          (xml/get-content elem))
                    :mensaje (assoc res-map
                                    :mensaje
                                    (xml/get-content elem))
                    :informacionAdicional (assoc res-map
                                                 :informacionAdicional
                                                 (xml/get-content elem))
                    :tipo (assoc res-map
                                 :tipo
                                 (xml/get-content elem))
                    res-map))]
    (reduce reducer {} (:content mensaje-elem))))

(defn transform-mensajes-seq [mensajes-elem]
  (map transform-mensaje (:content mensajes-elem)))

(defn decode-validar-comprobante-res [xml-string]
  (let [envelope (xml/parse xml-string)
        respuesta (xml/find-by-tag envelope
                                   :RespuestaRecepcionComprobante)
        reducer (fn [res-map elem]
                  (condp = (:tag elem)
                    :estado (assoc res-map
                                   :estado
                                   (xml/get-content elem))
                    :comprobantes (assoc res-map
                                         :mensajes
                                         (transform-mensajes-seq
                                          (xml/find-by-tag elem :mensajes)))
                    res-map))]
    (reduce reducer {} (:content respuesta))))

(defn encode-validar-comprobante-req [xml-string]
  (let [base64-str (base64/encode xml-string)
        body {:tag :validarComprobante
              :attrs {:xmlns "http://ec.gob.sri.ws.recepcion"}
              :content [{:tag :xml
                         :attrs {:xmlns ""}
                         :content [base64-str]}]}]
    (xml/encode-soap-message body)))

(defn validar-comprobante [url xml-string]
  (let [req (encode-validar-comprobante-req xml-string)
        {:keys [body
                status]} @(http/post url {:body req
                                          :headers {"Content-Type" "text/xml"
                                                    "SOAPAction" ""}})]
    (if (= 200 status) (decode-validar-comprobante-res body) nil)))
