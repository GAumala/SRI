(ns com.gaumala.sri.encoders-test
  (:require [clojure.test :refer [deftest is]]
            [com.gaumala.resources :refer [load-xml]]
            [com.gaumala.sri.encoders :as encoders]))

(deftest should-encode-validar-comprobante
  (let [expected (load-xml "./test/res/encoded-validar-comprobante.xml")
        actual (encoders/validar-comprobante "<foo><bar/></foo>")]
    (is (= expected actual))))

(deftest should-encode-autorizacion-comprobante
  (let [expected (load-xml "./test/res/encoded-autorizacion-comprobante.xml")
        actual (encoders/autorizacion-comprobante "2110201201179214673900110020010000000011234567818")]
    (is (= expected actual))))
