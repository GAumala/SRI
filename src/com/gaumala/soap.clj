(ns com.gaumala.soap
  (:require [clojure.data.xml :as xml]))

(defn message-with-body [body]
  (xml/element :soap:Envelope {:xmlns:soap "http://schemas.xmlsoap.org/soap/envelope/"}
               (xml/element :soap:Body {} body)))
