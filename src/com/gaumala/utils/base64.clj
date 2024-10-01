(ns com.gaumala.utils.base64
  (:import java.util.Base64))

(defn encode [to-encode]
  (.encodeToString (Base64/getEncoder) (.getBytes to-encode)))

(defn decode [to-decode]
  (String. (.decode (Base64/getDecoder) to-decode)))
