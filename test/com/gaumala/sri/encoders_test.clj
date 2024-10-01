(ns com.gaumala.sri.encoders-test
  (:require [clojure.test :refer [deftest is]]
            [com.gaumala.sri.encoders :as encoders]))

(deftest should-encode-validar-comprobante
  (let [expected (slurp "./test/res/encoded-validar-comprobante.xml")
        actual (encoders/validar-comprobante "<foo><bar/></foo>")]
    (is (= expected actual))))
