(ns com.gaumala.sri.clave-acceso
  (:require [clojure.string :as s]))

(defn compute-mod-11-verificador [input]
  (let [multipliers (take (count input) (cycle [2 3 4 5 6 7]))
        digits (->> input
                    (s/reverse)
                    (char-array)
                    (seq)
                    (map #(java.lang.Integer/parseInt (str %))))
        products (map * multipliers digits) ; pasos 1 y 2
        total (reduce + products); paso 3
        mod-11 (mod total 11) ; paso 4
        digito-verificador (- 11 mod-11)] ; paso 5
    (condp = digito-verificador
      11 0 ; cuando el resultado es 11 devolver 0
      10 1 ; cuando el resultado es 10 devolver 1
      digito-verificador)))

(defn gen-clave-acceso [{:keys [fechaEmision
                                ambiente
                                ruc
                                codDoc
                                secuencial
                                estab
                                ptoEmi
                                codigoNumerico]}]
  (let [tipo-emision "1" ;solo hay tipo 1 para autorizacion offline
        fecha-digits (s/replace fechaEmision #"/" "")
        serie (str estab ptoEmi)
        first-48-digits (str fecha-digits
                             codDoc
                             ruc
                             ambiente
                             serie
                             secuencial
                             codigoNumerico
                             tipo-emision)
        verificador (compute-mod-11-verificador first-48-digits)]
    (str first-48-digits verificador)))
