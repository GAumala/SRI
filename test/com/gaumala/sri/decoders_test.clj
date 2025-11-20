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

(deftest respuesta-recepcion-comprobante-returns-nil-with-invalid-response
  (let [xml-string (load-xml "./test/res/respuesta-invalid.xml")
        actual (decoders/respuesta-recepcion-comprobante xml-string)
        expected nil]
    (is (= expected actual))))

(deftest respuesta-recepcion-comprobante-returns-nil-with-invalid-xml
  (let [xml-string "<invalid"
        actual (decoders/respuesta-recepcion-comprobante xml-string)
        expected nil]
    (is (= expected actual))))

(deftest should-decode-error-respuesta-autorizacion-comprobante
  (let [xml-string (load-xml "./test/res/respuesta-autorizacion-comprobante.error.xml")
        actual (decoders/respuesta-autorizacion-comprobante xml-string)
        expected {:claveAccesoConsultada "0710202401179214673900110020010000000011234567810",
                  :numeroComprobantes "1"
                  :autorizaciones [{:estado "NO AUTORIZADO"
                                    :fechaAutorizacion "2024-10-07T13:48:46-05:00"
                                    :ambiente "PRUEBAS"
                                    :comprobante "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"no\\\"?>"
                                    :mensajes [{:identificador "56"
                                                :mensaje "ERROR ESTABLECIMIENTO CERRADO"
                                                :informacionAdicional "El establecimiento 002 está cerrado"
                                                :tipo "ERROR"}]}]}]
    (is (= expected actual))))

(deftest respuesta-autorizacion-comprobante-returns-nil-with-invalid-response
  (let [xml-string (load-xml "./test/res/respuesta-invalid.xml")
        actual (decoders/respuesta-autorizacion-comprobante xml-string)
        expected nil]
    (is (= expected actual))))

(deftest respuesta-autorizacion-comprobante-returns-nil-with-invalid-xml
  (let [xml-string "<invalid"
        actual (decoders/respuesta-autorizacion-comprobante xml-string)
        expected nil]
    (is (= expected actual))))
