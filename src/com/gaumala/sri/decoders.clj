(ns com.gaumala.sri.decoders
  (:require [com.gaumala.xml-map :as xml]))

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

(defn respuesta-recepcion-comprobante [xml-string]
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
