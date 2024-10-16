(ns com.gaumala.sri.decoders
  "Funciones para decodificar las respuestas XML de los web services
  del SRI."
  (:require [com.gaumala.xml :as xml]
            [clojure.spec.alpha :as s]))

(s/def :sri.respuestas/estado string?)
(s/def :sri.respuestas/claveAccesoConsultada string?)
(s/def :sri.respuestas/numeroComprobantes string?)
(s/def :sri.respuestas/ambiente string?)
(s/def :sri.respuestas/comprobante string?)
(s/def :sri.respuestas/fechaAutorizacion string?)

(s/def :sri.respuestas.mensaje/identificador string?)
(s/def :sri.respuestas.mensaje/mensaje string?)
(s/def :sri.respuestas.mensaje/informacionAdicional string?)
(s/def :sri.respuestas.mensaje/tipo string?)

(s/def :sri.respuestas/mensaje
  (s/keys :req-un [:sri.respuestas.mensaje/identificador
                   :sri.respuestas.mensaje/mensaje]
          :opt-un [:sri.respuestas.mensaje/informacionAdicional
                   :sri.respuestas.mensaje/tipo]))
(s/def :sri.respuestas/mensajes (s/* :sri.respuestas/mensaje))

(s/def :sri.respuestas/autorizacion
  (s/keys :req-un [:sri.respuestas/estado
                   :sri.respuestas/comprobante]
          :opt-un [:sri.respuestas/fechaAutorizacion
                   :sri.respuestas/ambiente
                   :sri.respuestas/mensajes]))
(s/def :sri.respuestas/autorizaciones (s/* :sri.respuestas/autorizacion))

(s/def :sri.respuestas/RespuestaRecepcionComprobante
  (s/keys :req-un [:sri.respuestas/estado]
          :opt-un [:sri.respuestas/mensajes]))

(s/def :sri.respuestas/RespuestaAutorizacionComprobante
  (s/keys :req-un [:sri.respuestas/claveAccesoConsultada
                   :sri.respuestas/numeroComprobantes
                   :sri.respuestas/autorizaciones]))

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

(defn- safely-parse-xml [input]
  (try (xml/parse input)
       ;; if parse fail, return an empty xml element
       (catch Exception e {:content []})))

(defn respuesta-recepcion-comprobante
  "Decodifica la respuesta SOAP `xml-string` del web service
  `validarComprobante`. El resultado es un mapa con los campos del tipo
  `RespuestaRecepcionComprobante `(spec 
  `:sri.respuestas/RespuestaRecepcionComprobante`).
  
  Si la respuesta no logra ser decodificada correctamente, devuelve `nil`.
  ```clojure
  (-> (slurp \"./respuesta_error.xml\")
      (respuesta-recepcion-comprobante))
  ;; => {:estado \"DEVUELTA\"
  ;;     :mensajes [{:identificador \"35\"
  ;;                 :mensaje \"ARCHIVO NO CUMPLE ESTRUCTURA XML\"
  ;;                 :informacionAdicional \"No existe un contribuyente registrado con el RUC 1704476523001\"
  ;;                 :tipo \"ERROR\"}]}
  ```"
  {:doc/format :markdown}
  [xml-string]
  (let [envelope (safely-parse-xml xml-string)
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
                    res-map))
        parsed (reduce reducer {} (:content respuesta))]
    (if (s/valid? :sri.respuestas/RespuestaRecepcionComprobante parsed)
      parsed nil)))

(defn respuesta-autorizacion-comprobante
  "Decodifica la respuesta SOAP `xml-string` del web service 
  `autorizacionComprobante`. El resultado es un mapa con los campos del tipo
  `RespuestaAutorizacionComprobante`
  (spec `:sri.respuestas/RespuestaAutorizacionComprobante`).
  
  Si la respuesta no logra ser decodificada correctamente, devuelve `nil`.
  ```clojure
  (-> (slurp \"./respuesta_error.xml\")
      (respuesta-autorizacion-comprobante))
  ;; => {:claveAccesoConsultada \"0710202401179214673900110020010000000011234567810\"
  ;;     :numeroComprobantes \"1\"
  ;;     :autorizaciones [{:estado \"NO AUTORIZADO\"
  ;;                       :fechaAutorizacion \"2024-10-07T13:48:46-05:00\"
  ;;                       :ambiente \"PRUEBAS\"
  ;;                       :comprobante \"<?xml version=\"1.0\" encoding=\"UTF-8\"...\"
  ;;                       :mensajes [{:identificador \"56\"
  ;;                                   :mensaje \"ERROR ESTABLECIMIENTO CERRADO\"
  ;;                                   :informacionAdicional \"El establecimiento 002 está cerrado\"
  ;;                                   :tipo \"ERROR\"}]}]}
  ```"
  {:doc/format :markdown}
  [xml-string]
  (let [envelope (safely-parse-xml xml-string)
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
                    res-map))
        parsed (reduce reducer {} (:content respuesta))]
    (if (s/valid? :sri.respuestas/RespuestaAutorizacionComprobante parsed)
      parsed nil)))
