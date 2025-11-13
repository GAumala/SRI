(ns com.gaumala.sri.nota-credito-test
  (:require [clojure.test :refer [deftest is]]
            [com.gaumala.resources :refer [load-xml]]
            [com.gaumala.sri.encoders :as encoders]))

(deftest should-generate-and-encode-example-nota-credito
  (let [expected (load-xml "./test/res/example-nota-credito.xml")
        actual (encoders/nota-credito
                {:infoTributaria {:ambiente "1"
                                  :tipoEmision "1"
                                  :razonSocial "Distribuidora de Suministros Nacional S.A."
                                  :nombreComercial "Empresa Importadora y Exportadora de Piezas"
                                  :ruc "1792146739001"
                                  :claveAcceso "2110201104179214673900110020010000000011234567812"
                                  :codDoc "04"
                                  :estab "002"
                                  :ptoEmi "001"
                                  :secuencial "000000001"
                                  :dirMatriz "Enrique Guerrero Portilla OE1-34 AV. Galo Plaza Lasso"}
                 :infoNotaCredito {:fechaEmision "21/10/2012"
                                   :dirEstablecimiento "Sebastián Moreno S/N Francisco García"
                                   :tipoIdentificacionComprador "04"
                                   :razonSocialComprador "PRUEBAS SERVICIO DE RENTAS INTERNAS"
                                   :identificacionComprador "1713328506001"
                                   :contribuyenteEspecial "5368"
                                   :obligadoContabilidad "SI"
                                   :codDocModificado "01"
                                   :numDocModificado "002-001-000000001"
                                   :fechaEmisionDocSustento "21/10/2011"
                                   :totalSinImpuestos "295000.00"
                                   :totalConImpuestos [{:codigo "3"
                                                        :codigoPorcentaje "3072"
                                                        :baseImponible "295000.00"
                                                        :valor "14750.00"}
                                                       {:codigo "2"
                                                        :codigoPorcentaje "2"
                                                        :descuentoAdicional "5.00"
                                                        :baseImponible "309750.00"
                                                        :valor "37169.40"}
                                                       {:codigo "5"
                                                        :codigoPorcentaje "5001"
                                                        :baseImponible "12000.00"
                                                        :valor "240.00"}]
                                   :valorModificacion "346920.00"
                                   :moneda "DOLAR"
                                   :motivo "DEVOLUCIÓN"}
                 :detalles [{:codigoInterno "125BJC-01"
                             :codigoAuxiliar "1234D56789-A"
                             :descripcion "CAMIONETA 4X4 DIESEL 3.7"
                             :cantidad "10.00"
                             :precioUnitario "300000.00"
                             :descuento "5000.00"
                             :precioTotalSinImpuesto "295000.00"
                             :detallesAdicionales [{:nombre "Marca Chevrolet"
                                                    :valor "Chevrolet"}
                                                   {:nombre "Modelo"
                                                    :valor "2012"}
                                                   {:nombre "Chasis"
                                                    :valor "8LDETA03V20003289"}]
                             :impuestos [{:codigo "3"
                                          :codigoPorcentaje "3072"
                                          :baseImponible "295000.00"
                                          :valor "14750.00"
                                          :tarifa 5}
                                         {:codigo "2"
                                          :codigoPorcentaje "2"
                                          :baseImponible "309750.00"
                                          :valor "37170.00"
                                          :tarifa 12}
                                         {:codigo "5"
                                          :codigoPorcentaje "5001"
                                          :baseImponible "12000.00"
                                          :valor "240.00"
                                          :tarifa "0.02"}]}]
                 :infoAdicional [{:nombre "E-MAIL"
                                  :texto "info@organizacion.com"}]}
                "12345678")]
    (is (= expected actual))))

