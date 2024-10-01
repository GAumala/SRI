(ns com.gaumala.xml-map
  (:require [clojure.string :as s]
            [clojure.xml :as xml])
  (:import java.io.InputStream
           javax.xml.parsers.SAXParser
           org.xml.sax.helpers.DefaultHandler))

;; replace clojure.xml version with one that doesn't do illegal access
(defn- startparse [^InputStream ins ^DefaultHandler ch]
  (let [^SAXParser p (xml/disable-external-entities (xml/sax-parser))]
    (.parse p ins ch)))

(defn parse [^java.lang.String input] (xml/parse (java.io.ByteArrayInputStream. (.getBytes input))
                                                 startparse))

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

(defn encode-soap-message [body]
  (let [elem {:tag :soap:Envelope
              :attrs {:xmlns:soap "http://schemas.xmlsoap.org/soap/envelope/"}
              :content [{:tag :soap:Body
                         :attrs nil
                         :content [body]}]}
        emitted (with-out-str (xml/emit-element elem))]
    ; emitted uses single quotes instead of
    ; double quotes. we have to fix that
    (s/replace emitted #"'" "\"")))
