(ns com.gaumala.sri.factura
  (:require [com.gaumala.sri.clave-acceso :refer [gen-clave-acceso]]))

(defn- simple-tag-hof [params]
  (fn [tag] (if-let [content (get params tag)]
              {:tag tag
               :attrs {}
               :content [(str content)]}
              nil)))

(defn- gen-total-impuesto [params]
  (let [simple-tag (simple-tag-hof params)]
    {:tag :totalImpuesto
     :attrs {}
     :content (filter some? [(simple-tag :codigo)
                             (simple-tag :codigoPorcentaje)
                             (simple-tag :descuentoAdicional)
                             (simple-tag :baseImponible)
                             (simple-tag :valor)])}))

(defn- gen-impuesto [params]
  (let [simple-tag (simple-tag-hof params)]
    {:tag :impuesto
     :attrs {}
     :content (filter some? [(simple-tag :codigo)
                             (simple-tag :codigoPorcentaje)
                             (simple-tag :tarifa)
                             (simple-tag :baseImponible)
                             (simple-tag :valor)])}))

(defn- gen-pago [params]
  (let [simple-tag (simple-tag-hof params)]
    {:tag :pago
     :attrs {}
     :content (filter some? [(simple-tag :formaPago)
                             (simple-tag :total)
                             (simple-tag :plazo)
                             (simple-tag :unidadTiempo)])}))

(defn- gen-detalle [params]
  (let [simple-tag (simple-tag-hof params)
        {:keys [detallesAdicionales impuestos]} params
        adicionales-tag {:tag :detallesAdicionales
                         :attrs {}
                         :content (map (fn [attrs]
                                         {:tag :detAdicional
                                          :attrs (select-keys attrs
                                                              [:nombre
                                                               :valor])})
                                       detallesAdicionales)}
        impuestos-tag {:tag :impuestos
                       :attrs {}
                       :content (map gen-impuesto impuestos)}]
    {:tag :detalle
     :attrs {}
     :content (filter some? [(simple-tag :codigoPrincipal)
                             (simple-tag :codigoAuxiliar)
                             (simple-tag :descripcion)
                             (simple-tag :cantidad)
                             (simple-tag :precioUnitario)
                             (simple-tag :descuento)
                             (simple-tag :precioTotalSinImpuesto)
                             adicionales-tag
                             impuestos-tag])}))

(defn- gen-campo-adicional [{:keys [nombre texto]}]
  {:tag :campoAdicional
   :attrs {:nombre nombre}
   :content [(str texto)]})

(defn gen-info-tributaria [params]
  (let [simple-tag (simple-tag-hof params)]
    {:tag :infoTributaria
     :attrs {}
     :content (filter some? [(simple-tag :ambiente)
                             (simple-tag :tipoEmision)
                             (simple-tag :razonSocial)
                             (simple-tag :nombreComercial)
                             (simple-tag :ruc)
                             (simple-tag :claveAcceso)
                             (simple-tag :codDoc)
                             (simple-tag :estab)
                             (simple-tag :ptoEmi)
                             (simple-tag :secuencial)
                             (simple-tag :dirMatriz)])}))

(defn gen-info-factura [params]
  (let [simple-tag (simple-tag-hof params)
        {:keys [pagos totalConImpuestos]} params
        total-con-impuestos-tag {:tag :totalConImpuestos
                                 :attrs {}
                                 :content (map gen-total-impuesto
                                               totalConImpuestos)}
        pagos-tag {:tag :pagos
                   :attrs {}
                   :content (map gen-pago pagos)}]
    {:tag :infoFactura
     :attrs {}
     :content (filter some? [(simple-tag :fechaEmision)
                             (simple-tag :dirEstablecimiento)
                             (simple-tag :contribuyenteEspecial)
                             (simple-tag :obligadoContabilidad)
                             (simple-tag :tipoIdentificacionComprador)
                             (simple-tag :guiaRemision)
                             (simple-tag :razonSocialComprador)
                             (simple-tag :identificacionComprador)
                             (simple-tag :direccionComprador)
                             (simple-tag :totalSinImpuestos)
                             (simple-tag :totalDescuento)
                             total-con-impuestos-tag
                             (simple-tag :propina)
                             (simple-tag :importeTotal)
                             (simple-tag :moneda)
                             pagos-tag
                             (simple-tag :valorRetIva)
                             (simple-tag :valorRetRenta)])}))

(defn- complete-info-tributaria [params fechaEmision codigo]
  (let [tipoEmision (or (:tipoEmision params) "1") ;offline solo tiene tipo 1
        claveAcceso (or (:claveAcceso params)
                        (gen-clave-acceso (conj params
                                                {:codigoNumerico codigo
                                                 :fechaEmision fechaEmision})))]
    (conj params
          {:tipoEmision tipoEmision
           :claveAcceso claveAcceso})))

(defn gen-factura [params codigo]
  (let [{:keys [infoFactura detalles infoAdicional]} params
        infoTributaria (complete-info-tributaria (:infoTributaria params)
                                                 (:fechaEmision infoFactura)
                                                 codigo)
        info-tributaria-tag (gen-info-tributaria infoTributaria)
        info-factura-tag (gen-info-factura infoFactura)
        detalles-tag {:tag :detalles
                      :attrs {}
                      :content (map gen-detalle detalles)}
        info-adicional-tag {:tag :infoAdicional
                            :attrs {}
                            :content (map gen-campo-adicional
                                          infoAdicional)}]
    {:tag :factura
     :attrs {:id "comprobante" :version "1.0.0"}
     :content [info-tributaria-tag
               info-factura-tag
               detalles-tag
               info-adicional-tag]}))
