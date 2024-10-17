(ns com.gaumala.sri.factura-test
  (:require [clojure.test :refer [deftest is]]
            [com.gaumala.resources :refer [load-xml]]
            [com.gaumala.sri.encoders :as encoders]))

(deftest should-generate-and-encode-example-factura
  (let [expected (load-xml "./test/res/example-factura.xml")
        actual (encoders/factura
                {:infoTributaria {:ambiente "1"
                                  :razonSocial "Distribuidora de Suministros Nacional S.A."
                                  :codDoc "01"
                                  :dirMatriz "Enrique Guerrero Portilla OE1-34 AV. Galo Plaza Lasso"
                                  :secuencial "000000001"
                                  :estab "002"
                                  :ptoEmi "001"
                                  :nombreComercial "Empresa Importadora y Exportadora de Piezas"
                                  :ruc "1792146739001"}
                 :infoFactura {:fechaEmision "21/10/2012"
                               :dirEstablecimiento "Sebastián Moreno S/N Francisco García"
                               :contribuyenteEspecial "5368"
                               :obligadoContabilidad "SI"
                               :tipoIdentificacionComprador "04"
                               :guiaRemision "001-001-000000001"
                               :razonSocialComprador "PRUEBAS SERVICIO DE RENTAS INTERNAS"
                               :identificacionComprador "1713328506001"
                               :direccionComprador "salinas y santiago"
                               :totalSinImpuestos "295000.00"
                               :totalDescuento "5005.00"
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
                               :propina "0.00"
                               :importeTotal "347159.40"
                               :moneda "DOLAR"
                               :pagos [{:formaPago "01"
                                        :total "347159.40"
                                        :plazo "30"
                                        :unidadTiempo "dias"}]
                               :valorRetIva "10620.00"
                               :valorRetRenta "2950.00"}
                 :detalles [{:codigoPrincipal "125BJC-01"
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
                                          :descuentoAdicional "5.00"
                                          :baseImponible "309750.00"
                                          :valor "37170.00"
                                          :tarifa 12}
                                         {:codigo "5"
                                          :codigoPorcentaje "5001"
                                          :baseImponible "12000.00"
                                          :valor "240.00"
                                          :tarifa "0.02"}]}]
                 :infoAdicional [{:nombre "Codigo Impuesto ISD"
                                  :texto "4580"}
                                 {:nombre "Impuesto ISD"
                                  :texto "15.42x"}]}
                "12345678")]
    (is (= expected actual))))

(deftest should-generate-and-encode-example-factura-with-nil-codigo
  (let [expected (load-xml "./test/res/example-factura.xml")
        actual (encoders/factura
                {:infoTributaria {:ambiente "1"
                                  :razonSocial "Distribuidora de Suministros Nacional S.A."
                                  :claveAcceso "2110201201179214673900110020010000000011234567818"
                                  :codDoc "01"
                                  :dirMatriz "Enrique Guerrero Portilla OE1-34 AV. Galo Plaza Lasso"
                                  :secuencial "000000001"
                                  :estab "002"
                                  :ptoEmi "001"
                                  :nombreComercial "Empresa Importadora y Exportadora de Piezas"
                                  :ruc "1792146739001"}
                 :infoFactura {:fechaEmision "21/10/2012"
                               :dirEstablecimiento "Sebastián Moreno S/N Francisco García"
                               :contribuyenteEspecial "5368"
                               :obligadoContabilidad "SI"
                               :tipoIdentificacionComprador "04"
                               :guiaRemision "001-001-000000001"
                               :razonSocialComprador "PRUEBAS SERVICIO DE RENTAS INTERNAS"
                               :identificacionComprador "1713328506001"
                               :direccionComprador "salinas y santiago"
                               :totalSinImpuestos "295000.00"
                               :totalDescuento "5005.00"
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
                               :propina "0.00"
                               :importeTotal "347159.40"
                               :moneda "DOLAR"
                               :pagos [{:formaPago "01"
                                        :total "347159.40"
                                        :plazo "30"
                                        :unidadTiempo "dias"}]
                               :valorRetIva "10620.00"
                               :valorRetRenta "2950.00"}

                 :detalles [{:codigoPrincipal "125BJC-01"
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
                                          :descuentoAdicional "5.00"
                                          :baseImponible "309750.00"
                                          :valor "37170.00"
                                          :tarifa 12}
                                         {:codigo "5"
                                          :codigoPorcentaje "5001"
                                          :baseImponible "12000.00"
                                          :valor "240.00"
                                          :tarifa "0.02"}]}]
                 :infoAdicional [{:nombre "Codigo Impuesto ISD"
                                  :texto "4580"}
                                 {:nombre "Impuesto ISD"
                                  :texto "15.42x"}]})]
    (is (= expected actual))))

(deftest should-generate-factura-without-infoAdicional-or-detallesAdicionales
  (let [expected (load-xml "./test/res/factura-without-adicionales.xml")
        actual (encoders/factura
                {:infoTributaria {:ambiente 1,
                                  :razonSocial "Jose Olmedo",
                                  :ruc "0957616386001"
                                  :codDoc "01"
                                  :estab "001"
                                  :ptoEmi "001"
                                  :secuencial "000000003"
                                  :dirMatriz "Guayaquil"}
                 :infoFactura {:pagos [{:formaPago "01"
                                        :total 230
                                        :plazo 30
                                        ,:unidadTiempo "dias"}]
                               :direccionComprador "salinas y santiago"
                               :identificacionComprador "1713328506001"
                               :totalConImpuestos [{:codigo 2
                                                    :codigoPorcentaje 4
                                                    :baseImponible 200
                                                    :valor 30}]
                               :razonSocialComprador "PRUEBAS SERVICIO DE RENTAS INTERNAS"
                               :totalDescuento 0
                               :tipoIdentificacionComprador "04"
                               :importeTotal "230"
                               :totalSinImpuestos 200
                               :fechaEmision "17/10/2024"
                               :moneda "DOLAR"
                               :propina 0}
                 :detalles [{:codigoPrincipal "001"
                             :descripcion "Servicios informaticos"
                             :cantidad 1
                             :precioUnitario 200
                             :descuento 0
                             :precioTotalSinImpuesto 200
                             :impuestos [{:codigo 2
                                          :codigoPorcentaje 4
                                          :baseImponible 200
                                          :valor 30
                                          :tarifa 15}]}]})]
    (is (= expected actual))))
