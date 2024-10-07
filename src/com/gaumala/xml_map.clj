(ns com.gaumala.xml-map
  (:require [clojure.data.xml :as xml])
  (:import java.io.InputStream
           javax.xml.parsers.SAXParser
           org.xml.sax.helpers.DefaultHandler))

(defn parse [input] (xml/parse-str input))

(defn find-by-tag [elem tag]
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

(defn get-content [elem] (first (:content elem)))

(defn map->element [elem]
  (if (string? elem) elem
      (xml/element (:tag elem)
                   (:attrs elem)
                   (map map->element (:content elem)))))

(defn emit [elem] (xml/emit-str elem))
