(ns com.gaumala.sri.clave-acceso-test
  (:require [clojure.test :refer [deftest is]]
            [com.gaumala.sri.clave-acceso :refer [compute-mod-11-verificador
                                                  gen-clave-acceso]]))

; el manual del sri usa como ejemplo la cadena "41261533"
(deftest should-compute-correct-mod-11-digit
  (is (= 6 (compute-mod-11-verificador "41261533"))))

(deftest should-generate-correct-clave-acceso
  (let [actual (gen-clave-acceso {:fechaEmision "01/10/2024"
                                  :ambiente 1
                                  :ruc "0928523464001"
                                  :codDoc "01"
                                  :secuencial "000000001"
                                  :estab "001"
                                  :ptoEmi "001"
                                  :codigoNumerico "12341234"})
        expected "0110202401092852346400110010010000000011234123415"]
    (is (= expected actual))))
