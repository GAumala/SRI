(ns hello
  (:require [datil.sri-clj.api :refer [authorize-receipt]]
            [selmer.parser :refer [render-file set-resource-path!]]
            [com.gaumala.xml :as xml-lib]))

(defn run [opts]
  (set-resource-path! "/home/gabriel/Projects/felec")
  (let [my-data {:ambiente 0 ;test
                 :razon-social "Diego Umejuarez"
                 :ruc "1704476523001"
                 :clave-acceso "000"
                 :establecimiento {:codigo "001"
                                   :dir "9 de Octubre Guayaquil"}
                 :punto-emision "001"
                 :secuencial "000000001"
                 :matriz "9 de Octubre Guayaquil"
                 :fecha "26/09/2024"
                 :cliente {:tipo "04"
                           :id "1701565725001"
                           :razon-social "Mi Panaderia"
                           :dir "Alborada 10ma etapa mz. 5 villa 19"}
                 :subtotal "10.00"
                 :iva "1.50"
                 :cart [{:codigo "01"
                         :descripcion "Gaseosa 1L"
                         :cantidad "10"
                         :precio "1.00"
                         :importe "10.00"
                         :iva "1.50"}]
                 :total "11.50"}
        xml-string (render-file "resources/factura.xml" my-data)
        xml-doc (xml-lib/string->document xml-string)]
    (println "doctype" (type xml-doc))
    (println "factura\n" xml-string)))
