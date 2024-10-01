(ns com.gaumala.xml-doc
  (:import java.io.File
           java.io.StringReader
           java.io.StringWriter

           javax.xml.parsers.DocumentBuilderFactory
           javax.xml.transform.OutputKeys
           javax.xml.transform.TransformerFactory
           javax.xml.transform.dom.DOMSource
           javax.xml.transform.stream.StreamResult

           org.xml.sax.InputSource))

(defn string->document [input]
  (let [factory (DocumentBuilderFactory/newInstance)
        builder (.newDocumentBuilder factory)
        is (InputSource. (StringReader. input))]
    (try (.parse builder is)
         (catch Exception e nil))))

(defn document->string [^org.w3c.dom.Document input]
  (let [sw (StringWriter.)
        tf (TransformerFactory/newInstance)
        transformer (.newTransformer tf)]
    (.setOutputProperty transformer OutputKeys/OMIT_XML_DECLARATION "no")
    (.setOutputProperty transformer OutputKeys/METHOD "xml")
    (.setOutputProperty transformer OutputKeys/INDENT "yes")
    (.setOutputProperty transformer OutputKeys/ENCODING "UTF-8")
    (.transform transformer (DOMSource. input) (StreamResult. sw))
    (.toString sw)))

(defn node->string [^org.w3c.dom.Node input]
  (let [sw (StringWriter.)
        tf (TransformerFactory/newInstance)
        transformer (.newTransformer tf)]
    (.setOutputProperty transformer OutputKeys/OMIT_XML_DECLARATION "no")
    (.setOutputProperty transformer OutputKeys/METHOD "xml")
    (.setOutputProperty transformer OutputKeys/INDENT "yes")
    (.setOutputProperty transformer OutputKeys/ENCODING "UTF-8")
    (.transform transformer (DOMSource. input) (StreamResult. sw))
    (.toString sw)))

(defn write-document-at [doc path]
  (let [factory (TransformerFactory/newInstance)
        transformer (.newTransformer factory)
        source (DOMSource. doc)
        result (StreamResult. (File. path))]
    (.transform transformer source result)))

(defn find-by-tag-name [^org.w3c.dom.Element input tag-name]
  (-> input
      (.getElementsByTagName tag-name)
      (.item 0)))

(defn find-all-by-tag-name [^org.w3c.dom.Element input tag-name]
  (let [results (.getElementsByTagName input tag-name)
        indices (range (.getLength results))]
    (map #(.item results %) indices)))
