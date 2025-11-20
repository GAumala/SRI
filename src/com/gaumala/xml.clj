(ns com.gaumala.xml
  "Funciones para manipular XML usando [data.xml](
  https://github.com/clojure/data.xml)"
  {:doc/format :markdown}
  (:require [clojure.data.xml :as xml]
            [com.gaumala.xml-doc :as xml-doc])
  (:import java.io.InputStream
           javax.xml.parsers.SAXParser
           org.xml.sax.helpers.DefaultHandler))

(defn parse
  "Convierte un string XML a un record element de la libreria [data.xml](
  https://github.com/clojure/data.xml)"
  {:doc/format :markdown}
  [input] (xml/parse-str input))

(defn find-by-tag
  "Busca en el contenido de `elem` un elemento con tag `tag`.
  Si no lo encuentra devuelve `nil`.
  ```clojure
  (find-by-tag {:tag :date :content [{:tag :day :content [\"01\"]}
                                     {:tag :month :content [\"Jan\"]}
                                     {:tag :year :content [\"2024\"]}]}
               :month)
  ;; => {:tag :month :content [\"Jan\"]}
 ```"
  {:doc/format :markdown}
  [elem tag]
  (cond
    (nil? elem) nil
    (string? elem) nil
    (= tag (:tag elem)) elem
    ; if this is not the tag we are looking for
    ; look trough every child node and return
    ; the first match
    :else (->> (:content elem)
               (map #(find-by-tag % tag))
               (filter some?)
               (first))))

(defn get-content
  "Devuelve el primer elemento del contenido de `elem`.
  Esta diseñada para elementos que solo contienen texto.
  ```clojure
  (get-content {:tag :description :content [\"My description\"]})
  ;; => \"My description\"
  ```"
  {:doc/format :markdown}
  [elem] (first (:content elem)))

(defn map->element
  "Convierte un mapa con keys :tag, :attrs y :content a un
  record element de la librería [data.xml](
  https://github.com/clojure/data.xml). Los items de :content
  también son convertidos recursivamente."
  {:doc/format :markdown}
  [elem]
  (if (string? elem) elem
      (xml/element (:tag elem)
                   (:attrs elem)
                   (map map->element (:content elem)))))

(defn emit
  "Convierte un record element a un string XML formateado para
  ser legible. Puedes usar [[map->element]] para generar el record.
 ```clojure
 (-> {:tag :greeting :attrs {:lang \"EN\"} :content [\"hello\"]}
     (map->element)
     (emit))
 ;; => <?xml version='1.0' encoding='UTF-8'?>
 ;;    <greeting lang=\"EN\">hello</greeting>
 ```"
  {:doc/format :markdown}
  [elem] (-> elem
             xml/emit-str
             xml-doc/pretty-print))
