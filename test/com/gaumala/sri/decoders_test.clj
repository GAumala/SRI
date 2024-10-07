(ns com.gaumala.sri.decoders-test
  (:require [clojure.test :refer [deftest is]]
            [com.gaumala.resources :refer [load-xml]]
            [com.gaumala.sri.decoders :as decoders]))

(deftest should-decode-error-respuesta-recepcion-comprobante
  (let [xml-string (load-xml "./test/res/respuesta-recepcion-comprobante.error.xml")
        actual (decoders/respuesta-recepcion-comprobante xml-string)
        expected {:estado "DEVUELTA",
                  :mensajes [{:identificador "35"
                              :mensaje "ARCHIVO NO CUMPLE ESTRUCTURA XML"
                              :informacionAdicional "No existe un contribuyente registrado con el RUC 1704476523001"
                              :tipo "ERROR"}
                             {:identificador "35"
                              :mensaje "ARCHIVO NO CUMPLE ESTRUCTURA XML"
                              :informacionAdicional "Se encontró el siguiente error en la estructura del comprobante: 0 no corresponde a ningun tipo de ambiente."
                              :tipo "ERROR"}]}]
    (is (= expected actual))))
