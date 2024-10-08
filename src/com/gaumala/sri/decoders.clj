(ns com.gaumala.sri.decoders
  (:require [com.gaumala.xml :as xml]))

(defn- transform-mensaje [mensaje-elem]
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

(defn- transform-mensajes-seq [mensajes-elem]
  (map transform-mensaje (:content mensajes-elem)))

(defn- transform-autorizacion [autorizacion-elem]
  (let [reducer (fn [res-map elem]
                  (condp = (:tag elem)
                    :estado (assoc res-map
                                   :estado
                                   (xml/get-content elem))
                    :numeroAutorizacion (assoc res-map
                                               :numeroAutorizacion
                                               (xml/get-content elem))
                    :fechaAutorizacion (assoc res-map
                                              :fechaAutorizacion
                                              (xml/get-content elem))
                    :ambiente (assoc res-map
                                     :ambiente
                                     (xml/get-content elem))
                    :comprobante (assoc res-map
                                        :comprobante
                                        (xml/get-content elem))
                    :mensajes (assoc res-map
                                     :mensajes
                                     (transform-mensajes-seq elem))
                    res-map))]
    (reduce reducer {} (:content autorizacion-elem))))

(defn- transform-autorizaciones-seq [autorizaciones-elem]
  (map transform-autorizacion (:content autorizaciones-elem)))

(defn respuesta-recepcion-comprobante [xml-string]
  "decodifica la respuesta SOAP `xml-string` del web service validarComprobante
  el resultado es un mapa con los campos del tipo
  `RespuestaRecepcionComprobante`
  ```clojure
  (-> (slurp \"./respuesta_error.xml\")
      (respuesta-recepcion-comprobante))
  ;; => {:estado \"DEVUELTA\"
  ;;     :mensajes [{:identificador \"35\"
  ;;                 :mensaje \"ARCHIVO NO CUMPLE ESTRUCTURA XML\"
  ;;                 :informacionAdicional \"No existe un contribuyente registrado con el RUC 1704476523001\"
  ;;                 :tipo \"ERROR\"}]}
  ```"
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

(defn respuesta-autorizacion-comprobante [xml-string]
  "decodifica la respuesta SOAP `xml-string` del web service autorizacionComprobante
  el resultado es un mapa con los campos del tipo
  `RespuestaAutorizacionComprobante`
  ```clojure
  (-> (slurp \"./respuesta_error.xml\")
      (respuesta-recepcion-comprobante))
  ;; => {:claveAccesoConsultada \"0710202401179214673900110020010000000011234567810\"
  ;;     :numeroComprobantes \"1\"
  ;;     :autorizaciones [{:estado \"NO AUTORIZADO\"
  ;;                       :fechaAutorizacion \"2024-10-07T13:48:46-05:00\"
  ;;                       :ambiente \"PRUEBAS\"
  ;;                       :comprobante \"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\"
  ;;                       :mensajes [{:identificador \"56\"
  ;;                                   :mensaje \"ERROR ESTABLECIMIENTO CERRADO\"
  ;;                                   :informacionAdicional \"El establecimiento 002 está cerrado\"
  ;;                                   :tipo \"ERROR\"}]}]}
  ```"
  (let [envelope (xml/parse xml-string)
        respuesta (xml/find-by-tag envelope
                                   :RespuestaAutorizacionComprobante)
        reducer (fn [res-map elem]
                  (condp = (:tag elem)
                    :claveAccesoConsultada (assoc res-map
                                                  :claveAccesoConsultada
                                                  (xml/get-content elem))
                    :numeroComprobantes (assoc res-map
                                               :numeroComprobantes
                                               (xml/get-content elem))
                    :autorizaciones (assoc res-map
                                           :autorizaciones
                                           (transform-autorizaciones-seq elem))
                    res-map))]
    (reduce reducer {} (:content respuesta))))
