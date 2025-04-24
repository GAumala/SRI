(ns com.gaumala.sri.comprobantes
  "Funciones para construir mapas con datos de comprobantes
  eletrónicos para posteriormente codificarlos a XML"
  (:require [clojure.spec.alpha :as s]
            [com.gaumala.sri.clave-acceso :refer [gen-clave-acceso]]
            [com.gaumala.xml :as xml]
            [com.gaumala.sri.predicates :refer [code-single-digit?
                                                code-2-digits?
                                                code-3-digits?
                                                code-8-digits?
                                                code-9-digits?
                                                code-13-digits?
                                                code-49-digits?
                                                int-single-digit?
                                                int-up-to-4-digits?
                                                monetary-value?
                                                si-o-no-caps?
                                                some-string?
                                                some-string-under-10?
                                                some-string-under-15?
                                                some-string-under-25?
                                                some-string-under-300?
                                                fechaEmision?
                                                identificacionComprador?
                                                contribuyenteEspecial?
                                                guiaRemision?
                                                tarifa?]]))

(s/def :sri.comprobantes/ruc code-13-digits?)
(s/def :sri.comprobantes/ambiente int-single-digit?)
(s/def :sri.comprobantes/tipoEmision int-single-digit?)
(s/def :sri.comprobantes/razonSocial some-string-under-300?)
(s/def :sri.comprobantes/dirMatriz some-string-under-300?)
(s/def :sri.comprobantes/nombreComercial some-string-under-300?)
(s/def :sri.comprobantes/claveAcceso code-49-digits?)
(s/def :sri.comprobantes/codDoc code-2-digits?)
(s/def :sri.comprobantes/secuencial code-9-digits?)
(s/def :sri.comprobantes/estab code-3-digits?)
(s/def :sri.comprobantes/ptoEmi code-3-digits?)

(s/def :sri.comprobantes/infoTributaria
  (s/keys :req-un [:sri.comprobantes/ambiente
                   :sri.comprobantes/razonSocial
                   :sri.comprobantes/ruc
                   :sri.comprobantes/codDoc
                   :sri.comprobantes/estab
                   :sri.comprobantes/ptoEmi
                   :sri.comprobantes/secuencial
                   :sri.comprobantes/dirMatriz]
          :opt-un [:sri.comprobantes/nombreComercial
                   :sri.comprobantes/claveAcceso
                   :sri.comprobantes/tipoEmision]))

(s/def :sri.comprobantes/fechaEmision fechaEmision?)
(s/def :sri.comprobantes/dirEstablecimiento some-string-under-300?)
(s/def :sri.comprobantes/contribuyenteEspecial contribuyenteEspecial?)
(s/def :sri.comprobantes/obligadoContabilidad si-o-no-caps?)
(s/def :sri.comprobantes/tipoIdentificacionComprador code-2-digits?)
(s/def :sri.comprobantes/identificacionComprador identificacionComprador?)
(s/def :sri.comprobantes/razonSocialComprador some-string-under-300?)
(s/def :sri.comprobantes/direccionComprador some-string-under-300?)
(s/def :sri.comprobantes/guiaRemision guiaRemision?)
(s/def :sri.comprobantes/totalSinImpuestos monetary-value?)
(s/def :sri.comprobantes/totalDescuento monetary-value?)

(s/def :sri.comprobantes.impuesto/codigo int-single-digit?)
(s/def :sri.comprobantes.impuesto/codigoPorcentaje int-up-to-4-digits?)
(s/def :sri.comprobantes.impuesto/baseImponible monetary-value?)
(s/def :sri.comprobantes.impuesto/descuentoAdicional monetary-value?)
(s/def :sri.comprobantes.impuesto/valor monetary-value?)
(s/def :sri.comprobantes.impuesto/tarifa tarifa?)

(s/def :sri.comprobantes/totalImpuesto
  (s/keys :req-un [:sri.comprobantes.impuesto/codigo
                   :sri.comprobantes.impuesto/codigoPorcentaje
                   :sri.comprobantes.impuesto/baseImponible
                   :sri.comprobantes.impuesto/valor]
          :opt-un [:sri.comprobantes.impuesto/descuentoAdicional]))
(s/def :sri.comprobantes/totalConImpuestos (s/+ :sri.comprobantes/totalImpuesto))

(s/def :sri.comprobantes/propina monetary-value?)
(s/def :sri.comprobantes/importeTotal monetary-value?)
(s/def :sri.comprobantes/moneda some-string-under-15?)

(s/def :sri.comprobantes.pago/formaPago code-2-digits?)
(s/def :sri.comprobantes.pago/total monetary-value?)
(s/def :sri.comprobantes.pago/plazo monetary-value?)
(s/def :sri.comprobantes.pago/unidadTiempo some-string-under-10?)

(s/def :sri.comprobantes/pago
  (s/keys :req-un [:sri.comprobantes.pago/formaPago
                   :sri.comprobantes.pago/total]
          :opt-un [:sri.comprobantes.pago/plazo
                   :sri.comprobantes.pago/unidadTiempo]))
(s/def :sri.comprobantes/pagos (s/+ :sri.comprobantes/pago))

(s/def :sri.comprobantes/valorRetIva monetary-value?)
(s/def :sri.comprobantes/valorRetRenta monetary-value?)

(s/def :sri.comprobantes/infoFactura
  (s/keys :req-un [:sri.comprobantes/fechaEmision
                   :sri.comprobantes/razonSocialComprador
                   :sri.comprobantes/tipoIdentificacionComprador
                   :sri.comprobantes/identificacionComprador
                   :sri.comprobantes/totalSinImpuestos
                   :sri.comprobantes/totalDescuento
                   :sri.comprobantes/totalConImpuestos
                   :sri.comprobantes/propina
                   :sri.comprobantes/pagos]
          :opt-un [:sri.comprobantes/dirEstablecimiento
                   :sri.comprobantes/contribuyenteEspecial
                   :sri.comprobantes/obligadoContabilidad
                   :sri.comprobantes/guiaRemision
                   :sri.comprobantes/direccionComprador
                   :sri.comprobantes/moneda
                   :sri.comprobantes/valorRetIva
                   :sri.comprobantes/valorRetRenta]))

(s/def :sri.comprobantes.detalle/codigoPrincipal some-string-under-25?)
(s/def :sri.comprobantes.detalle/codigoAuxiliar  some-string-under-25?)
(s/def :sri.comprobantes.detalle/descripcion  some-string-under-300?)
(s/def :sri.comprobantes.detalle/cantidad  monetary-value?)
(s/def :sri.comprobantes.detalle/precioUnitario  monetary-value?)
(s/def :sri.comprobantes.detalle/descuento  monetary-value?)
(s/def :sri.comprobantes.detalle/precioTotalSinImpuesto  monetary-value?)

(s/def :sri.comprobantes.detAdicional/nombre  some-string-under-300?)
(s/def :sri.comprobantes.detAdicional/valor  some-string-under-300?)

(s/def :sri.comprobantes/detAdicional
  (s/keys :req-un [:sri.comprobantes.detAdicional/nombre
                   :sri.comprobantes.detAdicional/valor]))
(s/def :sri.comprobantes/detallesAdicionales
  (s/+ :sri.comprobantes/detAdicional))

(s/def :sri.comprobantes.detalle/impuesto
  (s/keys :req-un [:sri.comprobantes.impuesto/codigo
                   :sri.comprobantes.impuesto/codigoPorcentaje
                   :sri.comprobantes.impuesto/baseImponible
                   :sri.comprobantes.impuesto/valor
                   :sri.comprobantes.impuesto/tarifa]))
(s/def :sri.comprobantes.detalle/impuestos (s/+ :sri.comprobantes.detalle/impuesto))

(s/def :sri.comprobantes/detalle
  (s/keys :req-un [:sri.comprobantes.detalle/codigoPrincipal
                   :sri.comprobantes.detalle/descripcion
                   :sri.comprobantes.detalle/cantidad
                   :sri.comprobantes.detalle/precioUnitario
                   :sri.comprobantes.detalle/descuento
                   :sri.comprobantes.detalle/precioTotalSinImpuesto
                   :sri.comprobantes.detalle/impuestos]
          :opt-un [:sri.comprobantes.detalle/codigoAuxiliar
                   :sri.comprobantes/detallesAdicionales]))
(s/def :sri.comprobantes/detalles (s/+ :sri.comprobantes/detalle))

(s/def :sri.comprobantes.campoAdicional/nombre  some-string-under-300?)
(s/def :sri.comprobantes.campoAdicional/texto  some-string-under-300?)
(s/def :sri.comprobantes/campoAdicional
  (s/keys :req-un [:sri.comprobantes.campoAdicional/nombre
                   :sri.comprobantes.campoAdicional/texto]))
(s/def :sri.comprobantes/infoAdicional (s/+ :sri.comprobantes/campoAdicional))

(s/def :sri.comprobantes/factura
  (s/keys :req-un [:sri.comprobantes/infoTributaria
                   :sri.comprobantes/infoFactura
                   :sri.comprobantes/detalles]
          :opt-un [:sri.comprobantes/infoAdicional]))

(defn- simple-tag-hof [params]
  (fn [tag] (if-let [content (get params tag)] {:tag tag :attrs {}
                                                :content [(str content)]}
                    nil)))

(defn- sequence-tag [tag content]
  (if (empty? content) nil {:tag tag
                            :attrs {}
                            :content content}))

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

(defn- gen-det-adicional [attrs]
  {:tag :detAdicional
   :attrs (select-keys attrs
                       [:nombre
                        :valor])})

(defn- gen-detalle [params]
  (let [simple-tag (simple-tag-hof params)
        {:keys [detallesAdicionales impuestos]} params
        detalles-adicionales-tag (some->> detallesAdicionales
                                          (map gen-det-adicional)
                                          (sequence-tag :detallesAdicionales))
        impuestos-tag (some->> impuestos
                               (map gen-impuesto)
                               (sequence-tag :impuestos))]
    {:tag :detalle
     :attrs {}
     :content (filter some? [(simple-tag :codigoPrincipal)
                             (simple-tag :codigoAuxiliar)
                             (simple-tag :descripcion)
                             (simple-tag :cantidad)
                             (simple-tag :precioUnitario)
                             (simple-tag :descuento)
                             (simple-tag :precioTotalSinImpuesto)
                             detalles-adicionales-tag
                             impuestos-tag])}))

(defn- gen-campo-adicional [{:keys [nombre texto]}]
  {:tag :campoAdicional
   :attrs {:nombre nombre}
   :content [(str texto)]})

(defn- gen-info-tributaria [params]
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

(defn- gen-info-factura [params]
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
        detalles-tag (some->> detalles
                              (map gen-detalle)
                              (sequence-tag :detalles))
        info-adicional-tag (some->> infoAdicional
                                    (map gen-campo-adicional)
                                    (sequence-tag :infoAdicional))]
    (xml/map->element
     {:tag :factura
      :attrs {:id "comprobante" :version "1.0.0"}
      :content (filter some? [info-tributaria-tag
                              info-factura-tag
                              detalles-tag
                              info-adicional-tag])})))
