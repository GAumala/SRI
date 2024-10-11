(ns com.gaumala.utils.base64
  (:import java.util.Base64))

(defmulti encode
  "Multimethod para codificar valores a Base64.
  Solo String y byte array son soportados. Devuelve un String en Base64.
  ```clojure
  (encode \"hello world!\")
  ; => \"aGVsbG8gd29ybGQh\"
  (encode (.getBytes \"hello world!\"))
  ; => \"aGVsbG8gd29ybGQh\"
  ```" {:doc/format :markdown} class)

(defmethod encode java.lang.String [input]
  (.encodeToString (Base64/getEncoder) (.getBytes input)))
(defmethod encode (Class/forName "[B") [input]
  (.encodeToString (Base64/getEncoder) input))

(defn decode
  "Decodifica un String en Base64. Devuelve un byte array."
  [input] (.decode (Base64/getDecoder) input))
