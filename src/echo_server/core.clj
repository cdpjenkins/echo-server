(ns echo-server.core
  (:use clojure.contrib.server-socket))

(import
 '(java.io InputStreamReader BufferedReader PrintWriter)
 '(java.net InetAddress ServerSocket))

(defn echo-protocol [in out]
  (binding [*in* (->> in InputStreamReader. BufferedReader.)
            *out* (->> out PrintWriter.)]
    (loop []
      (let [line ( read-line)]
        (println line)
        (when ( not= line "quit")
          (recur)))))
  )

(defn make-server []
  (println "skank-ston")
  (create-server 1234 echo-protocol))
