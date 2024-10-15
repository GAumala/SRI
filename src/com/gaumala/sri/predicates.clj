(ns com.gaumala.sri.predicates
  "Predicados para specs de comprobantes del SRI"
  (:require [clojure.string :as str]))

(def monetary-value-regex #"^[0-9]+.?[0-9]*$")
(defn monetary-value?
  "Predicado que acepta números o strings numéricos con 14 caracteres o menos"
  [x]
  (let [text (str x)] (and (>= 14 (count text))
                           (re-matches monetary-value-regex text))))

(def int-regex #"^[0-9]+$")

(defn int-single-digit? [x]
  (let [text (str x)]
    (and (re-matches int-regex text) (= 1 (count text)))))

(defn int-up-to-4-digits? [x]
  (let [text (str x)]
    (and (re-matches int-regex text) (>= 4 (count text)))))

(defn code-single-digit? [x]
  (and (string? x) (= 1 (count x)) (re-matches int-regex x)))

(defn code-2-digits? [x]
  (and (string? x) (= 2 (count x)) (re-matches int-regex x)))

(defn code-3-digits? [x]
  (and (string? x) (= 3 (count x)) (re-matches int-regex x)))

(defn code-8-digits? [x]
  (and (string? x) (= 8 (count x)) (re-matches int-regex x)))

(defn code-9-digits? [x]
  (and (string? x) (= 9 (count x)) (re-matches int-regex x)))

(defn code-13-digits? [x]
  (and (string? x) (= 13 (count x)) (re-matches int-regex x)))

(defn code-49-digits? [x]
  (and (string? x) (= 49 (count x)) (re-matches int-regex x)))

(defn some-string?
  "Predicado que acepta strings que no esten vacíos"
  [x] (and (string? x) (not-empty x)))

(defn some-string-under-10? [x]
  (and (string? x) (not-empty x) (>= 10 (count x))))

(defn some-string-under-15? [x]
  (and (string? x) (not-empty x) (>= 15 (count x))))

(defn some-string-under-25? [x]
  (and (string? x) (not-empty x) (>= 25 (count x))))

(defn some-string-under-300? [x]
  (and (string? x) (not-empty x) (>= 300 (count x))))

(defn si-o-no-caps?
  "Predicado que acepta unicamente los strings \"SI\" y \"NO\""
  [x] (or (= "SI" x) (= "NO" x)))

(def id-comprador-regex #"^[0-9a-zA-Z]{3,20}$")
(defn identificacionComprador?
  "Predicado para el campo `identificacionComprador`.
  Acepta strings alfanumericos de hasta 20 caracteres y mínimo 3 caracteres."
  {:doc/format :markdown}
  [x] (and (string? x) (re-matches id-comprador-regex x)))

(defn contribuyenteEspecial?
  "Predicado para el campo `contribuyenteEspecial`.
 Acepta cualquier string de hasta 20 caracteres y mínimo 3 caracteres."
  {:doc/format :markdown}
  [x] (and (string? x)
           (<= 3 (count x))
           (>= 13 (count x))))

(def guia-remision-regex #"^[0-9]{3}-?[0-9]{3}-?[0-9]{9}$")
(defn guiaRemision?
  "Predicado para el campo `guiaRemision`.
 Acepta strings numéricos de 15 digitos que pueden adicionalmente
 contener guiones como parte del formateo"
  {:doc/format :markdown}
  [x] (and (string? x) (re-matches guia-remision-regex x)))

(defn- parse-fecha [x] (try
                         (doall (map #(Integer/parseInt %) (str/split x #"/")))
                         (catch Exception e nil)))

(defn fechaEmision? [x]
  "Predicado para el campo `fechaEmision`.
  Acepta strings con fechas en formato dd/mm/yyyy"
  {:doc/format :markdown}
  (and (string? x)
       (= 10 (count x))
       (if-let [[dd mm yyyy] (parse-fecha x)]
         (cond
           (< dd 1) false
           (> dd 31) false
           (< mm 1) false
           (> mm 12) false
           (< yyyy 2012) false
           :else true) false)))

(defn tarifa?
  "Predicado para el campo `tarifa`
  Acepta números o strings numéricos con 4 caracteres o menos"
  [x]
  (let [text (str x)] (and (>= 4 (count text))
                           (re-matches monetary-value-regex text))))
