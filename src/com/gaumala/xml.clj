(ns com.gaumala.xml
  (:import [javax.xml.parsers DocumentBuilderFactory]
           [java.io StringReader] 
           [org.xml.sax InputSource]))

(defn string->document [input]
  (let [factory (DocumentBuilderFactory/newInstance)
        builder (.newDocumentBuilder factory)
        is (InputSource. (StringReader. input))]
    (try (.parse builder is)
         (catch Exception e nil))))
